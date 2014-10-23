package com.blinkbox.books.slick

import com.codahale.metrics.health.HealthCheck
import org.scalatest.FlatSpec

import scala.slick.jdbc.JdbcBackend.Database

class DatabaseHealthCheckSpec extends FlatSpec {

  "DatabaseHealthCheck" should "return healthy for connected databases" in {
    val db = Database.forURL(s"jdbc:h2:mem:", driver = "org.h2.Driver")
    val check = new DatabaseHealthCheck[H2DatabaseSupport](db)
    assert(check.check() == HealthCheck.Result.healthy())
  }

  it should "return unhealthy for inaccessible databases" in {
    val db = Database.forURL(s"jdbc:mysql://invalid/invalid", driver = "com.mysql.jdbc.Driver")
    val check = new DatabaseHealthCheck[MySQLDatabaseSupport](db)
    val result = check.check()
    assert(!result.isHealthy)
    assert(result.getError != null)
  }

}
