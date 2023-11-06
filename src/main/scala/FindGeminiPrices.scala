package com.invincible

import com.invincible.CryptoData.Crypto
import com.invincible.Utils.{dateTimeSuffix, initDriver}
import com.mongodb.client.MongoCollection
import org.bson.Document
import org.openqa.selenium.By
import org.slf4j.LoggerFactory

import java.lang.Thread.sleep


object FindGeminiPrices {

  val logger = LoggerFactory.getLogger(this.getClass.getName)

  def main(args:Array[String]):Unit = {

    implicit val driver = initDriver()
    implicit val client = MongoDB.getClient()
    implicit val db = MongoDB.getDb(s"cryptoscraper")
    implicit val coll: MongoCollection[Document] = MongoDB.getCollection(s"crypto_${dateTimeSuffix()}")
    try {
      driver.get(s"https://gemini.com/prices")
      val cryptos =
        (
          for (i <- 2 to 143) yield {
                      sleep(500)
            try {
              val row = driver.findElement(By.xpath(s"//section/div/div/div/div[1]/div[$i]"))
              val u = row.getText.split("\\n")
              val name = u(0)
//              logger.debug(s"name $name")
              val symbol = u(1)
//              logger.debug(s"symbol $symbol")
              val price = if (u.length >= 3) u(2).replaceAll(",", "").replaceAll("\\$", "").toDouble else 0
//              logger.debug(s"price $price")
              val twentyFourHrChange =
                if (u.length >= 4) u(3).replaceAll(",", "").replaceAll("\\$", "") else ""
//              logger.debug(s"twentyFourHrChange $twentyFourHrChange")
              val change =
                if (u.length >= 5) u(4).replace("%", "").toDouble else 0
//              logger.debug(s"change $change")
              val marketCap =
                if (u.length == 6) u(5).replaceAll(",", "").replaceAll("\\$", "") else ""
//              logger.debug(s"marketCap $marketCap")
              val c = Crypto(
                name = name,
                symbol = symbol,
                price = price,
                twentyFourHrChange = twentyFourHrChange,
                change = change,
                marketCap = marketCap
              )
              logger.debug(c.toString)
              Some(c)
            } catch {
              case e: Throwable =>
                logger.debug(e.getMessage)
                None
            }
          }
          ).flatten.toList


      val oldCollectionName = MongoDB.findLatestCollection()
      val oldColl = MongoDB.getCollection(oldCollectionName)
      val distinctCryptos2 = MongoDB.findAll()(oldColl).toSet
      val distinctCryptos = cryptos.toSet
      MongoDB.insert(distinctCryptos)


      val symbolSet1 = distinctCryptos2.map(_.symbol)
      val symbolSet2 = distinctCryptos.map(_.symbol)
      val missingEntries = (symbolSet1 diff symbolSet2) ++ (symbolSet2 diff symbolSet1)
      logger.debug(s"Missing entries $missingEntries")
      logger.debug(s"Total cryptos scraped ${distinctCryptos.size}")
      logger.debug(s"Total cryptos in db ${distinctCryptos2.size}")
      driver.close()
      System.exit(0)
    } catch {
      case e:Throwable => logger.debug(e.getMessage)
    } finally driver.close()
  }
}
