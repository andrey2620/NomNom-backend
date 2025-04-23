package com.project.demo.rest.menu;


import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.menu.Menu;
import com.project.demo.logic.entity.menu.MenuDTO;
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

    @PostMapping
    public ResponseEntity<?> createMenu(@RequestBody MenuDTO dto, HttpServletRequest request) {
        try {
            Menu menu = menuService.createMenu(dto);
            return new GlobalResponseHandler().handleResponse("Menú creado con éxito", menu, HttpStatus.CREATED, request);
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error al crear el menú: " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getMenusByUser(@PathVariable Long userId, HttpServletRequest request) {
        try {
            List<Menu> menus = menuService.findByUserId(userId);
            return new GlobalResponseHandler().handleResponse("Menús del usuario cargados correctamente", menus, HttpStatus.OK, request);
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error al cargar menús del usuario", null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMenuItems(@PathVariable Long id, @RequestBody MenuDTO dto, HttpServletRequest request) {
        try {
            Menu updatedMenu = menuService.updateMenuItems(id, dto);
            return new GlobalResponseHandler().handleResponse("Menú actualizado correctamente", updatedMenu, HttpStatus.OK, request);
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error al actualizar el menú: " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @DeleteMapping("/{menuId}")
    public ResponseEntity<?> deleteMenu(@PathVariable Long menuId, HttpServletRequest request) {
        try {
            menuService.deleteMenuById(menuId);
            return new GlobalResponseHandler().handleResponse(
                    "Menú eliminado correctamente.",
                    null,
                    HttpStatus.OK,
                    request
            );
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error al eliminar el menú: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request
            );
        }
    }
}
