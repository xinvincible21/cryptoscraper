package com.invincible

import com.invincible.CryptoData.Crypto
import com.invincible.Utils.{dateTimeSuffix, initDriver}
import com.mongodb.client.MongoCollection
import org.bson.Document
import org.openqa.selenium.{By, JavascriptExecutor}

import java.lang.Thread.sleep

object FindGeminiPrices {

  def main(args:Array[String]):Unit = {

    implicit val driver = initDriver()
    implicit val client = MongoDB.getClient()
    implicit val db = MongoDB.getDb(s"cryptoscraper")
    implicit val coll: MongoCollection[Document] = MongoDB.getCollection(s"crypto_${dateTimeSuffix()}")
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
            val twentyFourHrChange = driver.findElement(By.xpath(s"//h5[contains(text(),'${u(0)}')]/../../../../../..//div/div/div"))
            val tempMarketCap = driver.findElement(By.xpath(s"//h5[contains(text(),'${u(0)}')]/../../../../../..//div/div[3]/span")).getAttribute("innerHTML")
            val marketCap = if(tempMarketCap.startsWith("<span")) "" else tempMarketCap
            val crypto =
              Crypto(
                name = u(0),
                symbol = u(1),
                price = price.getText.replaceAll(",", "").replaceAll("\\$", "").toDouble,
                twentyFourHrChange = twentyFourHrChange.getText.replaceAll(",", "").replaceAll("\\$", "").toDouble,
                marketCap = marketCap
              )
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
    val twentyFourHrChange2 = driver.findElement(By.xpath(s"//h5[contains(text(),'${u2(0)}')]/../../../../../..//div/div/div"))
    val tempMarketCap2 = driver.findElement(By.xpath(s"//h5[contains(text(),'${u2(0)}')]/../../../../../..//div/div[3]/span")).getAttribute("innerHTML")
    val marketCap2 = if (tempMarketCap2.startsWith("<span")) "" else tempMarketCap2
    val secondLast =
      Crypto(
        name = u2(0),
        symbol = u2(1),
        price = price2.getText.replaceAll(",", "").replaceAll("\\$", "").toDouble,
        twentyFourHrChange = twentyFourHrChange2.getText.replaceAll(",", "").replaceAll("\\$", "").toDouble,
        marketCap = marketCap2
      )
    println(secondLast)
    val nameSymbol3 = driver.findElement(By.xpath(s"//h5[(text()='Mirror')]/.."))
    val u3 = nameSymbol3.getText.split("\\n")
    val price3 = driver.findElement(By.xpath(s"//h5[contains(text(),'${u3(0)}')]/../../../../../..//h2/span"))
    val twentyFourHrChange3 = driver.findElement(By.xpath(s"//h5[contains(text(),'${u3(0)}')]/../../../../../..//div/div/div"))
    val tempMarketCap3 = driver.findElement(By.xpath(s"//h5[contains(text(),'${u3(0)}')]/../../../../../..//div/div[3]/span")).getAttribute("innerHTML")
    val marketCap3 = if (tempMarketCap3.startsWith("<span")) "" else tempMarketCap3
    val last =
      Crypto(
        name = u3(0),
        symbol = u3(1),
        price = price3.getText.replaceAll(",", "").replaceAll("\\$", "").toDouble,
        twentyFourHrChange = twentyFourHrChange3.getText.replaceAll(",", "").replaceAll("\\$", "").toDouble,
        marketCap = marketCap3
      )
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
