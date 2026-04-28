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
 * شاشة تعرض ListView للمستخدمين باستخدام Adapter مخصص
 *
 * المسؤوليات
 * 1 تهيئة الواجهة activity_main1
 * 2 ربط ListView بالـ Adapter
 * 3 عند الرجوع للشاشة onResume, تحديث محتوى القائمة حسب البيانات
 *
 * ملاحظة
 * جلب المستخدمين من Room موجود لكنه معلق حاليا, لذلك القائمة لن تمتلئ ببيانات
 */
public class Activity_main1 extends AppCompatActivity {

    // ListView لعرض المستخدمين
    private ListView listusers;

    // Adapter مخصص لعرض عناصر المستخدمين داخل ListView
    private MyCinemAdapter adapteruser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // تفعيل edge to edge وتحميل الواجهة
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main1);

        // ربط ListView من الواجهة
        listusers = findViewById(R.id.listusers);

        /**
         * إنشاء Adapter
         * - this سياق الشاشة الحالية
         * - R.layout.actor_item_layout هو layout لكل عنصر داخل القائمة
         */
        adapteruser = new MyCinemAdapter(this, R.layout.actor_item_layout);

        // ربط الـ ListView بالـ Adapter
        listusers.setAdapter(adapteruser);

        // ضبط padding حسب system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * onResume
     * يتم استدعاؤها كل مرة الشاشة ترجع للواجهة
     *
     * اللي بيصير حاليا
     * 1 تفريغ بيانات الـ adapter
     * 2 تحديث الواجهة notifyDataSetChanged
     *
     * ملاحظة
     * سطر جلب البيانات من Room معلق
     * لو بدك القائمة تمتلئ لازم تفك التعليق عن addAll وتجيب البيانات
     */
    @Override
    protected void onResume() {
        super.onResume();

        // تفريغ العناصر الحالية
        adapteruser.clear();

        // جلب المستخدمين من Room واضافتهم للـ adapter (معلق حاليا)
        adapteruser.addAll(AppDatabase.getDb(this).myCinemaUserQuery().getAllUsers());

        // اعلام الـ ListView انه البيانات تغيرت
        adapteruser.notifyDataSetChanged();
    }
    //todo add load data from firebase

    /**
     * جلب البيانات من Firebase Realtime Database
     */
    private void loadDataFromFirebase() {
        // الوصول لمرجع "users" في قاعدة البيانات
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adapteruser.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    // تحويل البيانات من Firebase إلى كائن MyCinemaUser
                    MyCinemaUser user = data.getValue(MyCinemaUser.class);
                    if (user != null) {
                        adapteruser.add(user);
                    }
                }
                // تحديث القائمة
                adapteruser.notifyDataSetChanged();}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Activity_main1.this, "فشل جلب البيانات: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }

                });
    }

}