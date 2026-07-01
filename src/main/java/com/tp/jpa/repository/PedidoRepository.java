package com.tp.jpa.repository;

import com.tp.jpa.model.Pedido;
import com.tp.jpa.model.enums.EstadoPedido;
import jakarta.persistence.EntityManager;

import java.util.List;

public class PedidoRepository extends BaseRepository<Pedido> {

    public PedidoRepository() {
        super(Pedido.class);
    }

    // Retorna los pedidos activos que coinciden con el estado indicado
    public List<Pedido> buscarPorEstado(EstadoPedido estadoPedido) {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT p FROM Pedido p WHERE p.estado = :estado AND p.eliminado = false";
            return em.createQuery(jpql, Pedido.class)
                    .setParameter("estado", estadoPedido)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
