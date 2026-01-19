package com.key.peminjaman.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.key.peminjaman.model.PeminjamanModel;

@Repository
public interface PeminjamanRepository extends JpaRepository<PeminjamanModel, Long> {
    List<PeminjamanModel> findByAnggotaId(Long anggotaId);
}
