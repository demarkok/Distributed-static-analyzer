package ru.spbau.kaysin.scala.parser.actors

import akka.actor.{Actor, ActorRef}
import akka.pattern.ask
import akka.util.Timeout
import ru.spbau.kaysin.scala.parser.VisitorParser
import ru.spbau.kaysin.scala.parser.codeEntities.{CompilationUnit, ImportDeclaration}
import ru.spbau.kaysin.scala.parser.protocol.CommonProtocol
import ru.spbau.kaysin.scala.parser.protocol.IndexingProtocol.{IndexRequest, IndexResponse, UpdateQualifiedName}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class FileActor(indexActor: ActorRef, sourceCode: String, fileName: String) extends Actor{

  private val index = scala.collection.mutable.Map[String, Future[ActorRef]]() // qualified class name -> actor
  private var root: CompilationUnit = _

  override def receive: Receive = {
    case CommonProtocol.Parse =>
      root = new VisitorParser().parse(sourceCode)
      indexActor ! UpdateQualifiedName(getQualifiedName)
      buildIndex()
    case CommonProtocol.PrintIndex =>
      println(index.mapValues(future => Await.result(future,5 seconds)))
  }


  def buildIndex() : Unit = {
    for (ImportDeclaration(name) <- root.importDeclarations) {
      implicit val timeout = Timeout(5 seconds)
      val futureResponse = (indexActor ? IndexRequest(name)).mapTo[IndexResponse]
        .map[ActorRef](response => response.actor)
      index.put(name, futureResponse)
    }
  }

  def getQualifiedName : String = {
    val packageName = root.packageDeclaration.qualifiedName
    if (packageName.isEmpty)
      fileName
    else
      packageName + '.' + fileName
  }

}
