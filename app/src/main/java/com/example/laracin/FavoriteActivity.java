package com.example.laracin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.laracin.data.AppDatabase;
import com.example.laracin.data.MyCinemaUserTable.MyCinemAdapter;

/**
 * FavoriteActivity
 *
 * شاشة المفضلة
 *
 * الوظائف الأساسية
 * 1) عرض المستخدمين الذين تم وضع نجمة عليهم فقط
 * 2) تحديث القائمة كل مرة نرجع فيها للشاشة
 * 3) التنقل إلى شاشة أخرى عبر عناصر الـ navigation
 */
public class FavoriteActivity extends AppCompatActivity {

    // القائمة التي ستعرض المستخدمين المفضلين
    private ListView listFavoriteUsers;

    // الأدابتر المسؤول عن عرض عناصر المستخدمين داخل القائمة
    private MyCinemAdapter adapteruser;

    // عناصر الـ bottom navigation
    private TextView navHome;
    private TextView navProjects;
    private TextView navFavorite;
    private TextView navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // تفعيل EdgeToEdge
        EdgeToEdge.enable(this);

        // تحميل واجهة شاشة المفضلة
        setContentView(R.layout.activity_favorite);

        // ربط عناصر الواجهة
        listFavoriteUsers = findViewById(R.id.listFavoriteUsers);

        navHome = findViewById(R.id.navHome);
        navProjects = findViewById(R.id.navProjects);
        navFavorite = findViewById(R.id.navFavorite);
        navProfile = findViewById(R.id.navProfile);

        // إنشاء الأدابتر وربطه بالـ ListView
        adapteruser = new MyCinemAdapter(this, R.layout.actor_item_layout);
        listFavoriteUsers.setAdapter(adapteruser);

        /**
         * navHome
         * عند الضغط عليه ينتقل المستخدم إلى الشاشة المحددة
         * حاليا موجه إلى Activity_main1
         */
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteActivity.this, Activity_main1.class);
            startActivity(intent);
            finish();
        });


        navProjects.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteActivity.this, WorkActivity.class);
            startActivity(intent);
            finish();
        });

        navFavorite.setOnClickListener(v -> {
            // لا يلزم تغيير الشاشة إذا كان نفسها
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }
    /**
     * onResume
     *
     * يتم استدعاؤها كل مرة نرجع فيها إلى الشاشة
     *
     * الوظيفة هنا
     * 1) تنظيف الأدابتر الحالي
     * 2) جلب المستخدمين الذين عليهم favorite فقط من قاعدة البيانات
     * 3) تحديث القائمة لعرض النتائج الجديدة
     */
    @Override
    protected void onResume() {
        super.onResume();

        // تنظيف العناصر الحالية من الأدابتر
        adapteruser.clear();

        // جلب المستخدمين المفضلين فقط من قاعدة البيانات
        adapteruser.addAll(AppDatabase.getDb(this).myCinemaUserQuery().getFavoriteUsers());

        // تحديث الأدابتر بعد الإضافة
        adapteruser.notifyDataSetChanged();
    }
}
