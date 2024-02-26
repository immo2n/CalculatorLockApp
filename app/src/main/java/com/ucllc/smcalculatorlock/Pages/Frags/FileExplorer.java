package com.ucllc.smcalculatorlock.Pages.Frags;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ucllc.smcalculatorlock.Custom.Explorer;
import com.ucllc.smcalculatorlock.Interfaces.AllFilesCallback;
import com.ucllc.smcalculatorlock.Interfaces.FilesInPathCallback;
import com.ucllc.smcalculatorlock.databinding.FragExplorerBinding;

import java.io.File;
import java.util.List;

public class FileExplorer extends Fragment {
    Explorer explorer;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragExplorerBinding binding = FragExplorerBinding.inflate(inflater);
        explorer = new Explorer(requireContext());

        explorer.explore(Environment.getExternalStorageDirectory().getPath(), new FilesInPathCallback() {
            @Override
            public void onSuccess(List<File> files) {
                Toast.makeText(requireContext(), "Files: " + files.size(), Toast.LENGTH_SHORT).show();
                StringBuilder names = new StringBuilder();
                for(File file : files) {
                    names.append(file.getName()).append("\n");
                }
                binding.tvExplorer.setText(names.toString());
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEmpty() {
                Toast.makeText(requireContext(), "Empty", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNoPermission() {
                Toast.makeText(requireContext(), "No Permission", Toast.LENGTH_SHORT).show();
            }
        });


        return binding.getRoot();
    }
}
