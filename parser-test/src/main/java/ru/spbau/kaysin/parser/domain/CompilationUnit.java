package ru.spbau.kaysin.parser.domain;

import java.util.Collection;

/**
 * Class representing java compilation unit.
 */
public class CompilationUnit {

    private final Collection<Class> classes;

    @Override
    public String toString() {
        return "CompilationUnit{" +
            "classes=" + classes +
            '}';
    }

    public CompilationUnit(Collection<Class> classes) {
        this.classes = classes;
    }
}
