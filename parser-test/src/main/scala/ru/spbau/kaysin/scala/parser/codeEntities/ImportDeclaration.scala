package ru.spbau.kaysin.scala.parser.codeEntities


case class ImportDeclaration(qualifiedName: String) {

  override def toString = s"ImportDeclaration($qualifiedName)"
}
