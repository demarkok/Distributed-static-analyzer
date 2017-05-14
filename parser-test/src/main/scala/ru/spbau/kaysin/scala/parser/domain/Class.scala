package ru.spbau.kaysin.scala.parser.domain

/**
  * Class representing java class.
  */
class Class(val name: String, val methods: Set[Method]) {
  override def toString: String = "Class{" + "\nname='" + name + '\'' + "\nmethods=" + methods + '}'
}