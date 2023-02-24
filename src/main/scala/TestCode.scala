package com.invincible

import com.invincible.MongoDB._
import com.invincible.Utils.dateTimeSuffix
import com.mongodb.client.MongoCollection
import org.bson.Document


object TestCode {

  def main(args:Array[String]):Unit = {
    implicit val client = getClient()
    implicit val db = getDb("cryptoscraper")
    implicit val coll: MongoCollection[Document] = getCollection(s"crypto_${dateTimeSuffix}")
    val cryptos = findAll()
    cryptos.map(println)
    println(s"Total cryptos ${cryptos.size}")
  }
}
