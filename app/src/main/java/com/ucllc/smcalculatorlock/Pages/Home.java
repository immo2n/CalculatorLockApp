package com.ucllc.smcalculatorlock.Pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ucllc.smcalculatorlock.Adapters.HomeFragmentAdapter;
import com.ucllc.smcalculatorlock.Custom.DBHandler;
import com.ucllc.smcalculatorlock.Custom.Global;
import com.ucllc.smcalculatorlock.DataClasses.StateKeys;
import com.ucllc.smcalculatorlock.R;
import com.ucllc.smcalculatorlock.databinding.ActivityHomeBinding;

public class Home extends AppCompatActivity {
    ActivityHomeBinding binding;
    private Global global;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        global = new Global(this, this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.midnight_black_button));

        //Tab adapter
        HomeFragmentAdapter adapter = new HomeFragmentAdapter(this);
        binding.homePager.setAdapter(adapter);
        binding.homePager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if(null != global.getContext()) {
                    binding.tabVault.setTextColor(ContextCompat.getColor(global.getContext(),
                            (0 == position) ? R.color.white : R.color.off_white
                    ));
                    binding.tabApps.setTextColor(ContextCompat.getColor(global.getContext(),
                            (1 == position) ? R.color.white : R.color.off_white
                    ));
                    binding.tabFiles.setTextColor(ContextCompat.getColor(global.getContext(),
                            (2 == position) ? R.color.white : R.color.off_white
                    ));
                    //Titles
                    binding.vaultText.setTextColor(ContextCompat.getColor(global.getContext(),
                            (0 == position) ? R.color.white : R.color.off_white
                    ));
                    binding.appsText.setTextColor(ContextCompat.getColor(global.getContext(),
                            (1 == position) ? R.color.white : R.color.off_white
                    ));
                    binding.filesText.setTextColor(ContextCompat.getColor(global.getContext(),
                            (2 == position) ? R.color.white : R.color.off_white
                    ));
                    YoYo.with(Techniques.ZoomIn).duration(120).playOn(binding.homePager);
                }
            }
        });

        //Hooks
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_key);
        if(null != dialog.getWindow()){
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.dialogue_background));
            dialog.getWindow().findViewById(R.id.changePinButton).setOnClickListener(v-> {
                startActivity(new Intent(Home.this, PatternLock.class));
            });
            dialog.getWindow().findViewById(R.id.chagePatternButton).setOnClickListener(v-> {
                if(null != global.getDBHandler()) global.getDBHandler().deleteStateValue(StateKeys.RECOVERY_PATTERN);
                startActivity(new Intent(Home.this, PatternLock.class).putExtra("setup", true));
            });
        }
        binding.key.setOnClickListener(v-> dialog.show());
        binding.tabVault.setOnClickListener(v-> binding.homePager.setCurrentItem(0, false));
        binding.tabApps.setOnClickListener(v-> binding.homePager.setCurrentItem(1, false));
        binding.tabFiles.setOnClickListener(v-> binding.homePager.setCurrentItem(2, false));
        binding.homePager.setUserInputEnabled(false);

    }
}