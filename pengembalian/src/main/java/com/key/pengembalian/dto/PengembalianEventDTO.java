package com.key.pengembalian.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event DTO untuk notifikasi pengembalian buku
 * Dikirim ke RabbitMQ setelah pengembalian berhasil diproses
 */
public class PengembalianEventDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String eventType;
    private LocalDateTime eventTime;
    private String correlationId;
    private PengembalianData data;

    public PengembalianEventDTO() {
    }

    public PengembalianEventDTO(String eventType, PengembalianData data) {
        this.eventType = eventType;
        this.eventTime = LocalDateTime.now();
        this.correlationId = UUID.randomUUID().toString();
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

    public PengembalianData getData() {
        return data;
    }

    public void setData(PengembalianData data) {
        this.data = data;
    }

    /**
     * Inner class untuk data payload pengembalian
     */
    public static class PengembalianData implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long pengembalianId;
        private Long peminjamanId;
        private LocalDate tanggalDikembalikan;
        private Integer terlambat;
        private Double denda;
        private AnggotaInfo anggota;
        private BukuInfo buku;

        public PengembalianData() {
        }

        // Getters and Setters
        public Long getPengembalianId() {
            return pengembalianId;
        }

        public void setPengembalianId(Long pengembalianId) {
            this.pengembalianId = pengembalianId;
        }

        public Long getPeminjamanId() {
            return peminjamanId;
        }

        public void setPeminjamanId(Long peminjamanId) {
            this.peminjamanId = peminjamanId;
        }

        public LocalDate getTanggalDikembalikan() {
            return tanggalDikembalikan;
        }

        public void setTanggalDikembalikan(LocalDate tanggalDikembalikan) {
            this.tanggalDikembalikan = tanggalDikembalikan;
        }

        public Integer getTerlambat() {
            return terlambat;
        }

        public void setTerlambat(Integer terlambat) {
            this.terlambat = terlambat;
        }

        public Double getDenda() {
            return denda;
        }

        public void setDenda(Double denda) {
            this.denda = denda;
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

    /**
     * Info anggota untuk email notification
     */
    public static class AnggotaInfo implements Serializable {
        private static final long serialVersionUID = 1L;

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

    /**
     * Info buku untuk email notification
     */
    public static class BukuInfo implements Serializable {
        private static final long serialVersionUID = 1L;

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
