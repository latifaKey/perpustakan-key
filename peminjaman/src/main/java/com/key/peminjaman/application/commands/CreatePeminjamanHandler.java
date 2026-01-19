package com.key.peminjaman.application.commands;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.key.peminjaman.application.CommandHandler;
import com.key.peminjaman.dto.CommandResult;
import com.key.peminjaman.dto.PeminjamanEventDTO;
import com.key.peminjaman.model.PeminjamanModel;
import com.key.peminjaman.repository.PeminjamanRepository;
import com.key.peminjaman.service.RabbitMQProducerService;
import com.key.peminjaman.vo.Anggota;
import com.key.peminjaman.vo.Buku;

/**
 * Handler for CreatePeminjamanCommand
 * Returns CommandResult (ID + status) instead of full entity (CQRS compliant)
 */
@Component
public class CreatePeminjamanHandler implements CommandHandler<CreatePeminjamanCommand, CommandResult> {

    @Autowired
    private PeminjamanRepository peminjamanRepository;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RabbitMQProducerService rabbitMQProducerService;

    @Override
    public CommandResult handle(CreatePeminjamanCommand command) {
        try {
            // 1. Create entity from command (Rich Domain approach)
            PeminjamanModel peminjaman = PeminjamanModel.create(
                    command.getAnggotaId(),
                    command.getBukuId(),
                    command.getTanggalPinjam(),
                    command.getTanggalKembali());

            // 2. Save to repository
            PeminjamanModel savedPeminjaman = peminjamanRepository.save(peminjaman);

            // 3. Publish event (async side-effect)
            publishEvent(savedPeminjaman);

            // 4. Return only ID and status (CQRS compliant)
            return CommandResult.success(savedPeminjaman.getId(), "Peminjaman created successfully");

        } catch (Exception e) {
            return CommandResult.failure("Failed to create peminjaman: " + e.getMessage());
        }
    }

    private void publishEvent(PeminjamanModel savedPeminjaman) {
        try {
            ServiceInstance anggotaInstance = discoveryClient.getInstances("ANGGOTA").get(0);
            Anggota anggota = restTemplate.getForObject(
                    anggotaInstance.getUri() + "/api/anggota/" + savedPeminjaman.getAnggotaId(),
                    Anggota.class);

            ServiceInstance bukuInstance = discoveryClient.getInstances("BUKU").get(0);
            Buku buku = restTemplate.getForObject(
                    bukuInstance.getUri() + "/api/buku/" + savedPeminjaman.getBukuId(),
                    Buku.class);

            PeminjamanEventDTO.PeminjamanData data = new PeminjamanEventDTO.PeminjamanData();
            data.setPeminjamanId(savedPeminjaman.getId());
            data.setTanggalPinjam(savedPeminjaman.getTanggal_pinjam());
            data.setTanggalKembali(savedPeminjaman.getTanggal_kembali());

            if (anggota != null) {
                data.setAnggota(new PeminjamanEventDTO.AnggotaInfo(
                        anggota.getId(), anggota.getNama(), anggota.getEmail()));
            }

            if (buku != null) {
                data.setBuku(new PeminjamanEventDTO.BukuInfo(buku.getId(), buku.getJudul()));
            }

            PeminjamanEventDTO event = new PeminjamanEventDTO(
                    "PEMINJAMAN_CREATED",
                    LocalDateTime.now(),
                    UUID.randomUUID().toString(),
                    data);

            rabbitMQProducerService.sendPeminjamanEvent(event);
            System.out.println("📤 [CQRS] Event published for Peminjaman ID: " + savedPeminjaman.getId());

        } catch (Exception e) {
            System.err.println("⚠️ [CQRS] Failed to publish event: " + e.getMessage());
        }
    }
}
