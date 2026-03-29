package com.example.laracin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.laracin.data.AppDatabase;
import com.example.laracin.data.MyCinemaUserTable.MyCinemUserAdapter;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUser;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private TextView tvSearchMovies, tvMyList, navProfile;
    private EditText etSearch;
    private ListView listusers;
    private MyCinemUserAdapter adapteruser;
    private ArrayList<MyCinemaUser> allUsers = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_screen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvSearchMovies = findViewById(R.id.tvSearchMovies);
        tvMyList = findViewById(R.id.tvMyList);
        navProfile = findViewById(R.id.navProfile);
        etSearch = findViewById(R.id.etSearch);
        listusers = findViewById(R.id.listusers);

        adapteruser = new MyCinemUserAdapter(this, R.layout.actor_item_layout);
        listusers.setAdapter(adapteruser);

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        allUsers.clear();
        allUsers.addAll(AppDatabase.getDb(this).myCinemaUserQuery().getAllUsers());

        adapteruser.clear();
        adapteruser.addAll(allUsers);
        adapteruser.notifyDataSetChanged();
    }

    private void filterUsers(String text) {
        ArrayList<MyCinemaUser> filteredList = new ArrayList<>();
        String searchText = text.toLowerCase().trim();

        for (MyCinemaUser user : allUsers) {
            String fullName = user.getFullName() != null ? user.getFullName().toLowerCase() : "";
            String role = user.getRole() != null ? user.getRole().toLowerCase() : "";

            if (fullName.contains(searchText) || role.contains(searchText)) {
                filteredList.add(user);
            }
        }

        adapteruser.clear();
        adapteruser.addAll(filteredList);
        adapteruser.notifyDataSetChanged();
    }
}