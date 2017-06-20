package ru.spbau.kaysin.scala.parser

import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import ru.spbau.kaysin.antlr4.{JavaBaseVisitor, JavaLexer, JavaParser}
import ru.spbau.kaysin.scala.parser.codeEntities._

import scala.collection.JavaConverters._

object VisitorParser {

  private class CompilationUnitVisitor extends JavaBaseVisitor[CompilationUnit] {
    override def visitCompilationUnit(ctx: JavaParser.CompilationUnitContext): CompilationUnit = {

      val packageDeclarationVisitor = new PackageDeclarationVisitor

      val importDeclarationVisitor = new ImportDeclarationVisitor

      val packageDeclaration =
        if (ctx.packageDeclaration() == null)
          new PackageDeclaration("")
        else
          ctx.packageDeclaration().accept(packageDeclarationVisitor)

      val classModifierVisitor = new ClassModifierVisitor()

      val importDeclarations = ctx.importDeclaration().asScala
        .map(context => context.accept(importDeclarationVisitor))

      val classes = ctx.typeDeclaration.asScala
        .map(context => context.classDeclaration.accept(
          new ClassVisitor(context.classOrInterfaceModifier().asScala
            .map(context => context.accept(classModifierVisitor)).toSet))) // a little bit confusing, I suppose

      new CompilationUnit(packageDeclaration, importDeclarations.toSet, classes.toSet)
    }
  }

  private class PackageDeclarationVisitor extends JavaBaseVisitor[codeEntities.PackageDeclaration] {
    override def visitPackageDeclaration(ctx: JavaParser.PackageDeclarationContext): PackageDeclaration =
      new PackageDeclaration(ctx.qualifiedName.getText)
  }

  private class ImportDeclarationVisitor extends JavaBaseVisitor[codeEntities.ImportDeclaration] {
    override def visitImportDeclaration(ctx: JavaParser.ImportDeclarationContext): ImportDeclaration =
    ImportDeclaration(ctx.qualifiedName.getText)
  }

  private class ClassVisitor(modifiers: Set[ClassModifier]) extends JavaBaseVisitor[codeEntities.Class] {
    override def visitClassDeclaration(ctx: JavaParser.ClassDeclarationContext): codeEntities.Class = {

      val methodVisitor = new VisitorParser.MethodVisitor

      val className = ctx.Identifier.getText


      val methods = ctx.classBody.classBodyDeclaration.asScala
        .map(context => context.memberDeclaration.methodDeclaration)
        .filter(x => x != null)
        .map(context => context.accept(methodVisitor))

      new codeEntities.Class(modifiers, className, methods.toSet)
    }
  }

  private class ClassModifierVisitor extends JavaBaseVisitor[codeEntities.ClassModifier] {

    override def visitClassOrInterfaceModifier(ctx: JavaParser.ClassOrInterfaceModifierContext): ClassModifier = {

//      new ClassModifier(ctx.getText)

      ctx.getText match {
        case "public" => codeEntities.PublicClassModifier
        case _ => codeEntities.OtherClassModifier
      }
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