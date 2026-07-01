package com.tp.jpa.repository;

import com.tp.jpa.model.Pedido;
import com.tp.jpa.model.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Usuario. Además del CRUD heredado implementa la búsqueda de
 * un usuario activo por su mail y la consulta de los pedidos de un usuario.
 *
 * Nota de diseño: como la relación es unidireccional y Usuario es el dueño de
 * la colección Set<Pedido>, la navegación se hace desde Usuario hacia sus
 * pedidos (p. ej. JPQL con JOIN sobre u.pedidos).
 */
public class UsuarioRepository extends BaseRepository<Usuario> {

    public UsuarioRepository() {
        super(Usuario.class);
    }

    /**
     * Retorna el usuario activo con el mail indicado.
     */
    public Optional<Usuario> buscarPorMail(String mail) {
        // TODO: implementar
        throw new UnsupportedOperationException("Método no implementado aún");
    }

    /**
     * Retorna los pedidos activos del usuario indicado.
     */
    public List<Pedido> buscarPedidosPorUsuario(Long idUsuario) {
        // TODO: implementar
        throw new UnsupportedOperationException("Método no implementado aún");
    }
}
