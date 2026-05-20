package com.example.laracin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.laracin.data.MyCinemaUserTable.MyCinemaUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton btnBack, btnSettings;
    private LinearLayout btnEditProfile, btnViewWorks;
    private TextView navHome, navProjects, navFavorite, navProfile;

    private ImageView imgProfile;
    private TextView tvName, tvRole, tvBio, tvWorksCount;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();

        btnBack = findViewById(R.id.btnBack);
        btnSettings = findViewById(R.id.btnSettings);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnViewWorks = findViewById(R.id.btnViewWorks);

        navHome = findViewById(R.id.navHome);
        navProjects = findViewById(R.id.navProjects);
        navFavorite = findViewById(R.id.navFavorite);
        navProfile = findViewById(R.id.navProfile);

        imgProfile = findViewById(R.id.imgProfile);
        tvName = findViewById(R.id.tvName);
        tvRole = findViewById(R.id.tvRole);
        tvBio = findViewById(R.id.tvBio);
        tvWorksCount = findViewById(R.id.tvWorksCount);

        btnBack.setOnClickListener(v -> finish());

        btnViewWorks.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, WorkActivity.class);
            startActivity(intent);
        });

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        navProjects.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, WorkActivity.class);
            startActivity(intent);
        });

        navFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, FavoriteActivity.class);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users");
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        myRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MyCinemaUser user = snapshot.getValue(MyCinemaUser.class);

                if (user == null) {
                    Toast.makeText(ProfileActivity.this, "user error", Toast.LENGTH_SHORT).show();
                    return;
                }

                btnEditProfile.setOnClickListener(v -> {
                    Intent intent = new Intent(ProfileActivity.this, SaveProfileActivity.class);
                    intent.putExtra("cinmaUser", user);
                    startActivity(intent);
                });

                tvName.setText(user.getFullName() != null ? user.getFullName() : "");
                tvRole.setText(user.getRole() != null ? user.getRole() : "");
                tvBio.setText(user.getSkills() != null ? user.getSkills() : "");
                tvWorksCount.setText("0");

                if (user.getProfileImageUri() != null && !user.getProfileImageUri().isEmpty()) {
                    imgProfile.setImageBitmap(stringToBitmap(user.getProfileImageUri()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private Bitmap stringToBitmap(String imageString) {
        if (imageString == null || imageString.isEmpty()) {
            return null;
        }

        try {
            byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception e) {
            return null;
        }
    }
}