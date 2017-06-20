package ru.spbau.kaysin.scala.parser.codeEntities

sealed trait ClassModifier
case object PublicClassModifier extends ClassModifier
case object OtherClassModifier extends ClassModifier


