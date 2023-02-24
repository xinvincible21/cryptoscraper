package com.invincible

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}

import java.io.{BufferedWriter, FileWriter}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.math.pow


object Utils {

  import java.text.DecimalFormat

  def convertToDisplayNumber(count: Long): String = {
    if (count < 1000) return "" + count
    val exp = (Math.log(count) / Math.log(1000)).toInt
    val format = new DecimalFormat("0.#")
    val value = format.format(count / Math.pow(1000, exp))
    "$%s%c".format(value, "KMBTPE".charAt(exp - 1))
  }

  def convertDisplayNumber(strNumber:String):Long = {
    if(strNumber == "") return 0L
    val factor =
      if (strNumber.contains("B")) {
        pow(10, 9)
      } else if (strNumber.contains("M")) {
        pow(10, 6)
      } else if (strNumber.contains("K")) {
        pow(10, 3)
      } else {
        1
      }
    val temp =
      strNumber
        .replace("$","")
        .replace("B","")
        .replace("M","")
        .replace("K","")
    (temp.toDouble * factor).toLong
  }

  def dateTimeSuffix(): String = {
    val sdf = DateTimeFormatter.ofPattern("yyyy_MM_dd_hh_mm_ss")
    val dt = LocalDateTime.now()
    dt.format(sdf)
  }

  def dateSuffix(): String = {
    val sdf = DateTimeFormatter.ofPattern("yyyy_MM_dd")
    val dt = LocalDateTime.now()
    dt.format(sdf)
  }

  def writeFile(filename: String, content: String) = {
    val writer: BufferedWriter = new BufferedWriter(new FileWriter(filename))
    writer.write(content)
    writer.close()
  }

  def createWriter(filename: String) = new BufferedWriter(new FileWriter(filename))

  def writerAppend(line: String)(implicit writer: BufferedWriter) = writer.write(line+"\n")

  def closeWriter()(implicit writer: BufferedWriter) = writer.close


  def initDriver() = {
    WebDriverManager.chromedriver.setup()
    val options = new ChromeOptions
    options.addArguments("--headless=chrome")
    new ChromeDriver(options)
  }

  def isAllUpper(s: String): Boolean = {
    for (c <- s.toCharArray) {
      if (Character.isLetter(c) && Character.isLowerCase(c)) return false
    }
    true
  }
}