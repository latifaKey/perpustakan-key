package com.key.anggota.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class AnggotaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nim;
    private String nama;
    private String alamat;
    private String email;
    @Enumerated(EnumType.STRING)
    private jekel jenis_kelamin;

    // inisialisasi jenis kelamin
    public enum jekel {
        L, P
    };
}
