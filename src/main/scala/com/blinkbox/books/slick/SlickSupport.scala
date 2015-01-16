package com.blinkbox.books.slick

import java.sql._

import com.mysql.jdbc.exceptions._
import org.h2.api.ErrorCode
import org.joda.time.{DateTime, DateTimeZone}

import scala.slick.driver.JdbcProfile
import scala.slick.profile._

sealed abstract class DatabaseException(cause: SQLException) extends Exception(cause.getMessage, cause)
case class ConstraintException(cause: SQLException) extends DatabaseException(cause)
case class UnknownDatabaseException(cause: SQLException) extends DatabaseException(cause)

/**
 * Utility to mix in to get type alias for classes depending on a specific slick profile
 */
trait SlickTypes[Profile <: BasicProfile] {
  type Session = Profile#Backend#Session
  type Database = Profile#Backend#Database
}

/**
 * Trait that provides support for a specific profile; ideal to mix in a wrapper for slick tables definitions
 */
trait TablesContainer[Profile <: JdbcProfile] {
  val driver: Profile

  import driver.simple._

  protected implicit def jodaDateTimeColumnType = MappedColumnType.base[DateTime, java.sql.Timestamp](
    dt => new java.sql.Timestamp(dt.getMillis),
    ts => new DateTime(ts.getTime, DateTimeZone.UTC))
}

/**
 * Trait to mix in to signal that an implementation of the TablesContainer is needed for the proper Profile
 */
trait TablesSupport[Profile <: JdbcProfile, Tables <: TablesContainer[Profile]] {
  val tables: Tables
}

/**
 * Basic types used in the subsequent components to remain db-agnostic
 */
trait DatabaseSupport {
  type Session = Profile#Backend#Session
  type Profile <: JdbcProfile
  type Database = Profile#Backend#Database

  // The exception transformer should be implemented to wrap db-specific exception in db-agnostic ones
  protected type ExceptionTransformer = PartialFunction[Throwable, DatabaseException]
  protected def exceptionTransformer: ExceptionTransformer

  // This lifts the exception transformer so that it is defined for any throwable in a way that transform db-specific
  // exceptions and leave non-db-specific ones untouched
  private def liftedTransformer = exceptionTransformer.orElse[Throwable, Throwable] { case ex => ex }

  // This object should be used to catch db exceptions
  object ExceptionFilter {
    def apply(f: PartialFunction[Throwable, Throwable]) = liftedTransformer andThen f
    // propagates the transformed exception as is
    def default = apply { case e => e }
  }

  type ExceptionFilter = ExceptionFilter.type
}

/**
 * Base component to be implemented for a profile-parametrized database connection. Implementations should
 * rely on the exceptionFilter to abstract away the different exception types used in different databases
 * (i.e. at the moment exceptions related to constraint violations)
 */
trait DatabaseComponent {
  val DB: DatabaseSupport
  type Tables <: TablesContainer[DB.Profile]

  def driver: DB.Profile
  def db: DB.Database
  def tables: Tables

  lazy val exceptionFilter = DB.ExceptionFilter
}

/**
 * Stub for implementing repository components
 */
trait RepositoriesComponent {
  this: DatabaseComponent =>
}

class MySQLDatabaseSupport extends DatabaseSupport {
  type Profile = JdbcProfile

  override def exceptionTransformer = {
    case ex: MySQLIntegrityConstraintViolationException => ConstraintException(ex)
    case ex: SQLIntegrityConstraintViolationException => ConstraintException(ex)
    case ex: BatchUpdateException if ex.getCause != null && ex.getCause.isInstanceOf[SQLIntegrityConstraintViolationException] => ConstraintException(ex)
    case ex: SQLException => UnknownDatabaseException(ex)
  }
}

class H2DatabaseSupport extends DatabaseSupport {
  type Profile = JdbcProfile

  private val constraintViolationCodes = Set(
    ErrorCode.DUPLICATE_KEY_1,
    ErrorCode.REFERENTIAL_INTEGRITY_VIOLATED_CHILD_EXISTS_1,
    ErrorCode.REFERENTIAL_INTEGRITY_VIOLATED_PARENT_MISSING_1)

  override def exceptionTransformer = {
    case ex: SQLException if constraintViolationCodes contains ex.getErrorCode => ConstraintException(ex)
  }
}
