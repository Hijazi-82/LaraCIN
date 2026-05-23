package com.example.laracin.data.MyCinemaUserTable;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.laracin.HomeActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

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

    /**
     * saveUserToFirebase
     *
     * تحفظ أو تحدث بيانات المستخدم في Firebase.
     *
     * مهم:
     * لا نستخدم setValue(user) هنا لأنه يستبدل كل بيانات المستخدم
     * وقد يحذف works و workCount.
     *
     * لذلك نستخدم updateChildren حتى نحدث بيانات البروفايل فقط
     * ونترك الأعمال محفوظة كما هي.
     */
    private void saveUserToFirebase(MyCinemaUser user) {

        DatabaseReference usersRef = FirebaseDatabase
                .getInstance()
                .getReference("users");

        if (user.getKey() == null || user.getKey().isEmpty()) {
            String key = usersRef.push().getKey();
            user.setKey(key);
        }

        HashMap<String, Object> updates = new HashMap<>();

        updates.put("key", user.getKey());
        updates.put("keyId", user.getKeyId());

        updates.put("fullName", user.getFullName());
        updates.put("email", user.getEmail());
        updates.put("password", user.getPassword());

        updates.put("phone", user.getPhone());
        updates.put("role", user.getRole());
        updates.put("portfolio", user.getPortfolio());
        updates.put("profileImageUri", user.getProfileImageUri());

        updates.put("experienceYears", user.getExperienceYears());
        updates.put("skills", user.getSkills());

        updates.put("workName", user.getWorkName());
        updates.put("workType", user.getWorkType());
        updates.put("workDescription", user.getWorkDescription());
        updates.put("workLink", user.getWorkLink());

        updates.put("favorite", user.isFavorite());
        updates.put("workCount", user.getWorkCount());

        usersRef.child(user.getKey()).updateChildren(updates).addOnCompleteListener(fbTask -> {
            if (fbTask.isSuccessful()) {
                Toast.makeText(getApplicationContext(),
                        "User Saved Successfully",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            } else {
                Toast.makeText(getApplicationContext(),
                        "Saving Failed",
                        Toast.LENGTH_SHORT).show();
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