package com.invincible

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}

import java.io.{BufferedWriter, FileWriter}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


object Utils {

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