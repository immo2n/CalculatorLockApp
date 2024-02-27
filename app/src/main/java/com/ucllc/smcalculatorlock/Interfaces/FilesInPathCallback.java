package com.ucllc.smcalculatorlock.Interfaces;

import java.io.File;
import java.util.List;

public interface FilesInPathCallback {
    void onSuccess(List<File> files);
    void onError(Exception e);
    void onEmpty();
    void onNoPermission();
    void loading();
}
