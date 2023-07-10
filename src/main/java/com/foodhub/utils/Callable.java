package com.foodhub.utils;

/**
 * a standard callable with no exceptions in method signature
 * {@inheritDoc}
 */
public interface Callable<V> extends java.util.concurrent.Callable<V> {

    @Override
    V call();
}
