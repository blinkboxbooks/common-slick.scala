package com.blinkbox.books.slick

import com.codahale.metrics.health.HealthCheck
import com.codahale.metrics.health.HealthCheck.Result

import scala.slick.jdbc.{GetResult, StaticQuery}
import scala.util.control.NonFatal

class DatabaseHealthCheck[DB <: DatabaseSupport](db: DB#Database) extends HealthCheck {
  override def check(): Result =
    try {
      db.withSession { implicit session =>
        StaticQuery.queryNA("SELECT 1")(GetResult.GetInt) foreach { c => }
      }
      HealthCheck.Result.healthy()
    } catch {
      case NonFatal(e) => HealthCheck.Result.unhealthy(e)
    }
}
