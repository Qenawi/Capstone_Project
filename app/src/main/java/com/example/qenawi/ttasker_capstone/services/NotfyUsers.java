package com.example.qenawi.ttasker_capstone.services;

import android.content.Intent;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.qenawi.ttasker_capstone.modle.NotificationItem;
import com.example.qenawi.ttasker_capstone.modle.Smsitem;
import com.example.qenawi.ttasker_capstone.modle.UserprojectItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by QEnawi on 7/27/2017.
 */

public class NotfyUsers extends MyBaseTaskService {
    private FirebaseDatabase fdb = FirebaseDatabase.getInstance();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        taskStarted();
        Smsitem smsitem = (Smsitem) intent.getExtras().getParcelable("Smsitem");
        UserprojectItem userprojectItem = (UserprojectItem) intent.getExtras().getParcelable("UserprojectItem");
        //------------------
        try {
            get_users_list(smsitem, userprojectItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_REDELIVER_INTENT;//
    }

    void get_users_list(final Smsitem item, final UserprojectItem Pkey) throws Exception {
        DatabaseReference fdbr = fdb.getReference().child("pmember").child(Pkey.getPkey());
        final ArrayList<String> e = new ArrayList<>();
        fdbr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("servcexyz", dataSnapshot.getChildrenCount() + " ");
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    e.add(dataSnapshot1.child("key").getValue(String.class));
                    Log.v("servcexyz", e.get(e.size() - 1) + " ");
                }
                notfy_user(e, item, Pkey);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void notfy_user(ArrayList<String> userkyes, Smsitem item, UserprojectItem Pkey) {
        for (int i = 0; i < userkyes.size(); i++) {
            Log.v("servcexyz", userkyes.size() + " ");
            Log.v("servcexyz", userkyes.get(i) + " ");
            DatabaseReference dbr = fdb.getReference().child("Notification").child(userkyes.get(i));
            dbr.child(dbr.push().getKey()).setValue(new NotificationItem("chattype", item.getMsg(), item.getSender(), Pkey.getPkey(), getStoredPair(), item.getTime(), "0"));
        }
        taskCompleted();
    }

    String getStoredPair() {
        return PreferenceManager.getDefaultSharedPreferences(this).getString("eTa", "null");
    }
}
