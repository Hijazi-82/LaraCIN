package com.example.laracin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * WorkActivity
 *
 * شاشة روابط الأعمال.
 *
 * وظيفة الشاشة:
 * - عرض روابط الأعمال التي يضيفها المستخدم.
 * - إضافة رابط جديد عن طريق AddLinkActivity.
 * - البحث داخل الروابط حسب اسم العمل أو نوعه أو وصفه.
 * - فتح الرابط عند الضغط العادي عليه.
 * - حذف الرابط عند الضغط المطوّل عليه.
 */
public class WorkActivity extends AppCompatActivity {

    // حقول إدخال
    // حقل البحث داخل روابط الأعمال
    private EditText etProjectsSearch;

    // أزرار
    // زر ينقل المستخدم إلى شاشة إضافة رابط جديد
    private Button btnAddLink;

    // قوائم عرض
    // ListView لعرض روابط الأعمال داخل الشاشة
    private ListView listProjectsLinks;

    // عناصر التنقل بين الشاشات
    // navHome ينقل المستخدم إلى شاشة البيت
    // navProjects يمثل شاشة المشاريع الحالية
    // navFavorite ينقل المستخدم إلى شاشة المفضلة
    // navProfile ينقل المستخدم إلى شاشة البروفايل
    private TextView navHome, navProjects, navFavorite, navProfile;

    // قوائم البيانات
    // allLinksList تحفظ جميع الروابط التي تمت إضافتها
    // filteredLinksList تحفظ الروابط بعد عملية البحث أو الفلترة
    private ArrayList<HashMap<String, String>> allLinksList, filteredLinksList;

    // Adapter
    // يربط البيانات الموجودة داخل filteredLinksList مع ListView
    private SimpleAdapter adapter;

    /**
     * addLinkLauncher
     *
     * مسؤول عن فتح شاشة AddLinkActivity وانتظار النتيجة منها.
     * عندما يرجع المستخدم من شاشة إضافة الرابط، يتم أخذ البيانات:
     * workName, workType, workDescription, workLink
     * ثم إضافتها إلى القائمة وعرضها في الشاشة.
     */
    private final ActivityResultLauncher<Intent> addLinkLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {

                            String workName = result.getData().getStringExtra("workName");
                            String workType = result.getData().getStringExtra("workType");
                            String workDescription = result.getData().getStringExtra("workDescription");
                            String workLink = result.getData().getStringExtra("workLink");

                            HashMap<String, String> item = new HashMap<>();
                            item.put("workName", workName);
                            item.put("workType", workType);
                            item.put("workDescription", workDescription);
                            item.put("workLink", workLink);

                            allLinksList.add(item);

                            filterLinks(etProjectsSearch.getText().toString().trim());
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);

        // ربط عناصر الواجهة مع الكود
        etProjectsSearch = findViewById(R.id.etProjectsSearch);
        btnAddLink = findViewById(R.id.btnAddLink);
        listProjectsLinks = findViewById(R.id.listProjectsLinks);

        navHome = findViewById(R.id.navHome);
        navProjects = findViewById(R.id.navProjects);
        navFavorite = findViewById(R.id.navFavorite);
        navProfile = findViewById(R.id.navProfile);

        // إنشاء القوائم
        allLinksList = new ArrayList<>();
        filteredLinksList = new ArrayList<>();

        // إنشاء Adapter لعرض اسم العمل ونوعه داخل القائمة
        adapter = new SimpleAdapter(
                this,
                filteredLinksList,
                android.R.layout.simple_list_item_2,
                new String[]{"workName", "workType"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );

        // ربط الـ Adapter مع الـ ListView
        listProjectsLinks.setAdapter(adapter);

        // عند الضغط على زر Add Link يتم فتح شاشة إضافة رابط جديد
        btnAddLink.setOnClickListener(v -> {
            Intent intent = new Intent(WorkActivity.this, AddLinkActivity.class);
            addLinkLauncher.launch(intent);
        });

        // عند الضغط العادي على عنصر من القائمة يتم فتح الرابط في المتصفح
        listProjectsLinks.setOnItemClickListener((parent, view, position, id) -> {

            HashMap<String, String> item = filteredLinksList.get(position);
            String link = item.get("workLink");

            if (link == null || link.trim().isEmpty()) {
                Toast.makeText(this, "No link available", Toast.LENGTH_SHORT).show();
                return;
            }

            link = link.trim();

            if (!link.startsWith("http://") && !link.startsWith("https://")) {
                link = "https://" + link;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(intent);
        });

        // عند الضغط المطوّل على عنصر من القائمة يتم حذف الرابط
        listProjectsLinks.setOnItemLongClickListener((parent, view, position, id) -> {

            HashMap<String, String> item = filteredLinksList.get(position);

            allLinksList.remove(item);
            filteredLinksList.remove(item);

            adapter.notifyDataSetChanged();

            Toast.makeText(this, "Link deleted", Toast.LENGTH_SHORT).show();

            return true;
        });

        // البحث داخل الروابط أثناء الكتابة
        etProjectsSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // غير مستخدمة
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterLinks(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // غير مستخدمة
            }
        });

        // الانتقال إلى شاشة البيت
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(WorkActivity.this, Activity_main1.class);
            startActivity(intent);
            finish();
        });

        // الانتقال إلى شاشة المفضلة
        navFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(WorkActivity.this, FavoriteActivity.class);
            startActivity(intent);
            finish();
        });

        // الانتقال إلى شاشة البروفايل / تعديل البروفايل
        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(WorkActivity.this, SaveProfileActivity.class);
            startActivity(intent);
            finish();
        });

        // المستخدم موجود أصلاً في شاشة المشاريع
        navProjects.setOnClickListener(v -> {
        });
    }

    /**
     * filterLinks
     *
     * دالة تبحث داخل روابط الأعمال.
     *
     * البحث يتم حسب:
     * - اسم العمل
     * - نوع العمل
     * - وصف العمل
     *
     * @param query النص الذي يكتبه المستخدم في خانة البحث
     */
    private void filterLinks(String query) {

        filteredLinksList.clear();

        if (query.isEmpty()) {
            filteredLinksList.addAll(allLinksList);
        } else {

            String lowerQuery = query.toLowerCase();

            for (HashMap<String, String> item : allLinksList) {

                String workName = item.get("workName") != null
                        ? item.get("workName").toLowerCase()
                        : "";

                String workType = item.get("workType") != null
                        ? item.get("workType").toLowerCase()
                        : "";

                String workDescription = item.get("workDescription") != null
                        ? item.get("workDescription").toLowerCase()
                        : "";

                if (workName.contains(lowerQuery)
                        || workType.contains(lowerQuery)
                        || workDescription.contains(lowerQuery)) {

                    filteredLinksList.add(item);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }
}