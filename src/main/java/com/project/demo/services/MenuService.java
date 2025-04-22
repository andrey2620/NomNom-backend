package com.project.demo.services;

import com.project.demo.logic.entity.menu.Menu;
import com.project.demo.logic.entity.menu.MenuRepository;
import com.project.demo.logic.entity.recipe.Recipe;
import com.project.demo.logic.entity.recipe.RecipeRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.rest.menu.MenuDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    public List<Menu> findAll() {
        return menuRepository.findAll();
    }

    public Menu save(Menu menu) {
        return menuRepository.save(menu);
    }

    public List<Menu> findByUserId(Long userId) {
        return menuRepository.findByUserId(userId);
    }

    public List<Menu> bulkCreate(List<MenuDTO> dtoList) {
        List<Menu> menus = dtoList.stream().map(dto -> {
            User user = userRepository.findById(dto.getUserId()).orElseThrow();
            Recipe recipe = recipeRepository.findById(dto.getRecipeId()).orElseThrow();

            Menu menu = new Menu();
            menu.setUser(user);
            menu.setRecipe(recipe);
            menu.setMealType(dto.getMealType());
            menu.setDayOfWeek(dto.getDayOfWeek());
            return menu;
        }).toList();

        return menuRepository.saveAll(menus);
    }

    public void bulkDelete(List<Long> ids) {
        menuRepository.deleteAllById(ids);
    }
}