package com.invincible

import com.invincible.CryptoData.Crypto
import com.invincible.Utils.convertToDisplayNumber
import com.mongodb.client.{MongoClient, MongoClients, MongoCollection, MongoDatabase}
import org.bson.Document
import org.bson.conversions.Bson

import scala.collection.JavaConverters._
import com.mongodb.client.model.Filters
import org.bson.types.ObjectId

object MongoDB {

  def getClient() = MongoClients.create("mongodb://127.0.0.1:27017")
  def getDb(name:String)(implicit client:MongoClient) = client.getDatabase(name)
  def getCollection(name: String)(implicit db: MongoDatabase) = db.getCollection(name)

  def findLatestCollection()(implicit db: MongoDatabase) =
    db.listCollections().iterator().asScala.toList.map(_.get("name").toString).sorted.last

  def loadAll() = {
    implicit val client = MongoDB.getClient()
    implicit val db = MongoDB.getDb("cryptoscraper")
    implicit val coll: MongoCollection[Document] = getCollection(MongoDB.findLatestCollection())
    val cryptos = findAll()
    client.close()
    cryptos
  }

  def findAll()(implicit coll:MongoCollection[Document]):List[Crypto] = {

    val cryptos = coll.find()
    val o =
      cryptos.asScala.map{ result =>
          Crypto(
            name = result.get("name").asInstanceOf[String],
            symbol = result.get("symbol").asInstanceOf[String],
            price = result.get("price").asInstanceOf[Double],
            twentyFourHrChange = result.get("twentyFourHrChange").asInstanceOf[String],
            change = result.get("change").asInstanceOf[Double],
            marketCap = convertToDisplayNumber(result.get("marketCap").asInstanceOf[Long]),
          )
        }
      o.toList
  }

  def findAllIds()(implicit coll: MongoCollection[Document]): List[String] = {
    val ids = coll.distinct("_id",classOf[ObjectId])
    ids.asScala.map(_.toString).toList
  }

  def findAllSymbols()(implicit coll: MongoCollection[Document]): List[String] = {
    val ids = coll.distinct("symbol", classOf[String])
    ids.asScala.toList
  }

  def findAllNames()(implicit coll: MongoCollection[Document]): List[String] = {
    val ids = coll.distinct("name", classOf[String])
    ids.asScala.toList
  }

  def delete(symbol: String)(implicit coll:MongoCollection[Document]):Unit = {
    val query:Bson = Filters.eq("symbol", symbol)
    coll.deleteOne(query)
  }

  def insert(cryptos:Set[Crypto])(implicit coll:MongoCollection[Document]):Unit = {

    val docs =
      for (c <- cryptos) yield {
        val doc = new Document()
        doc.append("name", c.name)
        doc.append("symbol", c.symbol)
        doc.append("price", c.price)
        doc.append("twentyFourHrChange", c.twentyFourHrChange)
        doc.append("change", c.change)
        doc.append("marketCap",c.convertMarketCap)
        doc
      }
    if(docs.size > 0) {
      coll.insertMany(docs.toList.asJava)
    }
  }

  def main(args: Array[String]):Unit = {

  }
}


