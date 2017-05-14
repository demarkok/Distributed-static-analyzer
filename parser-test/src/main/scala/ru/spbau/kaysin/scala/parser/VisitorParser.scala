package ru.spbau.kaysin.scala.parser

import java.util.Objects
import java.util.stream.Collectors.toList

import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import ru.spbau.kaysin.antlr4.{JavaBaseVisitor, JavaLexer, JavaParser}
import ru.spbau.kaysin.scala.parser.domain.{CompilationUnit, Method}
import scala.collection.JavaConversions._


object VisitorParser {

  private class CompilationUnitVisitor extends JavaBaseVisitor[CompilationUnit] {
    override def visitCompilationUnit(ctx: JavaParser.CompilationUnitContext): CompilationUnit = {
      val classVisitor = new ClassVisitor

      val classes = ctx.typeDeclaration.toList
        .map(context => context.classDeclaration.accept(classVisitor))

      new CompilationUnit(classes.toSet)
    }
  }

  private class ClassVisitor extends JavaBaseVisitor[domain.Class] {
    override def visitClassDeclaration(ctx: JavaParser.ClassDeclarationContext): domain.Class = {
      val className = ctx.Identifier.getText
      val methodVisitor = new VisitorParser.MethodVisitor

      val methods = ctx.classBody.classBodyDeclaration.toList
        .map(context => context.memberDeclaration.methodDeclaration)
        .filter(x => x != null)
        .map(context => context.accept(methodVisitor))

      new domain.Class(className, methods.toSet)
    }
  }

  private class MethodVisitor extends JavaBaseVisitor[Method] {
    override def visitMethodDeclaration(ctx: JavaParser.MethodDeclarationContext) = new Method(ctx.Identifier.getText)
  }

}

class VisitorParser extends ParserInterface {
  /**
    * Parse java code into simple AST.
    *
    * @param sourceCode - java source code
    * @return root node of the ast
    */
    override def parse(sourceCode: String): CompilationUnit = {
      val charStream = CharStreams.fromString(sourceCode)
      val lexer = new JavaLexer(charStream)
      val tokens = new CommonTokenStream(lexer)
      val parser = new JavaParser(tokens)
      val compilationUnitVisitor = new VisitorParser.CompilationUnitVisitor
      compilationUnitVisitor.visit(parser.compilationUnit)
    }
}