package com.ucllc.smcalculatorlock.Custom;

import android.app.Activity;
import android.os.Environment;
import android.widget.Toast;

import com.ucllc.smcalculatorlock.DataClasses.LockedFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Locker {
    private final Global global;
    private final DBHandler dbHandler;
    public Locker(Global global) {
        this.global = global;
        dbHandler = global.getDBHandler();
    }
    public interface onLock {
        void onLocked(File file);
    }
    public int lockFiles(List<File> files, onLock onLock){
        int c = 0;
        Activity activity = global.getActivity();
        if(null == activity){
            Global.logError(new Exception("Activity is null - Locker not initialized properly"));
            return 0;
        }
        File destination = new File(activity.getFilesDir(), "locked");
        if(!destination.exists()) {
            if(!destination.mkdirs()){
                Global.logError(new Exception("Failed to create directory: " + destination.getAbsolutePath()));
                return 0;
            }
        }
        for(File file : files){
            if(file.exists() && file.isFile()){
                String hash = Global.md5(file.getAbsolutePath());
                if(null != hash){
                    LockedFile lockedFile = new LockedFile(
                            file.getAbsolutePath(),
                            file.getName(),
                            hash,
                            global.currentTimeStamp()
                    );
                    if(moveFile(file, new File(destination, hash))){
                        dbHandler.addLockedFile(lockedFile);
                        c++;
                        if(null != onLock){
                            onLock.onLocked(file);
                        }
                    }
                    else {
                        Global.logError(new Exception("Failed to lock file: " + file.getAbsolutePath() + " to " + hash));
                    }
                }
                else {
                    Global.logError(new Exception("Failed to hash file: " + file.getAbsolutePath()));
                }
            }
            else {
                Global.logError(new Exception("File not found: " + file.getAbsolutePath()));
            }
        }
        return c;
    }
    public boolean unlockFile(String hash){
        Activity activity = global.getActivity();
        if(null == activity){
            Global.logError(new Exception("Activity is null - Locker not initialized properly"));
            return false;
        }
        File lockedSource = new File(activity.getFilesDir(), "locked");
        LockedFile target = dbHandler.getFileByHash(hash);
        if(null != target){
            File file = new File(lockedSource, hash);
            if(file.exists()) {
                File internalStorage = new File(Environment.getExternalStorageDirectory(), "Calculator Vault");
                if(!internalStorage.exists()){
                    if(!internalStorage.mkdirs()){
                        activity.runOnUiThread(()-> Toast.makeText(activity, "Can't create internal storage directory! Check permissions!", Toast.LENGTH_LONG).show());
                        return false;
                    }
                }
                return moveFile(file, getSafeFileName(new File(internalStorage, target.getFileName())));
            }
        }
        return false;
    }

    private File getSafeFileName(File file) {
        if(file.exists()){
            String name = file.getName();
            String[] split = name.split("\\.");
            String ext = split[split.length - 1];
            String base = name.substring(0, name.length() - ext.length() - 1);
            int i = 1;
            while(file.exists()){
                file = new File(file.getParent(), base + "(" + i + ")." + ext);
                i++;
            }
        }
        return file;
    }

    private boolean moveFile(File sourceFile, File destFile) {
        try {
            FileInputStream inStream = new FileInputStream(sourceFile);
            FileOutputStream outStream = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, length);
            }
            inStream.close();
            outStream.close();
            return sourceFile.delete();
        } catch (IOException e) {
            Global.logError(e);
            return false;
        }
    }
}
