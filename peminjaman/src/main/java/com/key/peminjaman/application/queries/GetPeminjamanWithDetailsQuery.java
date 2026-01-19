package com.key.peminjaman.application.queries;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.key.peminjaman.application.Query;
import com.key.peminjaman.application.QueryHandler;
import com.key.peminjaman.model.PeminjamanModel;
import com.key.peminjaman.repository.PeminjamanRepository;
import com.key.peminjaman.vo.Anggota;
import com.key.peminjaman.vo.Buku;
import com.key.peminjaman.vo.ResponseTemplate;

/**
 * Query for getting Peminjaman with Anggota and Buku details
 */
public class GetPeminjamanWithDetailsQuery implements Query<List<ResponseTemplate>> {
    private final Long id;

    public GetPeminjamanWithDetailsQuery(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}

/**
 * Handler for GetPeminjamanWithDetailsQuery
 * Enriches peminjaman with Anggota and Buku data from other services
 * Using direct URLs for local development (bypass Eureka)
 */
@Component
class GetPeminjamanWithDetailsHandler implements QueryHandler<GetPeminjamanWithDetailsQuery, List<ResponseTemplate>> {

    // Direct URLs for local development
    private static final String ANGGOTA_URL = "http://localhost:8081/api/anggota/";
    private static final String BUKU_URL = "http://localhost:8082/api/buku/";

    @Autowired
    private PeminjamanRepository peminjamanRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<ResponseTemplate> handle(GetPeminjamanWithDetailsQuery query) {
        List<ResponseTemplate> responseList = new ArrayList<>();

        System.out.println("========== DEBUG START ==========");
        System.out.println("Query ID: " + query.getId());

        // 1. Get Peminjaman from DB
        PeminjamanModel peminjaman = peminjamanRepository.findById(query.getId()).orElse(null);
        if (peminjaman == null) {
            System.out.println("❌ Peminjaman with ID " + query.getId() + " NOT FOUND!");
            return responseList;
        }
        System.out.println("✅ Found Peminjaman: id=" + peminjaman.getId() +
                ", anggotaId=" + peminjaman.getAnggotaId() +
                ", bukuId=" + peminjaman.getBukuId());

        // 2. Get Anggota from Anggota Service (direct URL)
        Anggota anggota = null;
        try {
            String anggotaUrl = ANGGOTA_URL + peminjaman.getAnggotaId();
            System.out.println("📡 Fetching Anggota from: " + anggotaUrl);
            anggota = restTemplate.getForObject(anggotaUrl, Anggota.class);
            if (anggota != null) {
                System.out.println("✅ Fetched Anggota: id=" + anggota.getId() + ", nama=" + anggota.getNama());
            } else {
                System.out.println("⚠️ Anggota returned null");
            }
        } catch (Exception e) {
            System.out.println("❌ Failed to fetch Anggota: " + e.getMessage());
            e.printStackTrace();
        }

        // 3. Get Buku from Buku Service (direct URL)
        Buku buku = null;
        try {
            String bukuUrl = BUKU_URL + peminjaman.getBukuId();
            System.out.println("📡 Fetching Buku from: " + bukuUrl);
            buku = restTemplate.getForObject(bukuUrl, Buku.class);
            if (buku != null) {
                System.out.println("✅ Fetched Buku: id=" + buku.getId() + ", judul=" + buku.getJudul());
            } else {
                System.out.println("⚠️ Buku returned null");
            }
        } catch (Exception e) {
            System.out.println("❌ Failed to fetch Buku: " + e.getMessage());
            e.printStackTrace();
        }

        // 4. Build response
        ResponseTemplate vo = new ResponseTemplate();
        vo.setPeminjaman(peminjaman);
        vo.setAnggota(anggota);
        vo.setBuku(buku);

        responseList.add(vo);

        System.out.println("========== DEBUG END ==========");
        return responseList;
    }
}
