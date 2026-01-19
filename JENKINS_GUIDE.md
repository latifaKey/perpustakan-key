# 🚀 Panduan Lengkap Jenkins CI/CD

> **Terakhir diperbarui**: Januari 2026
> Panduan step-by-step untuk menggunakan Jenkins dan implementasi pipeline

---

## 📑 Daftar Isi

1. [Apa itu Jenkins?](#1-apa-itu-jenkins)
2. [Menjalankan Jenkins](#2-menjalankan-jenkins)
3. [Setup Awal Jenkins](#3-setup-awal-jenkins)
4. [Konfigurasi Tools](#4-konfigurasi-tools)
5. [Membuat Pipeline Job](#5-membuat-pipeline-job)
6. [Memahami Jenkinsfile](#6-memahami-jenkinsfile)
7. [Menjalankan Pipeline](#7-menjalankan-pipeline)
8. [Troubleshooting](#8-troubleshooting)
9. [GitHub Webhook - Auto Deploy](#9-github-webhook---auto-deploy-🔄)

---

## 1. Apa itu Jenkins?

Jenkins adalah **automation server** open-source yang digunakan untuk mengotomatisasi:

| Istilah | Penjelasan |
|---------|------------|
| **CI (Continuous Integration)** | Build & test code secara otomatis setiap ada perubahan |
| **CD (Continuous Deployment)** | Deploy aplikasi secara otomatis ke server |
| **Pipeline** | Serangkaian langkah otomatis dari kode → testing → deployment |

### Keuntungan:
- ✅ Otomatisasi proses build, test, dan deploy
- ✅ Deteksi error lebih awal
- ✅ Konsistensi dalam proses deployment
- ✅ Pipeline as Code (Jenkinsfile)

---

## 2. Menjalankan Jenkins

### 2.1 Start Jenkins Container

```powershell
# Di folder project
cd d:\Downloads\ArsitekturLayanan_PerpustakaanService-main\ArsitekturLayanan_PerpustakaanService-main

# Start Jenkins
docker compose up -d jenkins
```

### 2.2 Akses Jenkins

| Item | Nilai |
|------|-------|
| **URL** | `http://localhost:1080` |
| **Status** | Jenkins berjalan di port 1080 (mapping dari 8080 internal) |

### 2.3 Mendapatkan Password Awal

```powershell
# Cara 1: Dari docker logs
docker logs jenkins 2>&1 | Select-String -Pattern "password" -Context 2,5

# Cara 2: Langsung dari file
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

---

## 3. Setup Awal Jenkins

### Step 1: Unlock Jenkins
1. Buka `http://localhost:1080`
2. Masukkan **Administrator Password** dari langkah sebelumnya
3. Klik **Continue**

### Step 2: Install Plugins
1. Pilih **"Install suggested plugins"**
2. Tunggu proses instalasi (~3-5 menit)

### Step 3: Buat Admin User
| Field | Contoh Nilai |
|-------|--------------|
| Username | `admin` |
| Password | `admin123` |
| Confirm Password | `admin123` |
| Full Name | `Administrator` |
| Email | `admin@localhost` |

4. Klik **"Save and Continue"**

### Step 4: Instance Configuration
1. Biarkan URL default (`http://localhost:1080/`)
2. Klik **"Save and Finish"**
3. Klik **"Start using Jenkins"**

---

## 4. Konfigurasi Tools

### 4.1 Konfigurasi Maven (WAJIB!)

⚠️ **PENTING**: Nama tool harus **SAMA PERSIS** dengan yang ada di Jenkinsfile

1. Buka **Manage Jenkins** → **Tools**
2. Scroll ke **Maven installations**
3. Klik **"Add Maven"**
4. Isi:
   | Field | Nilai |
   |-------|-------|
   | Name | `MAVEN` |
   | Install automatically | ✅ Centang |
   | Version | `3.9.9` (atau terbaru) |
5. Klik **Save**

### 4.2 Verifikasi di Jenkinsfile

```groovy
tools {
    maven 'MAVEN'  // ← Nama ini HARUS sama dengan konfigurasi di Tools
}
```

---

## 5. Membuat Pipeline Job

### Step 1: Create New Item
1. Klik **"+ New Item"** di sidebar
2. Masukkan nama: `perpustakaan-microservices`
3. Pilih **"Pipeline"**
4. Klik **OK**

### Step 2: Configure Pipeline

#### Opsi A: Pipeline script from SCM (Recommended)

| Field | Nilai |
|-------|-------|
| Definition | `Pipeline script from SCM` |
| SCM | `Git` |
| Repository URL | Path lokal atau URL GitHub Anda |
| Branch | `*/main` atau `*/master` |
| Script Path | `Jenkinsfile` |

**Contoh Repository URL:**
```
# Lokal (Windows path format)
file:///c:/Users/Administrator/Downloads/Compressed/ArsitekturLayanan_PerpustakaanService-main/ArsitekturLayanan_PerpustakaanService-main

# GitHub
https://github.com/username/repository.git
```

#### Opsi B: Pipeline Script (Manual)

Copy-paste isi `Jenkinsfile` langsung ke text area.

5. Klik **Save**

---

## 6. Memahami Jenkinsfile

### 6.1 Struktur Dasar Jenkinsfile

```groovy
pipeline {
    agent any             // Di mana pipeline dijalankan
    
    tools {               // Tools yang digunakan
        maven 'MAVEN'
    }
    
    environment {         // Variabel environment (opsional)
        MY_VAR = 'value'
    }
    
    stages {              // Tahapan-tahapan pipeline
        stage('Stage Name') {
            steps {
                // Commands yang dijalankan
            }
        }
    }
    
    post {                // Actions setelah pipeline selesai
        success { ... }
        failure { ... }
        always  { ... }
    }
}
```

### 6.2 Jenkinsfile Project Ini

```
┌─────────────────────────────────────────────────────────────┐
│                    PIPELINE OVERVIEW                        │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  📥 Stage 1: Checkout                                       │
│     └── Clone repository dari SCM                           │
│                          │                                  │
│                          ▼                                  │
│  🔨 Stage 2: Build All Services (PARALLEL)                  │
│     ├── Build Anggota Service     (mvn clean package)       │
│     ├── Build Buku Service        (mvn clean package)       │
│     ├── Build Peminjaman Service  (mvn clean package)       │
│     └── Build Pengembalian Service(mvn clean package)       │
│                          │                                  │
│                          ▼                                  │
│  🧪 Stage 3: Test All Services (PARALLEL)                   │
│     ├── Test Anggota Service      (mvn test)                │
│     ├── Test Buku Service         (mvn test)                │
│     ├── Test Peminjaman Service   (mvn test)                │
│     └── Test Pengembalian Service (mvn test)                │
│                          │                                  │
│                          ▼                                  │
│  ✅ Post Actions                                            │
│     ├── Success → Display success message                   │
│     ├── Failure → Display failure message                   │
│     └── Always  → Display completion time                   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 6.3 Parallel Execution

```groovy
stage('Build All Services') {
    parallel {                    // ← Jalankan secara paralel
        stage('Build Anggota') { ... }
        stage('Build Buku') { ... }
        stage('Build Peminjaman') { ... }
        stage('Build Pengembalian') { ... }
    }
}
```

**Keuntungan**: 4 service di-build bersamaan → waktu lebih cepat!

---

## 7. Menjalankan Pipeline

### 7.1 Manual Build
1. Buka job `perpustakaan-microservices`
2. Klik **"Build Now"**
3. Monitor di **Build History**

### 7.2 Melihat Progress
1. Klik nomor build (misal **#1**)
2. Klik **"Console Output"** untuk log detail
3. Atau klik **"Pipeline Steps"** untuk view visual

### 7.3 Status Build

| Icon | Status | Artinya |
|------|--------|---------|
| 🔵 | Blue/Green | Success - Build dan test berhasil |
| 🔴 | Red | Failed - Ada error dalam build/test |
| 🟡 | Yellow | Unstable - Build sukses tapi ada warning |
| ⚪ | Grey | Aborted - Build dibatalkan |

---

## 8. Troubleshooting

### 8.1 "mvn: command not found"

**Solusi**: Konfigurasi Maven di Tools
```
Manage Jenkins → Tools → Maven installations → Add Maven
Name: MAVEN (harus sama dengan Jenkinsfile)
```

### 8.2 "Cannot checkout from SCM"

**Solusi untuk path lokal Windows**:
```
# Gunakan format file URL
file:///c:/path/to/your/project

# Bukan format Windows path
c:\path\to\your\project  ❌
```

### 8.3 "sh: command not found" (Windows)

**Solusi**: Ganti `sh` dengan `bat` di Jenkinsfile:
```groovy
// Untuk Linux/Jenkins container
sh 'mvn clean package'

// Untuk Windows
bat 'mvn clean package'
```

### 8.4 "Jenkins container cannot access Docker"

**Solusi**: Mount Docker socket ke container Jenkins:
```yaml
jenkins:
  volumes:
    - jenkins_home:/var/jenkins_home
    - /var/run/docker.sock:/var/run/docker.sock  # Tambahkan ini
```

### 8.5 Melihat Log & Debug

```powershell
# Log Jenkins container
docker logs jenkins

# Log dengan follow
docker logs -f jenkins

# Masuk ke container
docker exec -it jenkins bash
```

---

## 📋 Quick Reference

### Commands Cepat

```powershell
# Start Jenkins
docker compose up -d jenkins

# Stop Jenkins
docker compose stop jenkins

# Restart Jenkins
docker compose restart jenkins

# Lihat logs
docker logs jenkins

# Get initial password
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

### URLs Penting

| Service | URL |
|---------|-----|
| Jenkins | http://localhost:1080 |
| Eureka | http://localhost:8761 |
| RabbitMQ UI | http://localhost:15672 |
| Prometheus | http://localhost:9090 |
| Grafana | http://localhost:3000 |
| Kibana | http://localhost:5601 |

---

## 🎯 Best Practices

1. **Versioning Jenkinsfile**: Simpan Jenkinsfile di repository bersama kode
2. **Parallel Stages**: Gunakan parallel untuk mempercepat build
3. **Environment Variables**: Gunakan untuk configuration yang berbeda per environment
4. **Post Actions**: Selalu tambahkan cleanup dan notifikasi
5. **Artifact Archiving**: Archive hasil build untuk referensi

---

## 9. GitHub Webhook - Auto Deploy 🔄

### 9.1 Kenapa Perlu Webhook?

Dengan GitHub Webhook, setiap kali Anda **push** kode ke GitHub:
1. GitHub akan mengirim notifikasi ke Jenkins
2. Jenkins otomatis trigger build baru
3. Build, test, dan deploy berjalan otomatis

**Hasil**: Tidak perlu lagi klik "Build Now" manual!

### 9.2 Prasyarat

1. ✅ Repository di GitHub (public atau private)
2. ✅ Jenkins sudah berjalan
3. ✅ Plugin GitHub terinstall di Jenkins

### 9.3 Install Plugin GitHub

```
1. Manage Jenkins → Manage Plugins
2. Tab "Available"
3. Search: "GitHub Integration Plugin"
4. Install dan restart Jenkins
```

### 9.4 Konfigurasi Pipeline Job

1. Buka job `perpustakaan-microservices`
2. Klik **Configure**
3. Pastikan SCM mengarah ke GitHub:
   | Field | Nilai |
   |-------|-------|
   | Definition | `Pipeline script from SCM` |
   | SCM | `Git` |
   | Repository URL | `https://github.com/USERNAME/ABL_PerpustakaanService.git` |
   | Branch | `*/main` |
4. Di **Build Triggers**, centang:
   - ✅ **GitHub hook trigger for GITScm polling**
5. Klik **Save**

### 9.5 Setup Ngrok (Untuk Local Jenkins)

Karena Jenkins berjalan di localhost, GitHub tidak bisa mengakses langsung. Gunakan **ngrok** sebagai tunnel:

```powershell
# Download ngrok dari https://ngrok.com/download

# Jalankan tunnel ke Jenkins port
ngrok http 1080
```

Anda akan mendapat URL seperti:
```
Forwarding   https://abc123.ngrok.io -> http://localhost:1080
```

**Simpan URL ini!** Akan digunakan untuk webhook.

### 9.6 Konfigurasi Webhook di GitHub

1. Buka repository GitHub Anda
2. **Settings** → **Webhooks** → **Add webhook**
3. Isi form:

| Field | Nilai |
|-------|-------|
| **Payload URL** | `https://abc123.ngrok.io/github-webhook/` |
| **Content type** | `application/json` |
| **Secret** | (kosongkan atau buat secret) |
| **Events** | `Just the push event` |
| **Active** | ✅ Centang |

4. Klik **Add webhook**

### 9.7 Test Webhook

```powershell
# Di folder project
git add .
git commit -m "Test auto deploy"
git push origin main
```

Cek Jenkins - seharusnya ada build baru yang otomatis berjalan!

### 9.8 Diagram Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    AUTO DEPLOY FLOW                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  👨‍💻 Developer                                                   │
│     │                                                           │
│     │ git push origin main                                      │
│     ▼                                                           │
│  ┌──────────┐                                                   │
│  │  GitHub  │                                                   │
│  └────┬─────┘                                                   │
│       │                                                         │
│       │ POST /github-webhook/                                   │
│       ▼                                                         │
│  ┌──────────┐     ┌──────────┐                                  │
│  │  ngrok   │────▶│ Jenkins  │                                  │
│  └──────────┘     └────┬─────┘                                  │
│                        │                                        │
│                        │ Trigger Pipeline                       │
│                        ▼                                        │
│  ┌─────────────────────────────────────────────────────┐        │
│  │              JENKINS PIPELINE                        │        │
│  ├─────────────────────────────────────────────────────┤        │
│  │  1. 📥 Checkout code from GitHub                     │        │
│  │  2. 🔨 Build all services (parallel)                 │        │
│  │  3. 🧪 Run tests (parallel)                          │        │
│  │  4. 🐳 Build Docker images                           │        │
│  │  5. 🚀 Deploy with docker compose                    │        │
│  │  6. 🏥 Health check                                  │        │
│  └─────────────────────────────────────────────────────┘        │
│                        │                                        │
│                        ▼                                        │
│  ┌─────────────────────────────────────────────────────┐        │
│  │           DEPLOYED SERVICES                          │        │
│  ├─────────────────────────────────────────────────────┤        │
│  │  • Eureka Server     - http://localhost:8761         │        │
│  │  • API Gateway       - http://localhost:8080         │        │
│  │  • All Microservices - via API Gateway               │        │
│  │  • Prometheus        - http://localhost:9090         │        │
│  │  • Grafana           - http://localhost:3000         │        │
│  │  • Kibana            - http://localhost:5601         │        │
│  └─────────────────────────────────────────────────────┘        │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 9.9 Alternatif: Jenkins di Cloud/VPS

Jika Jenkins berjalan di server cloud (tidak localhost), tidak perlu ngrok:

| Jenis Server | Payload URL |
|--------------|-------------|
| VPS/Cloud | `http://YOUR_SERVER_IP:1080/github-webhook/` |
| Domain | `https://jenkins.yourdomain.com/github-webhook/` |

### 9.10 Troubleshooting Webhook

**❌ Webhook gagal (Delivery failed)**
```
- Pastikan ngrok masih berjalan
- Cek URL sudah benar (akhiri dengan /github-webhook/)
- Cek Jenkins logs: docker logs jenkins
```

**❌ Build tidak trigger otomatis**
```
- Pastikan "GitHub hook trigger for GITScm polling" sudah dicentang
- Cek plugin GitHub sudah terinstall
- Refresh halaman job dan cek ulang konfigurasi
```

**❌ Permission denied di Docker**
```
# Di Jenkins container, pastikan socket Docker bisa diakses
docker exec jenkins chmod 666 /var/run/docker.sock
```

---

## 🎯 Best Practices

1. **Versioning Jenkinsfile**: Simpan Jenkinsfile di repository bersama kode
2. **Parallel Stages**: Gunakan parallel untuk mempercepat build
3. **Environment Variables**: Gunakan untuk configuration yang berbeda per environment
4. **Post Actions**: Selalu tambahkan cleanup dan notifikasi
5. **Artifact Archiving**: Archive hasil build untuk referensi
6. **Webhook Security**: Gunakan secret token untuk webhook GitHub
7. **Health Checks**: Selalu verifikasi deployment berhasil sebelum selesai

---

**Happy CI/CD with Auto Deploy! 🚀**
