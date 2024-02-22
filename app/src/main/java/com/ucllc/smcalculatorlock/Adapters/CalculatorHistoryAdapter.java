package com.ucllc.smcalculatorlock.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ucllc.smcalculatorlock.Pages.Calculator;
import com.ucllc.smcalculatorlock.Custom.Global;
import com.ucllc.smcalculatorlock.R;

import java.util.List;

public class CalculatorHistoryAdapter extends RecyclerView.Adapter<CalculatorHistoryAdapter.ViewHolder> {
    private final List<String> itemList;
    public CalculatorHistoryAdapter(List<String> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calculator_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = itemList.get(position);
        try {
            holder.data.setText(Calculator.signFix(item));
        } catch (Exception e) {
            Global.logError(e);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView data;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            data = itemView.findViewById(R.id.data);
        }
    }
}