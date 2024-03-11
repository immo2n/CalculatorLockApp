package com.ucllc.smcalculatorlock.Sheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ucllc.smcalculatorlock.R;

import java.io.File;
import java.util.List;

public class FileSelection extends BottomSheetDialogFragment {
    private final List<File> fileList;
    private View view;
    public FileSelection(List<File> fileList){
        this.fileList = fileList;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sheet_notice_privacy, container, false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    public void loadFiles(){
    }
}