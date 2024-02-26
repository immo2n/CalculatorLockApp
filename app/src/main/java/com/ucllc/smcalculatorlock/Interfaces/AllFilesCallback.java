package com.ucllc.smcalculatorlock.Interfaces;

import java.io.File;
import java.util.List;

public interface AllFilesCallback {
    void failedToLoad(Exception e);
    void onSuccess(List<File> fileList);
    void onNoPermission();
}
