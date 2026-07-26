package com.financas.backend.repository;

import com.financas.backend.model.MetaEconomia;
import com.financas.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MetaEconomiaRepository extends JpaRepository<MetaEconomia, Long> {
    List<MetaEconomia> findByUsuarioAndAtivoTrueOrderByDataFimAsc(Usuario usuario);
}
