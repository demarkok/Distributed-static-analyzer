package ru.spbau.kaysin.scala.parser.codeEntities


/**
  * Class representing java compilation unit.
  */
class CompilationUnit(val packageDeclaration: PackageDeclaration,
                      val importDeclarations: Set[ImportDeclaration],
                      val classes: Set[Class]) {

  override def toString = s"CompilationUnit($packageDeclaration, $importDeclarations, $classes)"
}