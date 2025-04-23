package com.project.demo.logic.entity.menu;

import com.project.demo.logic.entity.menu_item.MenuItemDTO;

import java.util.List;

public class MenuDTO {
    private String name;
    private Long userId;
    private List<MenuItemDTO> items;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<MenuItemDTO> getItems() {
        return items;
    }

    public void setItems(List<MenuItemDTO> items) {
        this.items = items;
    }
}
