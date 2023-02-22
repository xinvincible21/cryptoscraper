package com.invincible

import caliban.GraphQL.graphQL
import caliban.{GraphQL, RootResolver}
import caliban.schema.Annotations.GQLDescription
import caliban.schema.{GenericSchema, Schema}
import caliban.wrappers.ApolloTracing.apolloTracing
import caliban.wrappers.Wrappers._
import com.invincible.CryptoData.{Crypto, CryptoArgs, CryptosArgs}
import com.invincible.CryptoService.CryptoService
import zio._

import scala.language.postfixOps

object CryptoApi extends GenericSchema[CryptoService] {

  case class Queries(
    @GQLDescription("Return all symbols")
    cryptos: CryptosArgs => URIO[CryptoService, List[Crypto]],
    @GQLDescription("Return crypto from a given symbol")
    crypto: CryptoArgs => URIO[CryptoService, Option[Crypto]]
  )

  implicit val cryptoSchema: Schema[Any, Crypto]           = Schema.gen
  implicit val cryptoArgsSchema: Schema[Any, CryptoArgs]   = Schema.gen
  implicit val cryptosArgsSchema: Schema[Any, CryptosArgs] = Schema.gen

  val api: GraphQL[CryptoService] =
    graphQL(
      RootResolver(
        Queries(
          _ => CryptoService.getCryptos(),
          args => CryptoService.findCrypto(args.symbol)
        ),
      )
    ) @@
      maxFields(200) @@               // query analyzer that limit query fields
      maxDepth(30) @@                 // query analyzer that limit query depth
      timeout(3 seconds) @@           // wrapper that fails slow queries
      printSlowQueries(500 millis) @@ // wrapper that logs slow queries
      printErrors @@                  // wrapper that logs errors
      apolloTracing                   // wrapper for https://github.com/apollographql/apollo-tracing

}
