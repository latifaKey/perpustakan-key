package com.key.peminjaman.application.commands;

import com.key.peminjaman.application.Command;
import com.key.peminjaman.dto.CommandResult;

/**
 * Command for creating a new Peminjaman (borrowing)
 * Returns CommandResult with ID, not full entity (CQRS compliant)
 */
public class CreatePeminjamanCommand implements Command<CommandResult> {
    private final Long anggotaId;
    private final Long bukuId;
    private final java.time.LocalDate tanggalPinjam;
    private final java.time.LocalDate tanggalKembali;

    public CreatePeminjamanCommand(Long anggotaId, Long bukuId, java.time.LocalDate tanggalPinjam,
            java.time.LocalDate tanggalKembali) {
        this.anggotaId = anggotaId;
        this.bukuId = bukuId;
        this.tanggalPinjam = tanggalPinjam;
        this.tanggalKembali = tanggalKembali;
    }

    public CreatePeminjamanCommand(com.key.peminjaman.model.PeminjamanModel model) {
        this.anggotaId = model.getAnggotaId();
        this.bukuId = model.getBukuId();
        this.tanggalPinjam = model.getTanggal_pinjam();
        this.tanggalKembali = model.getTanggal_kembali();
    }

    public Long getAnggotaId() {
        return anggotaId;
    }

    public Long getBukuId() {
        return bukuId;
    }

    public java.time.LocalDate getTanggalPinjam() {
        return tanggalPinjam;
    }

    public java.time.LocalDate getTanggalKembali() {
        return tanggalKembali;
    }
}
