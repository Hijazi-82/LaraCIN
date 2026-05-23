package com.example.laracin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * FavoriteActivity
 *
 * شاشة المفضلة الخاصة بالمستخدم الحالي.
 *
 * الوظائف:
 * 1. قراءة المفضلة من Firebase حسب المستخدم الحالي.
 * 2. جلب بيانات كل مستخدم مفضل من users.
 * 3. عرض المستخدمين المفضلين داخل ListView.
 * 4. البحث داخل المفضلة حسب الاسم أو الدور.
 * 5. التنقل بين Home و Projects و Profile.
 *
 * المسار المستخدم للمفضلة:
 * favorites/currentUserUid/userKey
 */
public class FavoriteActivity extends AppCompatActivity {

    private EditText etFavoriteSearch;
    private ListView listFavoriteUsers;

    private MyCinemAdapter adapteruser;

    private TextView navHome;
    private TextView navProjects;
    private TextView navFavorite;
    private TextView navProfile;

    private ArrayList<MyCinemaUser> allFavoriteUsers = new ArrayList<>();

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorite);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainFavorite), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etFavoriteSearch = findViewById(R.id.etFavoriteSearch);
        listFavoriteUsers = findViewById(R.id.listFavoriteUsers);

        navHome = findViewById(R.id.navHome);
        navProjects = findViewById(R.id.navProjects);
        navFavorite = findViewById(R.id.navFavorite);
        navProfile = findViewById(R.id.navProfile);

        adapteruser = new MyCinemAdapter(this, R.layout.actor_item_layout);
        listFavoriteUsers.setAdapter(adapteruser);

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        etFavoriteSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // غير مستخدمة
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFavoriteUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // غير مستخدمة
            }
        });

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        navProjects.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteActivity.this, WorkActivity.class);
            startActivity(intent);
            finish();
        });

        navFavorite.setOnClickListener(v -> {
            // المستخدم موجود أصلًا في شاشة Favorite
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavoriteUsersFromFirebase();
    }

    /**
     * loadFavoriteUsersFromFirebase
     *
     * تجلب مفضلة المستخدم الحالي فقط.
     *
     * أولًا نقرأ:
     * favorites/currentUserUid
     *
     * ثم لكل userKey موجود هناك،
     * نجلب بيانات المستخدم من:
     * users/userKey
     */
    private void loadFavoriteUsersFromFirebase() {

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "No signed in user", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUid = FirebaseAuth
                .getInstance()
                .getCurrentUser()
                .getUid();

        DatabaseReference favoritesRef = FirebaseDatabase
                .getInstance()
                .getReference("favorites")
                .child(currentUid);

        favoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot favoriteSnapshot) {

                allFavoriteUsers.clear();

                if (!favoriteSnapshot.exists()) {
                    adapteruser.clear();
                    adapteruser.notifyDataSetChanged();
                    return;
                }

                for (DataSnapshot favData : favoriteSnapshot.getChildren()) {

                    String userKey = favData.getKey();

                    if (userKey == null || userKey.isEmpty()) {
                        continue;
                    }

                    usersRef.child(userKey)
                            .addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot userSnapshot) {

                                    MyCinemaUser user =
                                            userSnapshot.getValue(MyCinemaUser.class);

                                    if (user != null) {
                                        user.setKey(userSnapshot.getKey());
                                        user.setFavorite(true);
                                        allFavoriteUsers.add(user);
                                    }

                                    filterFavoriteUsers(
                                            etFavoriteSearch
                                                    .getText()
                                                    .toString()
                                    );
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(FavoriteActivity.this,
                                            "Failed to load user",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FavoriteActivity.this,
                        "Failed to load favorites",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * filterFavoriteUsers
     *
     * تبحث داخل قائمة المفضلة حسب الاسم أو الدور.
     *
     * @param text النص المكتوب في خانة البحث
     */
    private void filterFavoriteUsers(String text) {

        ArrayList<MyCinemaUser> filteredList = new ArrayList<>();
        String searchText = text.toLowerCase().trim();

        for (MyCinemaUser user : allFavoriteUsers) {

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
}