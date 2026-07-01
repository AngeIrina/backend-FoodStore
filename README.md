# Food Store — Backend (Parte 2: JPA + Consola)

Backend del sistema de gestión de pedidos Food Store, desarrollado con Java, JPA/Hibernate y base de datos H2 en archivo. La interacción con el sistema es completamente a través de un menú de consola que permite gestionar Categorías, Productos, Usuarios y Pedidos (con sus DetallePedido).

Este proyecto es la Parte 2 del TPI de Programación III. La Parte 1 (frontend web) es un proyecto independiente.

---

## Tecnologías

- Java 21
- JPA / Hibernate 6
- H2 (base de datos en archivo — `./data/jpa_db`)
- Lombok
- Gradle 8

---

## Estructura del proyecto
src/main/java/com/tp/jpa/
│
├── model/                        # Entidades JPA
│   ├── Base.java                 # Clase abstracta base (id, eliminado, createdAt)
│   ├── Calculable.java           # Interfaz con calcularTotal()
│   ├── Categoria.java
│   ├── Producto.java
│   ├── Usuario.java
│   ├── Pedido.java
│   ├── DetallePedido.java
│   └── enums/
│       ├── Rol.java
│       ├── EstadoPedido.java
│       └── FormaPago.java
│
├── util/
│   └── JPAUtil.java              # Factory singleton de EntityManagerFactory
│
├── repository/
│   ├── BaseRepository.java       # CRUD genérico (guardar, buscarPorId, listarActivos, eliminarLogico)
│   ├── ProductoRepository.java   # Hereda el CRUD, sin queries propias
│   ├── CategoriaRepository.java  # + buscarProductosPorCategoria()
│   ├── UsuarioRepository.java    # + buscarPorMail(), buscarPedidosPorUsuario()
│   └── PedidoRepository.java     # + buscarPorEstado()
│
└── Main.java                     # Menú de consola

---

## Cómo ejecutar

```bash
./gradlew run --console=plain
```

(El flag `--console=plain` evita problemas de consola interactiva en algunos terminales, como Git Bash.)

La base de datos H2 se crea automáticamente en `./data/jpa_db.mv.db` al primer arranque. No requiere instalación de base de datos aparte.

---

## Menú principal

1. Gestionar Categorías (alta, modificar, baja lógica, listado)
2. Gestionar Productos (alta con selección de categoría, modificar, baja lógica, listado)
3. Gestionar Usuarios (alta con mail único, modificar, baja lógica, listado, buscar por mail)
4. Gestionar Pedidos (alta con transacción atómica, cambiar estado, baja lógica, listados por usuario/estado)
5. Reportes (productos por categoría, pedidos por usuario/estado, total facturado)
0. Salir

---

## Datos de prueba

No hay carga inicial automática (seed). Los datos se cargan desde el menú de consola, respetando este orden de dependencias:

1. Categorías
2. Productos (requieren al menos una categoría activa)
3. Usuarios
4. Pedidos (requieren al menos un usuario y un producto activos)

---

## Reglas de negocio destacadas

- Las bajas son siempre lógicas (`eliminado = true`); los registros no se eliminan físicamente y no aparecen en los listados activos.
- El alta de un pedido se ejecuta en una única transacción: valida stock y disponibilidad de cada producto, calcula subtotales y total, y reduce el stock. Si algo falla, se hace rollback completo y no se persiste nada.
- Dejar un campo vacío durante una modificación conserva el valor anterior.