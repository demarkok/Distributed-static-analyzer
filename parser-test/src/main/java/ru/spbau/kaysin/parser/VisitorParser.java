package ru.spbau.kaysin.parser;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Objects;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.jetbrains.annotations.NotNull;
import ru.spbau.kaysin.antlr4.JavaBaseVisitor;
import ru.spbau.kaysin.antlr4.JavaLexer;
import ru.spbau.kaysin.antlr4.JavaParser;
import ru.spbau.kaysin.antlr4.JavaParser.ClassDeclarationContext;
import ru.spbau.kaysin.antlr4.JavaParser.CompilationUnitContext;
import ru.spbau.kaysin.antlr4.JavaParser.MethodDeclarationContext;
import ru.spbau.kaysin.parser.domain.Class;
import ru.spbau.kaysin.parser.domain.CompilationUnit;
import ru.spbau.kaysin.parser.domain.Method;


public class VisitorParser implements ParserInterface {

    /**
     * Parse java code into simple AST.
     * @param sourceCode - java source code
     * @return root node of the ast
     */
    @Override
    public CompilationUnit parse(String sourceCode) {
        CharStream charStream = CharStreams.fromString(sourceCode);
        JavaLexer lexer = new JavaLexer(charStream);
        TokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);

        CompilationUnitVisitor compilationUnitVisitor = new CompilationUnitVisitor();
        return  compilationUnitVisitor.visit(parser.compilationUnit());
    }


    private static class CompilationUnitVisitor extends JavaBaseVisitor<CompilationUnit> {
        @Override
        public CompilationUnit visitCompilationUnit(@NotNull CompilationUnitContext ctx) {
            ClassVisitor classVisitor = new ClassVisitor();
            List<Class> classes = ctx.typeDeclaration().stream()
                .map(context -> context.classDeclaration().accept(classVisitor))
                .collect(toList());
            return new CompilationUnit(classes);
        }
    }

    private static class ClassVisitor extends JavaBaseVisitor<Class> {
        @Override
        public Class visitClassDeclaration(@NotNull ClassDeclarationContext ctx) {
            String className = ctx.Identifier().getText();
            MethodVisitor methodVisitor = new MethodVisitor();

            List<Method> methods = ctx.classBody().classBodyDeclaration().stream()
                .map(context -> context.memberDeclaration().methodDeclaration())
                .filter(Objects::nonNull)
                .map(context -> context.accept(methodVisitor))
                .collect(toList());

            return new Class(className, methods);
        }
    }

    private static class MethodVisitor extends JavaBaseVisitor<Method> {
        @Override
        public Method visitMethodDeclaration(@NotNull MethodDeclarationContext ctx) {
            return new Method(ctx.Identifier().getText());
        }
    }
}