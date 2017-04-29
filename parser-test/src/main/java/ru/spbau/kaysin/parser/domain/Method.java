package ru.spbau.kaysin.parser.domain;


/**
 * Class representing java method.
 */
public class Method {
    private final String name;

    public Method(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Method{" +
            "\nname='" + name + '\'' +
            '}';
    }
}
