package com.key.pengembalian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.key.pengembalian.model.PengembalianModel;

@Repository
public interface PengembalianRepository extends JpaRepository<PengembalianModel, Long> {

}
