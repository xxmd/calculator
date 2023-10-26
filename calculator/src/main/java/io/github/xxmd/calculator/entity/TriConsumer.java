package io.github.xxmd.calculator.entity;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface TriConsumer<T, U, V> {
    void accept(T var1, U var2, V var3);

//    default BiConsumer<T, U, V> andThen(BiConsumer<? super T, ? super U> after) {
//        throw new RuntimeException("Stub!");
//    }
}
