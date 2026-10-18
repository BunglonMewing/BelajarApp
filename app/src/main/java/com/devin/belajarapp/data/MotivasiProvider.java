package com.devin.belajarapp.data;

import java.util.Random;

public class MotivasiProvider {

    // Motivasi saat sesi belajar DIMULAI
    private static final String[] MOTIVASI_MULAI = {
        "Waktunya fokus! Setiap menit belajar hari ini adalah investasi buat kursi kuliahmu.",
        "Kelas 10 adalah pondasi. Bangun sekarang, nikmati hasilnya di seleksi masuk kuliah.",
        "Sainganmu juga kelas 10. Bedanya, kamu sudah mulai hari ini.",
        "PTN impianmu menunggu. Buktikan kamu layak dengan belajar sekarang!",
        "Buka bukunya, mulai fokusnya. Perjalanan ribuan mil dimulai dari satu langkah.",
        "Waktu belajar dimulai! Bayangkan kamu sudah pakai almamater kampus impianmu.",
        "Semangat! Orang yang berhasil masuk kuliah impian bukan yang paling pintar, tapi yang paling konsisten.",
        "Ayo mulai! Belajar 1 jam sehari sama dengan 365 jam setahun. Itu sangat berarti.",
        "Satu sesi belajar ini mungkin kecil, tapi akumulasinya yang akan mengubah hidupmu.",
        "Fokus sekarang, bebas nanti. Ayo mulai sesi belajarnya!"
    };

    // Motivasi pengingat TIAP JAM selama sesi
    private static final String[] MOTIVASI_TIAP_JAM = {
        "Sudah sejam berlalu! Kamu hebat, pertahankan fokusnya.",
        "1 jam lagi terlewati. Sainganmu mungkin sudah menyerah, tapi kamu belum!",
        "Pengingat sejam: tetap di jalur. Kuliah impian tidak datang sendiri.",
        "Jangan sampai pikiranmu melantur. Balik fokus, masih ada waktu tersisa!",
        "Sudah sejam nih. Minum air dulu, lalu lanjut. Kamu bisa!",
        "Wah, sudah sejam! Itu bukan hal kecil. Lanjutkan perjalananmu.",
        "Tiap jam yang kamu selesaikan adalah satu langkah lebih dekat ke PTN impianmu.",
        "Sejam belajar konsisten lebih bernilai dari 3 jam belajar setengah-setengah.",
        "Ini pengingatmu: masih ada sesi yang tersisa. Tetap semangat!",
        "Udah capek? Wajar. Tapi ingat tujuanmu. Sedikit lagi, kamu pasti bisa."
    };

    // Motivasi saat sesi SELESAI
    private static final String[] MOTIVASI_SELESAI = {
        "Sesi belajar selesai! Kamu luar biasa. Istirahat yang cukup ya.",
        "Done! Kamu sudah selangkah lebih dekat ke kuliah impianmu hari ini.",
        "Keren! Konsistensi seperti ini yang akan membedakan kamu dari yang lain.",
        "Sesi selesai. Bangga sama diri sendiri, karena nggak semua orang se-disiplin kamu.",
        "Belajar hari ini sudah selesai. Besok, ulangi lagi. Itu kuncinya."
    };

    private static final Random random = new Random();

    public static String getMotivasiMulai(String namaSesi) {
        String motivasi = MOTIVASI_MULAI[random.nextInt(MOTIVASI_MULAI.length)];
        return "Sesi \"" + namaSesi + "\" dimulai!\n\n" + motivasi;
    }

    public static String getMotivasiTiapJam() {
        return MOTIVASI_TIAP_JAM[random.nextInt(MOTIVASI_TIAP_JAM.length)];
    }

    public static String getMotivasiSelesai(String namaSesi) {
        String motivasi = MOTIVASI_SELESAI[random.nextInt(MOTIVASI_SELESAI.length)];
        return "Sesi \"" + namaSesi + "\" selesai!\n\n" + motivasi;
    }

    public static String getMotivasiRandom() {
        // Untuk chatbot
        String[] all = new String[MOTIVASI_MULAI.length + MOTIVASI_TIAP_JAM.length];
        System.arraycopy(MOTIVASI_MULAI, 0, all, 0, MOTIVASI_MULAI.length);
        System.arraycopy(MOTIVASI_TIAP_JAM, 0, all, MOTIVASI_MULAI.length, MOTIVASI_TIAP_JAM.length);
        return all[random.nextInt(all.length)];
    }
}
