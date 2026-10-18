# Belajar Yuk! - Aplikasi Pengingat Belajar

Aplikasi Android pengingat belajar dengan chatbot jadwal dan notifikasi motivasi bertema persiapan kuliah.

## Fitur

- **Chatbot Jadwal** - atur jadwal belajar mingguan lewat percakapan
- **Notifikasi Motivasi** - notif saat sesi mulai + tiap jam selama belajar
- **Kelola Jadwal** - aktif/nonaktif, hapus jadwal dari halaman Jadwal Saya
- **Auto-resume** - alarm otomatis diatur ulang setelah HP restart

## Cara Pakai Chatbot

Setelah buka aplikasi, tap "Chatbot Jadwal" lalu ketik:

- `buat jadwal` - mulai buat jadwal baru
- `lihat jadwal` - lihat semua jadwalmu
- `motivasi` - dapat kata semangat
- `help` - lihat semua perintah

Chatbot akan memandu step by step: nama sesi, hari, jam mulai, jam selesai.

## Build via GitHub Actions

### Setup repository

1. Push kode ini ke GitHub:
```bash
git init
git add .
git commit -m "Initial commit: Belajar Yuk app"
git remote add origin https://github.com/USERNAME/REPO_NAME.git
git push -u origin main
```

2. GitHub Actions otomatis berjalan setiap push ke `main`

3. Download APK dari tab **Actions** → pilih workflow run → **Artifacts** → `belajar-app-debug`

### Build manual (trigger dari GitHub)

1. Buka repository di GitHub
2. Tab **Actions**
3. Pilih workflow **Build APK**
4. Klik **Run workflow**

## Build lokal (opsional)

Butuh: Android Studio atau JDK 17 + Android SDK

```bash
./gradlew assembleDebug
# APK ada di: app/build/outputs/apk/debug/app-debug.apk
```

## Struktur Proyek

```
app/src/main/java/com/devin/belajarapp/
├── data/
│   ├── AlarmScheduler.java    - set/batalkan alarm AlarmManager
│   ├── ChatbotEngine.java     - logic percakapan chatbot
│   ├── JadwalRepository.java  - simpan/baca jadwal (SharedPreferences)
│   └── MotivasiProvider.java  - kumpulan kata motivasi
├── model/
│   ├── ChatMessage.java
│   └── JadwalBelajar.java
├── receiver/
│   ├── AlarmReceiver.java     - terima trigger alarm → tampilkan notif
│   └── BootReceiver.java      - reschedule setelah reboot
├── service/
│   └── BelajarService.java    - foreground service + alarm tiap jam
└── ui/
    ├── MainActivity.java
    ├── ChatbotActivity.java
    ├── ChatAdapter.java
    ├── JadwalActivity.java
    └── JadwalAdapter.java
```

## Persyaratan

- Android 8.0 (API 26) ke atas
- Izin notifikasi (diminta otomatis saat pertama buka)
