package ru.spbau.kaysin.scala.parser.actors

import akka.actor.{Actor, ActorRef}
import ru.spbau.kaysin.scala.parser.protocol.IndexingProtocol.{IndexRequest, IndexResponse, UpdateQualifiedName}

import scala.collection.mutable.ListBuffer

class IndexActor(numberOfFiles: Int) extends Actor{

  private val index = scala.collection.mutable.Map[String, ActorRef]() // qualified class name -> actor

  private case class Request(sender: ActorRef, name: String)
  private val buffer = new ListBuffer[Request]

  def collecting: Receive = {
    case UpdateQualifiedName(qualifiedName) =>
      println(qualifiedName)
      index.put(qualifiedName, sender)
      if (index.size == numberOfFiles) {
        for (Request(localSender : ActorRef, name) <- buffer) { // pay the debts
          localSender ! IndexResponse(index.getOrElse(name, null))
        }
        println(index)
        context.become(answering)
      }
    case IndexRequest(qualifiedName) =>
      buffer.append(Request(sender, qualifiedName))
  }

  def answering: Receive = {
    case IndexRequest(qualifiedName) =>
      sender ! IndexResponse(index.getOrElse(qualifiedName, null))
  }

  override def receive: Receive = collecting
}
