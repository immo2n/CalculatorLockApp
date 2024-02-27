package com.ucllc.smcalculatorlock.Pages.Frags;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ucllc.smcalculatorlock.Adapters.FileManagerAdapter;
import com.ucllc.smcalculatorlock.Custom.DBHandler;
import com.ucllc.smcalculatorlock.Custom.Explorer;
import com.ucllc.smcalculatorlock.DataClasses.StateKeys;
import com.ucllc.smcalculatorlock.Interfaces.ExplorerUI;
import com.ucllc.smcalculatorlock.Interfaces.FilesInPathCallback;
import com.ucllc.smcalculatorlock.Pages.Home;
import com.ucllc.smcalculatorlock.Pages.PatternLock;
import com.ucllc.smcalculatorlock.R;
import com.ucllc.smcalculatorlock.databinding.FragExplorerBinding;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

public class FileExplorer extends Fragment {
    Explorer explorer;
    Stack<String> pathHistory = new Stack<>();
    private FragExplorerBinding binding;
    private ExplorerUI explorerUI;
    public static boolean showHiddenFiles = false;
    public static Explorer.FileSort sortMode = Explorer.FileSort.NAME;
    private DBHandler dbHandler;
    private String currentPath = Environment.getExternalStorageDirectory().getPath();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragExplorerBinding.inflate(inflater);
        explorer = new Explorer(requireContext(), requireActivity());
        dbHandler = new DBHandler(requireContext());
        explorerUI = new ExplorerUI() {
            @Override
            public void onPathChanged(String path, boolean storeHistory) {
                filesLoaded();
                if(storeHistory) pathHistory.push(path);
                binding.path.setText(((path.equals(Environment.getExternalStorageDirectory().getPath())) ? "Internal Storage" : path)
                        .replace(Environment.getExternalStorageDirectory().getPath(), "Internal Storage"));
                currentPath = path;
            }

            @Override
            public void loading() {
                filesLoading();
            }

            @Override
            public void onEmpty() {
                filesOnEmpty();
            }

            @Override
            public void onNoPermission() {
                filesOnNoPermission();
            }

            @Override
            public void onError(Exception e) {
                filesOnError();
            }
        };
        binding.back.setOnClickListener(v -> {
            if (!pathHistory.isEmpty()) {
                pathHistory.pop();
                if (!pathHistory.isEmpty()) {
                    String previousPath = pathHistory.peek();
                    loadPath(previousPath);
                } else {
                    loadPath(Environment.getExternalStorageDirectory().getPath());
                }
            } else {
                Toast.makeText(requireContext(), "Home directory", Toast.LENGTH_SHORT).show();
            }
        });

        //Set layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        binding.explorerView.setLayoutManager(layoutManager);

        //Setup settings
        String check = dbHandler.getStateValue(StateKeys.SHOW_HIDDEN_FILES);
        showHiddenFiles = null != check && check.equals(StateKeys.VALUE_TRUE);
        check = dbHandler.getStateValue(StateKeys.SORT_MODE);
        if(null != check){
            switch (check) {
                case StateKeys.SORT_BY_NAME:
                    sortMode = Explorer.FileSort.NAME;
                    break;
                case StateKeys.SORT_BY_DATE:
                    sortMode = Explorer.FileSort.DATE;
                    break;
                case StateKeys.SORT_BY_SIZE:
                    sortMode = Explorer.FileSort.SIZE;
                    break;
            }
        }

        //Hooks
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialogue_explorer_settings);
        if(null != dialog.getWindow()){
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.dialogue_background));
            @SuppressLint("UseSwitchCompatOrMaterialCode")
            Switch hiddenFileSwitch = dialog.getWindow().findViewById(R.id.switchShowHiddenFiles);
            RadioGroup sortGroup = dialog.getWindow().findViewById(R.id.radioGroup);
            hiddenFileSwitch.setChecked(showHiddenFiles);
            hiddenFileSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                showHiddenFiles = isChecked;
                dbHandler.setAppState(StateKeys.SHOW_HIDDEN_FILES, isChecked ? StateKeys.VALUE_TRUE : StateKeys.VALUE_FALSE);
                loadPath(currentPath);
            });
            RadioButton radioName = dialog.getWindow().findViewById(R.id.radioButtonName);
            RadioButton radioDate = dialog.getWindow().findViewById(R.id.radioButtonDate);
            RadioButton radioSize = dialog.getWindow().findViewById(R.id.radioButtonSize);
            sortGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if(checkedId == radioName.getId()){
                    sortMode = Explorer.FileSort.NAME;
                    dbHandler.setAppState(StateKeys.SORT_MODE, StateKeys.SORT_BY_NAME);
                }
                else if(checkedId == radioDate.getId()){
                    sortMode = Explorer.FileSort.DATE;
                    dbHandler.setAppState(StateKeys.SORT_MODE, StateKeys.SORT_BY_DATE);
                }
                else if(checkedId == radioSize.getId()){
                    sortMode = Explorer.FileSort.SIZE;
                    dbHandler.setAppState(StateKeys.SORT_MODE, StateKeys.SORT_BY_SIZE);
                }
                loadPath(currentPath);
            });
        }
        binding.settings.setOnClickListener(view -> dialog.show());

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
        explorer.explore(path, sortMode, showHiddenFiles, new FilesInPathCallback() {
            @Override
            public void onSuccess(List<File> files) {
                filesLoaded();
                explorerUI.onPathChanged(path, false);
                binding.explorerView.setAdapter(new FileManagerAdapter(files, explorer, explorerUI));
                currentPath = path;
            }

            @Override
            public void onError(Exception e) {
                filesOnError();
            }

            @Override
            public void onEmpty() {
                filesOnEmpty();
            }

            @Override
            public void onNoPermission() {
                filesOnNoPermission();
            }

            @Override
            public void loading() {
                filesLoading();
            }
        });
    }
    private void filesLoading(){
        binding.explorerView.setVisibility(View.GONE);
        binding.loading.setVisibility(View.VISIBLE);
        binding.message.setText(R.string.loading);
    }
    private void filesLoaded(){
        binding.loading.setVisibility(View.GONE);
        binding.explorerView.setVisibility(View.VISIBLE);
    }
    private void filesOnEmpty(){
        binding.explorerView.setVisibility(View.GONE);
        binding.loading.setVisibility(View.VISIBLE);
        binding.message.setText(R.string.empty_text);
    }
    private void filesOnNoPermission(){
    }
    private void filesOnError(){
        binding.explorerView.setVisibility(View.GONE);
        binding.loading.setVisibility(View.VISIBLE);
        binding.message.setText(R.string.failed_to_load);
    }
}
