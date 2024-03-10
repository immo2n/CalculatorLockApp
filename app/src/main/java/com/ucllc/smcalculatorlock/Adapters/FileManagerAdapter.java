package com.ucllc.smcalculatorlock.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import com.ucllc.smcalculatorlock.Custom.Explorer;
import com.ucllc.smcalculatorlock.Custom.Global;
import com.ucllc.smcalculatorlock.Interfaces.ExplorerUI;
import com.ucllc.smcalculatorlock.Interfaces.FilesInPathCallback;
import com.ucllc.smcalculatorlock.Pages.Frags.FileExplorer;
import com.ucllc.smcalculatorlock.R;

import java.io.File;
import java.util.List;

public class FileManagerAdapter extends RecyclerView.Adapter<FileManagerAdapter.ViewHolder> {
    List<File> fileList;
    Explorer explorer;
    ExplorerUI callbackUI;
    FileExplorer.OnFileSelectedCallback fileSelectedCallback;
    public FileManagerAdapter(List<File> fileList, Explorer explorer, ExplorerUI callbackUI, FileExplorer.OnFileSelectedCallback fileSelectedCallback) {
        this.fileList = fileList;
        this.explorer = explorer;
        this.callbackUI = callbackUI;
        this.fileSelectedCallback = fileSelectedCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_explorer_folder, parent, false);
        return new FileManagerAdapter.ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        File file = fileList.get(position);
        holder.name.setText(file.getName());
        holder.icon.setImageDrawable(FileExplorer.getFileIcon(fileList.get(position), holder.icon.getContext()));
        if(file.isDirectory()){
            holder.icon.setPadding(10, 10, 10, 10);
            File[] files = file.listFiles();
            if (files != null) {
                int numItems = files.length;
                long lastModified = file.lastModified();
                holder.info.setText(String.format("%d item%s · %s", numItems, (numItems > 1)?"s":"" , FileExplorer.formatLastModified(lastModified)));
            } else {
                holder.info.setText(R.string.empty_folder);
            }
        }
        else {
            Explorer.FileType fileType = Explorer.fileType(file.getName());
            if(fileType == Explorer.FileType.IMAGE){
                Picasso.get()
                        .load(file)
                        .resize(100, 100)
                        .placeholder(R.drawable.file_image)
                        .centerCrop()
                        .into(holder.icon);
            } else if (fileType == Explorer.FileType.VIDEO) {
                Glide.with(explorer.getContext())
                        .load(file)
                        .apply(new RequestOptions().override(100, 100))
                        .placeholder(R.drawable.file_video)
                        .centerCrop()
                        .into(holder.icon);
            }
            holder.info.setText(String.format("%s · %s", FileExplorer.formatFileSize(file.length()), FileExplorer.formatLastModified(file.lastModified())));
        }
        //CLICK EVENT
        holder.itemView.setOnClickListener(v -> {
            if (file.isDirectory()) {
                explorer.explore(file.getPath(), FileExplorer.sortMode, FileExplorer.showHiddenFiles, new FilesInPathCallback() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(List<File> files) {
                        fileList.clear();
                        fileList.addAll(files);
                        callbackUI.onPathChanged(file.getPath(), true);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Exception e) {
                        callbackUI.onError(e);
                    }

                    @Override
                    public void onEmpty() {
                        callbackUI.onPathChanged(file.getPath(), true);
                        callbackUI.onEmpty();
                    }

                    @Override
                    public void onNoPermission() {
                        callbackUI.onNoPermission();
                    }

                    @Override
                    public void loading() {
                        callbackUI.loading();
                    }
                });
            }
            else {
                //Act as file
                openFileInExternalApp(explorer.getContext(), file);
            }
        });
        //SELECTION EVENT - RESET - SET
        holder.selectionSwitch.setOnCheckedChangeListener(null);
        if(file.isDirectory()){
            File[] list = file.listFiles();
            if(null != list) {
                int actualFiles = 0;
                for (File f : list) {
                    if (f.isFile()) actualFiles++;
                }
                if (actualFiles == 0) {
                    holder.selectionSwitch.setEnabled(false);
                    return;
                }
            }
        }
        holder.selectionSwitch.setEnabled(true);
        boolean check = FileExplorer.lockableFiles.containsKey(file);
        holder.selectionSwitch.setChecked(check);
        if(check) FileExplorer.lockableFiles.put(file, holder.selectionSwitch);
        holder.selectionSwitch.setOnCheckedChangeListener((compoundButton, b) -> fileSelectedCallback.onSelect(file, holder.selectionSwitch));
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name, info;
        CheckBox selectionSwitch;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
            info = itemView.findViewById(R.id.info);
            selectionSwitch = itemView.findViewById(R.id.selectionSwitch);
        }
    }
    public static void openFileInExternalApp(Context context, File file) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
            intent.setDataAndType(fileUri, getMimeType(file.getAbsolutePath()));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        }
        catch (Exception e){
            Global.logError(e);
            Toast.makeText(context, "Can't open file!", Toast.LENGTH_SHORT).show();
        }
    }
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }
        return type;
    }
}
