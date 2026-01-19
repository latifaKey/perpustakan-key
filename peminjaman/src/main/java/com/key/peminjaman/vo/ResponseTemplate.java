package com.key.peminjaman.vo;

import com.key.peminjaman.model.PeminjamanModel;

public class ResponseTemplate {
    PeminjamanModel peminjaman;
    Anggota anggota;
    Buku buku;
    
    public ResponseTemplate() {
    }

    public ResponseTemplate(PeminjamanModel peminjaman, Anggota anggota, Buku buku) {
        this.peminjaman = peminjaman;
        this.anggota = anggota;
        this.buku = buku;
    }

    public PeminjamanModel getPeminjaman() {
        return peminjaman;
    }

    public void setPeminjaman(PeminjamanModel peminjaman) {
        this.peminjaman = peminjaman;
    }

    public Anggota getAnggota() {
        return anggota;
    }

    public void setAnggota(Anggota anggota) {
        this.anggota = anggota;
    }

    public Buku getBuku() {
        return buku;
    }

    public void setBuku(Buku buku) {
        this.buku = buku;
    }

}
