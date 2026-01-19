# Tutorial: Menjalankan Project dengan Docker dan Monitoring Grafana

Panduan ini akan membantu Anda menjalankan seluruh stack microservices, database, logging, dan monitoring menggunakan Docker Compose.

## 📋 Prasyarat

Pastikan Anda telah menginstal:
- **Docker Desktop** (untuk Windows/Mac) atau **Docker Engine** & **Docker Compose** (untuk Linux).
- Pastikan Docker daemon sedang berjalan.

## 🚀 Langkah 1: Menjalankan Aplikasi

1.  Buka terminal (Command Prompt, PowerShell, atau Terminal) di root folder project `ABL_PerpustakaanService`.
2.  Jalankan perintah berikut untuk membangun dan menjalankan semua container:

    ```bash
    docker-compose up -d --build
    ```

    *Opsi `-d` menjalankan container di background (detached mode).*
    *Opsi `--build` memastikan image dibangun ulang jika ada perubahan kode.*

3.  Tunggu beberapa saat hingga semua service berjalan. Proses ini mungkin memakan waktu beberapa menit saat pertama kali dijalankan karena perlu mendownload image dan build aplikasi.

## ✅ Langkah 2: Verifikasi Status

Untuk memastikan semua container berjalan dengan baik, jalankan:

```bash
docker ps
```

Anda harus melihat container berikut (status `Up`):
- `server-eureka`
- `rabbitmq`
- `elasticsearch`, `logstash`, `kibana` (ELK Stack)
- `anggota-service`, `buku-service`, `peminjaman-service`, `pengembalian-service`
- `api-gateway` (jika diaktifkan)
- `prometheus`, `grafana` (Monitoring Stack)
- `jenkins`

## 🌐 Langkah 3: Mengakses Layanan

Berikut adalah daftar URL untuk mengakses layanan-layanan utama:

| Layanan | URL | Keterangan |
| :--- | :--- | :--- |
| **Eureka Server** | [http://localhost:8761](http://localhost:8761) | Dashboard Service Discovery |
| **Grafana** | [http://localhost:3000](http://localhost:3000) | Dashboard Monitoring |
| **Prometheus** | [http://localhost:9090](http://localhost:9090) | Metrics Collection |
| **Kibana** | [http://localhost:5601](http://localhost:5601) | Log Visualization |
| **RabbitMQ** | [http://localhost:15672](http://localhost:15672) | Management UI (User: `guest`, Pass: `guest`) |
| **Jenkins** | [http://localhost:1080](http://localhost:1080) | CI/CD Server |

## 📊 Langkah 4: Menggunakan Grafana

Grafana digunakan untuk memvisualisasikan metrik dari aplikasi Spring Boot Anda.

1.  **Login ke Grafana**
    - Buka [http://localhost:3000](http://localhost:3000).
    - Masukkan kredensial default:
        - **Username:** `admin`
        - **Password:** `admin`
    - Anda mungkin diminta untuk mengganti password saat login pertama kali (bisa di-skip).

2.  **Melihat Dashboard**
    Project ini sudah dikonfigurasi dengan *provisioning*, artinya dashboard sudah otomatis dimuat.
    - Klik menu **Dashboards** di sidebar kiri.
    - Buka folder **General** atau folder yang tersedia.
    - Anda akan melihat dashboard berikut:
        - **Perpustakaan Complete Dashboard**: Overview lengkap sistem.
        - **Perpustakaan DB & API Dashboard**: Fokus pada performa database dan API.
        - **Spring Boot Dashboard**: Metrik standar JVM dan Spring Boot.

3.  **Eksplorasi Metrik**
    - **System Metrics**: CPU Usage, Memory Usage, Uptime.
    - **JVM Metrics**: Heap Memory, Garbage Collection, Threads.
    - **HTTP Metrics**: Request Rate, Error Rate, Response Time.
    - **Database Metrics**: Connection Pool usage, Query execution time.

## 🛠️ Troubleshooting

- **Container Gagal Start / Exit**:
    - Cek logs container yang bermasalah: `docker logs <nama-container>` (contoh: `docker logs anggota-service`).
    - Pastikan port tidak bentrok dengan aplikasi lain di komputer Anda.
- **Service Tidak Muncul di Eureka**:
    - Tunggu beberapa saat, service butuh waktu untuk register.
    - Pastikan `server-eureka` berjalan normal.
- **Grafana Tidak Menampilkan Data**:
    - Pastikan `prometheus` berjalan ([http://localhost:9090](http://localhost:9090)).
    - Cek target di Prometheus (Status -> Targets) untuk memastikan service Spring Boot terdeteksi (UP).

## 🛑 Langkah 5: Menghentikan Aplikasi

Untuk menghentikan dan menghapus semua container:

```bash
docker-compose down
```

Jika ingin menghapus volume data juga (reset database dan logs):

```bash
docker-compose down -v
```
