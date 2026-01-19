package com.key.peminjaman.application.queries;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.key.peminjaman.application.Query;
import com.key.peminjaman.application.QueryHandler;
import com.key.peminjaman.dto.PeminjamanDTO;
import com.key.peminjaman.repository.PeminjamanRepository;

/**
 * Query for getting Peminjaman by Anggota ID
 * Returns DTO list (CQRS compliant)
 */
public class GetPeminjamanByAnggotaIdQuery implements Query<List<PeminjamanDTO>> {
    private final Long anggotaId;

    public GetPeminjamanByAnggotaIdQuery(Long anggotaId) {
        this.anggotaId = anggotaId;
    }

    public Long getAnggotaId() {
        return anggotaId;
    }
}

/**
 * Handler for GetPeminjamanByAnggotaIdQuery
 */
@Component
class GetPeminjamanByAnggotaIdHandler implements QueryHandler<GetPeminjamanByAnggotaIdQuery, List<PeminjamanDTO>> {

    @Autowired
    private PeminjamanRepository peminjamanRepository;

    @Override
    public List<PeminjamanDTO> handle(GetPeminjamanByAnggotaIdQuery query) {
        return peminjamanRepository.findByAnggotaId(query.getAnggotaId()).stream()
                .map(PeminjamanDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
