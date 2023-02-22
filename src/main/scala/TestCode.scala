package com.invincible

import com.invincible.MongoDB._
import com.invincible.Utils.dateSuffix
import com.mongodb.client.MongoCollection
import org.bson.Document


object TestCode {

  def main(args:Array[String]):Unit = {
    implicit val client = getClient()
    implicit val db = getDb("theorg")
    implicit val coll: MongoCollection[Document] = getCollection(s"crypto_${dateSuffix}")
    val cryptos = findAll()
    cryptos.map(println)
    println(s"Total cryptos ${cryptos.size}")
  }
}
