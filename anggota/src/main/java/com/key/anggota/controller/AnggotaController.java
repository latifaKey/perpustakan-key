package com.key.anggota.controller;

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

import com.key.anggota.model.AnggotaModel;
import com.key.anggota.service.AnggotaService;

import static net.logstash.logback.argument.StructuredArguments.kv;

@RestController
@RequestMapping("/api/anggota")
public class AnggotaController {

    private static final Logger log = LoggerFactory.getLogger(AnggotaController.class);

    @Autowired
    private AnggotaService anggotaService;

    @GetMapping
    public List<AnggotaModel> getAllAnggota() {
        log.info("Request received", kv("action", "GET_ALL"));
        List<AnggotaModel> result = anggotaService.getAllAnggota();
        log.info("Request completed", kv("action", "GET_ALL"), kv("status", "SUCCESS"), kv("count", result.size()));
        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnggotaModel> getAnggotabyId(@PathVariable Long id) {
        log.info("Request received", kv("action", "GET_BY_ID"), kv("id", id));
        AnggotaModel anggota = anggotaService.getAnggotaById(id);
        if (anggota != null) {
            log.info("Request completed", kv("action", "GET_BY_ID"), kv("status", "SUCCESS"), kv("id", id));
            return ResponseEntity.ok(anggota);
        } else {
            log.warn("Request completed", kv("action", "GET_BY_ID"), kv("status", "NOT_FOUND"), kv("id", id));
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public AnggotaModel creteAnggota(@RequestBody AnggotaModel anggota) {
        log.info("Request received", kv("action", "CREATE"), kv("nama", anggota.getNama()));
        AnggotaModel result = anggotaService.createAnggota(anggota);
        log.info("Request completed", kv("action", "CREATE"), kv("status", "SUCCESS"), kv("id", result.getId()));
        return result;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnggota(@PathVariable Long id) {
        log.info("Request received", kv("action", "DELETE"), kv("id", id));
        anggotaService.deleteAnggota(id);
        log.info("Request completed", kv("action", "DELETE"), kv("status", "SUCCESS"), kv("id", id));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnggotaModel> updateAnggota(@PathVariable Long id, @RequestBody AnggotaModel anggota) {
        log.info("Request received", kv("action", "UPDATE"), kv("id", id));
        AnggotaModel updated = anggotaService.updateAnggota(id, anggota);
        if (updated != null) {
            log.info("Request completed", kv("action", "UPDATE"), kv("status", "SUCCESS"), kv("id", id));
            return ResponseEntity.ok(updated);
        } else {
            log.warn("Request completed", kv("action", "UPDATE"), kv("status", "NOT_FOUND"), kv("id", id));
            return ResponseEntity.notFound().build();
        }
    }
}
