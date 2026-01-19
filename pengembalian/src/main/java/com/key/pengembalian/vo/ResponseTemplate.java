package com.key.pengembalian.vo;

import com.key.pengembalian.model.PengembalianModel;

public class ResponseTemplate {
    PengembalianModel pengembalian;
    Peminjaman peminjaman;
    Anggota anggota;
    Buku buku;
    
    public ResponseTemplate() {
    }

    public ResponseTemplate(PengembalianModel pengembalian, Peminjaman peminjaman, Anggota anggota, Buku buku) {
        this.pengembalian = pengembalian;
        this.peminjaman = peminjaman;
        this.anggota = anggota;
        this.buku = buku;
    }

    public PengembalianModel getPengembalian() {
        return pengembalian;
    }

    public void setPengembalian(PengembalianModel pengembalian) {
        this.pengembalian = pengembalian;
    }

    public Peminjaman getPeminjaman() {
        return peminjaman;
    }

    public void setPeminjaman(Peminjaman peminjaman) {
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
