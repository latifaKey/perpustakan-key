package com.key.pengembalian.controller;

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

import com.key.pengembalian.dto.PengembalianRequest;
import com.key.pengembalian.model.PengembalianModel;
import com.key.pengembalian.service.PengembalianService;
import com.key.pengembalian.vo.ResponseTemplate;

import static net.logstash.logback.argument.StructuredArguments.kv;

@RestController
@RequestMapping("/api/pengembalian")
public class PengembalianController {

    private static final Logger log = LoggerFactory.getLogger(PengembalianController.class);

    @Autowired
    private PengembalianService pengembalianService;

    @GetMapping
    public List<PengembalianModel> getAllPengembalian() {
        log.info("Request received", kv("action", "GET_ALL"));
        List<PengembalianModel> result = pengembalianService.getAllPengembalian();
        log.info("Request completed", kv("action", "GET_ALL"), kv("status", "SUCCESS"), kv("count", result.size()));
        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PengembalianModel> getPengembalianById(@PathVariable Long id) {
        log.info("Request received", kv("action", "GET_BY_ID"), kv("id", id));
        PengembalianModel pengembalian = pengembalianService.getPengembalianById(id);
        if (pengembalian != null) {
            log.info("Request completed", kv("action", "GET_BY_ID"), kv("status", "SUCCESS"), kv("id", id));
            return ResponseEntity.ok(pengembalian);
        } else {
            log.warn("Request completed", kv("action", "GET_BY_ID"), kv("status", "NOT_FOUND"), kv("id", id));
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/peminjaman/{id}")
    public List<ResponseTemplate> getPengembalianWithPeminjamanById(@PathVariable Long id) {
        log.info("Request received", kv("action", "GET_WITH_PEMINJAMAN"), kv("id", id));
        List<ResponseTemplate> result = pengembalianService.getPengembalianWithPeminjamanById(id);
        log.info("Request completed", kv("action", "GET_WITH_PEMINJAMAN"), kv("status", "SUCCESS"),
                kv("count", result.size()));
        return result;
    }

    @PostMapping
    public PengembalianModel createPengembalian(@RequestBody PengembalianModel pengembalian) {
        log.info("Request received", kv("action", "CREATE"), kv("peminjamanId", pengembalian.getPeminjamanId()));
        PengembalianModel result = pengembalianService.createPengembalian(pengembalian);
        log.info("Request completed", kv("action", "CREATE"), kv("status", "SUCCESS"), kv("id", result.getId()));
        return result;
    }

    @PostMapping("/proses")
    public ResponseEntity<PengembalianModel> prosesPengembalian(@RequestBody PengembalianRequest request) {
        log.info("Request received", kv("action", "PROSES_PENGEMBALIAN"), kv("idAnggota", request.getIdAnggota()));

        if (request.getIdAnggota() == null || request.getTanggalKembali() == null) {
            log.warn("Request completed", kv("action", "PROSES_PENGEMBALIAN"), kv("status", "BAD_REQUEST"),
                    kv("reason", "MISSING_FIELDS"));
            return ResponseEntity.badRequest().build();
        }

        PengembalianModel result = pengembalianService.prosesPengembalian(
                request.getIdAnggota(),
                request.getTanggalKembali());

        if (result == null) {
            log.warn("Request completed", kv("action", "PROSES_PENGEMBALIAN"), kv("status", "NOT_FOUND"),
                    kv("idAnggota", request.getIdAnggota()));
            return ResponseEntity.notFound().build();
        }

        log.info("Request completed", kv("action", "PROSES_PENGEMBALIAN"), kv("status", "SUCCESS"),
                kv("id", result.getId()), kv("denda", result.getDenda()));
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PengembalianModel> updatePengembalian(@PathVariable Long id,
            @RequestBody PengembalianModel pengembalian) {
        log.info("Request received", kv("action", "UPDATE"), kv("id", id));
        PengembalianModel updated = pengembalianService.updatePengembalian(id, pengembalian);
        if (updated != null) {
            log.info("Request completed", kv("action", "UPDATE"), kv("status", "SUCCESS"), kv("id", id));
            return ResponseEntity.ok(updated);
        } else {
            log.warn("Request completed", kv("action", "UPDATE"), kv("status", "NOT_FOUND"), kv("id", id));
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePengembalian(@PathVariable Long id) {
        log.info("Request received", kv("action", "DELETE"), kv("id", id));
        pengembalianService.deletePengembalian(id);
        log.info("Request completed", kv("action", "DELETE"), kv("status", "SUCCESS"), kv("id", id));
        return ResponseEntity.ok().build();
    }
}
