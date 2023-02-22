package com.invincible

import com.invincible.CryptoData.Crypto
import com.invincible.Utils.{dateSuffix, initDriver}
import com.mongodb.client.MongoCollection
import org.bson.Document
import org.openqa.selenium.{By, JavascriptExecutor}

import java.lang.Thread.sleep

object FindGeminiPrices {

  def main(args:Array[String]):Unit = {

    implicit val driver = initDriver()
    implicit val client = MongoDB.getClient()
    implicit val db = MongoDB.getDb(s"cryptoscraper")
    implicit val coll: MongoCollection[Document] = MongoDB.getCollection(s"crypto_${dateSuffix()}")
    driver.get(s"https://gemini.com/prices")
    val js = driver.asInstanceOf[JavascriptExecutor]
    var lastCrypto:Crypto = null
    val tempCryptos =
      (
        for (c <- 0 to 136) yield {
          sleep(500)
          try {
            val nameSymbol = driver.findElement(By.xpath(s"//div/div/div/div[1]/div[2]/div/div[1]/div[1]/div/span"))
            val u = nameSymbol.getText.split("\\n")
            val price = driver.findElement(By.xpath(s"//h5[contains(text(),'${u(0)}')]/../../../../../..//h2/span"))
            val crypto = Crypto(u(0), u(1), price.getText.replaceAll(",", "").replaceAll("\\$", "").toDouble)
            js.executeScript("window.scrollBy(0,105)", "")
            if(crypto == lastCrypto) None
            else {
              lastCrypto = crypto
              println(crypto)
              Some(crypto)
            }
          }catch {case e:Throwable => None
          }
        }
      ).flatten.toList


    js.executeScript("window.scrollBy(0,14000)")
    val nameSymbol2 = driver.findElement(By.xpath(s"//h5[(text()='Terra')]/.."))
    val u2 = nameSymbol2.getText.split("\\n")
    val price2 = driver.findElement(By.xpath(s"//h5[contains(text(),'${u2(0)}')]/../../../../../..//h2/span"))
    val secondLast = Crypto(u2(0), u2(1), price2.getText.replaceAll(",", "").replaceAll("\\$", "").toDouble)
    println(secondLast)
    val nameSymbol3 = driver.findElement(By.xpath(s"//h5[(text()='Mirror')]/.."))
    val u3 = nameSymbol3.getText.split("\\n")
    val price3 = driver.findElement(By.xpath(s"//h5[contains(text(),'${u3(0)}')]/../../../../../..//h2/span"))
    val last = Crypto(u3(0), u3(1), price3.getText.replaceAll(",", "").replaceAll("\\$", "").toDouble)
    println(last)
    val cryptos = tempCryptos++List(secondLast,last)
    val distinctCryptos = cryptos.toSet
    MongoDB.insert(distinctCryptos)
    val distinctCryptos2 = MongoDB.findAll().toSet
    println(s"Missing entries ${distinctCryptos2.map(_.symbol) -- distinctCryptos.map(_.symbol)}")
    println(s"Total cryptos ${distinctCryptos.size}")
    driver.close()
    System.exit(0)
  }
}
