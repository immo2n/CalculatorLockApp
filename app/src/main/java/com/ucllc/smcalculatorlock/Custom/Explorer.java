package com.ucllc.smcalculatorlock.Custom;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MergeCursor;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import com.ucllc.smcalculatorlock.Interfaces.AllFilesCallback;
import com.ucllc.smcalculatorlock.Interfaces.FilesInPathCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Explorer {
    //Interfaces
    private final Context context;
    public Explorer(Context context) {
        this.context = context;
    }
    public enum FileType {
        IMAGE,
        VIDEO,
        AUDIO,
        ALL
    }
    public enum FileSort {
        NAME,
        DATE,
        SIZE
    }

    //Main Methods
    public void getMediaFiles(@NonNull FileType fileType, @NonNull AllFilesCallback callback) {
        if (noMediaFilesPermission()) {
            callback.onNoPermission();
            return;
        }
        List<File> files = new ArrayList<>();
        MergeCursor cursor;
        try {
            if(fileType == FileType.IMAGE || fileType == FileType.ALL) {
                //Images
                String[] imageColumns = new String[]{
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DATE_ADDED,
                        MediaStore.Images.Media.BUCKET_ID,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
                String imageSort = MediaStore.Images.Media.DATE_ADDED + " DESC";
                cursor = new MergeCursor(new Cursor[]{
                        context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageSort)
                });
                addCursorToList(cursor, files);
            }

            if(fileType == FileType.VIDEO || fileType == FileType.ALL) {
                //Videos
                String[] videoColumns = new String[]{
                        MediaStore.Video.Media.DATA,
                        MediaStore.Video.Media.DATE_ADDED,
                        MediaStore.Video.Media.BUCKET_ID,
                        MediaStore.Video.Media.BUCKET_DISPLAY_NAME};
                String videoSort = MediaStore.Video.Media.DATE_ADDED + " DESC";
                cursor = new MergeCursor(new Cursor[]{
                        context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoColumns, null, null, videoSort)
                });
                addCursorToList(cursor, files);
            }

            if(fileType == FileType.AUDIO || fileType == FileType.ALL) {
                //Audio
                String[] audioColumns = new String[]{
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.DATE_ADDED,
                        MediaStore.Audio.Media.BUCKET_ID,
                        MediaStore.Audio.Media.DISPLAY_NAME
                };
                String audioSort = MediaStore.Audio.Media.DATE_ADDED + " DESC";
                cursor = new MergeCursor(new Cursor[]{
                        context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, audioColumns, null, null, audioSort)
                });
                addCursorToList(cursor, files);
            }

            callback.onSuccess(files);
        } catch (Exception e) {
            callback.failedToLoad(e);
        }
    }
    public void explore(@NonNull String path, @NonNull FileSort sort, @NonNull FilesInPathCallback callback){
        if(noMediaFilesPermission()){
            callback.onNoPermission();
            return;
        }
        try {
            List<File> r = new ArrayList<>();
            File[] list = new File(path).listFiles();
            if (null != list) {
                Collections.addAll(r, list);
                if (r.size() > 0) {
                    if(sort == FileSort.NAME){
                        r.sort((f1, f2) -> {
                            if (f1.isDirectory() && f2.isFile()) return -1;
                            else if (f1.isFile() && f2.isDirectory()) return 1;
                            else return f1.getName().compareTo(f2.getName());
                        });
                    }
                    else if(sort == FileSort.DATE){
                        r.sort((f1, f2) -> {
                            if (f1.isDirectory() && f2.isFile()) return -1;
                            else if (f1.isFile() && f2.isDirectory()) return 1;
                            else return Long.compare(f2.lastModified(), f1.lastModified());
                        });
                    }
                    else if(sort == FileSort.SIZE){
                        r.sort((f1, f2) -> {
                            if (f1.isDirectory() && f2.isFile()) return -1;
                            else if (f1.isFile() && f2.isDirectory()) return 1;
                            else return Long.compare(f2.length(), f1.length());
                        });
                    }
                    callback.onSuccess(r);
                }
                else callback.onEmpty();
            } else {
                callback.onEmpty();
            }
        }
        catch (Exception e){
            callback.onError(e);
        }
    }

    //Helper methods
    public String folderNameFromFilePath(@NonNull String path, @NonNull String name){
        StringBuilder r_str = new StringBuilder(), r_n_str = new StringBuilder();
        char ch;
        path = path.substring(0, path.indexOf(name)-1);
        for (int i=0; i< path.length(); i++)
        {
            ch = path.charAt(i);
            r_str.insert(0, ch);
        }
        r_str = new StringBuilder(r_str.substring(0, r_str.indexOf("/")));
        int i = r_str.length()-1;
        while(i >= 0){
            ch = r_str.charAt(i);
            r_n_str.append(ch);
            i--;
        }
        return r_n_str.toString();
    }
    private void addCursorToList(Cursor cursor, List<File> files){
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            int index = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
            String path = cursor.getString(index);
            int lastPoint = path.lastIndexOf(".");
            path = path.substring(0, lastPoint) + path.substring(lastPoint).toLowerCase();
            files.add(new File(path));
            cursor.moveToNext();
        }
    }
    private boolean noMediaFilesPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return !Environment.isExternalStorageManager();
        }
        else {
            return context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
        }
    }

    public Context getContext() {
        return context;
    }
}