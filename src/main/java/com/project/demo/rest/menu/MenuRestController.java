package com.project.demo.rest.menu;


import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.menu.Menu;
import com.project.demo.services.MenuService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menus")
@PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
public class MenuRestController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getMenusByUser(@PathVariable Long userId, HttpServletRequest request) {
        try {
            List<Menu> menus = menuService.findByUserId(userId);
            return new GlobalResponseHandler().handleResponse(
                    "Menus for user " + userId + " retrieved successfully", menus, HttpStatus.OK, request
            );
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error retrieving user's menus: " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR, request
            );
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> bulkCreate(@RequestBody List<MenuDTO> dtoList, HttpServletRequest request) {
        try {
            List<Menu> savedMenus = menuService.bulkCreate(dtoList);
            return new GlobalResponseHandler().handleResponse(
                    "Menus created successfully", savedMenus, HttpStatus.CREATED, request
            );
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error in bulk create: " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR, request
            );
        }
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<?> bulkDelete(@RequestBody List<Long> ids, HttpServletRequest request) {
        try {
            menuService.bulkDelete(ids);
            return new GlobalResponseHandler().handleResponse(
                    "Menus deleted successfully", null, HttpStatus.OK, request
            );
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error in bulk delete: " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR, request
            );
        }
    }

}