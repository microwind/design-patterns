package com.github.microwind.springwind.core;

@FunctionalInterface
public interface ObjectFactory<T> {
    T getObject();
}