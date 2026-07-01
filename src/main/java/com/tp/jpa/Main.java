package com.tp.jpa;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import com.tp.jpa.model.enums.EstadoPedido;
import com.tp.jpa.model.*;
import com.tp.jpa.model.enums.FormaPago;
import com.tp.jpa.model.enums.Rol;
import com.tp.jpa.repository.CategoriaRepository;
import com.tp.jpa.repository.PedidoRepository;
import com.tp.jpa.repository.ProductoRepository;
import com.tp.jpa.repository.UsuarioRepository;
import com.tp.jpa.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);

    private static final CategoriaRepository categoriaRepo = new CategoriaRepository();
    private static final ProductoRepository productoRepo = new ProductoRepository();
    private static final UsuarioRepository usuarioRepo = new UsuarioRepository();
    private static final PedidoRepository pedidoRepo = new PedidoRepository();

    public static void main(String[] args) {
        boolean salir = false;
        while (!salir) {
            System.out.println();
            System.out.println("===== FOOD STORE - MENÚ PRINCIPAL =====");
            System.out.println("1. Gestionar Categorías");
            System.out.println("2. Gestionar Productos");
            System.out.println("3. Gestionar Usuarios");
            System.out.println("4. Gestionar Pedidos");
            System.out.println("5. Reportes");
            System.out.println("0. Salir");
            System.out.print("Opción: ");
            String op = sc.nextLine().trim();
            switch (op) {
                case "1":
                    menuCategorias();
                    break;
                case "2":
                    menuProductos();
                    break;
                case "3":
                    menuUsuarios();
                    break;
                case "4":
                    menuPedidos();
                    break;
                case "5":
                    menuReportes();
                    break;
                case "0":
                    salir = true;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
        JPAUtil.close();
        System.out.println("Aplicación finalizada.");
    }

    // Helpers
    private static Long leerLong() {
        String linea = sc.nextLine().trim();
        try {
            return Long.parseLong(linea);
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
            return null;
        }
    }

    private static Double leerDouble() {
        String linea = sc.nextLine().trim();
        try {
            return Double.parseDouble(linea);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Integer leerInt() {
        String linea = sc.nextLine().trim();
        try {
            return Integer.parseInt(linea);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Submenú Categorías

    private static void menuCategorias() {
        boolean volver = false;
        while (!volver) {
            System.out.println();
            System.out.println("--- Gestionar Categorías ---");
            System.out.println("1. Alta");
            System.out.println("2. Modificar");
            System.out.println("3. Baja lógica");
            System.out.println("4. Listado");
            System.out.println("0. Volver");
            System.out.print("Opción: ");
            String op = sc.nextLine().trim();
            switch (op) {
                case "1":
                    altaCategoria();
                    break;
                case "2":
                    modificarCategoria();
                    break;
                case "3":
                    bajaCategoria();
                    break;
                case "4":
                    listarCategorias();
                    break;
                case "0":
                    volver = true;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private static void altaCategoria() {
        System.out.print("Nombre: ");
        String nombre = sc.nextLine().trim();
        if (nombre.isEmpty()) {
            System.out.println("El nombre es obligatorio. Operación cancelada.");
            return;
        }
        System.out.print("Descripción (opcional): ");
        String descripcion = sc.nextLine().trim();

        Categoria categoria = Categoria.builder()
                .nombre(nombre)
                .descripcion(descripcion.isEmpty() ? null : descripcion)
                .build();
        try {
            Categoria guardada = categoriaRepo.guardar(categoria);
            System.out.println("Categoría creada con ID: " + guardada.getId());
        } catch (RuntimeException e) {
            System.out.println("No se pudo guardar la categoría (¿nombre repetido?): " + e.getMessage());
        }
    }

    private static void modificarCategoria() {
        List<Categoria> activas = categoriaRepo.listarActivos();
        if (activas.isEmpty()) {
            System.out.println("No hay categorías activas.");
            return;
        }
        listarCategorias();
        System.out.print("ID de la categoría a modificar: ");
        Long id = leerLong();
        if (id == null)
            return;

        Categoria categoria = activas.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (categoria == null) {
            System.out.println("No existe una categoría activa con ese ID.");
            return;
        }

        System.out.println("Valores actuales -> Nombre: " + categoria.getNombre()
                + " | Descripción: " + categoria.getDescripcion());
        System.out.print("Nuevo nombre (Enter para conservar): ");
        String nombre = sc.nextLine().trim();
        if (!nombre.isEmpty()) {
            categoria.setNombre(nombre);
        }
        System.out.print("Nueva descripción (Enter para conservar): ");
        String descripcion = sc.nextLine().trim();
        if (!descripcion.isEmpty()) {
            categoria.setDescripcion(descripcion);
        }

        try {
            categoriaRepo.guardar(categoria);
            System.out.println("Categoría actualizada.");
        } catch (RuntimeException e) {
            System.out.println("No se pudo actualizar la categoría: " + e.getMessage());
        }
    }

    private static void bajaCategoria() {
        listarCategorias();
        System.out.print("ID de la categoría a dar de baja: ");
        Long id = leerLong();
        if (id == null)
            return;

        Optional<Categoria> catOpt = categoriaRepo.buscarPorId(id);
        boolean ok = categoriaRepo.eliminarLogico(id);
        if (!ok) {
            System.out.println("No se encontró la categoría o ya estaba dada de baja.");
            return;
        }
        String nombre = catOpt.map(Categoria::getNombre).orElse("(sin nombre)");
        System.out.println("Categoría \"" + nombre + "\" dada de baja correctamente.");
    }

    private static void listarCategorias() {
        List<Categoria> activas = categoriaRepo.listarActivos();
        if (activas.isEmpty()) {
            System.out.println("No hay categorías activas.");
            return;
        }
        System.out.println("ID | Nombre | Descripción");
        for (Categoria c : activas) {
            System.out.println(c.getId() + " | " + c.getNombre() + " | " + c.getDescripcion());
        }
    }

    // Submenú Productos

    private static void menuProductos() {
        boolean volver = false;
        while (!volver) {
            System.out.println();
            System.out.println("--- Gestionar Productos ---");
            System.out.println("1. Alta");
            System.out.println("2. Modificar");
            System.out.println("3. Baja lógica");
            System.out.println("4. Listado");
            System.out.println("0. Volver");
            System.out.print("Opción: ");
            String op = sc.nextLine().trim();
            switch (op) {
                case "1":
                    altaProducto();
                    break;
                case "2":
                    modificarProducto();
                    break;
                case "3":
                    bajaProducto();
                    break;
                case "4":
                    listarProductos();
                    break;
                case "0":
                    volver = true;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private static void altaProducto() {
        List<Categoria> categorias = categoriaRepo.listarActivos();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorías activas. Creá una categoría antes de cargar productos.");
            return;
        }
        System.out.println("Categorías disponibles:");
        for (Categoria c : categorias) {
            System.out.println(c.getId() + " - " + c.getNombre());
        }
        System.out.print("ID de la categoría: ");
        Long catId = leerLong();
        if (catId == null)
            return;

        System.out.print("Nombre: ");
        String nombre = sc.nextLine().trim();
        if (nombre.isEmpty()) {
            System.out.println("El nombre es obligatorio. Operación cancelada.");
            return;
        }
        System.out.print("Descripción (opcional): ");
        String descripcion = sc.nextLine().trim();

        System.out.print("Precio: ");
        Double precio = leerDouble();
        if (precio == null || precio <= 0) {
            System.out.println("Precio inválido. Debe ser mayor a 0. Operación cancelada.");
            return;
        }

        System.out.print("Stock: ");
        Integer stock = leerInt();
        if (stock == null || stock < 0) {
            System.out.println("Stock inválido. Debe ser mayor o igual a 0. Operación cancelada.");
            return;
        }

        System.out.print("Imagen (URL, opcional): ");
        String imagen = sc.nextLine().trim();

        System.out.print("¿Disponible? (S/N, Enter = S): ");
        String dispStr = sc.nextLine().trim();
        boolean disponible = !dispStr.equalsIgnoreCase("N");

        // único EntityManager para toda la operación
        EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            Categoria categoria = em.find(Categoria.class, catId);
            if (categoria == null || categoria.isEliminado()) {
                System.out.println("Categoría inválida.");
                return;
            }

            Producto nuevo = Producto.builder()
                    .nombre(nombre)
                    .descripcion(descripcion.isEmpty() ? null : descripcion)
                    .precio(precio)
                    .stock(stock)
                    .imagen(imagen.isEmpty() ? null : imagen)
                    .disponible(disponible)
                    .build();

            tx.begin();
            categoria.addProducto(nuevo);
            em.persist(nuevo);
            tx.commit();

            System.out.println("Producto creado con ID: " + nuevo.getId()
                    + " en la categoría \"" + categoria.getNombre() + "\".");
        } catch (RuntimeException e) {
            if (tx.isActive())
                tx.rollback();
            System.out.println("No se pudo guardar el producto: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    private static void modificarProducto() {
        List<Producto> activos = productoRepo.listarActivos();
        if (activos.isEmpty()) {
            System.out.println("No hay productos activos.");
            return;
        }
        listarProductos();
        System.out.print("ID del producto a modificar: ");
        Long id = leerLong();
        if (id == null)
            return;

        Producto producto = activos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (producto == null) {
            System.out.println("No existe un producto activo con ese ID.");
            return;
        }

        System.out.println("Valores actuales -> Nombre: " + producto.getNombre()
                + " | Precio: " + producto.getPrecio() + " | Stock: " + producto.getStock());

        System.out.print("Nuevo nombre (Enter para conservar): ");
        String nombre = sc.nextLine().trim();
        if (!nombre.isEmpty()) {
            producto.setNombre(nombre);
        }

        System.out.print("Nuevo precio (Enter para conservar): ");
        String precioStr = sc.nextLine().trim();
        if (!precioStr.isEmpty()) {
            try {
                double precio = Double.parseDouble(precioStr);
                if (precio > 0) {
                    producto.setPrecio(precio);
                } else {
                    System.out.println("Precio inválido, se conserva el anterior.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Precio inválido, se conserva el anterior.");
            }
        }

        System.out.print("Nuevo stock (Enter para conservar): ");
        String stockStr = sc.nextLine().trim();
        if (!stockStr.isEmpty()) {
            try {
                int stock = Integer.parseInt(stockStr);
                if (stock >= 0) {
                    producto.setStock(stock);
                } else {
                    System.out.println("Stock inválido, se conserva el anterior.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Stock inválido, se conserva el anterior.");
            }
        }

        productoRepo.guardar(producto);
        System.out.println("Producto actualizado.");
    }

    private static void bajaProducto() {
        listarProductos();
        System.out.print("ID del producto a dar de baja: ");
        Long id = leerLong();
        if (id == null)
            return;

        Optional<Producto> prodOpt = productoRepo.buscarPorId(id);
        boolean ok = productoRepo.eliminarLogico(id);
        if (!ok) {
            System.out.println("No se encontró el producto o ya estaba dado de baja.");
            return;
        }
        String nombre = prodOpt.map(Producto::getNombre).orElse("(sin nombre)");
        System.out.println("Producto \"" + nombre + "\" dado de baja correctamente.");
    }

    private static void listarProductos() {
        List<Producto> activos = productoRepo.listarActivos();
        if (activos.isEmpty()) {
            System.out.println("No hay productos activos.");
            return;
        }
        System.out.println("ID | Nombre | Precio | Stock | Disponible");
        for (Producto p : activos) {
            System.out.println(p.getId() + " | " + p.getNombre() + " | $" + p.getPrecio()
                    + " | " + p.getStock() + " | " + (Boolean.TRUE.equals(p.getDisponible()) ? "Sí" : "No"));
        }
    }

    // Submenú Usuarios

    private static void menuUsuarios() {
        boolean volver = false;
        while (!volver) {
            System.out.println();
            System.out.println("--- Gestionar Usuarios ---");
            System.out.println("1. Alta");
            System.out.println("2. Modificar");
            System.out.println("3. Baja lógica");
            System.out.println("4. Listado");
            System.out.println("5. Buscar por mail");
            System.out.println("0. Volver");
            System.out.print("Opción: ");
            String op = sc.nextLine().trim();
            switch (op) {
                case "1":
                    altaUsuario();
                    break;
                case "2":
                    modificarUsuario();
                    break;
                case "3":
                    bajaUsuario();
                    break;
                case "4":
                    listarUsuarios();
                    break;
                case "5":
                    buscarUsuarioPorMail();
                    break;
                case "0":
                    volver = true;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private static void altaUsuario() {
        System.out.print("Nombre: ");
        String nombre = sc.nextLine().trim();
        System.out.print("Apellido: ");
        String apellido = sc.nextLine().trim();
        System.out.print("Mail: ");
        String mail = sc.nextLine().trim();

        if (usuarioRepo.buscarPorMail(mail).isPresent()) {
            System.out.println("Ya existe un usuario activo con ese mail. Operación cancelada.");
            return;
        }

        System.out.print("Celular (opcional): ");
        String celular = sc.nextLine().trim();
        System.out.print("Contraseña: ");
        String contrasena = sc.nextLine().trim();

        System.out.println("Rol: 1-ADMIN  2-USUARIO");
        System.out.print("Opción: ");
        String rolOp = sc.nextLine().trim();
        Rol rol = rolOp.equals("1") ? Rol.ADMIN : Rol.USUARIO;

        Usuario usuario = Usuario.builder()
                .nombre(nombre)
                .apellido(apellido)
                .mail(mail)
                .celular(celular.isEmpty() ? null : celular)
                .contraseña(contrasena)
                .rol(rol)
                .build();
        try {
            Usuario guardado = usuarioRepo.guardar(usuario);
            System.out.println("Usuario creado con ID: " + guardado.getId());
        } catch (RuntimeException e) {
            System.out.println("No se pudo guardar el usuario: " + e.getMessage());
        }
    }

    private static void modificarUsuario() {
        List<Usuario> activos = usuarioRepo.listarActivos();
        if (activos.isEmpty()) {
            System.out.println("No hay usuarios activos.");
            return;
        }
        listarUsuarios();
        System.out.print("ID del usuario a modificar: ");
        Long id = leerLong();
        if (id == null)
            return;

        Usuario usuario = activos.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (usuario == null) {
            System.out.println("No existe un usuario activo con ese ID.");
            return;
        }

        System.out.println("Valores actuales -> Nombre: " + usuario.getNombre()
                + " | Apellido: " + usuario.getApellido() + " | Mail: " + usuario.getMail()
                + " | Celular: " + usuario.getCelular());

        System.out.print("Nuevo nombre (Enter para conservar): ");
        String nombre = sc.nextLine().trim();
        if (!nombre.isEmpty())
            usuario.setNombre(nombre);

        System.out.print("Nuevo apellido (Enter para conservar): ");
        String apellido = sc.nextLine().trim();
        if (!apellido.isEmpty())
            usuario.setApellido(apellido);

        System.out.print("Nuevo celular (Enter para conservar): ");
        String celular = sc.nextLine().trim();
        if (!celular.isEmpty())
            usuario.setCelular(celular);

        System.out.print("Nueva contraseña (Enter para conservar): ");
        String contrasena = sc.nextLine().trim();
        if (!contrasena.isEmpty())
            usuario.setContraseña(contrasena);

        System.out.print("Nuevo mail (Enter para conservar): ");
        String mail = sc.nextLine().trim();
        if (!mail.isEmpty() && !mail.equals(usuario.getMail())) {
            Optional<Usuario> existente = usuarioRepo.buscarPorMail(mail);
            if (existente.isPresent() && !existente.get().getId().equals(usuario.getId())) {
                System.out.println("Ese mail ya está en uso por otro usuario. Se conserva el mail anterior.");
            } else {
                usuario.setMail(mail);
            }
        }

        usuarioRepo.guardar(usuario);
        System.out.println("Usuario actualizado.");
    }

    private static void bajaUsuario() {
        listarUsuarios();
        System.out.print("ID del usuario a dar de baja: ");
        Long id = leerLong();
        if (id == null)
            return;

        Optional<Usuario> userOpt = usuarioRepo.buscarPorId(id);
        boolean ok = usuarioRepo.eliminarLogico(id);
        if (!ok) {
            System.out.println("No se encontró el usuario o ya estaba dado de baja.");
            return;
        }
        String nombreCompleto = userOpt.map(u -> u.getNombre() + " " + u.getApellido()).orElse("(sin nombre)");
        System.out.println("Usuario \"" + nombreCompleto + "\" dado de baja correctamente.");
    }

    private static void listarUsuarios() {
        List<Usuario> activos = usuarioRepo.listarActivos();
        if (activos.isEmpty()) {
            System.out.println("No hay usuarios activos.");
            return;
        }
        System.out.println("ID | Nombre completo | Mail | Rol");
        for (Usuario u : activos) {
            System.out.println(u.getId() + " | " + u.getNombre() + " " + u.getApellido()
                    + " | " + u.getMail() + " | " + u.getRol());
        }
    }

    private static void buscarUsuarioPorMail() {
        System.out.print("Mail a buscar: ");
        String mail = sc.nextLine().trim();
        Optional<Usuario> usuarioOpt = usuarioRepo.buscarPorMail(mail);
        if (usuarioOpt.isEmpty()) {
            System.out.println("No existe un usuario activo con ese mail.");
            return;
        }
        Usuario u = usuarioOpt.get();
        System.out.println("ID: " + u.getId());
        System.out.println("Nombre: " + u.getNombre() + " " + u.getApellido());
        System.out.println("Mail: " + u.getMail());
        System.out.println("Celular: " + u.getCelular());
        System.out.println("Rol: " + u.getRol());
    }

    // Submenú Pedidos

    private static void menuPedidos() {
        boolean volver = false;
        while (!volver) {
            System.out.println();
            System.out.println("--- Gestionar Pedidos ---");
            System.out.println("1. Alta de pedido");
            System.out.println("2. Cambiar estado");
            System.out.println("3. Baja lógica");
            System.out.println("4. Listado");
            System.out.println("5. Pedidos por usuario");
            System.out.println("6. Pedidos por estado");
            System.out.println("0. Volver");
            System.out.print("Opción: ");
            String op = sc.nextLine().trim();
            switch (op) {
                case "1":
                    altaPedido();
                    break;
                case "2":
                    cambiarEstadoPedido();
                    break;
                case "3":
                    bajaPedido();
                    break;
                case "4":
                    listarPedidos();
                    break;
                case "5":
                    pedidosPorUsuarioMenu();
                    break;
                case "6":
                    pedidosPorEstadoMenu();
                    break;
                case "0":
                    volver = true;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private static FormaPago leerFormaPago() {
        System.out.println("Forma de pago: 1-TARJETA  2-TRANSFERENCIA  3-EFECTIVO");
        System.out.print("Opción: ");
        String op = sc.nextLine().trim();
        switch (op) {
            case "1":
                return FormaPago.TARJETA;
            case "2":
                return FormaPago.TRANSFERENCIA;
            case "3":
                return FormaPago.EFECTIVO;
            default:
                System.out.println("Opción inválida.");
                return null;
        }
    }

    private static EstadoPedido leerEstado() {
        System.out.println("Estado: 1-PENDIENTE  2-CONFIRMADO  3-TERMINADO  4-CANCELADO");
        System.out.print("Opción: ");
        String op = sc.nextLine().trim();
        switch (op) {
            case "1":
                return EstadoPedido.PENDIENTE;
            case "2":
                return EstadoPedido.CONFIRMADO;
            case "3":
                return EstadoPedido.TERMINADO;
            case "4":
                return EstadoPedido.CANCELADO;
            default:
                System.out.println("Opción inválida.");
                return null;
        }
    }

    private static void altaPedido() {
        List<Usuario> usuarios = usuarioRepo.listarActivos();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios activos. Creá un usuario antes de cargar un pedido.");
            return;
        }
        System.out.println("Usuarios disponibles:");
        for (Usuario u : usuarios) {
            System.out.println(u.getId() + " - " + u.getNombre() + " " + u.getApellido());
        }
        System.out.print("ID del usuario: ");
        Long idUsuario = leerLong();
        if (idUsuario == null)
            return;
        boolean usuarioValido = usuarios.stream().anyMatch(u -> u.getId().equals(idUsuario));
        if (!usuarioValido) {
            System.out.println("Usuario inválido.");
            return;
        }

        FormaPago formaPago = leerFormaPago();
        if (formaPago == null)
            return;

        List<Producto> catalogo = productoRepo.listarActivos();
        Map<Long, Integer> carrito = new LinkedHashMap<>();

        boolean agregarOtro = true;
        while (agregarOtro) {
            System.out.println("Catálogo de productos activos:");
            for (Producto p : catalogo) {
                if (!Boolean.TRUE.equals(p.getDisponible()))
                    continue;
                int yaEnCarrito = carrito.getOrDefault(p.getId(), 0);
                System.out.println(p.getId() + " - " + p.getNombre() + " | $" + p.getPrecio()
                        + " | stock disponible: " + (p.getStock() - yaEnCarrito));
            }
            System.out.print("ID del producto a agregar: ");
            Long idProducto = leerLong();
            if (idProducto == null)
                continue;

            Producto producto = catalogo.stream()
                    .filter(p -> p.getId().equals(idProducto))
                    .findFirst()
                    .orElse(null);
            if (producto == null) {
                System.out.println("Producto inválido.");
            } else if (!Boolean.TRUE.equals(producto.getDisponible())) {
                System.out.println("Ese producto no está disponible.");
            } else {
                System.out.print("Cantidad: ");
                Integer cantidad = leerInt();
                int yaEnCarrito = carrito.getOrDefault(idProducto, 0);
                int stockDisponible = producto.getStock() - yaEnCarrito;
                if (cantidad == null || cantidad <= 0) {
                    System.out.println("Cantidad inválida.");
                } else if (cantidad > stockDisponible) {
                    System.out.println("Stock insuficiente. Disponible: " + stockDisponible);
                } else {
                    carrito.merge(idProducto, cantidad, Integer::sum);
                    System.out.println("Agregado: " + cantidad + " x " + producto.getNombre());
                }
            }

            System.out.print("¿Agregar otro producto? (S/N): ");
            agregarOtro = sc.nextLine().trim().equalsIgnoreCase("S");
        }

        if (carrito.isEmpty()) {
            System.out.println("El pedido debe tener al menos un producto. Operación cancelada.");
            return;
        }

        EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Usuario usuario = em.find(Usuario.class, idUsuario);
            if (usuario == null || usuario.isEliminado()) {
                throw new IllegalStateException("El usuario seleccionado ya no está activo.");
            }

            Pedido pedido = Pedido.builder()
                    .fecha(LocalDate.now())
                    .estado(EstadoPedido.PENDIENTE)
                    .formaPago(formaPago)
                    .build();
            usuario.addPedido(pedido);

            for (Map.Entry<Long, Integer> item : carrito.entrySet()) {
                Producto producto = em.find(Producto.class, item.getKey());
                int cantidad = item.getValue();
                if (producto == null || producto.isEliminado() || producto.getStock() < cantidad) {
                    throw new IllegalStateException("Stock insuficiente para \""
                            + (producto != null ? producto.getNombre() : item.getKey()) + "\" al confirmar.");
                }
                pedido.addDetallePedido(cantidad, producto);
                producto.setStock(producto.getStock() - cantidad);
            }

            pedido.calcularTotal();
            em.persist(pedido);
            tx.commit();

            System.out.println("Pedido creado con ID: " + pedido.getId());
            System.out.println("Fecha: " + pedido.getFecha() + " | Usuario: " + usuario.getNombre()
                    + " " + usuario.getApellido() + " | Forma de pago: " + pedido.getFormaPago());
            for (DetallePedido d : pedido.getDetalles()) {
                System.out.println("  " + d.getCantidad() + " x " + d.getProducto().getNombre()
                        + " = $" + d.getSubtotal());
            }
            System.out.println("Total: $" + pedido.getTotal());
        } catch (RuntimeException e) {
            if (tx.isActive())
                tx.rollback();
            System.out.println("No se pudo confirmar el pedido: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    private static void cambiarEstadoPedido() {
        listarPedidos();
        System.out.print("ID del pedido: ");
        Long id = leerLong();
        if (id == null)
            return;

        Optional<Pedido> pedidoOpt = pedidoRepo.buscarPorId(id);
        if (pedidoOpt.isEmpty() || pedidoOpt.get().isEliminado()) {
            System.out.println("No existe un pedido activo con ese ID.");
            return;
        }
        Pedido pedido = pedidoOpt.get();
        System.out.println("Estado actual: " + pedido.getEstado());
        EstadoPedido nuevoEstado = leerEstado();
        if (nuevoEstado == null)
            return;

        pedido.setEstado(nuevoEstado);
        pedidoRepo.guardar(pedido);
        System.out.println("Pedido " + pedido.getId() + " actualizado a estado " + nuevoEstado + ".");
    }

    private static void bajaPedido() {
        listarPedidos();
        System.out.print("ID del pedido a dar de baja: ");
        Long id = leerLong();
        if (id == null)
            return;

        Optional<Pedido> pedidoOpt = pedidoRepo.buscarPorId(id);
        boolean ok = pedidoRepo.eliminarLogico(id);
        if (!ok) {
            System.out.println("No se encontró el pedido o ya estaba dado de baja.");
            return;
        }
        Double total = pedidoOpt.map(Pedido::getTotal).orElse(0.0);
        System.out.println("Pedido " + id + " dado de baja. Total: $" + total);
    }

    private static void listarPedidos() {
        List<Pedido> activos = pedidoRepo.listarActivos();
        if (activos.isEmpty()) {
            System.out.println("No hay pedidos activos.");
            return;
        }
        Map<Long, String> nombresPorPedido = obtenerNombresUsuarioPorPedido();
        System.out.println("ID | Fecha | Estado | Forma de pago | Usuario | Total");
        for (Pedido p : activos) {
            String usuario = nombresPorPedido.getOrDefault(p.getId(), "(desconocido)");
            System.out.println(p.getId() + " | " + p.getFecha() + " | " + p.getEstado()
                    + " | " + p.getFormaPago() + " | " + usuario + " | $" + p.getTotal());
        }
    }

    private static void pedidosPorUsuarioMenu() {
        List<Usuario> usuarios = usuarioRepo.listarActivos();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios activos.");
            return;
        }
        for (Usuario u : usuarios) {
            System.out.println(u.getId() + " - " + u.getNombre() + " " + u.getApellido());
        }
        System.out.print("ID del usuario: ");
        Long id = leerLong();
        if (id == null)
            return;

        List<Pedido> pedidos = usuarioRepo.buscarPedidosPorUsuario(id);
        if (pedidos.isEmpty()) {
            System.out.println("Ese usuario no tiene pedidos activos.");
            return;
        }
        System.out.println("ID | Fecha | Estado | Total");
        for (Pedido p : pedidos) {
            System.out.println(p.getId() + " | " + p.getFecha() + " | " + p.getEstado() + " | $" + p.getTotal());
        }
    }

    private static void pedidosPorEstadoMenu() {
        EstadoPedido estado = leerEstado();
        if (estado == null)
            return;

        List<Pedido> pedidos = pedidoRepo.buscarPorEstado(estado);
        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos con ese estado.");
            return;
        }
        Map<Long, String> nombresPorPedido = obtenerNombresUsuarioPorPedido();
        System.out.println("ID | Fecha | Usuario | Total");
        for (Pedido p : pedidos) {
            String usuario = nombresPorPedido.getOrDefault(p.getId(), "(desconocido)");
            System.out.println(p.getId() + " | " + p.getFecha() + " | " + usuario + " | $" + p.getTotal());
        }
    }

    private static Map<Long, String> obtenerNombresUsuarioPorPedido() {
        EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT u, p FROM Usuario u JOIN u.pedidos p WHERE p.eliminado = false";
            List<Object[]> filas = em.createQuery(jpql, Object[].class).getResultList();
            Map<Long, String> resultado = new HashMap<>();
            for (Object[] fila : filas) {
                Usuario u = (Usuario) fila[0];
                Pedido p = (Pedido) fila[1];
                resultado.put(p.getId(), u.getNombre() + " " + u.getApellido());
            }
            return resultado;
        } finally {
            em.close();
        }
    }

    // Submenú Reportes

    private static void menuReportes() {
        boolean volver = false;
        while (!volver) {
            System.out.println();
            System.out.println("--- Reportes ---");
            System.out.println("1. Productos por categoría");
            System.out.println("2. Pedidos por usuario");
            System.out.println("3. Pedidos por estado");
            System.out.println("4. Total facturado");
            System.out.println("0. Volver");
            System.out.print("Opción: ");
            String op = sc.nextLine().trim();
            switch (op) {
                case "1":
                    reporteProductosPorCategoria();
                    break;
                case "2":
                    pedidosPorUsuarioMenu();
                    break;
                case "3":
                    pedidosPorEstadoMenu();
                    break;
                case "4":
                    reporteTotalFacturado();
                    break;
                case "0":
                    volver = true;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private static void reporteProductosPorCategoria() {
        List<Categoria> categorias = categoriaRepo.listarActivos();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorías activas.");
            return;
        }
        for (Categoria c : categorias) {
            System.out.println(c.getId() + " - " + c.getNombre());
        }
        System.out.print("ID de la categoría: ");
        Long id = leerLong();
        if (id == null)
            return;

        List<Producto> productos = categoriaRepo.buscarProductosPorCategoria(id);
        if (productos.isEmpty()) {
            System.out.println("Esa categoría no tiene productos activos.");
            return;
        }
        System.out.println("ID | Nombre | Precio | Stock");
        for (Producto p : productos) {
            System.out.println(p.getId() + " | " + p.getNombre() + " | $" + p.getPrecio() + " | " + p.getStock());
        }
    }

    private static void reporteTotalFacturado() {
        List<Pedido> terminados = pedidoRepo.buscarPorEstado(EstadoPedido.TERMINADO);
        double total = terminados.stream()
                .mapToDouble(p -> p.getTotal() != null ? p.getTotal() : 0.0)
                .sum();
        System.out.println("Total facturado: " + String.format(Locale.US, "$%.2f", total));
    }

}