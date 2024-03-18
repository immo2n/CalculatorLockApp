package com.sm.calculatorlock.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sm.calculatorlock.Custom.DBHandler;
import com.sm.calculatorlock.Helpers.AppListHelper;
import com.sm.calculatorlock.Pages.Home;
import com.sm.calculatorlock.R;

import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder>{
    private final List<AppListHelper.AppInfo> itemList;
    private final DBHandler dbHandler;

    public AppListAdapter(List<AppListHelper.AppInfo> itemList, Context context) {
        this.itemList = itemList;
        this.dbHandler = new DBHandler(context);
    }

    @NonNull
    @Override
    public AppListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_list, parent, false);
        return new AppListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppListAdapter.ViewHolder holder, int position) {
        AppListHelper.AppInfo item = itemList.get(position);

        // Clearing the views
        holder.lockSwitch.setOnCheckedChangeListener(null);
        holder.icon.setImageDrawable(null);
        holder.name.setText(null);
        holder.info.setText(null);

        // Setting the views
        holder.lockSwitch.setChecked(dbHandler.isAppLocked(item.getPackageName()));
        holder.name.setText(item.getAppName());
        holder.info.setText(item.getPackageName());
        holder.icon.setImageDrawable(item.getIcon());

        if(position == 0 || position == getItemCount() - 1){
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.root.getLayoutParams();
            if(position == 0) layoutParams.setMargins(0, 15, 0, 0);
            else layoutParams.setMargins(0, 0, 0, 15);
            holder.root.setLayoutParams(layoutParams);
        }
        holder.lockSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) dbHandler.addLockedApp(item.getPackageName());
            else dbHandler.removeLockedApp(item.getPackageName());
            Home.loadAdOnHome.loadAd();
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, info;
        ImageView icon;
        LinearLayout root;
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch lockSwitch;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            icon = itemView.findViewById(R.id.icon);
            info = itemView.findViewById(R.id.info);
            name = itemView.findViewById(R.id.name);
            lockSwitch = itemView.findViewById(R.id.lockSwitch);
        }
    }
}
