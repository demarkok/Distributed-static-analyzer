package ru.spbau.kaysin.scala.parser

import java.io.IOException
import java.nio.file.{Files, Paths}

object Main {
  private val TEST_SOURCE = "testSources/Test1.java"

  @throws[IOException]
  def main(args: Array[String]): Unit = {
    val source = new String(Files.readAllBytes(Paths.get(TEST_SOURCE)))
    val result = new VisitorParser().parse(source)
    System.out.println(result)
  }
}