package com.key.pengembalian.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.key.pengembalian.dto.PengembalianEventDTO;
import com.key.pengembalian.model.PengembalianModel;
import com.key.pengembalian.repository.PengembalianRepository;
import com.key.pengembalian.vo.Anggota;
import com.key.pengembalian.vo.Buku;
import com.key.pengembalian.vo.Peminjaman;
import com.key.pengembalian.vo.ResponseTemplate;

@Service
public class PengembalianService {

    private static final Logger log = LoggerFactory.getLogger(PengembalianService.class);

    // Tarif denda per hari keterlambatan = Rp 2.000
    private static final double TARIF_DENDA_HARIAN = 2000.0;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private PengembalianRepository pengembalianRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RabbitMQProducerService rabbitMQProducerService;

    public List<PengembalianModel> getAllPengembalian() {
        return pengembalianRepository.findAll();
    }

    public PengembalianModel getPengembalianById(Long id) {
        return pengembalianRepository.findById(id).orElse(null);
    }

    public PengembalianModel createPengembalian(PengembalianModel pengembalian) {
        Peminjaman peminjaman = null;
        Anggota anggota = null;
        Buku buku = null;

        // Jika peminjamanId ada, fetch data peminjaman dan hitung denda otomatis
        if (pengembalian.getPeminjamanId() != null) {
            try {
                ServiceInstance peminjamanInstance = discoveryClient.getInstances("PEMINJAMAN").get(0);
                peminjaman = restTemplate.getForObject(
                        peminjamanInstance.getUri() + "/api/peminjaman/" + pengembalian.getPeminjamanId(),
                        Peminjaman.class);

                if (peminjaman != null) {
                    // Gunakan tanggal_dikembalikan dari input, jika null gunakan hari ini
                    LocalDate tanggalKembaliAktual = pengembalian.getTanggal_dikembalikan();
                    if (tanggalKembaliAktual == null) {
                        tanggalKembaliAktual = LocalDate.now();
                        pengembalian.setTanggal_dikembalikan(tanggalKembaliAktual);
                    }

                    // Hitung keterlambatan dan denda
                    int hariTerlambat = hitungKeterlambatan(peminjaman.getTanggal_kembali(), tanggalKembaliAktual);
                    double denda = hitungDenda(hariTerlambat);

                    pengembalian.setTerlambat(hariTerlambat);
                    pengembalian.setDenda(denda);

                    // Fetch data anggota dan buku untuk notifikasi
                    anggota = fetchAnggota(peminjaman.getAnggotaId());
                    buku = fetchBuku(peminjaman.getBukuId());
                }
            } catch (Exception e) {
                // Jika gagal fetch, tetap simpan tanpa perhitungan
                log.error("Failed to fetch peminjaman data: {}", e.getMessage());
            }
        }

        // Simpan pengembalian ke database
        PengembalianModel savedPengembalian = pengembalianRepository.save(pengembalian);

        // Publish event ke RabbitMQ (async, tidak mengganggu flow utama)
        publishPengembalianEvent(savedPengembalian, anggota, buku);

        return savedPengembalian;
    }

    public void deletePengembalian(Long id) {
        pengembalianRepository.deleteById(id);
    }

    /**
     * Proses pengembalian buku berdasarkan idAnggota dan tanggalKembali
     * Menghitung otomatis keterlambatan dan denda
     */
    public PengembalianModel prosesPengembalian(Long idAnggota, LocalDate tanggalKembali) {
        // 1. Fetch peminjaman dari service Peminjaman berdasarkan anggotaId
        ServiceInstance peminjamanInstance = discoveryClient.getInstances("PEMINJAMAN").get(0);
        String url = peminjamanInstance.getUri() + "/api/peminjaman/by-anggota/" + idAnggota;

        ResponseEntity<List<Peminjaman>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Peminjaman>>() {
                });

        List<Peminjaman> peminjamanList = response.getBody();

        if (peminjamanList == null || peminjamanList.isEmpty()) {
            return null; // Tidak ada peminjaman untuk anggota ini
        }

        // Ambil peminjaman pertama (FIFO - paling lama)
        Peminjaman peminjaman = peminjamanList.get(0);

        // 2. Hitung keterlambatan
        LocalDate tanggalJanjiKembali = peminjaman.getTanggal_kembali();
        int hariTerlambat = hitungKeterlambatan(tanggalJanjiKembali, tanggalKembali);

        // 3. Hitung denda
        double denda = hitungDenda(hariTerlambat);

        // 4. Buat dan simpan record pengembalian
        PengembalianModel pengembalian = new PengembalianModel();
        pengembalian.setPeminjamanId(peminjaman.getId());
        pengembalian.setTanggal_dikembalikan(tanggalKembali);
        pengembalian.setTerlambat(hariTerlambat);
        pengembalian.setDenda(denda);

        PengembalianModel savedPengembalian = pengembalianRepository.save(pengembalian);

        // 5. Fetch data anggota dan buku untuk notifikasi
        Anggota anggota = fetchAnggota(idAnggota);
        Buku buku = fetchBuku(peminjaman.getBukuId());

        // 6. Publish event ke RabbitMQ
        publishPengembalianEvent(savedPengembalian, anggota, buku);

