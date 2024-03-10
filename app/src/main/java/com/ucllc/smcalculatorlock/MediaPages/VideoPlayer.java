package com.ucllc.smcalculatorlock.MediaPages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.ucllc.smcalculatorlock.Custom.Global;
import com.ucllc.smcalculatorlock.R;
import com.ucllc.smcalculatorlock.databinding.ActivityVideoPlayerBinding;

import java.io.File;

public class VideoPlayer extends AppCompatActivity {
    ActivityVideoPlayerBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.midnight_black_button));
        binding.back.setOnClickListener(v -> finish());
        binding.videoView.setMediaController(new MediaController(this));
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String path = bundle.getString("path");
            String name = bundle.getString("name");
            if(path != null && name != null && !path.isEmpty() && !name.isEmpty()){
                try {
                    File file = new File(path);
                    binding.fileName.setText(name);
                    binding.videoView.setVideoURI(Uri.fromFile(file));
                    binding.videoView.start();
                }
                catch (Exception e){
                    Global.logError(e);
                    Toast.makeText(this, "Can not play video!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
        Toast.makeText(this, "Can not play video!", Toast.LENGTH_SHORT).show();
        finish();
    }
}