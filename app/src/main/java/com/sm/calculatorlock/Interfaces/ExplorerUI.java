package com.sm.calculatorlock.Interfaces;

public interface ExplorerUI {
    void onPathChanged(String path, boolean storeHistory);
    void loading();
    void onEmpty();
    void onNoPermission();
    void onError(Exception e);
}
