package ru.spbau.kaysin.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import ru.spbau.kaysin.parser.domain.CompilationUnit;


public class Main {
    private static final String TEST_SOURCE = "testSources/Test1.java";
    public static void main(String[] args) throws IOException {
        String source = new String(Files.readAllBytes(Paths.get(TEST_SOURCE)));
        CompilationUnit result = new VisitorParser().parse(source);
        System.out.println(result);
    }
}
