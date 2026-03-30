package com.example.laracin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.laracin.data.AppDatabase;
import com.example.laracin.data.MyCinemaUserTable.MyCinemAdapter;

public class FavoriteActivity extends AppCompatActivity {

    private ListView listFavoriteUsers;
    private MyCinemAdapter adapteruser;

    private TextView navHome;
    private TextView navProjects;
    private TextView navFavorite;
    private TextView navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorite);

        listFavoriteUsers = findViewById(R.id.listFavoriteUsers);

        navHome = findViewById(R.id.navHome);
        navProjects = findViewById(R.id.navProjects);
        navFavorite = findViewById(R.id.navFavorite);
        navProfile = findViewById(R.id.navProfile);

        adapteruser = new MyCinemAdapter(this, R.layout.actor_item_layout);
        listFavoriteUsers.setAdapter(adapteruser);

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteActivity.this, Activity_main1.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapteruser.clear();
        adapteruser.addAll(AppDatabase.getDb(this).myCinemaUserQuery().getFavoriteUsers());
        adapteruser.notifyDataSetChanged();
    }
}