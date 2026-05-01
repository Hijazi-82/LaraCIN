package com.example.laracin.data.MyCinemaUserTable;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.laracin.HomeActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CinemaUserService extends Service {

    public static final String EXTRA_USER = "user_extra";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.hasExtra(EXTRA_USER)) {
            MyCinemaUser user = (MyCinemaUser) intent.getSerializableExtra(EXTRA_USER);

            if (user != null) {
                saveUserToFirebase(user);
            } else {
                stopSelf();
            }
        } else {
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    private void saveUserToFirebase(MyCinemaUser user) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users");
        String key="";
        if(user.getKey()==null || user.getKey().isEmpty())
        {
            key = myRef.push().getKey();
            user.setKey(key);
        }



        myRef.child(user.getKey()).setValue(user).addOnCompleteListener(fbTask -> {
            if (fbTask.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "User Saved Successfully", Toast.LENGTH_SHORT).show();
                stopSelf();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            } else {
                Toast.makeText(getApplicationContext(), "Saving Failed", Toast.LENGTH_SHORT).show();
            }

            stopSelf();
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}