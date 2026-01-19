package com.key.pengembalian.vo;

import java.time.LocalDate;

public class Peminjaman {
    private Long id;
    private Long anggotaId;
    private Long bukuId;
    private LocalDate tanggal_pinjam;
    private LocalDate tanggal_kembali;

    public Peminjaman() {
    }

    public Peminjaman(Long anggotaId, Long bukuId, Long id, LocalDate tanggal_kembali, LocalDate tanggal_pinjam) {
        this.anggotaId = anggotaId;
        this.bukuId = bukuId;
        this.id = id;
        this.tanggal_kembali = tanggal_kembali;
        this.tanggal_pinjam = tanggal_pinjam;
    }

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

    public LocalDate getTanggal_pinjam() {
        return tanggal_pinjam;
    }

    public void setTanggal_pinjam(LocalDate tanggal_pinjam) {
        this.tanggal_pinjam = tanggal_pinjam;
    }

    public LocalDate getTanggal_kembali() {
        return tanggal_kembali;
    }

    public void setTanggal_kembali(LocalDate tanggal_kembali) {
        this.tanggal_kembali = tanggal_kembali;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
