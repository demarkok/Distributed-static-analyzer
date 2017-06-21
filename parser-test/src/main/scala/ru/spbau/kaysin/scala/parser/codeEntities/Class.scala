package ru.spbau.kaysin.scala.parser.codeEntities

/**
  * Class representing java class.
  */
class Class(val modifiers: Set[ClassModifier],
            val name: String,
            val extendsBlock: Option[ExtendsBlock],
            val methods: Set[Method]) {

  override def toString = s"Class($modifiers, $name, $extendsBlock, $methods)"
}