package com.project.demo.services;

import com.project.demo.logic.entity.menu.*;
import com.project.demo.logic.entity.menu_item.MenuItem;
import com.project.demo.logic.entity.menu_item.MenuItemDTO;
import com.project.demo.logic.entity.menu_item.MenuItemRepository;
import com.project.demo.logic.entity.recipe.Recipe;
import com.project.demo.logic.entity.recipe.RecipeRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    public Menu createMenu(MenuDTO dto) {
        User user = userRepository.findById(dto.getUserId()).orElseThrow();

        Menu menu = new Menu(dto.getName(), user);
        menu = menuRepository.save(menu);

        for (MenuItemDTO itemDto : dto.getItems()) {
            Recipe recipe = recipeRepository.findById(itemDto.getRecipeId()).orElseThrow();
            MenuItem item = new MenuItem(
                    menu,
                    recipe,
                    MealType.valueOf(itemDto.getMealType()),
                    DayOfWeek.valueOf(itemDto.getDayOfWeek())
            );
            menuItemRepository.save(item);
        }
        return menu;
    }

    public void deleteMenuById(Long menuId) {
        if (!menuRepository.existsById(menuId)) {
            throw new RuntimeException("El menú con ID " + menuId + " no existe");
        }
        menuRepository.deleteById(menuId);
    }

    public Menu updateMenuItems(Long menuId, MenuDTO dto) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new RuntimeException("Menú no encontrado"));

        menu.setName(dto.getName());
        menu = menuRepository.save(menu);

        List<MenuItem> oldItems = menuItemRepository.findByMenuId(menuId);
        menuItemRepository.deleteAll(oldItems);

        for (MenuItemDTO itemDto : dto.getItems()) {
            Recipe recipe = recipeRepository.findById(itemDto.getRecipeId()).orElseThrow(() -> new RuntimeException("Receta no encontrada"));
            MenuItem newItem = new MenuItem(
                    menu,
                    recipe,
                    MealType.valueOf(itemDto.getMealType()),
                    DayOfWeek.valueOf(itemDto.getDayOfWeek())
            );
            menuItemRepository.save(newItem);
        }
        return menu;
    }

    public List<Menu> findByUserId(Long userId) {
        return menuRepository.findByUserId(userId);
    }
}