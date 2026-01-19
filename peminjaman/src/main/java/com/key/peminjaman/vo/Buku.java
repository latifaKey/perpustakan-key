package com.key.peminjaman.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Value Object untuk data Buku dari service Buku
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Buku {
    private Long id;
    private String judul;
    private String pengarang;
    private String penerbit;
    private Integer tahun_terbit;

    public Buku() {
    }

    public Buku(Long id, String judul, String pengarang, String penerbit, Integer tahun_terbit) {
        this.id = id;
        this.judul = judul;
        this.pengarang = pengarang;
        this.penerbit = penerbit;
        this.tahun_terbit = tahun_terbit;
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

    public String getPengarang() {
        return pengarang;
    }

    public void setPengarang(String pengarang) {
        this.pengarang = pengarang;
    }

    public String getPenerbit() {
        return penerbit;
    }

    public void setPenerbit(String penerbit) {
        this.penerbit = penerbit;
    }

    public Integer getTahun_terbit() {
        return tahun_terbit;
    }

    public void setTahun_terbit(Integer tahun_terbit) {
        this.tahun_terbit = tahun_terbit;
    }

    @Override
    public String toString() {
        return "Buku{" +
                "id=" + id +
                ", judul='" + judul + '\'' +
                '}';
    }
}
