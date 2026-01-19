package com.key.peminjaman.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PeminjamanEventDTO {
    private String eventType;
    private LocalDateTime eventTime;
    private String correlationId;
    private PeminjamanData data;

    public PeminjamanEventDTO() {
    }

    public PeminjamanEventDTO(String eventType, LocalDateTime eventTime, String correlationId, PeminjamanData data) {
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.correlationId = correlationId;
        this.data = data;
    }

    // Getters and Setters
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public PeminjamanData getData() {
        return data;
    }

    public void setData(PeminjamanData data) {
        this.data = data;
    }

    // Inner class for data payload
    public static class PeminjamanData {
        private Long peminjamanId;
        private LocalDate tanggalPinjam;
        private LocalDate tanggalKembali;
        private AnggotaInfo anggota;
        private BukuInfo buku;

        public PeminjamanData() {
        }

        // Getters and Setters
        public Long getPeminjamanId() {
            return peminjamanId;
        }

        public void setPeminjamanId(Long peminjamanId) {
            this.peminjamanId = peminjamanId;
        }

        public LocalDate getTanggalPinjam() {
            return tanggalPinjam;
        }

        public void setTanggalPinjam(LocalDate tanggalPinjam) {
            this.tanggalPinjam = tanggalPinjam;
        }

        public LocalDate getTanggalKembali() {
            return tanggalKembali;
        }

        public void setTanggalKembali(LocalDate tanggalKembali) {
            this.tanggalKembali = tanggalKembali;
        }

        public AnggotaInfo getAnggota() {
            return anggota;
        }

        public void setAnggota(AnggotaInfo anggota) {
            this.anggota = anggota;
        }

        public BukuInfo getBuku() {
            return buku;
        }

        public void setBuku(BukuInfo buku) {
            this.buku = buku;
        }
    }

    public static class AnggotaInfo {
        private Long id;
        private String nama;
        private String email;

        public AnggotaInfo() {
        }

        public AnggotaInfo(Long id, String nama, String email) {
            this.id = id;
            this.nama = nama;
            this.email = email;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNama() {
            return nama;
        }

        public void setNama(String nama) {
            this.nama = nama;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class BukuInfo {
        private Long id;
        private String judul;

        public BukuInfo() {
        }

        public BukuInfo(Long id, String judul) {
            this.id = id;
            this.judul = judul;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getJudul() {
            return judul;
        }

        public void setJudul(String judul) {
            this.judul = judul;
        }
    }
}
