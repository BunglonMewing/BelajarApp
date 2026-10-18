package com.devin.belajarapp.data;

import android.content.Context;

import com.devin.belajarapp.model.JadwalBelajar;

import java.util.List;

public class ChatbotEngine {

    public enum State {
        IDLE,
        MENUNGGU_NAMA_SESI,
        MENUNGGU_HARI,
        MENUNGGU_JAM_MULAI,
        MENUNGGU_JAM_SELESAI,
        MENUNGGU_KONFIRMASI
    }

    private State state = State.IDLE;
    private JadwalBelajar jadwalTemp;
    private final JadwalRepository repository;
    private final Context context;

    // Callback ke UI
    public interface Callback {
        void onReply(String pesan);
        void onJadwalDisimpan(JadwalBelajar jadwal);
    }

    private final Callback callback;

    public ChatbotEngine(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
        this.repository = new JadwalRepository(context);
    }

    public void proses(String inputUser) {
        String input = inputUser.trim().toLowerCase();

        switch (state) {
            case IDLE:
                prosesIdle(input, inputUser);
                break;
            case MENUNGGU_NAMA_SESI:
                jadwalTemp.setNamaSesi(inputUser.trim());
                state = State.MENUNGGU_HARI;
                callback.onReply("Oke, sesi \"" + inputUser.trim() + "\" ya!\n\nBelajarnya hari apa? Ketik salah satu:\nSenin, Selasa, Rabu, Kamis, Jumat, Sabtu, Minggu");
                break;
            case MENUNGGU_HARI:
                prosesHari(inputUser.trim());
                break;
            case MENUNGGU_JAM_MULAI:
                prosesJamMulai(inputUser.trim());
                break;
            case MENUNGGU_JAM_SELESAI:
                prosesJamSelesai(inputUser.trim());
                break;
            case MENUNGGU_KONFIRMASI:
                prosesKonfirmasi(input);
                break;
        }
    }

    private void prosesIdle(String input, String inputAsli) {
        if (containsAny(input, "buat", "tambah", "jadwal baru", "set jadwal", "bikin")) {
            jadwalTemp = new JadwalBelajar();
            state = State.MENUNGGU_NAMA_SESI;
            callback.onReply("Oke, kita buat jadwal belajar baru!\n\nNama sesinya apa? Contoh: Matematika, Fisika, Bahasa Inggris");

        } else if (containsAny(input, "lihat", "list", "jadwalku", "jadwal saya", "tampil")) {
            tampilkanJadwal();

        } else if (containsAny(input, "hapus", "delete", "hilangkan")) {
            callback.onReply("Untuk hapus jadwal, buka halaman Jadwal Saya dan tekan ikon hapus di jadwal yang ingin dihapus.");

        } else if (containsAny(input, "motivasi", "semangat", "inspirasi")) {
            callback.onReply(MotivasiProvider.getMotivasiRandom());

        } else if (containsAny(input, "halo", "hai", "hi", "hello")) {
            callback.onReply("Halo! Aku asisten belajarmu.\n\nAku bisa bantu kamu:\n- Buat jadwal belajar (ketik \"buat jadwal\")\n- Lihat jadwal (ketik \"lihat jadwal\")\n- Kasih motivasi (ketik \"motivasi\")\n\nMau ngapain dulu?");

        } else if (containsAny(input, "help", "bantuan", "cara", "gimana", "bisa apa")) {
            callback.onReply("Aku bisa bantu kamu:\n\n1. Ketik \"buat jadwal\" - untuk tambah jadwal belajar baru\n2. Ketik \"lihat jadwal\" - untuk lihat semua jadwalmu\n3. Ketik \"motivasi\" - untuk dapat kata-kata penyemangat\n\nJadwal yang sudah dibuat akan otomatis kirim notifikasi saat waktunya tiba!");

        } else {
            callback.onReply("Hmm, aku kurang ngerti maksudnya.\n\nCoba ketik:\n- \"buat jadwal\" - untuk tambah jadwal baru\n- \"lihat jadwal\" - untuk lihat jadwalmu\n- \"motivasi\" - untuk kata semangat");
        }
    }

    private void prosesHari(String inputHari) {
        String hariUpper = inputHari.toUpperCase().trim();
        String[] hariValid = {"SENIN", "SELASA", "RABU", "KAMIS", "JUMAT", "SABTU", "MINGGU"};
        boolean valid = false;
        for (String h : hariValid) {
            if (hariUpper.equals(h) || h.contains(hariUpper)) {
                jadwalTemp.setHari(h);
                valid = true;
                break;
            }
        }

        if (!valid) {
            callback.onReply("Nama hari tidak dikenali. Coba ketik: Senin, Selasa, Rabu, Kamis, Jumat, Sabtu, atau Minggu");
            return;
        }

        state = State.MENUNGGU_JAM_MULAI;
        callback.onReply("Oke, hari " + jadwalTemp.getHari() + "!\n\nMulai jam berapa? Ketik dalam format HH:MM\nContoh: 08:00 atau 19:30");
    }

