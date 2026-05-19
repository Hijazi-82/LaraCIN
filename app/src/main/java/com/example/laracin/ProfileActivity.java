package com.example.laracin;

import android.content.Intent;
import android.os.Bundle;
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

/**
 * ProfileActivity
 *
 * شاشة بروفايل المستخدم.
 *
 * وظيفة الشاشة:
 * 1. جلب بيانات المستخدم الحالي من Firebase.
 * 2. عرض الاسم، الدور، والمهارات في الواجهة.
 * 3. فتح شاشة تعديل البروفايل عند الضغط على Edit Profile.
 * 4. التنقل بين Home, Projects, Favorite و Profile.
 */
public class ProfileActivity extends AppCompatActivity {

    // أزرار أعلى الشاشة
    private ImageButton btnBack;
    private ImageButton btnSettings;

    // أزرار داخل البروفايل
    private LinearLayout btnEditProfile;
    private LinearLayout btnViewWorks;

    // عناصر التنقل أسفل الشاشة
    private TextView navHome;
    private TextView navProjects;
    private TextView navFavorite;
    private TextView navProfile;

    // عناصر عرض بيانات المستخدم
    private ImageView imgProfile;
    private TextView tvName;
    private TextView tvRole;
    private TextView tvBio;
    private TextView tvWorksCount;

    // FirebaseAuth لمعرفة المستخدم المتصل حاليًا
    private FirebaseAuth auth;

    /**
     * onCreate
     *
     * يتم استدعاؤها عند فتح شاشة البروفايل.
     * داخلها يتم ربط عناصر الواجهة وتجهيز أزرار التنقل.
     *
     * @param savedInstanceState يحفظ حالة الشاشة عند إعادة إنشائها
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // ضبط الحواف حتى لا تدخل العناصر تحت أشرطة النظام
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // تهيئة FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // ربط أزرار أعلى الشاشة
        btnBack = findViewById(R.id.btnBack);
        btnSettings = findViewById(R.id.btnSettings);

        // ربط أزرار البروفايل
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnViewWorks = findViewById(R.id.btnViewWorks);

        // ربط عناصر التنقل
        navHome = findViewById(R.id.navHome);
        navProjects = findViewById(R.id.navProjects);
        navFavorite = findViewById(R.id.navFavorite);
        navProfile = findViewById(R.id.navProfile);

        // ربط عناصر عرض البيانات
        imgProfile = findViewById(R.id.imgProfile);
        tvName = findViewById(R.id.tvName);
        tvRole = findViewById(R.id.tvRole);
        tvBio = findViewById(R.id.tvBio);
        tvWorksCount = findViewById(R.id.tvWorksCount);

        // الرجوع إلى الشاشة السابقة
        btnBack.setOnClickListener(v -> finish());

        // فتح شاشة روابط الأعمال
        btnViewWorks.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, WorkActivity.class);
            startActivity(intent);
        });

        // الانتقال إلى Home
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        // الانتقال إلى Projects / Works
        navProjects.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, WorkActivity.class);
            startActivity(intent);
        });

        // الانتقال إلى Favorite
        navFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, FavoriteActivity.class);
            startActivity(intent);
        });

        // المستخدم موجود أصلًا في شاشة Profile
        navProfile.setOnClickListener(v -> {
            // لا حاجة لفتح شاشة جديدة
        });
    }

    /**
     * onResume
     *
     * يتم استدعاؤها كل مرة ترجع فيها الشاشة للواجهة.
     * هنا يتم تحديث بيانات المستخدم من Firebase.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    /**
     * loadUserData
     *
     * تجلب بيانات المستخدم الحالي من Firebase حسب uid.
     * بعدها تعرض البيانات داخل عناصر الواجهة.
     */
    private void loadUserData() {

        // الوصول إلى users في Firebase
        DatabaseReference myRef = FirebaseDatabase
                .getInstance()
                .getReference("users");

        // جلب uid للمستخدم الحالي
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        myRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {

            /**
             * onDataChange
             *
             * يتم استدعاؤها عند وصول بيانات المستخدم من Firebase.
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                MyCinemaUser user = snapshot.getValue(MyCinemaUser.class);

                if (user == null) {
                    Toast.makeText(ProfileActivity.this,
                            "user error",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // فتح شاشة تعديل البروفايل مع إرسال بيانات المستخدم
                btnEditProfile.setOnClickListener(v -> {
                    Intent intent = new Intent(ProfileActivity.this, SaveProfileActivity.class);
                    intent.putExtra("cinmaUser", user);
                    startActivity(intent);
                });

                // عرض بيانات المستخدم في الواجهة
                tvName.setText(user.getFullName() != null ? user.getFullName() : "");
                tvRole.setText(user.getRole() != null ? user.getRole() : "");
                tvBio.setText(user.getSkills() != null ? user.getSkills() : "");

                // عدد الأعمال، حاليًا ثابت
                tvWorksCount.setText("0");
            }

            /**
             * onCancelled
             *
             * يتم استدعاؤها إذا فشلت قراءة البيانات من Firebase.
             */
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this,
                        "Failed to load profile: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}