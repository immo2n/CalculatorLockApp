package com.sm.calculatorlock.MediaPages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.sm.calculatorlock.Custom.Global;
import com.sm.calculatorlock.R;
import com.sm.calculatorlock.databinding.ActivityPhotoViewerBinding;

import java.io.File;

public class PhotoViewer extends AppCompatActivity {
    ActivityPhotoViewerBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhotoViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.midnight_black));
        binding.back.setOnClickListener(v -> finish());
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String path = bundle.getString("path");
            String name = bundle.getString("name");
            if(path != null && name != null && !path.isEmpty() && !name.isEmpty()){
                try {
                    File file = new File(path);
                    if(file.length() > 10000000){
                        Picasso.get().load(file).fit().centerInside().placeholder(R.drawable.file_image).into(binding.imageView);
                        Toast.makeText(getApplicationContext(), "Unlock to see full resolution!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        binding.imageView.setImageURI(Uri.fromFile(file));
                    }
                    binding.fileName.setText(name);
                }
                catch (Exception e){
                    Global.logError(e);
                    Toast.makeText(this, "Can not show image!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
        Toast.makeText(this, "Can not show image!", Toast.LENGTH_SHORT).show();
        finish();
    }
}