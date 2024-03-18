package com.sm.calculatorlock.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sm.calculatorlock.Pages.Frags.AppLock;
import com.sm.calculatorlock.Pages.Frags.FileExplorer;
import com.sm.calculatorlock.Pages.Frags.FileVault;

public class HomeFragmentAdapter extends FragmentStateAdapter {
    public HomeFragmentAdapter(@NonNull FragmentActivity fragmentActivity){
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new FileVault();
            case 1:
                return new AppLock();
            case 2:
                return new FileExplorer();
        }
        return new FileVault();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
