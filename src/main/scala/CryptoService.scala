package com.invincible

import com.invincible.CryptoData.Crypto
import zio.{Ref, UIO, URIO, ZIO, ZLayer}

object CryptoService {


  trait CryptoService {
    def getCryptos(): UIO[List[Crypto]]

    def findCrypto(symbol: String): UIO[Option[Crypto]]
  }

  def getCryptos(): URIO[CryptoService, List[Crypto]] =
    ZIO.serviceWithZIO(_.getCryptos())

  def findCrypto(symbol: String): URIO[CryptoService, Option[Crypto]] =
    ZIO.serviceWithZIO(_.findCrypto(symbol))

  def make(initial: List[Crypto]): ZLayer[Any, Nothing, CryptoService] = ZLayer {
    for {
      cryptos  <- Ref.make(initial)
    } yield new CryptoService {

      def getCryptos(): UIO[List[Crypto]] = cryptos.get

      def findCrypto(symbol: String): UIO[Option[Crypto]] = cryptos.get.map(_.find(c => c.symbol == symbol))
    }
  }
}
