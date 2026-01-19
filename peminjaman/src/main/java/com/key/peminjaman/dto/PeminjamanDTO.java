package com.key.peminjaman.dto;

import java.time.LocalDate;

/**
 * DTO for Peminjaman Query results
 * Separates database entity from API response
 */
public class PeminjamanDTO {
    private Long id;
    private Long anggotaId;
    private Long bukuId;
    private LocalDate tanggalPinjam;
    private LocalDate tanggalKembali;

    public PeminjamanDTO() {
    }

    public PeminjamanDTO(Long id, Long anggotaId, Long bukuId, LocalDate tanggalPinjam, LocalDate tanggalKembali) {
        this.id = id;
        this.anggotaId = anggotaId;
        this.bukuId = bukuId;
        this.tanggalPinjam = tanggalPinjam;
        this.tanggalKembali = tanggalKembali;
    }

    // Factory method from Entity
    public static PeminjamanDTO fromEntity(com.key.peminjaman.model.PeminjamanModel entity) {
        if (entity == null)
            return null;
        return new PeminjamanDTO(
                entity.getId(),
                entity.getAnggotaId(),
                entity.getBukuId(),
                entity.getTanggal_pinjam(),
                entity.getTanggal_kembali());
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAnggotaId() {
        return anggotaId;
    }

    public void setAnggotaId(Long anggotaId) {
        this.anggotaId = anggotaId;
    }

    public Long getBukuId() {
        return bukuId;
    }

    public void setBukuId(Long bukuId) {
        this.bukuId = bukuId;
    }

    public LocalDate getTanggalPinjam() {
        return tanggalPinjam;
    }

    public void setTanggalPinjam(LocalDate tanggalPinjam) {
        this.tanggalPinjam = tanggalPinjam;
    }

    public LocalDate getTanggalKembali() {
        return tanggalKembali;
    }

    public void setTanggalKembali(LocalDate tanggalKembali) {
        this.tanggalKembali = tanggalKembali;
    }
}
