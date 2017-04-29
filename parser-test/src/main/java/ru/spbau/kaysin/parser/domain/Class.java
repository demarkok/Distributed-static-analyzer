package ru.spbau.kaysin.parser.domain;


import java.util.Collection;


/**
 * Class representing java class.
 */
public class Class {
    private final String name;
    private final Collection<Method> methods;

    public Class(String name, Collection<Method> methods) {
        this.name = name;
        this.methods = methods;
    }

    @Override
    public String toString() {
        return "Class{" +
            "\nname='" + name + '\'' +
            "\nmethods=" + methods +
            '}';
    }
}
