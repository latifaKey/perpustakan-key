package com.key.peminjaman.application.commands;

import java.time.LocalDate;

import com.key.peminjaman.application.Command;
import com.key.peminjaman.dto.CommandResult;
import com.key.peminjaman.model.PeminjamanModel;

/**
 * Command for updating an existing Peminjaman
 * Returns CommandResult (CQRS compliant)
 */
public class UpdatePeminjamanCommand implements Command<CommandResult> {
    private final Long id;
    private final Long anggotaId;
    private final Long bukuId;
    private final LocalDate tanggalPinjam;
    private final LocalDate tanggalKembali;

    public UpdatePeminjamanCommand(Long id, PeminjamanModel model) {
        this.id = id;
        this.anggotaId = model.getAnggotaId();
        this.bukuId = model.getBukuId();
        this.tanggalPinjam = model.getTanggal_pinjam();
        this.tanggalKembali = model.getTanggal_kembali();
    }

    public Long getId() {
        return id;
    }

    public Long getAnggotaId() {
        return anggotaId;
    }

    public Long getBukuId() {
        return bukuId;
    }

    public LocalDate getTanggalPinjam() {
        return tanggalPinjam;
    }

    public LocalDate getTanggalKembali() {
        return tanggalKembali;
    }
}
