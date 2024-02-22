package com.example.vertx.error

import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.kotlin.mysqlclient.mySQLConnectOptionsOf
import io.vertx.mysqlclient.MySQLBuilder
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class TestBuildError {

  @Test
  @DisplayName("数据库测试")
  fun testMysql(vertx: Vertx, testContext: VertxTestContext): Unit = runBlocking(vertx.dispatcher()) {
    val connectOption = mySQLConnectOptionsOf(
      port = 3306,
      host = "127.0.0.1",
      database = "test",
      user = "test",
      password = "test",
      charset = "utf8",
      tcpKeepAlive = true,
      idleTimeout = 600,
      connectTimeout = 1000,
      cachePreparedStatements = true,
      ssl = false
    )
    val mysql = MySQLBuilder.client()
      .connectingTo(connectOption)
      .using(vertx)
      .build()
    runCatching {
      mysql
        .query("SELECT 1 as id")
        .execute()
        .coAwait()
        .first()
        .getInteger("id")
        .let {
          assertThat(it).isEqualTo(1)
          testContext.completeNow()
        }
    }.onFailure {
      println(it)
      testContext.failNow(it)
    }
  }
}
