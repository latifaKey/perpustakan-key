# 🚀 Panduan Testing dengan Postman

Berikut adalah panduan lengkap untuk melakukan testing API Microservices Perpustakaan menggunakan Postman.

## 📋 Persiapan

1.  Pastikan semua service berjalan (`docker-compose up -d`).
2.  Import collection ini ke Postman atau buat request manual sesuai panduan di bawah.
3.  **Base URL**: `http://localhost:9000` (API Gateway) atau langsung ke port service masing-masing jika API Gateway tidak aktif.

---

## 1️⃣ Anggota Service
**Port Langsung**: `8081`
**Base Path**: `/api/anggota`

### a. Tambah Anggota (POST)
*   **URL**: `http://localhost:8081/api/anggota`
*   **Method**: `POST`
*   **Body** (JSON):
    ```json
    {
        "nim": "2024001",
        "nama": "Latifa Keysha",
        "alamat": "Jl. Teknologi No. 10",
        "email": "keysha@example.com",
        "jenis_kelamin": "P"
    }
    ```
    *Catatan: `jenis_kelamin` hanya menerima nilai `"L"` (Laki-laki) atau `"P"` (Perempuan).*

### b. Lihat Semua Anggota (GET)
*   **URL**: `http://localhost:8081/api/anggota`
*   **Method**: `GET`

### c. Lihat Anggota by ID (GET)
*   **URL**: `http://localhost:8081/api/anggota/{id}` (contoh: `/api/anggota/1`)
*   **Method**: `GET`

### d. Update Anggota (PUT)
*   **URL**: `http://localhost:8081/api/anggota/{id}`
*   **Method**: `PUT`
*   **Body** (JSON):
    ```json
    {
        "nim": "2024001",
        "nama": "Keysha Updated",
        "alamat": "Jl. Baru No. 5",
        "email": "keysha.updated@example.com",
        "jenis_kelamin": "P"
    }
    ```

### e. Hapus Anggota (DELETE)
*   **URL**: `http://localhost:8081/api/anggota/{id}`
*   **Method**: `DELETE`

---

## 2️⃣ Buku Service
**Port Langsung**: `8082`
**Base Path**: `/api/buku`

### a. Tambah Buku (POST)
*   **URL**: `http://localhost:8082/api/buku`
*   **Method**: `POST`
*   **Body** (JSON):
    ```json
    {
        "judul": "Belajar Microservices dengan Spring Boot",
        "pengarang": "keycantik",
        "penerbit": "Tech Press",
        "tahun_terbit": 2024
    }
    ```

### b. Lihat Semua Buku (GET)
*   **URL**: `http://localhost:8082/api/buku`
*   **Method**: `GET`

### c. Lihat Buku by ID (GET)
*   **URL**: `http://localhost:8082/api/buku/{id}`
*   **Method**: `GET`

### d. Update Buku (PUT)
*   **URL**: `http://localhost:8082/api/buku/{id}`
*   **Method**: `PUT`
*   **Body** (JSON):
    ```json
    {
        "judul": "Belajar Microservices Edisi 2",
        "pengarang": "keycantik",
        "penerbit": "Tech Press",
        "tahun_terbit": 2025
    }
    ```

### e. Hapus Buku (DELETE)
*   **URL**: `http://localhost:8082/api/buku/{id}`
*   **Method**: `DELETE`

---

## 3️⃣ Peminjaman Service
**Port Langsung**: `8083`
**Base Path**: `/api/peminjaman`

### a. Buat Peminjaman (POST)
*   **URL**: `http://localhost:8083/api/peminjaman`
*   **Method**: `POST`
*   **Body** (JSON):
    ```json
    {
        "anggotaId": 1,
        "bukuId": 1,
        "tanggal_pinjam": "2024-01-20",
        "tanggal_kembali": "2024-01-27"
    }
    ```
    *Catatan: Format tanggal `YYYY-MM-DD`.*

### b. Lihat Semua Peminjaman (GET)
*   **URL**: `http://localhost:8083/api/peminjaman`
*   **Method**: `GET`

### c. Lihat Peminjaman by ID (GET)
*   **URL**: `http://localhost:8083/api/peminjaman/{id}`
*   **Method**: `GET`

### d. Lihat Peminjaman Detail dengan Info Anggota (GET)
*   **URL**: `http://localhost:8083/api/peminjaman/anggota/{id}`
*   **Method**: `GET`
*   **Keterangan**: Mengambil detail peminjaman beserta data anggota.

### e. Lihat Peminjaman by Anggota ID (GET)
*   **URL**: `http://localhost:8083/api/peminjaman/by-anggota/{anggotaId}`
*   **Method**: `GET`

### f. Update Peminjaman (PUT)
*   **URL**: `http://localhost:8083/api/peminjaman/{id}`
*   **Method**: `PUT`
*   **Body** (JSON):
    ```json
    {
        "anggotaId": 1,
        "bukuId": 1,
        "tanggal_pinjam": "2024-01-20",
        "tanggal_kembali": "2024-02-03"
    }
    ```

### g. Hapus Peminjaman (DELETE)
*   **URL**: `http://localhost:8083/api/peminjaman/{id}`
*   **Method**: `DELETE`

---

## 4️⃣ Pengembalian Service
**Port Langsung**: `8084`
**Base Path**: `/api/pengembalian`

### a. Buat Pengembalian Manual (POST)
*   **URL**: `http://localhost:8084/api/pengembalian`
*   **Method**: `POST`
*   **Body** (JSON):
    ```json
    {
        "peminjamanId": 1,
        "tanggal_dikembalikan": "2024-01-28"
    }
    ```

### b. Proses Pengembalian Otomatis (POST)
*   **URL**: `http://localhost:8084/api/pengembalian/proses`
*   **Method**: `POST`
*   **Body** (JSON):
    ```json
    {
        "idAnggota": 1,
        "tanggalKembali": "2024-01-28"
    }
    ```
    *Catatan: Sistem akan otomatis menghitung denda jika terlambat (Rp 2000/hari).*

### c. Lihat Semua Pengembalian (GET)
*   **URL**: `http://localhost:8084/api/pengembalian`
*   **Method**: `GET`

### d. Lihat Pengembalian by ID (GET)
*   **URL**: `http://localhost:8084/api/pengembalian/{id}`
*   **Method**: `GET`

### e. Lihat Pengembalian dengan Detail Peminjaman (GET)
*   **URL**: `http://localhost:8084/api/pengembalian/peminjaman/{id}`
*   **Method**: `GET`

---

## 🧪 Skenario Testing Lengkap

Untuk memastikan sistem berjalan lancar, ikuti urutan ini:

1.  **Buat Anggota**: POST ke `/api/anggota` (Catat ID, misal: 1).
2.  **Buat Buku**: POST ke `/api/buku` (Catat ID, misal: 1).
3.  **Pinjam Buku**: POST ke `/api/peminjaman` dengan `anggotaId: 1` dan `bukuId: 1`.
4.  **Cek Peminjaman**: GET ke `/api/peminjaman` untuk melihat daftar peminjaman.
5.  **Kembalikan Buku**: POST ke `/api/pengembalian/proses` dengan `idAnggota: 1`.
6.  **Cek Pengembalian**: GET ke `/api/pengembalian` untuk melihat hasil termasuk denda.
