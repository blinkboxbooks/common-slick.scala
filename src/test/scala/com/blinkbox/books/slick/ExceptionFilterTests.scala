package com.blinkbox.books.slick

import java.sql.SQLException

import com.blinkbox.books.test.FailHelper
import com.mysql.jdbc.exceptions.jdbc4.{MySQLIntegrityConstraintViolationException => JDBC4MySQLIntegrityConstraintViolation}
import com.mysql.jdbc.exceptions._
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import java.sql.{BatchUpdateException, SQLIntegrityConstraintViolationException}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.slick.driver.JdbcProfile

import scala.language.reflectiveCalls

@RunWith(classOf[JUnitRunner])
class ExceptionFilterTests extends FlatSpec with FailHelper {

  "Default exception filter" should "propagate transformed database exceptions" in new TestFixture {
    failingWith[ConstraintException] {
      Future { throw new SQLException("test sql exception") } transform(identity, ExceptionFilter.default)
    }
  }

  "MySQLDatabaseSupport exception filter" should "work with java.sql.SQLIntegrityConstraintViolationException and subclasses" in new TestFixture {
    val ex = intercept[ConstraintException] {
      mysqlDbSupport.throwEx(new JDBC4MySQLIntegrityConstraintViolation("test sql exception"))
    }
    assert(ex.getMessage == "test sql exception")
  }

  it should "work with com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException" in new TestFixture {
    val ex = intercept[ConstraintException] {
      mysqlDbSupport.throwEx(new MySQLIntegrityConstraintViolationException("test sql exception"))
    }
    assert(ex.getMessage == "test sql exception")
  }

  it should "work with java.sql.BatchUpdateException" in new TestFixture {
    val ex = intercept[ConstraintException] {
      val cause = new SQLIntegrityConstraintViolationException("test sql exception")
      mysqlDbSupport.throwEx(new BatchUpdateException(cause))
    }
    assert(ex.getMessage == "java.sql.SQLIntegrityConstraintViolationException: test sql exception")
  }

  it should "work with MySQLSyntaxErrorException" in new TestFixture {
    val ex = intercept[UnknownDatabaseException] {
      val cause = new MySQLSyntaxErrorException("test mysql syntax exception")
      mysqlDbSupport.throwEx(cause)
    }
    assert(ex.getMessage == "test mysql syntax exception")
  }

  it should "work with jdbc4.MySQLSyntaxErrorException" in new TestFixture {
    val ex = intercept[UnknownDatabaseException] {
      val cause = new jdbc4.MySQLSyntaxErrorException("Table 'foo.bars' doesn't exist")
      mysqlDbSupport.throwEx(cause)
    }
    assert(ex.getMessage == "Table 'foo.bars' doesn't exist")
  }

  class TestFixture extends DatabaseSupport {
    override type Profile = JdbcProfile

    override protected def exceptionTransformer: ExceptionTransformer = {
      case ex: SQLException => ConstraintException(ex)
    }

    val mysqlDbSupport = new MySQLDatabaseSupport {
      def throwEx(ex: Throwable) = throw this.exceptionTransformer(ex)
    }
  }
}
