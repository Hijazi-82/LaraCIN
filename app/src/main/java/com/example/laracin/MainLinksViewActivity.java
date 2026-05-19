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

import com.example.laracin.data.MyCinemaUserTable.MyCinemaUser;
import com.example.laracin.data.MyCinemaUserTable.MyLinksAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * MainLinksViewActivity
 *
 * شاشة عرض روابط الأعمال.
 *
 * وظيفة الشاشة:
 * 1. جلب المستخدمين من Firebase Realtime Database.
 * 2. فحص المستخدمين الذين لديهم رابط عمل.
 * 3. عرض روابط الأعمال داخل ListView.
 * 4. استخدام MyLinksAdapter لربط البيانات مع item_work.xml.
 */
public class MainLinksViewActivity extends AppCompatActivity {

    // قائمة تعرض المستخدمين الذين لديهم روابط أعمال
    private ListView listusers;

    // قائمة تخزن المستخدمين الذين لديهم workLink
    private ArrayList<MyCinemaUser> linksList;

    // Adapter مخصص لعرض بيانات روابط الأعمال داخل ListView
    private MyLinksAdapter adapterLinks;

    /**
     * onCreate
     *
     * يتم استدعاؤها عند فتح شاشة روابط الأعمال.
     * داخلها يتم ربط عناصر الواجهة، تجهيز القائمة والـ Adapter،
     * ثم تحميل البيانات من Firebase.
     *
     * @param savedInstanceState يحفظ حالة الشاشة عند إعادة إنشائها
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // تفعيل عرض Edge To Edge
        EdgeToEdge.enable(this);

        // ربط الكلاس بملف تصميم الشاشة
        setContentView(R.layout.activity_main_links_view);

        // ربط ListView من ملف XML
        listusers = findViewById(R.id.listusers);

        // إنشاء قائمة فارغة لحفظ المستخدمين الذين لديهم روابط أعمال
        linksList = new ArrayList<>();

        // إنشاء Adapter وربطه مع تصميم item_work
        adapterLinks = new MyLinksAdapter(this, R.layout.item_work, linksList);

        // ربط الـ Adapter مع الـ ListView
        listusers.setAdapter(adapterLinks);

        // ضبط الحواف حتى لا تدخل عناصر الشاشة تحت أشرطة النظام
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // تحميل روابط الأعمال من Firebase
        loadLinksFromFirebase();
    }

    /**
     * loadLinksFromFirebase
     *
     * تجلب المستخدمين من Firebase من المسار users.
     * إذا كان المستخدم يحتوي على workLink غير فارغ،
     * يتم إضافته إلى القائمة وعرضه في الشاشة.
     */
    private void loadLinksFromFirebase() {

        // مرجع users داخل Firebase Realtime Database
        DatabaseReference usersRef = FirebaseDatabase
                .getInstance()
                .getReference("users");

        usersRef.addValueEventListener(new ValueEventListener() {

            /**
             * onDataChange
             *
             * يتم استدعاؤها عند وصول البيانات من Firebase
             * أو عند حدوث تغيير على البيانات.
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // تنظيف القائمة القديمة قبل إضافة البيانات الجديدة
                linksList.clear();

                // المرور على كل مستخدم موجود داخل users
                for (DataSnapshot data : snapshot.getChildren()) {

                    MyCinemaUser user = data.getValue(MyCinemaUser.class);

                    // عرض المستخدم فقط إذا كان لديه رابط عمل
                    if (user != null
                            && user.getWorkLink() != null
                            && !user.getWorkLink().trim().isEmpty()) {

                        linksList.add(user);
                    }
                }

                // تحديث ListView بعد تغيير البيانات
                adapterLinks.notifyDataSetChanged();
            }

            /**
             * onCancelled
             *
             * يتم استدعاؤها إذا فشلت عملية قراءة البيانات من Firebase.
             */
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainLinksViewActivity.this,
                        "فشل جلب الروابط: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}