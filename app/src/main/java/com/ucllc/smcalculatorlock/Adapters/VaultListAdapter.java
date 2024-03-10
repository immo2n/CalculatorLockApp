package com.ucllc.smcalculatorlock.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import com.ucllc.smcalculatorlock.Custom.Explorer;
import com.ucllc.smcalculatorlock.Custom.Global;
import com.ucllc.smcalculatorlock.DataClasses.LockedFile;
import com.ucllc.smcalculatorlock.Pages.Frags.FileExplorer;
import com.ucllc.smcalculatorlock.Pages.Frags.FileVault;
import com.ucllc.smcalculatorlock.R;

import java.io.File;
import java.util.List;

public class VaultListAdapter extends RecyclerView.Adapter<VaultListAdapter.ViewHolder>{
    private final List<LockedFile> lockedFileList;
    private final Activity activity;
    private final Context context;
    private final File lockerDestination;
    private final FileVault.OnFileSelectedCallback fileSelectedCallback;

    public VaultListAdapter(List<LockedFile> lockedFileList, Activity activity, Context context, FileVault.OnFileSelectedCallback fileSelectedCallback) {
        this.lockedFileList = lockedFileList;
        this.activity = activity;
        this.context = context;
        lockerDestination = new File(activity.getFilesDir(), "locked");
        this.fileSelectedCallback = fileSelectedCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_vault, parent, false);
        return new VaultListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Reset
        holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.file_unknown));
        holder.name.setText("");
        holder.info.setText("");
        holder.selectionSwitch.setOnCheckedChangeListener(null);

        LockedFile item = lockedFileList.get(position);
        File file = new File(lockerDestination, item.getHash());

        holder.selectionSwitch.setChecked(FileVault.unlockMap.containsKey(item));
        holder.selectionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                FileVault.unlockMap.put(item, holder.selectionSwitch);
            } else {
                FileVault.unlockMap.remove(item);
            }
            fileSelectedCallback.onSelect(item, holder.selectionSwitch);
        });

        holder.name.setText(item.getFileName());
        Explorer.FileType fileType = Explorer.fileType(item.getFileName());
        if(fileType == Explorer.FileType.IMAGE){
            Picasso.get()
                    .load(file)
                    .resize(100, 100)
                    .placeholder(R.drawable.file_image)
                    .centerCrop()
                    .into(holder.icon);
        } else if (fileType == Explorer.FileType.VIDEO) {
            Glide.with(context)
                    .load(file)
                    .apply(new RequestOptions().override(100, 100))
                    .placeholder(R.drawable.file_video)
                    .centerCrop()
                    .into(holder.icon);
        }
        holder.info.setText(String.format("%s · %s", FileExplorer.formatFileSize(file.length()), FileExplorer.formatLastModified(Long.parseLong(item.getDate()))));
        holder.itemView.setOnClickListener(v -> openFileInExternalApp(context, file, item.getFileName()));
    }

    @Override
    public int getItemCount() {
        return lockedFileList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name, info;
        CheckBox selectionSwitch;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            info = itemView.findViewById(R.id.info);
            icon = itemView.findViewById(R.id.icon);
            selectionSwitch = itemView.findViewById(R.id.selectionSwitch);
        }
    }
    private void openFileInExternalApp(Context context, File file, String actualFileName) {
        if(Explorer.fileType(actualFileName) == Explorer.FileType.IMAGE){
            activity.startActivity(new Intent(context, com.ucllc.smcalculatorlock.MediaPages.PhotoViewer.class)
                    .putExtra("path", file.getAbsolutePath())
                    .putExtra("name", actualFileName));
        } else if (Explorer.fileType(actualFileName) == Explorer.FileType.VIDEO) {
            activity.startActivity(new Intent(context, com.ucllc.smcalculatorlock.MediaPages.VideoPlayer.class)
                    .putExtra("path", file.getAbsolutePath())
                    .putExtra("name", actualFileName));
        } else {
            Toast.makeText(context, "Unlock first to open!", Toast.LENGTH_SHORT).show();
        }
    }
}
