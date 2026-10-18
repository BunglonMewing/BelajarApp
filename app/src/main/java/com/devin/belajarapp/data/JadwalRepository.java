package com.devin.belajarapp.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.devin.belajarapp.model.JadwalBelajar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JadwalRepository {
    private static final String PREF_NAME = "jadwal_belajar";
    private static final String KEY_JADWAL_LIST = "jadwal_list";
    private static final String KEY_NEXT_ID = "next_id";

    private final SharedPreferences prefs;

    public JadwalRepository(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public List<JadwalBelajar> getAllJadwal() {
        List<JadwalBelajar> list = new ArrayList<>();
        String json = prefs.getString(KEY_JADWAL_LIST, "[]");
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                JadwalBelajar j = new JadwalBelajar(
                        obj.getInt("id"),
                        obj.getString("namaSesi"),
                        obj.getString("hari"),
                        obj.getInt("jamMulai"),
                        obj.getInt("menitMulai"),
                        obj.getInt("jamSelesai"),
                        obj.getInt("menitSelesai")
                );
                j.setAktif(obj.getBoolean("aktif"));
                list.add(j);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void simpanJadwal(JadwalBelajar jadwal) {
        List<JadwalBelajar> list = getAllJadwal();

        // assign id jika baru
        if (jadwal.getId() == 0) {
            int nextId = prefs.getInt(KEY_NEXT_ID, 1);
            jadwal.setId(nextId);
            prefs.edit().putInt(KEY_NEXT_ID, nextId + 1).apply();
        }

        // update jika sudah ada
        boolean found = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == jadwal.getId()) {
                list.set(i, jadwal);
                found = true;
                break;
            }
        }
        if (!found) list.add(jadwal);

        saveAll(list);
    }

    public void hapusJadwal(int id) {
        List<JadwalBelajar> list = getAllJadwal();
        list.removeIf(j -> j.getId() == id);
        saveAll(list);
    }

    public void toggleAktif(int id) {
        List<JadwalBelajar> list = getAllJadwal();
        for (JadwalBelajar j : list) {
            if (j.getId() == id) {
                j.setAktif(!j.isAktif());
                break;
            }
        }
        saveAll(list);
    }

    private void saveAll(List<JadwalBelajar> list) {
        JSONArray arr = new JSONArray();
        for (JadwalBelajar j : list) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("id", j.getId());
                obj.put("namaSesi", j.getNamaSesi());
                obj.put("hari", j.getHari());
                obj.put("jamMulai", j.getJamMulai());
                obj.put("menitMulai", j.getMenitMulai());
                obj.put("jamSelesai", j.getJamSelesai());
                obj.put("menitSelesai", j.getMenitSelesai());
                obj.put("aktif", j.isAktif());
                arr.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        prefs.edit().putString(KEY_JADWAL_LIST, arr.toString()).apply();
    }
}
