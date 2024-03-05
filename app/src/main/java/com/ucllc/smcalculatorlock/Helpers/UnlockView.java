package com.ucllc.smcalculatorlock.Helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.itsxtt.patternlock.PatternLockView;
import com.ucllc.smcalculatorlock.Custom.Global;
import com.ucllc.smcalculatorlock.DataClasses.StateKeys;
import com.ucllc.smcalculatorlock.R;

import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Handler;

@SuppressLint("ViewConstructor")
public class UnlockView extends LinearLayout {
    private final String oldPattern;
    public UnlockView(Context context, String packageName) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_app_lock_screen, this, false);
        ImageView icon = view.findViewById(R.id.icon);
        PatternLockView patternLockView = view.findViewById(R.id.patternView);
        Global global = new Global(context, null);
        oldPattern = global.decrypt(Objects.requireNonNull(global.getDBHandler()).getStateValue(StateKeys.RECOVERY_PATTERN));
        try {
            icon.setImageDrawable(context.getPackageManager().getApplicationIcon(packageName));
        }
        catch (Exception e) {
            Global.log(e);
        }
        patternLockView.setOnPatternListener(new PatternLockView.OnPatternListener() {
            @Override
            public void onStarted() {
                //Do none
            }
            @Override
            public void onProgress(@NonNull ArrayList<Integer> arrayList) {
                //Do none
            }
            @Override
            public boolean onComplete(@NonNull ArrayList<Integer> arrayList) {
                if(null == oldPattern){
                    Toast.makeText(context, "No pattern found! Or, can not decrypt.", Toast.LENGTH_SHORT).show();
                    hideOverlay((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
                    return false;
                }
                else {
                    if(oldPattern.equals(arrayList.toString())){
                        hideOverlay((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
                    }
                    else {
                        showWrongPattern(view.findViewById(R.id.message));
                    }
                }
                return false;
            }
        });
        addView(view);
    }

    private void showWrongPattern(TextView message) {
        message.setText(R.string.wrong_pattern_try_again);
        new android.os.Handler().postDelayed(() -> message.setText(R.string.draw_your_pattern_to_unlock_the_app), 2000);
    }

    public void hideOverlay(WindowManager windowManager) {
        windowManager.removeView(this);
    }
}