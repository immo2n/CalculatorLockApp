package com.sm.calculatorlock.Sheets;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sm.calculatorlock.Adapters.MediaSelectorAdapter;
import com.sm.calculatorlock.Custom.DBHandler;
import com.sm.calculatorlock.Custom.Global;
import com.sm.calculatorlock.Custom.Locker;
import com.sm.calculatorlock.Custom.Media;
import com.sm.calculatorlock.DataClasses.StateKeys;
import com.sm.calculatorlock.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileSelection extends BottomSheetDialogFragment {
    public enum MediaType {
        IMAGE,
        VIDEO
    }
    public interface OnFileSelectedListener {
        void onFileSelection(boolean selected);
    }
    public interface OnFileLockDoneListener {
        void onDone();
    }
    private MediaType mediaType;
    private View view;
    private Locker locker = null;
    int sc = 0;
    public static List<String> selectedFilePaths = new ArrayList<>();
    private final Global global;
    private DBHandler dbHandler;
    private OnFileLockDoneListener onFileLockDoneListener;
    public FileSelection(Global global, MediaType mediaType, OnFileLockDoneListener onFileLockDoneListener){
        this.mediaType = mediaType;
        selectedFilePaths = new ArrayList<>();
        this.global = global;
        if(global.getContext() == null) return;
        this.onFileLockDoneListener = onFileLockDoneListener;
        locker = new Locker(global);
        dbHandler = new DBHandler(global.getContext());
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sheet_file_selection, container, false);
        view.findViewById(R.id.close).setOnClickListener(view -> dismiss());
        view.findViewById(R.id.lockButton).setOnClickListener(view -> {
            if(selectedFilePaths.size() > 0 && null != locker){
                AlertDialog.Builder Dbuilder = new AlertDialog.Builder(this.requireContext());
                Dbuilder.setMessage("Locking...");
                Dbuilder.setTitle("Please wait");
                Dbuilder.setCancelable(false);
                AlertDialog DDialog = Dbuilder.create();
                DDialog.show();
                sc = 0;
                new Thread(() -> {
                    if(!isAdded()) return;
                    List<File> lockableFiles = new ArrayList<>();
                    for(String path : selectedFilePaths){
                        File file = new File(path);
                        if(file.exists()){
                            lockableFiles.add(file);
                        }
                    }
                    int c = locker.lockFiles(new ArrayList<>(lockableFiles), (file) -> {
                        if(!isAdded()) return;
                        sc++;
                        requireActivity().runOnUiThread(() -> DDialog.setMessage("Locked " + sc + "/" + lockableFiles.size() +" files..." + "\n" + file.getName()));
                    });
                    requireActivity().runOnUiThread(() -> {
                        if(null == dbHandler.getStateValue(StateKeys.DATA_LOSS_WARNING)){
                            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
                            builder.setTitle("Read carefully")
                                    .setMessage("If you uninstall the app or, clear the app data or, delete the app data, all locked files will be lost. Please do not uninstall the app before unlocking all the files from the vault.")
                                    .setPositiveButton("OK, don't show again", (dialog, which) -> dbHandler.setAppState(StateKeys.DATA_LOSS_WARNING, StateKeys.VALUE_TRUE))
                                    .setNegativeButton("OK", (dialog, which) -> dialog.dismiss())
                                    .setCancelable(false)
                                    .show();
                        }
                        Toast.makeText(requireContext(), c + " files locked", Toast.LENGTH_SHORT).show();
                        DDialog.dismiss();
                        dismiss();
                        onFileLockDoneListener.onDone();
                    });
                }).start();
            }
        });
        loadFiles();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    @SuppressLint("DefaultLocale")
    private void loadFiles(){
        selectedFilePaths = new ArrayList<>();
        new Thread(() -> {
            if(!isAdded()) return;
            List<File> files;
            if(mediaType == MediaType.VIDEO){
                files = Media.getAllVideos(requireContext());
            }
            else if (mediaType == MediaType.IMAGE){
                files = Media.getAllImages(requireContext());
            } else {
                files = new ArrayList<>();
            }
            if(files.size() == 0){
                if(!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    view.findViewById(R.id.loading).setVisibility(View.GONE);
                    view.findViewById(R.id.empty).setVisibility(View.VISIBLE);
                });
                return;
            }
            if(!isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                if(!isAdded()) return;
                GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 3);
                RecyclerView filesView = view.findViewById(R.id.filesView);
                filesView.setLayoutManager(gridLayoutManager);
                filesView.setItemAnimator(null);
                filesView.setAdapter(new MediaSelectorAdapter(files, requireContext(), selected -> {
                    TextView title = view.findViewById(R.id.title);
                    title.setText(String.format("%d %s Selected", selectedFilePaths.size(), mediaType == MediaType.IMAGE ? "Images" : "Videos"));
                    if(selectedFilePaths.size() > 0){
                       if(view.findViewById(R.id.lockButton).getVisibility() == View.GONE) view.findViewById(R.id.lockButton).setVisibility(View.VISIBLE);
                    }
                    else {
                        if(view.findViewById(R.id.lockButton).getVisibility() == View.VISIBLE) view.findViewById(R.id.lockButton).setVisibility(View.GONE);
                    }
                }));
                view.findViewById(R.id.loading).setVisibility(View.GONE);
                view.findViewById(R.id.empty).setVisibility(View.GONE);
                filesView.setVisibility(View.VISIBLE);
            });
        }).start();
    }
}