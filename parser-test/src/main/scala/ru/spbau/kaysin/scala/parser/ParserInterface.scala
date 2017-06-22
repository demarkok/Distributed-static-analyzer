package ru.spbau.kaysin.scala.parser

import ru.spbau.kaysin.scala.parser.codeEntities.CompilationUnit

trait ParserInterface {
  def parse(sourceCode: String): CompilationUnit
}