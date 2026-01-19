package com.key.peminjaman.application.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.key.peminjaman.application.CommandHandler;
import com.key.peminjaman.dto.CommandResult;
import com.key.peminjaman.repository.PeminjamanRepository;

/**
 * Handler for UpdatePeminjamanCommand
 * Returns CommandResult (CQRS compliant)
 */
@Component
public class UpdatePeminjamanHandler implements CommandHandler<UpdatePeminjamanCommand, CommandResult> {

    @Autowired
    private PeminjamanRepository peminjamanRepository;

    @Override
    public CommandResult handle(UpdatePeminjamanCommand command) {
        return peminjamanRepository.findById(command.getId())
                .map(existing -> {
                    if (command.getAnggotaId() != null) {
                        existing.setAnggotaId(command.getAnggotaId());
                    }
                    if (command.getBukuId() != null) {
                        existing.setBukuId(command.getBukuId());
                    }
                    if (command.getTanggalPinjam() != null) {
                        existing.setTanggal_pinjam(command.getTanggalPinjam());
                    }
                    if (command.getTanggalKembali() != null) {
                        existing.setTanggal_kembali(command.getTanggalKembali());
                    }
                    peminjamanRepository.save(existing);
                    return CommandResult.success(existing.getId(), "Peminjaman updated successfully");
                })
                .orElse(CommandResult.failure("Peminjaman not found with ID: " + command.getId()));
    }
}
