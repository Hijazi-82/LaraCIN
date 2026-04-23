package com.example.laracin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.laracin.data.AppDatabase;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUser;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton btnBack, btnSettings;
    private LinearLayout btnEditProfile, btnViewWorks;
    private TextView navHome, navProjects, navMessages, navProfile;

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
        navMessages = findViewById(R.id.navMessages);
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

        navMessages.setOnClickListener(v -> {
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
        String currentEmail = null;

        if (auth.getCurrentUser() != null) {
            currentEmail = auth.getCurrentUser().getEmail();
        }

        if (TextUtils.isEmpty(currentEmail)) {
            return;
        }
        Intent i=getIntent();
        MyCinemaUser user = i.getParcelableExtra("cinmaUser");


        if (user == null) {
            return;
        }
        else
        {
         ///   btnEditProfile.setText("Edit Profile");
        }
        if(currentEmail.equals(user.email)==false)
        {
            btnEditProfile.setVisibility(View.GONE);
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
    }
}