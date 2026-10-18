package com.devin.belajarapp.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.devin.belajarapp.data.ChatbotEngine;
import com.devin.belajarapp.databinding.ActivityChatbotBinding;
import com.devin.belajarapp.model.ChatMessage;
import com.devin.belajarapp.model.JadwalBelajar;

import java.util.ArrayList;
import java.util.List;

public class ChatbotActivity extends AppCompatActivity {

    private ActivityChatbotBinding binding;
    private ChatAdapter adapter;
    private List<ChatMessage> messages = new ArrayList<>();
    private ChatbotEngine engine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatbotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chatbot Jadwal Belajar");
        }

        setupRecyclerView();
        setupEngine();

        // Pesan pembuka
        addBotMessage("Halo! Aku asisten belajarmu.\n\nAku bisa bantu kamu:\n- Buat jadwal belajar (ketik \"buat jadwal\")\n- Lihat jadwal (ketik \"lihat jadwal\")\n- Kasih motivasi (ketik \"motivasi\")\n\nMau mulai dari mana?");

        binding.btnKirim.setOnClickListener(v -> kirimPesan());
        binding.etInput.setOnEditorActionListener((v, actionId, event) -> {
            kirimPesan();
            return true;
        });
    }

    private void setupRecyclerView() {
        adapter = new ChatAdapter(messages);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        binding.rvChat.setLayoutManager(lm);
        binding.rvChat.setAdapter(adapter);
    }

    private void setupEngine() {
        engine = new ChatbotEngine(this, new ChatbotEngine.Callback() {
            @Override
            public void onReply(String pesan) {
                runOnUiThread(() -> addBotMessage(pesan));
            }

            @Override
            public void onJadwalDisimpan(JadwalBelajar jadwal) {
                // Notifikasi sukses sudah di-handle via onReply
            }
        });
    }

    private void kirimPesan() {
        String teks = binding.etInput.getText().toString().trim();
        if (teks.isEmpty()) return;

        addUserMessage(teks);
        binding.etInput.setText("");
        engine.proses(teks);
    }

    private void addUserMessage(String teks) {
        messages.add(new ChatMessage(teks, ChatMessage.TYPE_USER));
        adapter.notifyItemInserted(messages.size() - 1);
        scrollToBottom();
    }

    private void addBotMessage(String teks) {
        messages.add(new ChatMessage(teks, ChatMessage.TYPE_BOT));
        adapter.notifyItemInserted(messages.size() - 1);
        scrollToBottom();
    }

    private void scrollToBottom() {
        binding.rvChat.scrollToPosition(messages.size() - 1);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
