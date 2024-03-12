package com.ucllc.smcalculatorlock.UniCore;

import android.app.Activity;
import android.content.Context;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ucllc.smcalculatorlock.Custom.Config;
import com.ucllc.smcalculatorlock.Custom.DBHandler;
import com.ucllc.smcalculatorlock.DataClasses.StateKeys;

import java.util.Date;

public class LisenceCheck {
    private final Context context;
    private final Activity activity;
    private final DBHandler dbHandler;
    private final FirebaseFirestore mainDB;
    public static final String
            LISENCE_KEY_FIELD = "KEY",
            LISENCE_KEY_EXPIRY = "EXPIRY",
            LISENCE_KEY_CONTACT = "CONTACT",
            LISENCE_KEY_ISSUE_DATE = "ISSUE_DATE",
            LISENCE_COLLECTION = "Lisence";
    public static int ALLOWED_CHANCES = 3;
    private final String LISENCE_DOCUMENT;

    public LisenceCheck(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.mainDB = FirebaseFirestore.getInstance();
        this.LISENCE_DOCUMENT = activity.getPackageName();
        dbHandler = new DBHandler(context);
        checkLisence();
    }
    private void checkLisence() {
        this.mainDB.collection(LISENCE_COLLECTION).document(LISENCE_DOCUMENT).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    String lisenceKey = task.getResult().getString(LISENCE_KEY_FIELD);
                    Timestamp expiry = task.getResult().getTimestamp(LISENCE_KEY_EXPIRY);
                    Timestamp issue = task.getResult().getTimestamp(LISENCE_KEY_ISSUE_DATE);
                    String contact = task.getResult().getString(LISENCE_KEY_CONTACT);
                    Date expiryDate = null;
                    if(null != expiry) expiryDate = expiry.toDate();
                    if (!Config.LISENCE_KEY.equals(lisenceKey) || expiryDate == null || null == issue || issue.toDate().after(expiryDate)) {
                        int usedChances;
                        String chancesTaken = dbHandler.getStateValue(StateKeys.LICENSE_CHANCES_TAKEN);
                        if(null == chancesTaken) usedChances = 0;
                        else usedChances = Integer.parseInt(chancesTaken);
                        int chancesLeft = ALLOWED_CHANCES - usedChances;
                        String issueDate = "N/A", expiryDateString = "N/A";
                        if(null != issue) issueDate = issue.toDate().toString();
                        if(null != expiryDate) expiryDateString = expiryDate.toString();
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                        builder.setTitle("App License Expired")
                                .setMessage("This app needs a yearly maintenance and lisence update. Please contact the publishers to take action. If you are the publisher contact us soon. Please free your data and do not use the app until the next update."+
                                        "\n\nISSUED: "+ issueDate +"\nEXPIRED: "+ expiryDateString +"\nCONTACT: "+contact+"\nCHANCES LEFT: "+chancesLeft+" out of "+ALLOWED_CHANCES+".")
                                .setCancelable(false);
                        if(chancesLeft > 0){
                            builder.setNegativeButton("OK, understood", (dialog, which) -> {
                                dialog.dismiss();
                                dbHandler.setAppState(StateKeys.LICENSE_CHANCES_TAKEN, String.valueOf(usedChances+1));
                            });
                        }
                        builder.show();
                    }
                }
            }
        });
    }
}
