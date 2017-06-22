package ru.spbau.kaysin.scala.parser.actors

import akka.actor.{Actor, ActorRef}
import akka.pattern.ask
import akka.util.Timeout
import ru.spbau.kaysin.scala.parser.VisitorParser
import ru.spbau.kaysin.scala.parser.codeEntities.{CompilationUnit, ImportDeclaration}
import ru.spbau.kaysin.scala.parser.protocol.CommonProtocol
import ru.spbau.kaysin.scala.parser.protocol.IndexingProtocol.{IndexRequest, IndexResponse, UpdateQualifiedName}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class FileActor(indexActor: ActorRef, sourceCode: String, fileName: String) extends Actor{

  private val importIndex = scala.collection.mutable.Map[String, Future[ActorRef]]() // qualified class name -> actor
  private val extendsIndex = scala.collection.mutable.Map[String, Future[ActorRef]]()

  private var root: CompilationUnit = _

  private implicit val timeout = Timeout(5 seconds)

  override def receive: Receive = {
    case CommonProtocol.Parse =>
      root = new VisitorParser().parse(sourceCode)
      indexActor ! UpdateQualifiedName(getQualifiedName)
      buildImportIndex()
      buildExtendsIndex()
    case CommonProtocol.PrintIndex =>
      println(s"importIndex: ${importIndex.mapValues(future => Await.result(future, 5 seconds))}")
      println(s"extendsIndex: ${extendsIndex.mapValues(future => Await.result(future, 5 seconds))}")
  }

  def buildImportIndex() : Unit = {
    for (ImportDeclaration(name) <- root.importDeclarations) {
      val futureResponse = (indexActor ? IndexRequest(name)).mapTo[IndexResponse]
        .map[ActorRef](response => response.actor)
      importIndex.put(name, futureResponse)
    }
  }

  def buildExtendsIndex() : Unit = {
     for {
        clazz <- root.classes
        name: String <- clazz.extendsBlock.map(block => block.name)
        qualifiedName <- root.importDeclarations
          .map(declaration => declaration.qualifiedName)
          .find(qualifiedName => qualifiedName.endsWith(name))
     } yield {
        val futureResponse = (indexActor ? IndexRequest(qualifiedName)).mapTo[IndexResponse]
          .map[ActorRef](response => response.actor)
        extendsIndex.put(name, futureResponse)
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
