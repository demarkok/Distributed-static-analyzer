package ru.spbau.kaysin.scala.parser.domain


/**
  * Class representing java compilation unit.
  */
class CompilationUnit(val classes: Set[Class]) {
  override def toString: String = "CompilationUnit{" + "classes=" + classes + '}'
}