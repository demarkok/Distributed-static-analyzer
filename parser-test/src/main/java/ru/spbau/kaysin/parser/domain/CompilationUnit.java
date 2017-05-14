package ru.spbau.kaysin.parser.domain;

import java.util.Collection;

/**
 * Class representing java compilation unit.
 */
public class CompilationUnit {

    private final Collection<ru.spbau.kaysin.parser.domain.Class> classes;

    @Override
    public String toString() {
        return "CompilationUnit{" +
            "classes=" + classes +
            '}';
    }

    public CompilationUnit(Collection<ru.spbau.kaysin.parser.domain.Class> classes) {
        this.classes = classes;
    }
}
