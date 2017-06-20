package ru.spbau.kaysin.scala.parser.protocol

import akka.actor.ActorRef

/**
  * The message protocol between IndexActor and FileActor.
  */
object IndexingProtocol {
  case class UpdateQualifiedName(qualifiedName: String)
  case class IndexRequest(qualifiedName: String)
  case class IndexResponse(actor: ActorRef)
}
