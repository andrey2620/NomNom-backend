package com.project.demo.logic.entity.recipe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RecipeCategory {
    Comida,
    Ensalada,
    Jugos,
    Postre,
    Panes;

    @JsonCreator
    public static RecipeCategory fromValue(String value) {
        return RecipeCategory.valueOf(value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}