    private void prosesJamMulai(String input) {
        int[] waktu = parseWaktu(input);
        if (waktu == null) {
            callback.onReply("Format jam salah. Ketik dalam format HH:MM, contoh: 08:00");
            return;
        }
        jadwalTemp.setJamMulai(waktu[0]);
        jadwalTemp.setMenitMulai(waktu[1]);
        state = State.MENUNGGU_JAM_SELESAI;
        callback.onReply("Mulai jam " + String.format("%02d:%02d", waktu[0], waktu[1]) + ". Selesai jam berapa?\n\nContoh: 10:00");
    }

    private void prosesJamSelesai(String input) {
        int[] waktu = parseWaktu(input);
        if (waktu == null) {
            callback.onReply("Format jam salah. Ketik dalam format HH:MM, contoh: 10:00");
            return;
        }

        int totalMulai = jadwalTemp.getJamMulai() * 60 + jadwalTemp.getMenitMulai();
        int totalSelesai = waktu[0] * 60 + waktu[1];

        if (totalSelesai <= totalMulai) {
            callback.onReply("Jam selesai harus lebih dari jam mulai! Coba lagi.");
            return;
        }

        jadwalTemp.setJamSelesai(waktu[0]);
        jadwalTemp.setMenitSelesai(waktu[1]);

        int durasi = totalSelesai - totalMulai;
        int jam = durasi / 60;
        int menit = durasi % 60;
        String durasiStr = jam > 0 ? jam + " jam" : "";
        if (menit > 0) durasiStr += (durasiStr.isEmpty() ? "" : " ") + menit + " menit";

        state = State.MENUNGGU_KONFIRMASI;
        callback.onReply("Ini ringkasan jadwalmu:\n\n" +
                "Sesi: " + jadwalTemp.getNamaSesi() + "\n" +
                "Hari: " + jadwalTemp.getHari() + "\n" +
                "Waktu: " + jadwalTemp.getWaktuFormatted() + "\n" +
                "Durasi: " + durasiStr + "\n\n" +
                "Simpan jadwal ini? (ketik \"ya\" atau \"tidak\")");
    }

    private void prosesKonfirmasi(String input) {
        if (input.equals("ya") || input.equals("iya") || input.equals("yep") || input.equals("yes")) {
            repository.simpanJadwal(jadwalTemp);
            AlarmScheduler.jadwalkan(context, jadwalTemp);
            callback.onJadwalDisimpan(jadwalTemp);
            callback.onReply("Jadwal berhasil disimpan! Alarm sudah disetel.\n\nKamu akan dapat notifikasi motivasi setiap kali sesi \"" + jadwalTemp.getNamaSesi() + "\" dimulai dan tiap jam selama sesi berlangsung. Semangat persiapan kuliahnya!");
            jadwalTemp = null;
            state = State.IDLE;
        } else if (input.equals("tidak") || input.equals("gak") || input.equals("no") || input.equals("nope")) {
            jadwalTemp = null;
            state = State.IDLE;
            callback.onReply("Oke, jadwal tidak disimpan. Mau buat jadwal baru? Ketik \"buat jadwal\".");
        } else {
            callback.onReply("Ketik \"ya\" untuk simpan atau \"tidak\" untuk batal.");
        }
    }

    private void tampilkanJadwal() {
        List<JadwalBelajar> list = repository.getAllJadwal();
        if (list.isEmpty()) {
            callback.onReply("Kamu belum punya jadwal belajar.\n\nKetik \"buat jadwal\" untuk mulai!");
            return;
        }
        StringBuilder sb = new StringBuilder("Jadwal Belajarmu:\n\n");
        for (JadwalBelajar j : list) {
            sb.append(j.isAktif() ? "" : "[nonaktif] ");
            sb.append(j.getNamaSesi()).append("\n");
            sb.append(j.getHari()).append(", ").append(j.getWaktuFormatted()).append("\n\n");
        }
        sb.append("Untuk kelola jadwal, buka halaman Jadwal Saya.");
        callback.onReply(sb.toString().trim());
    }

    private int[] parseWaktu(String input) {
        try {
            String[] parts = input.split("[:.]");
            if (parts.length != 2) return null;
            int jam = Integer.parseInt(parts[0].trim());
            int menit = Integer.parseInt(parts[1].trim());
            if (jam < 0 || jam > 23 || menit < 0 || menit > 59) return null;
            return new int[]{jam, menit};
        } catch (Exception e) {
            return null;
        }
    }

    private boolean containsAny(String input, String... keywords) {
        for (String kw : keywords) {
            if (input.contains(kw)) return true;
        }
        return false;
    }
}
