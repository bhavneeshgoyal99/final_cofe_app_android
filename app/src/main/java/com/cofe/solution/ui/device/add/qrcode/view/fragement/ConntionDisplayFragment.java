package com.cofe.solution.ui.device.add.qrcode.view.fragement;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;

import com.cofe.solution.R;
import com.cofe.solution.ui.device.record.view.DevRecordActivity;

public class ConntionDisplayFragment extends Fragment {


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
    private PlayerView playerView;
    private ExoPlayer exoPlayer;
    private static boolean isPlayerInitialized = false; // Track initialization
    TextView countdownText;
    private CountDownTimer countDownTimer;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conntion_display, container, false);
        countdownText  = view.findViewById(R.id.count_down_text1);

        playerView = view.findViewById(R.id.playerView);

        // Initialize and set up ExoPlayer
        if (exoPlayer == null) {
            // Initialize ExoPlayer if it's null
            exoPlayer = new ExoPlayer.Builder(requireContext()).build();
            isPlayerInitialized = false;
        }

        // Attach PlayerView to ExoPlayer
        playerView.setPlayer(exoPlayer);

        if (!isPlayerInitialized) {
            // Initialize ExoPlayer only once
            initializePlayer();
            isPlayerInitialized = true;
        }

        return view;
    }

    private void initializePlayer() {
        if (exoPlayer != null) {
            // Add a media item to the player
            Uri videoUri = RawResourceDataSource.buildRawResourceUri(R.raw.video_work);
            MediaItem mediaItem = MediaItem.fromUri(videoUri);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.setRepeatMode(ExoPlayer.REPEAT_MODE_ALL);
            // Prepare the player
            exoPlayer.prepare();
            exoPlayer.setPlayWhenReady(true);
        }
        startCountdown();
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(150 * 1000, 1000) { // 150 seconds, updates every second
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the TextView with remaining time in seconds
                long secondsRemaining = millisUntilFinished / 1000;
                countdownText.setText("Time remaining: " + secondsRemaining + " seconds");
            }

            @Override
            public void onFinish() {
                // When the timer finishes
                countdownText.setText("Time's up!");
            }
        };

        countDownTimer.start(); // Start the timer
    }


    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        initializePlayer(); // Reinitialize player if needed
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer(); // Release player resources
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // Release ExoPlayer when the activity/fragment lifecycle ends
        if (requireActivity().isFinishing()) {
            if(exoPlayer!=null) {
                exoPlayer.release();
                exoPlayer = null;
            }
            playerView.setPlayer(null);
            countDownTimer.cancel();

        }
    }


    @Override
    public void onDetach() {
        super.onDetach();

    }
}
