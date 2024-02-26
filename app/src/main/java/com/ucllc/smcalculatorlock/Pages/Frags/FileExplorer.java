package com.ucllc.smcalculatorlock.Pages.Frags;

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
import com.ucllc.smcalculatorlock.Interfaces.FilesInPathCallback;
import com.ucllc.smcalculatorlock.R;
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

        //Set layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        binding.explorerView.setLayoutManager(layoutManager);

        //Base home files
        explorer.explore(Environment.getExternalStorageDirectory().getPath(), Explorer.FileSort.NAME, new FilesInPathCallback() {
            @Override
            public void onSuccess(List<File> files) {
                binding.explorerView.setAdapter(new FileManagerAdapter(files));
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
}
