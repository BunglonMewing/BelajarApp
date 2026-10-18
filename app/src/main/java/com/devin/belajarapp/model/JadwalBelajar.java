package com.devin.belajarapp.model;

import java.io.Serializable;

public class JadwalBelajar implements Serializable {
    private int id;
    private String namaSesi;       // contoh: "Belajar Matematika"
    private String hari;           // SENIN, SELASA, dst
    private int jamMulai;
    private int menitMulai;
    private int jamSelesai;
    private int menitSelesai;
    private boolean aktif;

    public JadwalBelajar() {}

    public JadwalBelajar(int id, String namaSesi, String hari,
                         int jamMulai, int menitMulai,
                         int jamSelesai, int menitSelesai) {
        this.id = id;
        this.namaSesi = namaSesi;
        this.hari = hari;
        this.jamMulai = jamMulai;
        this.menitMulai = menitMulai;
        this.jamSelesai = jamSelesai;
        this.menitSelesai = menitSelesai;
        this.aktif = true;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNamaSesi() { return namaSesi; }
    public void setNamaSesi(String namaSesi) { this.namaSesi = namaSesi; }

    public String getHari() { return hari; }
    public void setHari(String hari) { this.hari = hari; }

    public int getJamMulai() { return jamMulai; }
    public void setJamMulai(int jamMulai) { this.jamMulai = jamMulai; }

    public int getMenitMulai() { return menitMulai; }
    public void setMenitMulai(int menitMulai) { this.menitMulai = menitMulai; }

    public int getJamSelesai() { return jamSelesai; }
    public void setJamSelesai(int jamSelesai) { this.jamSelesai = jamSelesai; }

    public int getMenitSelesai() { return menitSelesai; }
    public void setMenitSelesai(int menitSelesai) { this.menitSelesai = menitSelesai; }

    public boolean isAktif() { return aktif; }
    public void setAktif(boolean aktif) { this.aktif = aktif; }

    public String getWaktuFormatted() {
        return String.format("%02d:%02d - %02d:%02d", jamMulai, menitMulai, jamSelesai, menitSelesai);
    }

    public int getDurasiMenit() {
        int totalMulai = jamMulai * 60 + menitMulai;
        int totalSelesai = jamSelesai * 60 + menitSelesai;
        return totalSelesai - totalMulai;
    }
}
