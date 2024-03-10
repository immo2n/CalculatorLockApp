package com.ucllc.smcalculatorlock.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ucllc.smcalculatorlock.Pages.Frags.AppLock;
import com.ucllc.smcalculatorlock.Pages.Frags.FileExplorer;
import com.ucllc.smcalculatorlock.Pages.Frags.FileVault;
import com.ucllc.smcalculatorlock.Pages.Home;

public class HomeFragmentAdapter extends FragmentStateAdapter {
    public interface UserActionEvents {
        void onCloseUiSignal();
    }
    private final UserActionEvents actionEvents;
    public HomeFragmentAdapter(@NonNull FragmentActivity fragmentActivity, UserActionEvents actionEvents){
        super(fragmentActivity);
        this.actionEvents = actionEvents;
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
                return new FileExplorer(actionEvents);
        }
        return new FileVault();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
