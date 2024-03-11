package com.ucllc.smcalculatorlock.Custom;


import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Media {
    public static List<File> getAllImages(Context context) {
        List<File> fileList = new ArrayList<>(getImagesFromMediaStore(context));
        if (fileList.isEmpty()) {
            fileList.addAll(getAllMediaFiles(Environment.getExternalStorageDirectory(), true));
        }
        return fileList;
    }
    public static List<File> getAllVideos(Context context) {
        List<File> fileList = new ArrayList<>(getVideosFromMediaStore(context));
        if (fileList.isEmpty()) {
            fileList.addAll(getAllMediaFiles(Environment.getExternalStorageDirectory(), false));
        }
        return fileList;
    }

    public static List<File> getImagesFromMediaStore(Context context) {
        List<File> imageList = new ArrayList<>();

        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.DATE_MODIFIED,
                MediaStore.MediaColumns.MIME_TYPE
        };

        try {
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    MediaStore.MediaColumns.DATE_MODIFIED + " ASC"
            );

            if (cursor != null) {
                int dataIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                while (cursor.moveToNext()) {
                    if (dataIndex != -1) {
                        String filePath = cursor.getString(dataIndex);
                        File file = new File(filePath);
                        if (file.exists()) {
                            imageList.add(file);
                        }
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageList;
    }

    public static List<File> getVideosFromMediaStore(Context context) {
        List<File> videoList = new ArrayList<>();
        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.DATE_MODIFIED,
                MediaStore.MediaColumns.MIME_TYPE
        };
        try {
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    MediaStore.MediaColumns.DATE_MODIFIED + " ASC"
            );
            if (cursor != null) {
                int dataIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                while (cursor.moveToNext()) {
                    if (dataIndex != -1) {
                        String filePath = cursor.getString(dataIndex);
                        File file = new File(filePath);
                        if (file.exists()) {
                            videoList.add(file);
                        }
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return videoList;
    }

    public static List<File> getAllMediaFiles(File directory, boolean imagesOnly) {
        List<File> mediaFiles = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    mediaFiles.addAll(getAllMediaFiles(file, imagesOnly));
                } else {
                    if(imagesOnly && !isImageFile(file)){
                        continue;
                    }
                    mediaFiles.add(file);
                }
            }
        }
        return mediaFiles;
    }

    public static boolean isImageFile(File file) {
        return file.getName().toLowerCase().endsWith(".jpg") ||
                file.getName().toLowerCase().endsWith(".jpeg") ||
                file.getName().toLowerCase().endsWith(".png") ||
                file.getName().toLowerCase().endsWith(".gif") ||
                file.getName().toLowerCase().endsWith(".bmp");
    }

    public static boolean isVideoFile(File file) {
        return file.getName().toLowerCase().endsWith(".mp4") ||
                file.getName().toLowerCase().endsWith(".avi") ||
                file.getName().toLowerCase().endsWith(".mkv") ||
                file.getName().toLowerCase().endsWith(".3gp") ||
                file.getName().toLowerCase().endsWith(".mov");
    }
}