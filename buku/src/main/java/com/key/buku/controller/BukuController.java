package com.key.buku.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.key.buku.model.BukuModel;
import com.key.buku.service.BukuService;

import static net.logstash.logback.argument.StructuredArguments.kv;

@RestController
@RequestMapping("/api/buku")
public class BukuController {

    private static final Logger log = LoggerFactory.getLogger(BukuController.class);

    @Autowired
    private BukuService bukuService;

    @GetMapping
    public List<BukuModel> getAllBuku() {
        log.info("Request received", kv("action", "GET_ALL"));
        List<BukuModel> result = bukuService.getAllBuku();
        log.info("Request completed", kv("action", "GET_ALL"), kv("status", "SUCCESS"), kv("count", result.size()));
        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BukuModel> getBukubyId(@PathVariable Long id) {
        log.info("Request received", kv("action", "GET_BY_ID"), kv("id", id));
        BukuModel buku = bukuService.getBukuById(id);
        if (buku != null) {
            log.info("Request completed", kv("action", "GET_BY_ID"), kv("status", "sukses"), kv("id", id));
            return ResponseEntity.ok(buku);
        } else {
            log.warn("Request completed", kv("action", "GET_BY_ID"), kv("status", "gagal"), kv("id", id));
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public BukuModel creteBuku(@RequestBody BukuModel buku) {
        log.info("Request received", kv("action", "CREATE"), kv("judul", buku.getJudul()));
        BukuModel result = bukuService.createBuku(buku);
        log.info("Request completed", kv("action", "CREATE"), kv("status", "SUCCESS"), kv("id", result.getId()));
        return result;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBuku(@PathVariable Long id) {
        log.info("Request received", kv("action", "DELETE"), kv("id", id));
        bukuService.deleteBuku(id);
        log.info("Request completed", kv("action", "DELETE"), kv("status", "SUCCESS"), kv("id", id));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<BukuModel> updateBuku(@PathVariable Long id, @RequestBody BukuModel buku) {
        log.info("Request received", kv("action", "UPDATE"), kv("id", id));
        BukuModel updated = bukuService.updateBuku(id, buku);
        if (updated != null) {
            log.info("Request completed", kv("action", "UPDATE"), kv("status", "SUCCESS"), kv("id", id));
            return ResponseEntity.ok(updated);
        } else {
            log.warn("Request completed", kv("action", "UPDATE"), kv("status", "NOT_FOUND"), kv("id", id));
            return ResponseEntity.notFound().build();
        }
    }
}
