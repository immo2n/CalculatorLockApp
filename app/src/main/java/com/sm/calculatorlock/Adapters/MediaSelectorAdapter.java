package com.sm.calculatorlock.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sm.calculatorlock.Custom.Global;
import com.sm.calculatorlock.R;
import com.sm.calculatorlock.Sheets.FileSelection;

import java.io.File;
import java.util.List;

public class MediaSelectorAdapter extends RecyclerView.Adapter<MediaSelectorAdapter.ViewHolder> {
    private final List<File> fileList;
    private final FileSelection.OnFileSelectedListener onFileSelectedListener;
    private final Context context;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_selector_item, parent, false);
        return new MediaSelectorAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        File file = fileList.get(position);
        //Reset needed!
        holder.videoLength.setVisibility(View.GONE);
        holder.selectedSymbol.setVisibility(View.GONE);
        holder.relativeLayout.setBackground(null);
        //Check if selected already
        if(FileSelection.selectedFilePaths.contains(file.getAbsolutePath())){
            holder.relativeLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.file_selected));
            holder.selectedSymbol.setVisibility(View.VISIBLE);
        }
        Glide.with(context)
                .load(file.getAbsolutePath())
                .centerCrop()
                .placeholder((Global.isVideoFile(file)) ? R.drawable.file_video : R.drawable.file_image)
                .into(holder.imageView);
        if(Global.isVideoFile(file)){
            holder.videoLength.setText(Global.getVideoDuration(file.getAbsolutePath()));
            holder.videoLength.setVisibility(View.VISIBLE);
        }
        holder.relativeLayout.setOnClickListener(view -> {
            if(FileSelection.selectedFilePaths.contains(file.getAbsolutePath())){
                //Remove
                FileSelection.selectedFilePaths.remove(file.getAbsolutePath());
                holder.selectedSymbol.setVisibility(View.GONE);
                onFileSelectedListener.onFileSelection(false);
            }
            else {
                //Add
                FileSelection.selectedFilePaths.add(file.getAbsolutePath());
                holder.selectedSymbol.setVisibility(View.VISIBLE);
                onFileSelectedListener.onFileSelection(true);
            }
        });
    }

    public MediaSelectorAdapter(List<File> fileList, Context context, FileSelection.OnFileSelectedListener onFileSelectedListener){
        this.onFileSelectedListener = onFileSelectedListener;
        this.fileList = fileList;
        this.context = context;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, selectedSymbol;
        RelativeLayout relativeLayout;
        TextView videoLength;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            relativeLayout = itemView.findViewById(R.id.lin);
            videoLength = itemView.findViewById(R.id.videoLength);
            selectedSymbol = itemView.findViewById(R.id.selectedSymbol);
        }
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }
}
