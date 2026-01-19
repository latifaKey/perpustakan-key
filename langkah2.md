# 📖 Panduan Lengkap Teknologi - Microservice Perpustakaan

> **Terakhir diperbarui**: Januari 2026  
> Panduan komprehensif mencakup versi, cara kerja, dan panduan menjalankan semua teknologi.

---

## 📑 Daftar Isi

1. [Arsitektur & Versi Teknologi](#1-arsitektur--versi-teknologi)
2. [CQRS Pattern](#2-cqrs-command-query-responsibility-segregation)
3. [RabbitMQ Event-Driven](#3-rabbitmq-event-driven-architecture)
4. [Structured Logging & ELK Stack](#4-structured-logging--elk-stack)
5. [Distributed Tracing](#5-distributed-tracing-micrometer)
6. [Prometheus & Grafana](#6-prometheus--grafana-monitoring)
7. [Spring Boot Actuator](#7-spring-boot-actuator)
8. [Jenkins CI/CD](#8-jenkins-cicd)
9. [Panduan Menjalankan Sistem](#9-panduan-menjalankan-sistem)
10. [Troubleshooting](#10-troubleshooting)
11. [Panduan Update](#11-panduan-update)

---

## 1. Arsitektur & Versi Teknologi

### 🏗️ Arsitektur Microservices

| Service | Port | Deskripsi |
|---------|------|-----------|
| **Eureka Server** | 8761 | Service Discovery |
| **API Gateway** | 9000 | Entry Point |
| **Anggota Service** | 8081 | Member Management |
| **Buku Service** | 8082 | Book Management |
| **Peminjaman Service** | 8083 | Borrowing (with CQRS) |
| **Pengembalian Service** | 8084 | Return Management |

### ☕ Backend Technologies

| Teknologi | Versi Proyek | Versi Terbaru | Status |
|-----------|--------------|---------------|--------|
| **Java** | 17 LTS | 21 LTS | ✅ Stay on 17 |
| **Spring Boot** | 3.3.5 | 3.4.1 | ⚠️ Update tersedia |
| **Spring Cloud Eureka** | 4.1.1 | 4.2.0 | ⚠️ Update tersedia |
| **Lombok** | Managed | 1.18.30+ | ✅ Stable |
| **Logstash Encoder** | 7.4 | 8.0 | 🔴 Update recommended |

**Dependencies (pom.xml)**:
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.5</version>
</parent>

<properties>
    <java.version>17</java.version>
</properties>
```

### ⚛️ Frontend Technologies

| Teknologi | Versi Proyek | Versi Terbaru | Status |
|-----------|--------------|---------------|--------|
| **React** | 18.2.0 | 18.3.1 | 🔴 Update recommended |
| **Vite** | 5.0.8 | 6.0.5 | ⚠️ Wait for stability |
| **TypeScript** | 5.3.3 | 5.7.2 | 🟡 Update tersedia |
| **Tailwind CSS** | 3.3.6 | 4.0.0 | ⚠️ Wait for stability |
| **Axios** | 1.6.2 | 1.7.9 | 🔴 Update recommended |

### 🐳 Infrastructure

| Teknologi | Versi Proyek | Versi Terbaru | Port |
|-----------|--------------|---------------|------|
| **RabbitMQ** | 3-management-alpine | 3.13.7 | 5672, 15672 |
| **Elasticsearch** | 8.11.0 | 8.17.0 | 9200 |
| **Logstash** | 8.11.0 | 8.17.0 | 5000 |
| **Kibana** | 8.11.0 | 8.17.0 | 5601 |
| **Prometheus** | latest | 3.8.1 | 9090 |
| **Grafana** | latest | 12.3.0 | 3000 |
| **H2 Database** | Auto-managed | - | In-memory |

---

## 2. CQRS (Command Query Responsibility Segregation)

### 📌 Pengertian

CQRS memisahkan operasi **write (Command)** dan **read (Query)** menjadi model berbeda untuk scalability dan maintainability.

### 📂 Lokasi Implementasi

**Service**: `peminjaman` (Port 8083)

```
peminjaman/src/main/java/com/pail/peminjaman/application/
├── Command.java / Query.java           # Interfaces
├── CommandHandler.java / QueryHandler.java
├── commands/
│   ├── CreatePeminjamanCommand.java
│   ├── CreatePeminjamanHandler.java
│   ├── UpdatePeminjamanCommand.java
│   └── DeletePeminjamanCommand.java
└── queries/
    ├── GetAllPeminjamanQuery.java
    ├── GetPeminjamanByIdQuery.java
    └── GetPeminjamanWithDetailsQuery.java
```

### 💻 Cara Kerja

**Command Flow (Write)**:
```java
@PostMapping
public ResponseEntity<CommandResult> createPeminjaman(@RequestBody PeminjamanModel peminjaman) {
    CreatePeminjamanCommand command = new CreatePeminjamanCommand(peminjaman);
    CommandResult result = createHandler.handle(command);
    return ResponseEntity.status(201).body(result);
}
```

**Query Flow (Read)**:
```java
@GetMapping("/anggota/{id}")
public List<ResponseTemplate> getPeminjamanWithDetails(@PathVariable Long id) {
    GetPeminjamanWithDetailsQuery query = new GetPeminjamanWithDetailsQuery(id);
    return queryHandler.handle(query);
}
```

### 🧪 Testing CQRS

```bash
# Start service
cd peminjaman && mvn spring-boot:run

# Test COMMAND - Create
curl -X POST http://localhost:8083/api/peminjaman \
  -H "Content-Type: application/json" \
  -d '{"anggotaId": 1, "bukuId": 1, "tanggal_pinjam": "2025-12-26", "tanggal_kembali": "2026-01-02"}'

# Test QUERY - Get All
curl http://localhost:8083/api/peminjaman
```

---

## 3. RabbitMQ Event-Driven Architecture

### 📌 Pengertian

RabbitMQ adalah message broker untuk komunikasi asynchronous antar service menggunakan event publishing/subscribing.

### 📂 Lokasi Implementasi

- **Producer**: `peminjaman/service/RabbitMQProducerService.java`
- **Consumer**: `peminjaman/service/RabbitMQConsumerService.java`
- **Config**: `peminjaman/config/RabbitMQConfig.java`

### ⚙️ Konfigurasi

**application.yml**:
```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest

app:
  rabbitmq:
    exchange: library_exchange
    routingkey: library_routing_key
    queue: library_queue
```

### 🧪 Testing RabbitMQ

```bash
# Start RabbitMQ
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management-alpine

# Access UI: http://localhost:15672 (guest/guest)

# Create peminjaman (triggers event)
curl -X POST http://localhost:8083/api/peminjaman \
  -H "Content-Type: application/json" \
  -d '{"anggotaId": 1, "bukuId": 1, "tanggal_pinjam": "2025-12-26"}'

# Check RabbitMQ UI → Queues → library_queue
```

---

## 4. Structured Logging & ELK Stack

### 📌 Pengertian

- **Structured Logging**: Log format JSON untuk parsing dan searching
- **ELK Stack**: Elasticsearch + Logstash + Kibana untuk centralized logging

### 📂 Lokasi Implementasi

- `*/src/main/resources/logback-spring.xml` - Logback configuration
- `logstash/pipeline/logstash.conf` - Logstash pipeline
- `docker-compose.yaml` - ELK services

### 💻 Arsitektur ELK

```
┌──────────────┐   TCP:5000   ┌───────────┐   HTTP   ┌──────────────┐   HTTP   ┌────────┐
│ Microservice │─────────────>│ Logstash  │─────────>│Elasticsearch │<─────────│ Kibana │
│  (JSON logs) │              │           │          │   (Storage)  │          │  (UI)  │
└──────────────┘              └───────────┘          └──────────────┘          └────────┘
```

### 🧪 Testing ELK

```bash
# Start ELK Stack
docker-compose up -d elasticsearch logstash kibana

# Wait 60-90 seconds, then verify
curl http://localhost:9200/_cluster/health  # Expected: {"status":"yellow"}

# Start service with ELK profile
mvn spring-boot:run -Dspring-boot.run.profiles=elk

# Generate logs
for i in {1..20}; do curl http://localhost:8081/api/anggota; sleep 0.5; done

# Access Kibana: http://localhost:5601
# Create Data View: logstash-perpustakaan-*
```

---

## 5. Distributed Tracing (Micrometer)

### 📌 Pengertian

Tracking request flow across multiple services menggunakan unique trace ID.

### 📂 Dependencies

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>
<dependency>
    <groupId>io.zipkin.reporter2</groupId>
    <artifactId>zipkin-reporter-brave</artifactId>
</dependency>
```

### 💻 Cara Kerja

```
Request → Peminjaman (TraceId: abc123, SpanId: span1)
              ↓
          Anggota (TraceId: abc123, SpanId: span2) ← Same trace!
              ↓
          Buku (TraceId: abc123, SpanId: span3)    ← Same trace!
```

### 🧪 Testing

```bash
# Start all services, then:
curl -X POST http://localhost:8083/api/peminjaman -H "Content-Type: application/json" \
  -d '{"anggotaId": 1, "bukuId": 1}'

# Check logs - TraceId identical across services!
# In Kibana: traceId: "abc123"
```

---

## 6. Prometheus & Grafana Monitoring

### 📌 Pengertian

- **Prometheus**: Time-series database untuk metrics collection
- **Grafana**: Visualization platform untuk metrics dashboards

### 📂 Dependencies

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
    <scope>runtime</scope>
</dependency>
```

### ⚙️ Konfigurasi

**prometheus.yml**:
```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'spring-boot-services'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets:
        - 'anggota-service:8081'
        - 'buku-service:8082'
        - 'peminjaman-service:8083'
        - 'pengembalian-service:8084'
```

### 🧪 Testing

```bash
# Start monitoring stack
docker-compose up -d prometheus grafana

# Verify Prometheus: http://localhost:9090/targets

# Access Grafana: http://localhost:3000 (admin/admin)
# Add datasource: Prometheus → http://prometheus:9090

# Verify metrics endpoint
curl http://localhost:8081/actuator/prometheus | head -20
```

---

## 7. Spring Boot Actuator

### 📌 Endpoints

| Endpoint | URL | Description |
|----------|-----|-------------|
| Health | `/actuator/health` | Service health status |
| Metrics | `/actuator/metrics` | Available metrics list |
| Prometheus | `/actuator/prometheus` | Prometheus format metrics |

### ⚙️ Configuration

```properties
management.endpoints.web.exposure.include=health,metrics,httpexchanges,prometheus
management.endpoint.health.show-details=always
management.metrics.tags.application=${spring.application.name}
```

### 🧪 Testing

```bash
curl http://localhost:8081/actuator/health | jq
# Expected: {"status":"UP","components":{...}}

curl http://localhost:8081/actuator/metrics | jq '.names'
```

---

## 8. Jenkins CI/CD

### 📌 Pengertian

Automation server untuk Continuous Integration/Continuous Deployment.

### 📂 Lokasi

**File**: `Jenkinsfile` di root repository

### 🚀 Setup Jenkins (Step by Step)

#### Step 1: Start Jenkins

```bash
# Via Docker Compose (sudah include di docker-compose.yaml)
docker compose up -d jenkins

# Get initial password
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword

# Access: http://localhost:8080
```

#### Step 2: Install Plugins

Setelah login pertama kali:
1. Pilih **"Install suggested plugins"** → tunggu selesai
2. Buat admin user (misal: `admin/admin`)
3. Klik **"Start using Jenkins"**

**Plugin tambahan (Manage Jenkins → Plugins → Available):**
- Docker Pipeline
- Pipeline: Stage View

#### Step 3: Konfigurasi Tools

Buka **Manage Jenkins → Tools** lalu konfigurasi:

**JDK Installation:**
| Field | Value |
|-------|-------|
| Name | `JDK17` |
| Install automatically | ✅ Check |
| Version | `jdk-17.0.x+xx` |

**Maven Installation:**
| Field | Value |
|-------|-------|
| Name | `Maven` |
| Install automatically | ✅ Check |
| Version | `3.9.x` |

> ⚠️ **PENTING**: Nama tools **HARUS** sama persis dengan yang ada di `Jenkinsfile`: `JDK17` dan `Maven`

#### Step 4: Buat Pipeline Job

1. Klik **"+ New Item"** di sidebar
2. Masukkan nama: `perpustakaan-pipeline`
3. Pilih **"Pipeline"** → Klik OK
4. Scroll ke **Pipeline** section:

| Field | Value |
|-------|-------|
| Definition | `Pipeline script from SCM` |
| SCM | `Git` |
| Repository URL | `file:///d:/Downloads/service-perpustakaan-fix-main` (local) atau URL GitHub |
| Branch | `*/main` atau `*/master` |
| Script Path | `Jenkinsfile` |

5. Klik **Save**

#### Step 5: Run Pipeline

1. Klik job `perpustakaan-pipeline`
2. Klik **"Build Now"**
3. Monitor di **Build History** → klik nomor build → **Console Output**

### 📋 Pipeline Stages

Pipeline Jenkinsfile akan menjalankan:

```
📥 Checkout       → Clone repository
🔨 Build          → mvn clean package (4 services parallel)
🧪 Test           → mvn test (4 services parallel)
🐳 Infrastructure → docker compose up rabbitmq, elasticsearch
📊 ELK Stack      → docker compose up logstash, kibana
📈 Monitoring     → docker compose up prometheus, grafana
🏥 Health Check   → Verifikasi semua service
✅ Display URLs   → Tampilkan akses URLs
```

### 🔧 Troubleshooting

| Problem | Solution |
|---------|----------|
| `mvn not found` | Pastikan Maven dikonfigurasi dengan nama `Maven` |
| `JDK not found` | Pastikan JDK dikonfigurasi dengan nama `JDK17` |
| `docker not found` | Install Docker Desktop dan restart Jenkins |
| Build failed on Windows | Pastikan menggunakan `bat` bukan `sh` di Jenkinsfile |

### 🧪 Verifikasi

```bash
# Cek Jenkins running
curl http://localhost:8080/login

# Cek pipeline status via API
curl http://localhost:8080/job/perpustakaan-pipeline/lastBuild/api/json
```

---

## 9. Panduan Menjalankan Sistem

### 🚀 Quick Start (Docker Compose)

```bash
cd d:\Downloads\service-perpustakaan-fix-main

# Start all
docker compose up -d

# OR step by step:
# 1. Infrastructure (30s)
docker start my-rabbitmq

# 2. Service Discovery (20s) Eureka
Start dari Springboot Dashboard

# 3. Backend Services (60s)
docker-compose up -d anggota-service buku-service peminjaman-service pengembalian-service

# 4. Gateway (15s)
docker-compose up -d api-gateway

# 5. Monitoring (60s)
docker-compose up -d logstash kibana prometheus grafana

```
### 🖥️ Local Development

```bash
# Run each service individually
cd anggota && mvn spring-boot:run
cd buku && mvn spring-boot:run
cd peminjaman && mvn spring-boot:run
cd pengembalian && mvn spring-boot:run
```

### 🎯 Health Check URLs

| Service | URL | Expected |
|---------|-----|----------|
| Eureka | http://localhost:8761 | Dashboard |
| Anggota | http://localhost:8081/actuator/health | `{"status":"UP"}` |
| Buku | http://localhost:8082/actuator/health | `{"status":"UP"}` |
| Peminjaman | http://localhost:8083/actuator/health | `{"status":"UP"}` |
| Pengembalian | http://localhost:8084/actuator/health | `{"status":"UP"}` |
| RabbitMQ | http://localhost:15672 | Management UI |
| Kibana | http://localhost:5601 | Kibana UI |
| Prometheus | http://localhost:9090 | Prometheus UI |
| Grafana | http://localhost:3000 | Grafana Login |
| Frontend | http://localhost:5173 | React App |

---

## 10. Troubleshooting

### Backend Services Not Starting

```bash
# Check Java version (should be 17+)
java -version

# Clean build
mvn clean install

# Check port conflicts
netstat -ano | findstr :8081

# Check logs
docker-compose logs anggota-service
```

### RabbitMQ Connection Failed

```bash
# Check RabbitMQ running
docker ps | grep rabbitmq

# Restart
docker-compose restart rabbitmq

# Verify credentials in application.yml
```

### Frontend Can't Connect to Backend

```bash
# Check backend running
curl http://localhost:8081/api/anggota

# Check CORS configuration in backend
```

### Elasticsearch Yellow Health

```bash
# Yellow is OK for single-node
curl http://localhost:9200/_cluster/health
```bv

---

## 11. Panduan Update

### 🔴 High Priority (Security)

```bash
# Axios
cd perpustakaan-frontend && npm install axios@1.7.9

# React
npm install react@18.3.1 react-dom@18.3.1
```

### 🟡 Medium Priority (Stability)

```yaml
# Docker images (docker-compose.yaml)
elasticsearch: docker.elastic.co/elasticsearch/elasticsearch:8.17.0
rabbitmq: rabbitmq:3.13.7-management-alpine
```

### 📋 Testing Checklist After Update

- [ ] Service startup tanpa error
- [ ] REST API endpoints working
- [ ] RabbitMQ message flow working
- [ ] Frontend builds successfully
- [ ] Logs appearing in Kibana
- [ ] Metrics visible in Grafana

---

## 📊 Summary

| # | Teknologi | Purpose | Status |
|---|-----------|---------|--------|
| 1 | CQRS | Separation of write/read | ✅ Implemented |
| 2 | RabbitMQ | Event-driven async | ✅ Implemented |
| 3 | Structured Logging | JSON format logs | ✅ Implemented |
| 4 | ELK Stack | Centralized logging | ✅ Implemented |
| 5 | Distributed Tracing | Cross-service tracking | ✅ Implemented |
| 6 | Prometheus & Grafana | Metrics monitoring | ✅ Implemented |
| 7 | Spring Boot Actuator | Health & metrics | ✅ Implemented |
| 8 | Jenkins CI/CD | Automated build | ✅ Implemented |
