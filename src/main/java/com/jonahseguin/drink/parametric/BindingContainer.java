package com.jonahseguin.drink.parametric;

import java.util.HashSet;
import java.util.Set;


public class BindingContainer<T> {

    private final Class<T> type;
    private final Set<DrinkBinding<T>> bindings;

    public BindingContainer(Class<T> type) {
        this.type = type;
        this.bindings = new HashSet<>();
    }

    public BindingContainer(Class<T> type, Set<DrinkBinding<T>> bindings) {
        this.type = type;
        this.bindings = bindings;
    }

    public Class<T> getType() {
        return type;
    }

    public Set<DrinkBinding<T>> getBindings() {
        return bindings;
    }
}
