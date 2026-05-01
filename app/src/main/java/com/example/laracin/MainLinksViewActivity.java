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
 * الهدف:
 * - جلب المستخدمين من Firebase
 * - عرض معلومات العمل الخاصة بكل مستخدم داخل ListView
 * - استعمال MyLinksAdapter مع item_work.xml
 */
public class MainLinksViewActivity extends AppCompatActivity {

    // ListView لعرض روابط الأعمال
    private ListView listusers;

    // قائمة المستخدمين الذين لديهم روابط أعمال
    private ArrayList<MyCinemaUser> linksList;

    // Adapter مخصص لعرض روابط الأعمال
    private MyLinksAdapter adapterLinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_links_view);

        // ربط ListView من XML
        listusers = findViewById(R.id.listusers);

        // تجهيز القائمة والـ Adapter
        linksList = new ArrayList<>();
        adapterLinks = new MyLinksAdapter(this, R.layout.item_work, linksList);

        // ربط الـ Adapter مع ListView
        listusers.setAdapter(adapterLinks);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // تحميل البيانات من Firebase
        loadLinksFromFirebase();
    }

    /**
     * loadLinksFromFirebase
     *
     * يجلب بيانات المستخدمين من Firebase.
     * إذا كان المستخدم يحتوي على workLink، يتم عرضه في القائمة.
     */
    private void loadLinksFromFirebase() {

        DatabaseReference usersRef = FirebaseDatabase
                .getInstance()
                .getReference("users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                linksList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {

                    MyCinemaUser user = data.getValue(MyCinemaUser.class);

                    if (user != null &&
                            user.getWorkLink() != null &&
                            !user.getWorkLink().trim().isEmpty()) {

                        linksList.add(user);
                    }
                }

                adapterLinks.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainLinksViewActivity.this,
                        "فشل جلب الروابط: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}