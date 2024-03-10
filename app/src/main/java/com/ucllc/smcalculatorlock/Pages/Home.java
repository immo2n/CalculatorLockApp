package com.ucllc.smcalculatorlock.Pages;

import static com.ucllc.smcalculatorlock.Custom.Global.hasOverlayPermission;
import static com.ucllc.smcalculatorlock.Custom.Global.hasUsageStatsPermission;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ucllc.smcalculatorlock.Adapters.HomeFragmentAdapter;
import com.ucllc.smcalculatorlock.Custom.Global;
import com.ucllc.smcalculatorlock.DataClasses.StateKeys;
import com.ucllc.smcalculatorlock.R;
import com.ucllc.smcalculatorlock.databinding.ActivityHomeBinding;

public class Home extends AppCompatActivity {
    public interface onFragmentControl {
        void onBack();
        void onNeedReload();
    }
    public static onFragmentControl fragmentControlExplorer = null,
            fragmentControlApps = null,
            fragmentControlVault = null;
    ActivityHomeBinding binding;
    private Global global;
    public static boolean appLockPermissionCheck = false;
    public static int currentTabIndex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        global = new Global(this, this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.midnight_black_button));

        //Back click handle
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(fragmentControlExplorer == null){
                    finish();
                }
                else {
                    fragmentControlExplorer.onBack();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        //Tab adapter
        HomeFragmentAdapter adapter = new HomeFragmentAdapter(this);
        binding.homePager.setAdapter(adapter);
        binding.homePager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentTabIndex = position;
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
                switch (position){
                    case 0:
                        if(null != fragmentControlVault) fragmentControlVault.onNeedReload();
                        binding.pageTitle.setText(getString(R.string.vault));
                        break;
                    case 1:
                        if(null != fragmentControlApps) fragmentControlApps.onNeedReload();
                        binding.pageTitle.setText(getString(R.string.apps));
                        break;
                    case 2:
                        if(null != fragmentControlExplorer) fragmentControlExplorer.onNeedReload();
                        binding.pageTitle.setText(getString(R.string.explorer));
                        break;
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
        binding.browser.setOnClickListener(view -> startActivity(new Intent(Home.this, Browser.class)));
    }

    private final ActivityResultLauncher<Intent> accessibilityServiceLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            if(hasUsageStatsPermission(this)){
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
            }
            if(hasOverlayPermission(this)){
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
            }
        }
    });

    @Override
    protected void onResume() {
        super.onResume();
        if(appLockPermissionCheck){
            if(hasOverlayPermission(this)){
                if(!hasUsageStatsPermission(this)){
                    Global.propUsageStatsPermission(this, accessibilityServiceLauncher);
                }
            }
            appLockPermissionCheck = false;
        }
    }
}