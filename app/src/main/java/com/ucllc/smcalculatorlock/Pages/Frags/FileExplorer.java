package com.ucllc.smcalculatorlock.Pages.Frags;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ucllc.smcalculatorlock.Adapters.FileManagerAdapter;
import com.ucllc.smcalculatorlock.Custom.Explorer;
import com.ucllc.smcalculatorlock.Interfaces.AllFilesCallback;
import com.ucllc.smcalculatorlock.Interfaces.ExplorerUI;
import com.ucllc.smcalculatorlock.Interfaces.FilesInPathCallback;
import com.ucllc.smcalculatorlock.R;
import com.ucllc.smcalculatorlock.databinding.FragExplorerBinding;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FileExplorer extends Fragment {
    Explorer explorer;
    List<String> pathHistory = new ArrayList<>();
    private FragExplorerBinding binding;
    private ExplorerUI explorerUI;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         binding = FragExplorerBinding.inflate(inflater);

        explorer = new Explorer(requireContext());
        explorerUI = path -> {
            pathHistory.add(path);
            binding.path.setText(((path.equals(Environment.getExternalStorageDirectory().getPath())) ? "Internal Storage" : path)
                    .replace(Environment.getExternalStorageDirectory().getPath(), "Internal Storage"));
        };

        //Go back
        binding.back.setOnClickListener(v -> {
            if (!pathHistory.isEmpty()) {
                pathHistory.remove(pathHistory.size() - 1);
                if (!pathHistory.isEmpty()) {
                    String previousPath = pathHistory.get(pathHistory.size() - 1);
                    loadPath(previousPath);
                } else {
                    loadPath(Environment.getExternalStorageDirectory().getPath());
                }
            }
        });


        //Set layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        binding.explorerView.setLayoutManager(layoutManager);

        //Base home files
        loadPath(Environment.getExternalStorageDirectory().getPath());
        return binding.getRoot();
    }
    public static Drawable getFileIcon(File file, Context context){
        Drawable r = ContextCompat.getDrawable(context, R.drawable.folder);
        if(file.isFile()){
            r = ContextCompat.getDrawable(context, R.drawable.file_unknown);
            switch (file.getName().substring(file.getName().lastIndexOf(".")+1).toLowerCase()){
                case "jpg":
                case "jpeg":
                case "png":
                case "gif":
                case "bmp":
                case "webp":
                case "svg":
                case "tiff":
                case "ico":
                case "psd":
                case "ai":
                case "eps":
                case "raw":
                case "cr2":
                case "orf":
                case "nef":
                case "sr2":
                case "rw2":
                case "arw":
                case "dng":
                    r = ContextCompat.getDrawable(context, R.drawable.file_image);
                    break;
                case "mp4":
                case "avi":
                case "mkv":
                case "flv":
                case "3gp":
                case "mov":
                case "wmv":
                case "rm":
                case "rmvb":
                case "m4v":
                case "webm":
                case "mpeg":
                case "mpg":
                case "mpe":
                case "m2v":
                case "vob":
                case "mts":
                case "m2ts":
                case "ts":
                    r = ContextCompat.getDrawable(context, R.drawable.file_video);
                    break;
                case "mp3":
                case "wav":
                case "flac":
                case "ogg":
                case "m4a":
                case "aac":
                case "wma":
                case "aiff":
                case "ape":
                case "alac":
                case "pcm":
                case "dsd":
                case "mid":
                case "midi":
                case "opus":
                case "amr":
                    r = ContextCompat.getDrawable(context, R.drawable.file_audio);
                    break;
                case "pdf":
                case "doc":
                case "docx":
                case "xls":
                case "xlsx":
                case "ppt":
                case "pptx":
                case "txt":
                case "rtf":
                case "csv":
                case "odt":
                case "ods":
                case "odp":
                case "xml":
                case "json":
                case "html":
                case "markdown":
                case "tex":
                case "log":
                    r = ContextCompat.getDrawable(context, R.drawable.file_document);
                    break;
            }
        }
        return r;
    }
    public static String formatLastModified(long lastModifiedTimestamp) {
        Date lastModifiedDate = new Date(lastModifiedTimestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
        return dateFormat.format(lastModifiedDate);
    }
    @SuppressLint("DefaultLocale")
    public static String formatFileSize(long sizeInBytes) {
        final long KB = 1024;
        final long MB = KB * 1024;
        final long GB = MB * 1024;
        if (sizeInBytes < KB) {
            return sizeInBytes + " B";
        } else if (sizeInBytes < MB) {
            return String.format("%.2f KB", (double) sizeInBytes / KB);
        } else if (sizeInBytes < GB) {
            return String.format("%.2f MB", (double) sizeInBytes / MB);
        } else {
            return String.format("%.2f GB", (double) sizeInBytes / GB);
        }
    }
    private void loadPath(String path){
        explorer.explore(path, Explorer.FileSort.NAME, new FilesInPathCallback() {
            @Override
            public void onSuccess(List<File> files) {
                pathHistory.add(path);
                explorerUI.onPathChanged(path);
                binding.explorerView.setAdapter(new FileManagerAdapter(files, explorer, explorerUI));
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
    }
}
