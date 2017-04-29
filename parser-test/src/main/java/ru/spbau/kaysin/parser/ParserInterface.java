package ru.spbau.kaysin.parser;

import ru.spbau.kaysin.parser.domain.CompilationUnit;

public interface ParserInterface {
    CompilationUnit parse(String sourceCode);
}