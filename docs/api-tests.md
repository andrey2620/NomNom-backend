# Documentación de Pruebas de API - Ingredientes

## Endpoints Probados

| Endpoint | Método | Parámetros | Respuesta Esperada | Código de Estado |
|----------|--------|------------|-------------------|-----------------|
| `/ingredients/{id}` | GET | `id=1` | `{"id":1,"name":"Tomate","category":"Verdura"}` | 200 OK |
| `/ingredients/formated/user/{userId}` | GET | `userId=1` | `[{"id":1,"name":"Tomate"}]` | 200 OK |
| `/ingredients/link/{ingredientId}/user/{userId}` | POST | `ingredientId=1, userId=1` | `"Ingrediente vinculado correctamente al usuario."` | 200 OK |

## Detalles de las Pruebas

### 1. Obtener Ingrediente por ID

**Endpoint:** `/ingredients/{id}`
**Método:** GET
**Descripción:** Recupera un ingrediente específico por su ID.

**Respuesta Exitosa:**
```json
{
  "id": 1,
  "name": "Tomate",
  "category": "Verdura"
}