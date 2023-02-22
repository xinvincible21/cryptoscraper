package com.invincible

import caliban.ZHttpAdapter
import zhttp.http._
import zhttp.service.Server
import zio._
import zio.stream._

object CryptoApp extends ZIOAppDefault {

  private val graphiql = Http.fromStream(ZStream.fromResource("graphiql.html"))

  override def run = {
    (for {
      interpreter <- CryptoApi.api.interpreter
      _           <- Server
                       .start(
                         8088,
                         Http.collectHttp[Request] {
                           case _ -> !! / "api" / "graphql" => ZHttpAdapter.makeHttpService(interpreter)
                           case _ -> !! / "ws" / "graphql"  => ZHttpAdapter.makeWebSocketService(interpreter)
                           case _ -> !! / "graphiql"        => graphiql
                         }
                       )
                       .forever
    } yield ())
      .provideLayer(CryptoService.make(MongoDB.loadAll()))
      .exitCode
  }
}
