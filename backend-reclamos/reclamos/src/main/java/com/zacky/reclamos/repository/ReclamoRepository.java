package com.zacky.reclamos.repository;

import com.zacky.reclamos.model.entity.ReclamoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReclamoRepository extends JpaRepository<ReclamoModel, Integer> {
    List<ReclamoModel> findByIdUsuario(Integer idUsuario);
}
