package com.example.truyenchu.fragment; // Thay bằng package của bạn

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.truyenchu.R; // Thay bằng package của bạn

public class TtsBottomSheetFragment extends BottomSheetDialogFragment {

    private boolean isPlaying = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_tts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton btnPlayPause = view.findViewById(R.id.btn_tts_play_pause);
        ImageButton btnRewind = view.findViewById(R.id.btn_tts_rewind);
        ImageButton btnForward = view.findViewById(R.id.btn_tts_forward);

        btnPlayPause.setOnClickListener(v -> {
            isPlaying = !isPlaying; // Đảo trạng thái
            if (isPlaying) {
                btnPlayPause.setImageResource(R.drawable.ic_pause); // Đổi sang icon Pause
                Toast.makeText(getContext(), "Playing...", Toast.LENGTH_SHORT).show();
                // TODO: Bắt đầu hoặc tiếp tục phát TTS
            } else {
                btnPlayPause.setImageResource(R.drawable.ic_play_arrow); // Đổi sang icon Play
                Toast.makeText(getContext(), "Paused", Toast.LENGTH_SHORT).show();
                // TODO: Tạm dừng phát TTS
            }
        });

        btnRewind.setOnClickListener(v -> {
            // TODO: Xử lý logic tua lại
            Toast.makeText(getContext(), "Rewind", Toast.LENGTH_SHORT).show();
        });

        btnForward.setOnClickListener(v -> {
            // TODO: Xử lý logic tua tới
            Toast.makeText(getContext(), "Forward", Toast.LENGTH_SHORT).show();
        });
    }
}