        return savedPengembalian;
    }

    /**
     * Menghitung jumlah hari keterlambatan
     */
    public int hitungKeterlambatan(LocalDate tanggalJanjiKembali, LocalDate tanggalKembaliAktual) {
        if (tanggalJanjiKembali == null || tanggalKembaliAktual == null) {
            return 0;
        }
        long selisih = ChronoUnit.DAYS.between(tanggalJanjiKembali, tanggalKembaliAktual);
        return selisih > 0 ? (int) selisih : 0; // 0 jika tidak terlambat atau lebih awal
    }

    /**
     * Menghitung denda berdasarkan jumlah hari terlambat
     */
    public double hitungDenda(int hariTerlambat) {
        return hariTerlambat * TARIF_DENDA_HARIAN;
    }

    public PengembalianModel updatePengembalian(Long id, PengembalianModel pengembalianUpdate) {
        return pengembalianRepository.findById(id)
                .map(existingPengembalian -> {
                    if (pengembalianUpdate.getPeminjamanId() != null) {
                        existingPengembalian.setPeminjamanId(pengembalianUpdate.getPeminjamanId());
                    }
                    if (pengembalianUpdate.getTanggal_dikembalikan() != null) {
                        existingPengembalian.setTanggal_dikembalikan(pengembalianUpdate.getTanggal_dikembalikan());
                    }
                    if (pengembalianUpdate.getTerlambat() != null) {
                        existingPengembalian.setTerlambat(pengembalianUpdate.getTerlambat());
                    }
                    if (pengembalianUpdate.getDenda() != null) {
                        existingPengembalian.setDenda(pengembalianUpdate.getDenda());
                    }
                    return pengembalianRepository.save(existingPengembalian);
                })
                .orElse(null);
    }

    public List<ResponseTemplate> getPengembalianWithPeminjamanById(Long id) {
        List<ResponseTemplate> responseList = new ArrayList<>();
        PengembalianModel pengembalian = getPengembalianById(id);

        if (pengembalian == null) {
            return responseList;
        }

        ServiceInstance peminjamanInstance = discoveryClient.getInstances("PEMINJAMAN").get(0);
        Peminjaman peminjaman = restTemplate.getForObject(
                peminjamanInstance.getUri() + "/api/peminjaman/" + pengembalian.getPeminjamanId(),
                Peminjaman.class);

        if (peminjaman == null) {
            return responseList;
        }

        ServiceInstance anggotaInstance = discoveryClient.getInstances("ANGGOTA").get(0);
        Anggota anggota = restTemplate.getForObject(
                anggotaInstance.getUri() + "/api/anggota/" + peminjaman.getAnggotaId(),
                Anggota.class);

        ServiceInstance bukuInstance = discoveryClient.getInstances("BUKU").get(0);
        Buku buku = restTemplate.getForObject(
                bukuInstance.getUri() + "/api/buku/" + peminjaman.getBukuId(),
                Buku.class);

        ResponseTemplate vo = new ResponseTemplate();

        vo.setPengembalian(pengembalian);
        vo.setPeminjaman(peminjaman);
        vo.setAnggota(anggota);
        vo.setBuku(buku);

        responseList.add(vo);
        return responseList;
    }

    // ==================== HELPER METHODS ====================

    /**
     * Fetch data anggota dari service Anggota
     */
    private Anggota fetchAnggota(Long anggotaId) {
        try {
            ServiceInstance anggotaInstance = discoveryClient.getInstances("ANGGOTA").get(0);
            return restTemplate.getForObject(
                    anggotaInstance.getUri() + "/api/anggota/" + anggotaId,
                    Anggota.class);
        } catch (Exception e) {
            log.warn("Failed to fetch anggota data: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Fetch data buku dari service Buku
     */
    private Buku fetchBuku(Long bukuId) {
        try {
            ServiceInstance bukuInstance = discoveryClient.getInstances("BUKU").get(0);
            return restTemplate.getForObject(
                    bukuInstance.getUri() + "/api/buku/" + bukuId,
                    Buku.class);
        } catch (Exception e) {
            log.warn("Failed to fetch buku data: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Publish event pengembalian ke RabbitMQ
     * Tidak mengganggu flow utama jika gagal
     */
    private void publishPengembalianEvent(PengembalianModel pengembalian, Anggota anggota, Buku buku) {
        try {
            PengembalianEventDTO.PengembalianData data = new PengembalianEventDTO.PengembalianData();
            data.setPengembalianId(pengembalian.getId());
            data.setPeminjamanId(pengembalian.getPeminjamanId());
            data.setTanggalDikembalikan(pengembalian.getTanggal_dikembalikan());
            data.setTerlambat(pengembalian.getTerlambat());
            data.setDenda(pengembalian.getDenda());

            if (anggota != null) {
                data.setAnggota(new PengembalianEventDTO.AnggotaInfo(
                        anggota.getId(), anggota.getNama(), anggota.getEmail()));
            }

            if (buku != null) {
                data.setBuku(new PengembalianEventDTO.BukuInfo(buku.getId(), buku.getJudul()));
            }

            PengembalianEventDTO event = new PengembalianEventDTO("PENGEMBALIAN_CREATED", data);
            rabbitMQProducerService.sendPengembalianEvent(event);

            log.info("📤 [Pengembalian] Event published for ID: {}", pengembalian.getId());
        } catch (Exception e) {
            log.error("⚠️ [Pengembalian] Failed to publish event: {}", e.getMessage());
        }
    }
}