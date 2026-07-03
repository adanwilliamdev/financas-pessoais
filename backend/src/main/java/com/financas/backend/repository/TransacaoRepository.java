package com.financas.backend.repository;

import com.financas.backend.model.Transacao;
import com.financas.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    List<Transacao> findByUsuarioOrderByDataDesc(Usuario usuario);
    
    List<Transacao> findByUsuarioAndDataBetweenOrderByDataDesc(Usuario usuario, LocalDate inicio, LocalDate fim);
    
    List<Transacao> findByUsuarioAndCategoria(Usuario usuario, String categoria);
    
    @Query("SELECT SUM(t.valor) FROM Transacao t WHERE t.usuario = :usuario AND t.tipo = :tipo AND t.data BETWEEN :inicio AND :fim")
    BigDecimal sumByUsuarioAndTipoAndDataBetween(
        @Param("usuario") Usuario usuario,
        @Param("tipo") String tipo,
        @Param("inicio") LocalDate inicio,
        @Param("fim") LocalDate fim
    );
    
    @Query("SELECT t.categoria, SUM(t.valor) FROM Transacao t WHERE t.usuario = :usuario AND t.tipo = :tipo AND t.data BETWEEN :inicio AND :fim GROUP BY t.categoria")
    List<Object[]> sumByCategoriaAndTipoAndDataBetween(
        @Param("usuario") Usuario usuario,
        @Param("tipo") String tipo,
        @Param("inicio") LocalDate inicio,
        @Param("fim") LocalDate fim
    );
}
