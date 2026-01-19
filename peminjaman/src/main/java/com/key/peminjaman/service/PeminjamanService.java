package com.key.peminjaman.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.key.peminjaman.dto.PeminjamanEventDTO;
import com.key.peminjaman.model.PeminjamanModel;
import com.key.peminjaman.repository.PeminjamanRepository;
import com.key.peminjaman.vo.Anggota;
import com.key.peminjaman.vo.Buku;
import com.key.peminjaman.vo.ResponseTemplate;

import org.springframework.cloud.client.discovery.DiscoveryClient;

@Service
public class PeminjamanService {
    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private PeminjamanRepository peminjamanRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RabbitMQProducerService rabbitMQProducerService;

    public List<PeminjamanModel> getAllPeminjaman() {
        return peminjamanRepository.findAll();
    }

    public PeminjamanModel getPeminjamanById(Long id) {
        return peminjamanRepository.findById(id).orElse(null);
    }

    public PeminjamanModel createPeminjaman(PeminjamanModel peminjaman) {
        // 1. Save peminjaman
        PeminjamanModel savedPeminjaman = peminjamanRepository.save(peminjaman);

        // 2. Fetch anggota and buku data for enriched event
        try {
            ServiceInstance anggotaInstance = discoveryClient.getInstances("ANGGOTA").get(0);
            Anggota anggota = restTemplate.getForObject(
                    anggotaInstance.getUri() + "/api/anggota/" + peminjaman.getAnggotaId(),
                    Anggota.class);

            ServiceInstance bukuInstance = discoveryClient.getInstances("BUKU").get(0);
            Buku buku = restTemplate.getForObject(
                    bukuInstance.getUri() + "/api/buku/" + peminjaman.getBukuId(),
                    Buku.class);

            // 3. Create enriched event DTO
            PeminjamanEventDTO.PeminjamanData data = new PeminjamanEventDTO.PeminjamanData();
            data.setPeminjamanId(savedPeminjaman.getId());
            data.setTanggalPinjam(savedPeminjaman.getTanggal_pinjam());
            data.setTanggalKembali(savedPeminjaman.getTanggal_kembali());

            if (anggota != null) {
                data.setAnggota(new PeminjamanEventDTO.AnggotaInfo(
                        anggota.getId(),
                        anggota.getNama(),
                        anggota.getEmail()));
            }

            if (buku != null) {
                data.setBuku(new PeminjamanEventDTO.BukuInfo(
                        buku.getId(),
                        buku.getJudul()));
            }

            PeminjamanEventDTO event = new PeminjamanEventDTO(
                    "PEMINJAMAN_CREATED",
                    LocalDateTime.now(),
                    UUID.randomUUID().toString(),
                    data);

            // 4. Publish to RabbitMQ
            rabbitMQProducerService.sendPeminjamanEvent(event);
            System.out.println("📤 Peminjaman event published for ID: " + savedPeminjaman.getId());

        } catch (Exception e) {
            System.err.println("⚠️ Failed to publish RabbitMQ event: " + e.getMessage());
            // Don't fail the transaction, just log the error
        }

        return savedPeminjaman;
    }

    public void deletePeminjaman(Long id) {
        peminjamanRepository.deleteById(id);
    }

    public List<ResponseTemplate> getPeminjamanWithAnggotaById(Long id) {
        List<ResponseTemplate> responseList = new ArrayList<>();
        PeminjamanModel peminjaman = getPeminjamanById(id);

        ServiceInstance anggotaInstance = discoveryClient.getInstances("ANGGOTA").get(0);
        Anggota anggota = restTemplate.getForObject(
                anggotaInstance.getUri() + "/api/anggota/" + peminjaman.getAnggotaId(),
                Anggota.class);

        ServiceInstance bukuInstance = discoveryClient.getInstances("BUKU").get(0);
        Buku buku = restTemplate.getForObject(
                bukuInstance.getUri() + "/api/buku/" + peminjaman.getBukuId(),
                Buku.class);

        ResponseTemplate vo = new ResponseTemplate();
        vo.setPeminjaman(peminjaman);
        vo.setAnggota(anggota);
        vo.setBuku(buku);

        responseList.add(vo);
        return responseList;
    }

    public List<PeminjamanModel> getPeminjamanByAnggotaId(Long anggotaId) {
        return peminjamanRepository.findByAnggotaId(anggotaId);
    }

    public PeminjamanModel updatePeminjaman(Long id, PeminjamanModel peminjamanUpdate) {
        return peminjamanRepository.findById(id)
                .map(existingPeminjaman -> {
                    if (peminjamanUpdate.getAnggotaId() != null) {
                        existingPeminjaman.setAnggotaId(peminjamanUpdate.getAnggotaId());
                    }
                    if (peminjamanUpdate.getBukuId() != null) {
                        existingPeminjaman.setBukuId(peminjamanUpdate.getBukuId());
                    }
                    if (peminjamanUpdate.getTanggal_pinjam() != null) {
                        existingPeminjaman.setTanggal_pinjam(peminjamanUpdate.getTanggal_pinjam());
                    }
                    if (peminjamanUpdate.getTanggal_kembali() != null) {
                        existingPeminjaman.setTanggal_kembali(peminjamanUpdate.getTanggal_kembali());
                    }
                    return peminjamanRepository.save(existingPeminjaman);
                })
                .orElse(null);
    }
}