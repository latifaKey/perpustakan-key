package com.key.buku.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.key.buku.model.BukuModel;
import com.key.buku.repository.BukuRepository;

@Service
public class BukuService {
    @Autowired

    private BukuRepository BukuRepository;

    public List<BukuModel> getAllBuku() {
        return BukuRepository.findAll();
    }

    public BukuModel getBukuById(Long id) {
        return BukuRepository.findById(id).orElse(null);
    }

    public BukuModel createBuku(BukuModel buku) {
        return BukuRepository.save(buku);
    }

    public void deleteBuku(Long id) {
        BukuRepository.deleteById(id);
    }

    public BukuModel updateBuku(Long id, BukuModel bukuUpdate) {
        return BukuRepository.findById(id)
                .map(existingBuku -> {
                    if (bukuUpdate.getJudul() != null) {
                        existingBuku.setJudul(bukuUpdate.getJudul());
                    }
                    if (bukuUpdate.getPengarang() != null) {
                        existingBuku.setPengarang(bukuUpdate.getPengarang());
                    }
                    if (bukuUpdate.getPenerbit() != null) {
                        existingBuku.setPenerbit(bukuUpdate.getPenerbit());
                    }
                    if (bukuUpdate.getTahun_terbit() != null) {
                        existingBuku.setTahun_terbit(bukuUpdate.getTahun_terbit());
                    }
                    return BukuRepository.save(existingBuku);
                })
                .orElse(null);
    }
}
