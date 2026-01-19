package com.key.pengembalian.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class PengembalianModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long peminjamanId;

    @JsonAlias({ "tanggalKembali", "tanggal_kembali" })
    private LocalDate tanggal_dikembalikan; // yyyy-mm-dd

    private Integer terlambat; // satuan hari ya
    private Double denda; // bayar berapa ntar, jika lewat sehari maka 2000 aja

    public Long getPeminjamanId() {
        return peminjamanId;
    }

    public void setPeminjamanId(Long peminjamanId) {
        this.peminjamanId = peminjamanId;
    }

}
