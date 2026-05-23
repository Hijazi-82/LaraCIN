package com.example.laracin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
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
 * شاشة عرض بروفايل المستخدم.
 *
 * وظيفة الشاشة:
 * 1. إذا تم فتحها من زر Profile في الـ navigation:
 *    تعرض بروفايل المستخدم الحالي.
 *
 * 2. إذا تم فتحها من القلم داخل item في Home:
 *    تعرض بروفايل المستخدم الذي تم الضغط عليه.
 *
 * 3. زر Edit Profile يظهر فقط إذا كان البروفايل المعروض
 *    هو بروفايل المستخدم الحالي.
 *
 * 4. تعرض بيانات المستخدم:
 *    الاسم، الدور، المهارات، عدد الأعمال، وصورة البروفايل.
 */
public class ProfileActivity extends AppCompatActivity {

    // أزرار أعلى الشاشة
    private ImageButton btnBack, btnSettings;

    // أزرار داخل البروفايل
    private LinearLayout btnEditProfile, btnViewWorks;

    // عناصر التنقل أسفل الشاشة
    private TextView navHome, navProjects, navFavorite, navProfile;

    // عناصر عرض بيانات المستخدم
    private ImageView imgProfile;
    private TextView tvName, tvRole, tvBio, tvWorksCount;

    // FirebaseAuth لمعرفة المستخدم الحالي
    private FirebaseAuth auth;

    // المستخدم المعروض حاليًا
    private MyCinemaUser currentProfileUser;

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

        // ربط عناصر الواجهة
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

        // زر الرجوع
        btnBack.setOnClickListener(v -> finish());



        // زر تعديل البروفايل
        btnEditProfile.setOnClickListener(v -> {
            if (currentProfileUser == null) {
                Toast.makeText(ProfileActivity.this,
                        "User data not loaded",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(ProfileActivity.this, SaveProfileActivity.class);
            intent.putExtra("cinmaUser", currentProfileUser);
            startActivity(intent);
        });

        // التنقل إلى Home
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        // التنقل إلى Works
        navProjects.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, WorkActivity.class);
            startActivity(intent);
        });

        // التنقل إلى Favorite
        navFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, FavoriteActivity.class);
            startActivity(intent);
        });

        // المستخدم موجود أصلًا في شاشة Profile
        navProfile.setOnClickListener(v -> {
            // لا حاجة لفتح الشاشة مرة ثانية
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    /**
     * loadUserData
     *
     * إذا وصل cinmaUser من الـ Adapter، يعني المستخدم فتح البروفايل
     * من القلم داخل item، لذلك نعرض بيانات هذا الـ item.
     *
     * إذا لم يصل cinmaUser، يعني المستخدم فتح البروفايل من navProfile،
     * لذلك نجلب بيانات المستخدم الحالي من Firebase.
     */
    private void loadUserData() {

        // الحالة الأولى: فتح البروفايل من القلم داخل item
        if (getIntent() != null && getIntent().hasExtra("cinmaUser")) {
            MyCinemaUser user =
                    (MyCinemaUser) getIntent().getSerializableExtra("cinmaUser");

            if (user != null) {
                showUserData(user);
                return;
            }
        }

        // الحالة الثانية: فتح البروفايل من navProfile
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this,
                    "No signed in user",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        DatabaseReference userRef = FirebaseDatabase
                .getInstance()
                .getReference("users")
                .child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                MyCinemaUser user = snapshot.getValue(MyCinemaUser.class);

                if (user == null) {
                    Toast.makeText(ProfileActivity.this,
                            "user error",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                showUserData(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this,
                        "Failed to load profile: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * showUserData
     *
     * تعرض بيانات المستخدم داخل عناصر الواجهة،
     * وتحدد هل زر Edit Profile يظهر أو يختفي.
     */
    private void showUserData(MyCinemaUser user) {

        currentProfileUser = user;

        // عرض البيانات النصية
        tvName.setText(user.getFullName() != null ? user.getFullName() : "");
        tvRole.setText(user.getRole() != null ? user.getRole() : "");
        tvBio.setText(user.getSkills() != null ? user.getSkills() : "");
        tvWorksCount.setText(String.valueOf(user.getWorkCount()));
        
        // عرض صورة البروفايل بنفس طريقتك الحالية
        if (user.getProfileImageUri() != null && !user.getProfileImageUri().isEmpty()) {
            imgProfile.setImageBitmap(stringToBitmap(user.getProfileImageUri()));
        } else {
            imgProfile.setImageResource(android.R.drawable.sym_def_app_icon);
        }

        // إظهار أو إخفاء زر Edit Profile حسب صاحب البروفايل
        updateEditProfileVisibility(user);
    }

    /**
     * updateEditProfileVisibility
     *
     * يظهر زر Edit Profile فقط إذا كان البروفايل المعروض
     * يخص المستخدم الحالي.
     */
    private void updateEditProfileVisibility(MyCinemaUser profileUser) {

        if (auth.getCurrentUser() == null || profileUser == null) {
            btnEditProfile.setVisibility(View.GONE);
            return;
        }

        String currentEmail = auth.getCurrentUser().getEmail();
        String profileEmail = profileUser.getEmail();

        if (currentEmail != null
                && profileEmail != null
                && currentEmail.equalsIgnoreCase(profileEmail)) {

            btnEditProfile.setVisibility(View.VISIBLE);

        } else {
            btnEditProfile.setVisibility(View.GONE);
        }
    }

    /**
     * stringToBitmap
     *
     * تحول الصورة من String Base64 إلى Bitmap
     * حتى يتم عرضها داخل ImageView.
     */
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