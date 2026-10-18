package com.devin.belajarapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devin.belajarapp.R;
import com.devin.belajarapp.model.JadwalBelajar;

import java.util.List;

public class JadwalAdapter extends RecyclerView.Adapter<JadwalAdapter.ViewHolder> {

    public interface Listener {
        void onToggleAktif(JadwalBelajar jadwal);
        void onHapus(JadwalBelajar jadwal);
    }

    private final List<JadwalBelajar> list;
    private final Listener listener;

    public JadwalAdapter(List<JadwalBelajar> list, Listener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_jadwal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JadwalBelajar jadwal = list.get(position);
        holder.tvNama.setText(jadwal.getNamaSesi());
        holder.tvHari.setText(jadwal.getHari());
        holder.tvWaktu.setText(jadwal.getWaktuFormatted());

        int durasi = jadwal.getDurasiMenit();
        int jam = durasi / 60;
        int menit = durasi % 60;
        String durasiStr = jam > 0 ? jam + " jam" : "";
        if (menit > 0) durasiStr += (durasiStr.isEmpty() ? "" : " ") + menit + " menit";
        holder.tvDurasi.setText(durasiStr);

        holder.swAktif.setChecked(jadwal.isAktif());
        holder.swAktif.setOnCheckedChangeListener(null);
        holder.swAktif.setOnCheckedChangeListener((btn, checked) ->
                listener.onToggleAktif(jadwal));

        holder.btnHapus.setOnClickListener(v -> listener.onHapus(jadwal));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvHari, tvWaktu, tvDurasi;
        Switch swAktif;
        ImageButton btnHapus;

        ViewHolder(View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvHari = itemView.findViewById(R.id.tvHari);
            tvWaktu = itemView.findViewById(R.id.tvWaktu);
            tvDurasi = itemView.findViewById(R.id.tvDurasi);
            swAktif = itemView.findViewById(R.id.swAktif);
            btnHapus = itemView.findViewById(R.id.btnHapus);
        }
    }
}
