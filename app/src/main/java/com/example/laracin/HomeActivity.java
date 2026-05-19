package com.example.laracin;

import android.annotation.SuppressLint;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * HomeActivity
 *
 * الشاشة الرئيسية في التطبيق.
 *
 * وظيفة الشاشة:
 * 1. جلب المستخدمين من Firebase Realtime Database.
 * 2. عرض المستخدمين داخل ListView.
 * 3. البحث عن مستخدم حسب الاسم أو الدور.
 * 4. التنقل إلى شاشة البروفايل، روابط الأعمال، والمفضلة.
 */
public class HomeActivity extends AppCompatActivity {

    // عناصر التنقل أسفل الشاشة
    private TextView navProfile, navProjects ,navFavorite;

    // حقل البحث عن المستخدمين
    private EditText etSearch;

    // قائمة عرض المستخدمين
    private ListView listusers;

    // Adapter يربط بيانات المستخدمين مع ListView
    private MyCinemAdapter adapteruser;

    // قائمة تحفظ جميع المستخدمين الذين تم جلبهم من Firebase
    private ArrayList<MyCinemaUser> allUsers = new ArrayList<>();

    /**
     * onCreate
     *
     * يتم استدعاؤها عند فتح شاشة Home.
     * داخلها يتم ربط عناصر الواجهة، تجهيز الـ Adapter،
     * تفعيل أزرار التنقل، وتجهيز البحث.
     */
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

        // ربط عناصر الواجهة
        navProfile = findViewById(R.id.navProfile);
        navProjects = findViewById(R.id.navProjects);
        navFavorite = findViewById(R.id.navFavorite);
        etSearch = findViewById(R.id.etSearch);
        listusers = findViewById(R.id.listusers);

        // إنشاء Adapter وربطه مع ListView
        adapteruser = new MyCinemAdapter(this, R.layout.actor_item_layout);
        listusers.setAdapter(adapteruser);

        // الانتقال إلى شاشة البروفايل
        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // الانتقال إلى شاشة روابط الأعمال
        navProjects.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, WorkActivity.class);
            startActivity(intent);
        });

        // الانتقال إلى شاشة المفضلة
        navFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, FavoriteActivity.class);
            startActivity(intent);
        });

        // البحث أثناء الكتابة
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
    }

    /**
     * onResume
     *
     * يتم استدعاؤها كل مرة يرجع فيها المستخدم إلى شاشة Home.
     * هنا يتم تحميل المستخدمين من Firebase.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadUsersFromFirebase();
    }

    /**
     * loadUsersFromFirebase
     *
     * تجلب جميع المستخدمين من Firebase Realtime Database
     * من المسار users، ثم تعرضهم داخل ListView.
     */
    private void loadUsersFromFirebase() {

        DatabaseReference usersRef = FirebaseDatabase
                .getInstance()
                .getReference("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                allUsers.clear();
                adapteruser.clear();

                for (DataSnapshot data : snapshot.getChildren()) {

                    MyCinemaUser user = data.getValue(MyCinemaUser.class);

                    if (user != null) {
                        allUsers.add(user);
                        adapteruser.add(user);
                    }
                }

                adapteruser.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this,
                        "Failed to load users: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * filterUsers
     *
     * تبحث داخل قائمة المستخدمين حسب:
     * - الاسم الكامل fullName
     * - الدور role
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
}