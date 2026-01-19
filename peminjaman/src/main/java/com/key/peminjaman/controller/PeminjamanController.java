package com.key.peminjaman.controller;

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

import com.key.peminjaman.application.QueryHandler;
import com.key.peminjaman.application.commands.CreatePeminjamanCommand;
import com.key.peminjaman.application.commands.CreatePeminjamanHandler;
import com.key.peminjaman.application.commands.DeletePeminjamanCommand;
import com.key.peminjaman.application.commands.DeletePeminjamanHandler;
import com.key.peminjaman.application.commands.UpdatePeminjamanCommand;
import com.key.peminjaman.application.commands.UpdatePeminjamanHandler;
import com.key.peminjaman.application.queries.GetAllPeminjamanQuery;
import com.key.peminjaman.application.queries.GetPeminjamanByAnggotaIdQuery;
import com.key.peminjaman.application.queries.GetPeminjamanByIdQuery;
import com.key.peminjaman.application.queries.GetPeminjamanWithDetailsQuery;
import com.key.peminjaman.dto.CommandResult;
import com.key.peminjaman.dto.PeminjamanDTO;
import com.key.peminjaman.model.PeminjamanModel;
import com.key.peminjaman.vo.ResponseTemplate;

import static net.logstash.logback.argument.StructuredArguments.kv;

/**
 * CQRS-compliant Controller with Structured Logging
 * - Commands return CommandResult (ID + status)
 * - Queries return DTOs (not raw entities)
 */
@RestController
@RequestMapping("/api/peminjaman")
public class PeminjamanController {

    private static final Logger log = LoggerFactory.getLogger(PeminjamanController.class);

    // Command Handlers (Write operations)
    @Autowired
    private CreatePeminjamanHandler createHandler;

    @Autowired
    private UpdatePeminjamanHandler updateHandler;

    @Autowired
    private DeletePeminjamanHandler deleteHandler;

    // Query Handlers (Read operations)
    @Autowired
    private QueryHandler<GetAllPeminjamanQuery, List<PeminjamanDTO>> getAllHandler;

    @Autowired
    private QueryHandler<GetPeminjamanByIdQuery, PeminjamanDTO> getByIdHandler;

    @Autowired
    private QueryHandler<GetPeminjamanByAnggotaIdQuery, List<PeminjamanDTO>> getByAnggotaIdHandler;

    @Autowired
    private QueryHandler<GetPeminjamanWithDetailsQuery, List<ResponseTemplate>> getWithDetailsHandler;

    // ==================== QUERIES (Read) ====================

    @GetMapping
    public List<PeminjamanDTO> getAllPeminjaman() {
        log.info("Query received", kv("action", "GET_ALL"), kv("type", "QUERY"));
        List<PeminjamanDTO> result = getAllHandler.handle(new GetAllPeminjamanQuery());
        log.info("Query completed", kv("action", "GET_ALL"), kv("status", "SUCCESS"), kv("count", result.size()));
        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeminjamanDTO> getPeminjamanById(@PathVariable Long id) {
        log.info("Query received", kv("action", "GET_BY_ID"), kv("type", "QUERY"), kv("id", id));
        PeminjamanDTO peminjaman = getByIdHandler.handle(new GetPeminjamanByIdQuery(id));
        if (peminjaman != null) {
            log.info("Query completed", kv("action", "GET_BY_ID"), kv("status", "SUCCESS"), kv("id", id));
            return ResponseEntity.ok(peminjaman);
        } else {
            log.warn("Query completed", kv("action", "GET_BY_ID"), kv("status", "NOT_FOUND"), kv("id", id));
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/anggota/{id}")
    public List<ResponseTemplate> getPeminjamanWithAnggotaById(@PathVariable Long id) {
        log.info("Query received", kv("action", "GET_WITH_DETAILS"), kv("type", "QUERY"), kv("peminjamanId", id));
        List<ResponseTemplate> result = getWithDetailsHandler.handle(new GetPeminjamanWithDetailsQuery(id));
        log.info("Query completed", kv("action", "GET_WITH_DETAILS"), kv("status", "SUCCESS"),
                kv("count", result.size()));
        return result;
    }

    @GetMapping(path = "/by-anggota/{anggotaId}")
    public ResponseEntity<List<PeminjamanDTO>> getPeminjamanByAnggotaId(@PathVariable Long anggotaId) {
        log.info("Query received", kv("action", "GET_BY_ANGGOTA_ID"), kv("type", "QUERY"), kv("anggotaId", anggotaId));
        List<PeminjamanDTO> peminjamanList = getByAnggotaIdHandler.handle(
                new GetPeminjamanByAnggotaIdQuery(anggotaId));
        if (peminjamanList.isEmpty()) {
            log.warn("Query completed", kv("action", "GET_BY_ANGGOTA_ID"), kv("status", "NOT_FOUND"),
                    kv("anggotaId", anggotaId));
            return ResponseEntity.notFound().build();
        }
        log.info("Query completed", kv("action", "GET_BY_ANGGOTA_ID"), kv("status", "SUCCESS"),
                kv("count", peminjamanList.size()));
        return ResponseEntity.ok(peminjamanList);
    }

    // ==================== COMMANDS (Write) ====================

    @PostMapping
    public ResponseEntity<CommandResult> createPeminjaman(@RequestBody PeminjamanModel peminjaman) {
        log.info("Command received", kv("action", "CREATE"), kv("type", "COMMAND"),
                kv("anggotaId", peminjaman.getAnggotaId()), kv("bukuId", peminjaman.getBukuId()));
        CommandResult result = createHandler.handle(new CreatePeminjamanCommand(peminjaman));
        if (result.isSuccess()) {
            log.info("Command completed", kv("action", "CREATE"), kv("status", "SUCCESS"), kv("id", result.getId()));
            return ResponseEntity.status(201).body(result);
        }
        log.error("Command failed", kv("action", "CREATE"), kv("status", "FAILED"), kv("error", result.getMessage()));
        return ResponseEntity.badRequest().body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommandResult> updatePeminjaman(@PathVariable Long id,
            @RequestBody PeminjamanModel peminjaman) {
        log.info("Command received", kv("action", "UPDATE"), kv("type", "COMMAND"), kv("id", id));
        CommandResult result = updateHandler.handle(new UpdatePeminjamanCommand(id, peminjaman));
        if (result != null && result.isSuccess()) {
            log.info("Command completed", kv("action", "UPDATE"), kv("status", "SUCCESS"), kv("id", id));
            return ResponseEntity.ok(result);
        }
        log.warn("Command completed", kv("action", "UPDATE"), kv("status", "NOT_FOUND"), kv("id", id));
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommandResult> deletePeminjaman(@PathVariable Long id) {
        log.info("Command received", kv("action", "DELETE"), kv("type", "COMMAND"), kv("id", id));
        CommandResult result = deleteHandler.handle(new DeletePeminjamanCommand(id));
        log.info("Command completed", kv("action", "DELETE"), kv("status", "SUCCESS"), kv("id", id));
        return ResponseEntity.ok(result);
    }
}
