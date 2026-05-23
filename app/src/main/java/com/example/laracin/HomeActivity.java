package com.example.laracin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.laracin.data.MyCinemaUserTable.MyCinemAdapter;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUser;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerativeBackend;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.Executor;

/**
 * HomeActivity
 *
 * شاشة الهوم.
 *
 * الوظائف:
 * 1. عرض جميع المستخدمين من Firebase.
 * 2. البحث العادي حسب الاسم أو الدور.
 * 3. البحث الذكي باستخدام AI.
 * 4. التنقل إلى Profile و Works و Favorite.
 */
public class HomeActivity extends AppCompatActivity {

    private TextView navProfile, navProjects, navFavorite;
    private EditText etSearch;
    private Button btnAiSearch;
    private ListView listusers;

    private MyCinemAdapter adapteruser;
    private ArrayList<MyCinemaUser> allUsers = new ArrayList<>();

    private GenerativeModel ai;
    private GenerativeModelFutures model;

    private DatabaseReference usersRef;
    private ValueEventListener usersListener;

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

        navProfile = findViewById(R.id.navProfile);
        navProjects = findViewById(R.id.navProjects);
        navFavorite = findViewById(R.id.navFavorite);
        etSearch = findViewById(R.id.etSearch);
        btnAiSearch = findViewById(R.id.btnAiSearch);
        listusers = findViewById(R.id.listusers);

        adapteruser = new MyCinemAdapter(this, R.layout.actor_item_layout);
        listusers.setAdapter(adapteruser);

        try {
            ai = FirebaseAI.getInstance(GenerativeBackend.googleAI())
                    .generativeModel("gemini-3-flash-preview");

            model = GenerativeModelFutures.from(ai);

        } catch (Exception e) {
            e.printStackTrace();
        }

        btnAiSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();

            if (query.isEmpty()) {
                etSearch.setError("Write what you are looking for");
                return;
            }

            if (model == null) {
                Toast.makeText(this, "AI is not available", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "AI is searching...", Toast.LENGTH_SHORT).show();
            askGemini(query);
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        navProjects.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, WorkActivity.class);
            startActivity(intent);
        });

        navFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, FavoriteActivity.class);
            startActivity(intent);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // غير مستخدمة
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // غير مستخدمة
            }
        });

        usersRef = FirebaseDatabase.getInstance().getReference("users");
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsersFromFirebase();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (usersRef != null && usersListener != null) {
            usersRef.removeEventListener(usersListener);
        }
    }

    /**
     * تحميل جميع المستخدمين من Firebase وعرضهم في Home.
     */
    private void loadUsersFromFirebase() {

        usersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                allUsers.clear();
                adapteruser.clear();

                for (DataSnapshot data : snapshot.getChildren()) {

                    MyCinemaUser user = data.getValue(MyCinemaUser.class);

                    if (user != null) {
                        allUsers.add(user);
                    }
                }

                adapteruser.addAll(allUsers);
                adapteruser.notifyDataSetChanged();

                if (allUsers.isEmpty()) {
                    Toast.makeText(HomeActivity.this,
                            "No users found in Firebase",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this,
                        "Failed to load users: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        };

        usersRef.addValueEventListener(usersListener);
    }

    /**
     * بحث عادي حسب الاسم أو الدور.
     */
    private void filterUsers(String text) {
        ArrayList<MyCinemaUser> filteredList = new ArrayList<>();
        String searchText = text.toLowerCase().trim();

        for (MyCinemaUser user : allUsers) {
            String fullName = user.getFullName() != null
                    ? user.getFullName().toLowerCase()
                    : "";

            String role = user.getRole() != null
                    ? user.getRole().toLowerCase()
                    : "";

            if (fullName.contains(searchText) || role.contains(searchText)) {
                filteredList.add(user);
            }
        }

        adapteruser.clear();
        adapteruser.addAll(filteredList);
        adapteruser.notifyDataSetChanged();
    }

    /**
     * بحث ذكي باستخدام Gemini.
     */
    private void askGemini(String userQuery) {
        if (allUsers.isEmpty()) {
            Toast.makeText(this, "No users available", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder usersInfo = new StringBuilder();

        for (MyCinemaUser user : allUsers) {
            usersInfo.append("- Name: ")
                    .append(user.getFullName() != null ? user.getFullName() : "")
                    .append(", Role: ")
                    .append(user.getRole() != null ? user.getRole() : "")
                    .append(", Skills: ")
                    .append(user.getSkills() != null ? user.getSkills() : "")
                    .append(", Experience: ")
                    .append(user.getExperienceYears())
                    .append(" years\n");
        }

        String promptStr =
                "You are a casting assistant for a cinema app.\n" +
                        "Here is a list of users:\n" +
                        usersInfo +
                        "\nUser request: " + userQuery + "\n" +
                        "Return ONLY the exact full names of matching users, separated by commas. " +
                        "If no one matches, return None. Do not explain.";

        Content prompt = new Content.Builder()
                .addText(promptStr)
                .build();

        ListenableFuture<GenerateContentResponse> response =
                model.generateContent(prompt);

        Executor executor = this::runOnUiThread;

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String aiNames = result.getText();

                if (aiNames == null
                        || aiNames.trim().isEmpty()
                        || aiNames.trim().equalsIgnoreCase("None")) {

                    Toast.makeText(HomeActivity.this,
                            "AI: No matches found",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                filterListByAI(aiNames);
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(HomeActivity.this,
                        "AI Error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }, executor);
    }

    /**
     * عرض نتائج AI فقط.
     */
    private void filterListByAI(String namesFromAI) {
        ArrayList<MyCinemaUser> filtered = new ArrayList<>();
        String namesLower = namesFromAI.toLowerCase();

        for (MyCinemaUser user : allUsers) {
            String fullName = user.getFullName() != null
                    ? user.getFullName().toLowerCase()
                    : "";

            if (!fullName.isEmpty() && namesLower.contains(fullName)) {
                filtered.add(user);
            }
        }

        adapteruser.clear();
        adapteruser.addAll(filtered);
        adapteruser.notifyDataSetChanged();

        Toast.makeText(this,
                "AI found " + filtered.size() + " result(s)",
                Toast.LENGTH_SHORT).show();
    }
}