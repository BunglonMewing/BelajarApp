package com.devin.belajarapp.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.devin.belajarapp.data.AlarmScheduler;
import com.devin.belajarapp.data.JadwalRepository;
import com.devin.belajarapp.databinding.ActivityJadwalBinding;
import com.devin.belajarapp.model.JadwalBelajar;

import java.util.List;

public class JadwalActivity extends AppCompatActivity implements JadwalAdapter.Listener {

    private ActivityJadwalBinding binding;
    private JadwalRepository repository;
    private JadwalAdapter adapter;
    private List<JadwalBelajar> jadwalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJadwalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Jadwal Belajar Saya");
        }

        repository = new JadwalRepository(this);
        loadJadwal();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadJadwal();
    }

    private void loadJadwal() {
        jadwalList = repository.getAllJadwal();
        if (jadwalList.isEmpty()) {
            binding.tvKosong.setVisibility(View.VISIBLE);
            binding.rvJadwal.setVisibility(View.GONE);
        } else {
            binding.tvKosong.setVisibility(View.GONE);
            binding.rvJadwal.setVisibility(View.VISIBLE);
        }

        adapter = new JadwalAdapter(jadwalList, this);
        binding.rvJadwal.setLayoutManager(new LinearLayoutManager(this));
        binding.rvJadwal.setAdapter(adapter);
    }

    @Override
    public void onToggleAktif(JadwalBelajar jadwal) {
        repository.toggleAktif(jadwal.getId());
        if (jadwal.isAktif()) {
            AlarmScheduler.batalkan(this, jadwal.getId());
        } else {
            AlarmScheduler.jadwalkan(this, jadwal);
        }
        loadJadwal();
    }

    @Override
    public void onHapus(JadwalBelajar jadwal) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Jadwal")
                .setMessage("Hapus jadwal \"" + jadwal.getNamaSesi() + "\"?")
                .setPositiveButton("Hapus", (d, w) -> {
                    AlarmScheduler.batalkan(this, jadwal.getId());
                    repository.hapusJadwal(jadwal.getId());
                    loadJadwal();
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
