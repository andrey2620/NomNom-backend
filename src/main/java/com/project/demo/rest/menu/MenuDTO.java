package com.project.demo.rest.menu;

import com.project.demo.logic.entity.menu.DayOfWeek;
import com.project.demo.logic.entity.menu.MealType;

public class MenuDTO {
    private Long userId;
    private Long recipeId;
    private MealType mealType;
    private DayOfWeek dayOfWeek;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getRecipeId() { return recipeId; }
    public void setRecipeId(Long recipeId) { this.recipeId = recipeId; }

    public MealType getMealType() { return mealType; }
    public void setMealType(MealType mealType) { this.mealType = mealType; }

    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }
}
