package com.key.pengembalian.dto;

import java.time.LocalDate;

public class PengembalianRequest {
    private Long idAnggota;
    private LocalDate tanggalKembali;

    public PengembalianRequest() {
    }

    public PengembalianRequest(Long idAnggota, LocalDate tanggalKembali) {
        this.idAnggota = idAnggota;
        this.tanggalKembali = tanggalKembali;
    }

    public Long getIdAnggota() {
        return idAnggota;
    }

    public void setIdAnggota(Long idAnggota) {
        this.idAnggota = idAnggota;
    }

    public LocalDate getTanggalKembali() {
        return tanggalKembali;
    }

    public void setTanggalKembali(LocalDate tanggalKembali) {
        this.tanggalKembali = tanggalKembali;
    }
}
