//package com.example.truyenchu.fragment; // Thay bằng package của bạn
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
//import com.example.truyenchu.R; // Thay bằng package của bạn
//
//public class TtsBottomSheetFragment extends BottomSheetDialogFragment {
//
//    private boolean isPlaying = false;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.bottom_sheet_tts, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        ImageButton btnPlayPause = view.findViewById(R.id.btn_tts_play_pause);
//        ImageButton btnRewind = view.findViewById(R.id.btn_tts_rewind);
//        ImageButton btnForward = view.findViewById(R.id.btn_tts_forward);
//
//        btnPlayPause.setOnClickListener(v -> {
//            isPlaying = !isPlaying; // Đảo trạng thái
//            if (isPlaying) {
//                btnPlayPause.setImageResource(R.drawable.ic_pause); // Đổi sang icon Pause
//                Toast.makeText(getContext(), "Playing...", Toast.LENGTH_SHORT).show();
//                // TODO: Bắt đầu hoặc tiếp tục phát TTS
//            } else {
//                btnPlayPause.setImageResource(R.drawable.ic_play_arrow); // Đổi sang icon Play
//                Toast.makeText(getContext(), "Paused", Toast.LENGTH_SHORT).show();
//                // TODO: Tạm dừng phát TTS
//            }
//        });
//
//        btnRewind.setOnClickListener(v -> {
//            // TODO: Xử lý logic tua lại
//            Toast.makeText(getContext(), "Rewind", Toast.LENGTH_SHORT).show();
//        });
//
//        btnForward.setOnClickListener(v -> {
//            // TODO: Xử lý logic tua tới
//            Toast.makeText(getContext(), "Forward", Toast.LENGTH_SHORT).show();
//        });
//    }
//}
package com.example.truyenchu.fragment;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.truyenchu.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Locale;

public class TtsBottomSheetFragment extends BottomSheetDialogFragment implements TextToSpeech.OnInitListener {

    private static final String TAG = "TtsBottomSheet";
    private static final String ARG_TEXT_TO_READ = "textToRead";

    private TextToSpeech tts;
    private ImageButton btnPlayPause;
    private String textToRead;
    private boolean isPlaying = false;

    // Factory method để truyền dữ liệu (nội dung chương) vào BottomSheet
    public static TtsBottomSheetFragment newInstance(String text) {
        TtsBottomSheetFragment fragment = new TtsBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT_TO_READ, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            textToRead = getArguments().getString(ARG_TEXT_TO_READ);
        }
        // Khởi tạo TextToSpeech engine
        tts = new TextToSpeech(getContext(), this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_tts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnPlayPause = view.findViewById(R.id.btn_tts_play_pause);
        // Các nút khác có thể được ánh xạ ở đây nếu cần
        // ImageButton btnRewind = view.findViewById(R.id.btn_tts_rewind);
        // ImageButton btnForward = view.findViewById(R.id.btn_tts_forward);

        btnPlayPause.setOnClickListener(v -> togglePlayPause());
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // Cài đặt ngôn ngữ là Tiếng Việt
            Locale locale = new Locale("vi", "VN");
            int result = tts.setLanguage(locale);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "Ngôn ngữ Tiếng Việt không được hỗ trợ.");
                Toast.makeText(getContext(), "Thiết bị không hỗ trợ đọc Tiếng Việt.", Toast.LENGTH_SHORT).show();
            } else {
                // Sẵn sàng để đọc, có thể tự động play nếu muốn
                // togglePlayPause();
            }
        } else {
            Log.e(TAG, "Không thể khởi tạo TextToSpeech.");
        }
    }

    private void togglePlayPause() {
        if (isPlaying) {
            // Nếu đang đọc -> Dừng lại
            tts.stop();
            isPlaying = false;
            btnPlayPause.setImageResource(R.drawable.ic_play_arrow); // Đổi icon thành nút Play
        } else {
            // Nếu đang dừng -> Bắt đầu đọc
            if (textToRead != null && !textToRead.isEmpty()) {
                tts.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, "utteranceId");
                isPlaying = true;
                btnPlayPause.setImageResource(R.drawable.ic_pause); // Đổi icon thành nút Pause
            }
        }
    }

    @Override
    public void onDestroy() {
        // Rất quan trọng: Dừng và giải phóng tài nguyên TTS khi không cần nữa
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}