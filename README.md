# Food Store JPA — Plantilla TPI (Parte 2)

Este README corresponde a la plantilla base para el desarrollo del TPI — Parte 2 (Backend JPA + Consola).

---

## Tecnologías

- Java 21
- JPA / Hibernate 6
- H2 (base de datos en archivo — `./data/jpa_db`)
- Lombok
- Gradle 8

---

## Estructura del proyecto

```
src/main/java/com/tp/jpa/
│
├── model/                        # Entidades JPA (NO modificar)
│   ├── Base.java                 # Clase abstracta base (id, eliminado, createdAt)
│   ├── Calculable.java           # Interfaz con calcularTotal()
│   ├── Categoria.java
│   ├── Producto.java
│   ├── Usuario.java
│   ├── Pedido.java
│   ├── DetallePedido.java
│   └── enums/
│       ├── Rol.java
│       ├── Estado.java
│       └── FormaPago.java
│
├── util/
│   └── JPAUtil.java              # Factory singleton (NO modificar — ya implementado)
│
├── repository/                   # ★ COMPLETAR — queries personalizadas
│   ├── BaseRepository.java       # CRUD genérico (NO modificar — ya implementado)
│   ├── ProductoRepository.java   # Sin queries extra (NO modificar)
│   ├── CategoriaRepository.java  # ★ Implementar buscarProductosPorCategoria()
│   ├── UsuarioRepository.java    # ★ Implementar buscarPorMail() y buscarPedidosPorUsuario()
│   └── PedidoRepository.java     # ★ Implementar buscarPorEstado()
│
└── Main.java                     # ★ COMPLETAR — menús de consola
```

---

## Qué está implementado

| Componente | Estado |
|---|---|
| `JPAUtil` | ✅ Completo |
| `BaseRepository` (guardar, buscarPorId, listarActivos, eliminarLogico) | ✅ Completo |
| `ProductoRepository` | ✅ Completo (hereda todo de Base) |
| Modelo completo (todas las entidades y enums) | ✅ Completo |
| `Main` — estructura del menú principal | ✅ Esqueleto listo |

---

## Qué hay que implementar

### Repositorios

| Clase | Método | Descripción |
|---|---|---|
| `CategoriaRepository` | `buscarProductosPorCategoria(Long categoriaId)` | JPQL navegando `c.productos` (la relación es unidireccional y Categoria es la dueña), filtrando por `eliminado = false` |
| `UsuarioRepository` | `buscarPorMail(String mail)` | JPQL filtrando por mail y `eliminado = false`, retorna `Optional<Usuario>` |
| `UsuarioRepository` | `buscarPedidosPorUsuario(Long idUsuario)` | JPQL navegando `u.pedidos` (la relación es unidireccional y Usuario es el dueño), filtrando por `eliminado = false` |
| `PedidoRepository` | `buscarPorEstado(EstadoPedido estado)` | JPQL filtrando por estado y `eliminado = false` |

### Menú de consola (`Main.java`)

| Método | Descripción |
|---|---|
| `menuCategorias()` | Alta, modificar, baja lógica, listado |
| `menuProductos()` | Alta (con selección de categoría), modificar, baja lógica, listado |
| `menuUsuarios()` | Alta (mail único), modificar, baja lógica, listado, buscar por mail |
| `menuPedidos()` | Alta (transacción atómica), cambiar estado, baja lógica, listados |
| `menuReportes()` | Productos por categoría, pedidos por usuario/estado, total facturado |

---

## Cómo ejecutar

```bash
./gradlew run
```

O compilar y ejecutar el JAR:

```bash
./gradlew jar
java -jar build/libs/foodstore-jpa-0.0.1-SNAPSHOT.jar
```

La base de datos H2 se crea automáticamente en `./data/jpa_db.mv.db` al primer arranque.

---

## Credenciales / datos de prueba

No hay carga inicial automática. Crear los datos desde el menú de consola en este orden:

1. Categorías
2. Productos (requieren categoría existente)
3. Usuarios
4. Pedidos (requieren usuario y productos existentes)

---

## Entrega

- **Video demostrativo:** [link aquí]
- **Informe PDF:** [link aquí]
