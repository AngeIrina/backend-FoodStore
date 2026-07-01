package com.tp.jpa.repository;

import com.tp.jpa.model.Pedido;
import com.tp.jpa.model.enums.EstadoPedido;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * Repositorio de Pedido. Además del CRUD heredado implementa la consulta de
 * pedidos por estado.
 *
 * Nota de diseño: la búsqueda de pedidos por usuario NO vive aquí porque la
 * relación Usuario–Pedido es unidireccional y la dueña es Usuario (es Usuario
 * quien posee el Set<Pedido>). Pedido no conoce a su usuario, por lo que esa
 * consulta se ubica en UsuarioRepository.
 */
public class PedidoRepository extends BaseRepository<Pedido> {

    public PedidoRepository() {
        super(Pedido.class);
    }

    /**
     * Retorna los pedidos activos que coinciden con el estado indicado.
     */
    public List<Pedido> buscarPorEstado(EstadoPedido estadoPedido) {
        // TODO: implementar
        throw new UnsupportedOperationException("Método no implementado aún");
    }
}
