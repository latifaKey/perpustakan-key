package com.key.peminjaman.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class PeminjamanModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long anggotaId;
    private Long bukuId;
    private LocalDate tanggal_pinjam;
    private LocalDate tanggal_kembali;

    /**
     * Factory method for creating Peminjaman (Rich Domain Model pattern)
     * Encapsulates entity creation logic
     */
    public static PeminjamanModel create(Long anggotaId, Long bukuId, LocalDate tanggalPinjam,
            LocalDate tanggalKembali) {
        PeminjamanModel peminjaman = new PeminjamanModel();
        peminjaman.setAnggotaId(anggotaId);
        peminjaman.setBukuId(bukuId);
        peminjaman.setTanggal_pinjam(tanggalPinjam);
        peminjaman.setTanggal_kembali(tanggalKembali);
        return peminjaman;
    }
}