package ru.spbau.kaysin.scala.parser

import java.io.IOException
import java.nio.file.{Files, Paths}

import akka.actor.{ActorRef, ActorSystem, Props}
import ru.spbau.kaysin.scala.parser.actors.{FileActor, IndexActor}
import ru.spbau.kaysin.scala.parser.protocol.CommonProtocol

object Main {
  private val TEST_PATHS = Array("testSources/Test1.java", "testSources/Test2.java")

  @throws[IOException]
  def main(args: Array[String]): Unit = {

    val paths = TEST_PATHS

    val actorsSystem = ActorSystem("hello")
    val indexActor = actorsSystem.actorOf(Props(new IndexActor(paths.length)), "indexActor")


    val fileActors: Seq[ActorRef] =
      for {
        filePath <- paths
        sourceCode = new String(Files.readAllBytes(Paths.get(filePath)))
        fileName = Paths.get(filePath).getFileName.toString
        fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'))
        fileActor: ActorRef = actorsSystem.actorOf(
          Props(new FileActor(indexActor, sourceCode, fileNameWithoutExtension)))

      } yield {
        fileActor ! CommonProtocol.Parse
        fileActor
      }

    for (actor <- fileActors) {
      actor ! CommonProtocol.PrintIndex
    }
  }
}