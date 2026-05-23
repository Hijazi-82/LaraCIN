package com.example.laracin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.laracin.data.AppDatabase;
import com.example.laracin.data.MyCinemaUserTable.MyCinemAdapter;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUser;

import java.util.ArrayList;

/**
 * FavoriteActivity
 *
 * شاشة المفضلة.
 *
 * وظيفة الشاشة:
 * 1. عرض المستخدمين الذين تم وضع نجمة عليهم.
 * 2. البحث داخل المستخدمين المفضلين حسب الاسم أو الدور.
 * 3. تحديث القائمة عند الرجوع للشاشة.
 * 4. التنقل بين Home و Projects و Profile.
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorite);

        etFavoriteSearch = findViewById(R.id.etFavoriteSearch);
        listFavoriteUsers = findViewById(R.id.listFavoriteUsers);

        navHome = findViewById(R.id.navHome);
        navProjects = findViewById(R.id.navProjects);
        navFavorite = findViewById(R.id.navFavorite);
        navProfile = findViewById(R.id.navProfile);

        adapteruser = new MyCinemAdapter(this, R.layout.actor_item_layout);
        listFavoriteUsers.setAdapter(adapteruser);

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
            // أنت موجود أصلًا في صفحة Favorite
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
        loadFavoriteUsers();
    }

    /**
     * loadFavoriteUsers
     *
     * تجلب المستخدمين المفضلين من Room
     * وتعرضهم داخل القائمة.
     */
    private void loadFavoriteUsers() {
        allFavoriteUsers.clear();
        allFavoriteUsers.addAll(
                AppDatabase.getDb(this)
                        .myCinemaUserQuery()
                        .getFavoriteUsers()
        );

        adapteruser.clear();
        adapteruser.addAll(allFavoriteUsers);
        adapteruser.notifyDataSetChanged();

        filterFavoriteUsers(etFavoriteSearch.getText().toString());
    }

    /**
     * filterFavoriteUsers
     *
     * تبحث داخل قائمة المفضلة حسب الاسم أو الدور.
     *
     * @param text النص الذي يكتبه المستخدم في البحث
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