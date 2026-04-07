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
import com.example.laracin.data.MyCinemaUserTable.MyCinemAdapter;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUser;

import java.util.ArrayList;

/**
 * HomeActivity
 *
 * شاشة الهوم
 *
 * الوظائف الأساسية:
 * 1) عرض كل المستخدمين الموجودين بقاعدة البيانات
 * 2) البحث عن المستخدمين حسب الاسم أو الدور
 * 3) الانتقال إلى شاشة البروفايل
 */
public class HomeActivity extends AppCompatActivity {

    // عناصر نصية من الواجهة
    private TextView tvSearchMovies, tvMyList, navProfile ,navProjects,navFavorite;

    // خانة البحث
    private EditText etSearch;

    // قائمة عرض المستخدمين
    private ListView listusers;

    // الأدابتر المسؤول عن عرض عناصر MyCinemaUser داخل ListView
    private MyCinemAdapter adapteruser;

    // قائمة محلية لتخزين كل المستخدمين الذين تم جلبهم من قاعدة البيانات
    private ArrayList<MyCinemaUser> allUsers = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // تفعيل EdgeToEdge
        EdgeToEdge.enable(this);

        // تحميل واجهة الشاشة
        setContentView(R.layout.activity_home_screen);

        // ضبط الحواف حسب system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ربط عناصر الواجهة
        tvSearchMovies = findViewById(R.id.tvSearchMovies);
        tvMyList = findViewById(R.id.tvMyList);
        navProfile = findViewById(R.id.navProfile);
        etSearch = findViewById(R.id.etSearch);
        listusers = findViewById(R.id.listusers);
        navProjects=findViewById(R.id.navProjects);
        navFavorite=findViewById(R.id.navFavorite);


        // إنشاء الأدابتر وربطه بالـ ListView
        adapteruser = new MyCinemAdapter(this, R.layout.actor_item_layout);
        listusers.setAdapter(adapteruser);

        /**
         * عند الضغط على navProfile
         * ينتقل المستخدم إلى شاشة ProfileActivity
         */
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
        /**
         * مراقبة النص داخل خانة البحث
         * كلما المستخدم يكتب، يتم استدعاء filterUsers
         */
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // غير مستخدمة حاليا
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // تصفية المستخدمين حسب النص المكتوب
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // غير مستخدمة حاليا
            }
        });
    }

    /**
     * onResume
     *
     * يتم استدعاؤها كل مرة ترجع فيها الشاشة للواجهة
     *
     * هنا نقوم بـ:
     * 1) تفريغ القائمة الحالية
     * 2) جلب كل المستخدمين من قاعدة البيانات
     * 3) تحديث الأدابتر لعرضهم
     */
    @Override
    protected void onResume() {
        super.onResume();

        // تنظيف القائمة المحلية
        allUsers.clear();

        // جلب جميع المستخدمين من Room
        allUsers.addAll(AppDatabase.getDb(this).myCinemaUserQuery().getAllUsers());

        // تحديث الأدابتر بالمستخدمين
        adapteruser.clear();
        adapteruser.addAll(allUsers);
        adapteruser.notifyDataSetChanged();
    }

    /**
     * filterUsers
     *
     * تقوم بتصفية المستخدمين حسب النص المدخل
     *
     * البحث يتم على:
     * - fullName
     * - role
     *
     * @param text النص الذي يكتبه المستخدم في خانة البحث
     */
    private void filterUsers(String text) {

        // قائمة مؤقتة لتخزين النتائج بعد التصفية
        ArrayList<MyCinemaUser> filteredList = new ArrayList<>();

        // تحويل النص إلى lowercase وإزالة الفراغات الزائدة
        String searchText = text.toLowerCase().trim();

        // المرور على جميع المستخدمين
        for (MyCinemaUser user : allUsers) {

            // حماية من null
            String fullName = user.getFullName() != null ? user.getFullName().toLowerCase() : "";
            String role = user.getRole() != null ? user.getRole().toLowerCase() : "";

            // إذا النص موجود في الاسم أو الدور، أضف المستخدم
            if (fullName.contains(searchText) || role.contains(searchText)) {
                filteredList.add(user);
            }
        }

        // تحديث الأدابتر بالنتائج المفلترة
        adapteruser.clear();
        adapteruser.addAll(filteredList);
        adapteruser.notifyDataSetChanged();
    }
}