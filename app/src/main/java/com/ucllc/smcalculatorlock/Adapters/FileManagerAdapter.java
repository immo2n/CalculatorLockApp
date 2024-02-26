package com.ucllc.smcalculatorlock.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import com.ucllc.smcalculatorlock.Custom.Explorer;
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
    public FileManagerAdapter(List<File> fileList, Explorer explorer, ExplorerUI callbackUI) {
        this.fileList = fileList;
        this.explorer = explorer;
        this.callbackUI = callbackUI;
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
            Explorer.FileType fileType = Explorer.fileType(file);
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
                explorer.explore(file.getPath(), Explorer.FileSort.NAME, new FilesInPathCallback() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(List<File> files) {
                        fileList.clear();
                        fileList.addAll(files);
                        callbackUI.onPathChanged(file.getPath());
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Exception e) {
                        //Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onEmpty() {
                        //Toast.makeText(requireContext(), "Empty", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNoPermission() {
                        //Toast.makeText(requireContext(), "No Permission", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                //Act as file
                Toast.makeText(explorer.getContext(), "File!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name, info;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
            info = itemView.findViewById(R.id.info);
        }
    }
}
