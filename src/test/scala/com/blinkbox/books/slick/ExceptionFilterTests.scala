package com.blinkbox.books.slick

import java.sql.SQLException

import com.blinkbox.books.test.FailHelper
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.slick.driver.JdbcProfile

@RunWith(classOf[JUnitRunner])
class ExceptionFilterTests extends FlatSpec with FailHelper {

  "Default exception filter" should "propagate transformed database exceptions" in new TestFixture {

    failingWith[ConstraintException] {
      Future.failed(new SQLException("test sql exception")).transform(identity, ExceptionFilter.default)
    }
  }

  class TestFixture extends DatabaseSupport {
    override type Profile = JdbcProfile

    override protected def exceptionTransformer: ExceptionTransformer = {
      case ex: SQLException => ConstraintException(ex)
    }
  }
}
