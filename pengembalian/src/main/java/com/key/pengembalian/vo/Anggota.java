package com.key.pengembalian.vo;

public class Anggota {
    private Long id;
    private String nim;
    private String nama;
    private String alamat;
    private String email;
    private String jenis_kelamin;

    public Anggota() {
    }

    public Anggota(Long id, String nim, String nama, String alamat, String email, String jenis_kelamin) {
        this.id = id;
        this.nim = nim;
        this.nama = nama;
        this.alamat = alamat;
        this.email = email;
        this.jenis_kelamin = jenis_kelamin;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setJenis_kelamin(String jenis_kelamin) {
        this.jenis_kelamin = jenis_kelamin;
    }

    public Long getId() {
        return id;
    }

    public String getNim() {
        return nim;
    }

    public String getNama() {
        return nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getEmail() {
        return email;
    }

    public String getJenis_kelamin() {
        return jenis_kelamin;
    }

    @Override
    public String toString() {
        return "Anggota{" +
                "id=" + id +
                ", nim='" + nim + '\'' +
                ", nama='" + nama + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
