package com.key.peminjaman.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.key.peminjaman.dto.PeminjamanEventDTO;

/**
 * Email Service untuk mengirim notifikasi ke anggota perpustakaan
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Mengirim notifikasi email untuk peminjaman baru
     */
    public void sendPeminjamanNotification(PeminjamanEventDTO.PeminjamanData data) {
        try {
            String toEmail = data.getAnggota().getEmail();
            String namaAnggota = data.getAnggota().getNama();
            String judulBuku = data.getBuku() != null ? data.getBuku().getJudul() : "Buku";
            String tanggalPinjam = data.getTanggalPinjam() != null ? data.getTanggalPinjam().toString() : "-";
            String tanggalKembali = data.getTanggalKembali() != null ? data.getTanggalKembali().toString() : "-";

            if (toEmail == null || toEmail.isEmpty()) {
                log.warn("⚠️ Email anggota tidak tersedia, skip pengiriman email");
                return;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("📚 Notifikasi Peminjaman Buku - Perpustakaan");
            message.setText(buildPeminjamanEmailBody(namaAnggota, judulBuku, tanggalPinjam, tanggalKembali));

            mailSender.send(message);
            log.info("✅ Email peminjaman berhasil dikirim ke: {}", toEmail);
        } catch (Exception e) {
            log.error("❌ Gagal mengirim email peminjaman: {}", e.getMessage());
        }
    }

    private String buildPeminjamanEmailBody(String nama, String judulBuku, String tanggalPinjam,
            String tanggalKembali) {
        return String.format("""
                Halo %s,

                Terima kasih telah meminjam buku di Perpustakaan kami.

                📖 Detail Peminjaman:
                - Judul Buku: %s
                - Tanggal Pinjam: %s
                - Tanggal Harus Kembali: %s

                Harap kembalikan buku tepat waktu untuk menghindari denda.

                Salam,
                Tim Perpustakaan
                """, nama, judulBuku, tanggalPinjam, tanggalKembali);
    }
}
