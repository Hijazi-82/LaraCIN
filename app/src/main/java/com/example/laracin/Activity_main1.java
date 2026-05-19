package com.example.laracin;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.laracin.data.AppDatabase;
import com.example.laracin.data.MyCinemaUserTable.MyCinemAdapter;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Activity_main1
 *
 * شاشة تعرض قائمة المستخدمين داخل ListView.
 *
 * وظيفة الشاشة:
 * 1. ربط الواجهة activity_main1 مع الكود.
 * 2. إنشاء Adapter مخصص لعرض عناصر MyCinemaUser.
 * 3. عرض المستخدمين المخزنين في Room Database.
 * 4. تحتوي أيضًا على دالة جاهزة لجلب المستخدمين من Firebase.
 */
public class Activity_main1 extends AppCompatActivity {

    // ListView لعرض المستخدمين داخل الشاشة
    private ListView listusers;

    // Adapter يربط بيانات المستخدمين مع تصميم العنصر actor_item_layout
    private MyCinemAdapter adapteruser;

    /**
     * onCreate
     *
     * يتم استدعاؤها عند فتح الشاشة.
     * داخلها يتم ربط عناصر الواجهة، إنشاء الـ Adapter،
     * وربطه مع الـ ListView.
     *
     * @param savedInstanceState يحفظ حالة الشاشة عند إعادة إنشائها
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // تفعيل عرض Edge To Edge
        EdgeToEdge.enable(this);

        // ربط الكلاس بملف تصميم الشاشة
        setContentView(R.layout.activity_main1);

        // ربط ListView من ملف XML
        listusers = findViewById(R.id.listusers);

        // إنشاء Adapter لعرض المستخدمين داخل القائمة
        adapteruser = new MyCinemAdapter(this, R.layout.actor_item_layout);

        // ربط الـ Adapter مع الـ ListView
        listusers.setAdapter(adapteruser);

        // ضبط الحواف حتى لا تدخل عناصر الشاشة تحت أشرطة النظام
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * onResume
     *
     * يتم استدعاؤها كل مرة ترجع فيها الشاشة للواجهة.
     * هنا يتم تفريغ القائمة القديمة، ثم جلب المستخدمين من Room
     * وعرضهم من جديد داخل ListView.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // تفريغ العناصر القديمة من الـ Adapter
        adapteruser.clear();

        // جلب جميع المستخدمين من Room وإضافتهم إلى القائمة
        adapteruser.addAll(AppDatabase.getDb(this).myCinemaUserQuery().getAllUsers());

        // تحديث عرض القائمة بعد تغيير البيانات
        adapteruser.notifyDataSetChanged();
    }

    /**
     * loadDataFromFirebase
     *
     * دالة تجلب المستخدمين من Firebase Realtime Database
     * من المسار users، ثم تعرضهم داخل ListView.
     */
    private void loadDataFromFirebase() {

        // مرجع users داخل Firebase
        DatabaseReference usersRef = FirebaseDatabase
                .getInstance()
                .getReference("users");

        usersRef.addValueEventListener(new ValueEventListener() {

            /**
             * onDataChange
             *
             * يتم استدعاؤها عند وصول البيانات من Firebase
             * أو عند حدوث تغيير عليها.
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // تفريغ القائمة قبل إضافة البيانات الجديدة
                adapteruser.clear();

                // المرور على جميع المستخدمين داخل users
                for (DataSnapshot data : snapshot.getChildren()) {

                    MyCinemaUser user = data.getValue(MyCinemaUser.class);

                    if (user != null) {
                        adapteruser.add(user);
                    }
                }

                // تحديث القائمة بعد إضافة المستخدمين
                adapteruser.notifyDataSetChanged();
            }

            /**
             * onCancelled
             *
             * يتم استدعاؤها إذا فشلت قراءة البيانات من Firebase.
             */
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Activity_main1.this,
                        "فشل جلب البيانات: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}