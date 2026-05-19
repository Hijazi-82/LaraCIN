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
 * شاشة روابط الأعمال في التطبيق.
 *
 * وظيفة الشاشة:
 * 1. عرض روابط الأعمال التي يضيفها المستخدم.
 * 2. فتح شاشة AddLinkActivity لإضافة رابط جديد.
 * 3. البحث داخل الروابط حسب اسم العمل أو نوعه أو وصفه.
 * 4. فتح رابط العمل في المتصفح عند الضغط العادي.
 * 5. حذف الرابط من القائمة عند الضغط المطوّل.
 * 6. التنقل بين شاشة الهوم، المفضلة، والبروفايل.
 */
public class WorkActivity extends AppCompatActivity {

    // حقل البحث داخل روابط الأعمال
    private EditText etProjectsSearch;

    // زر فتح شاشة إضافة رابط جديد
    private Button btnAddLink;

    // قائمة عرض روابط الأعمال
    private ListView listProjectsLinks;

    // عناصر التنقل أسفل الشاشة
    private TextView navHome;
    private TextView navProjects;
    private TextView navFavorite;
    private TextView navProfile;

    // قائمة تحفظ كل الروابط التي تمت إضافتها
    private ArrayList<HashMap<String, String>> allLinksList;

    // قائمة تحفظ الروابط بعد البحث أو الفلترة
    private ArrayList<HashMap<String, String>> filteredLinksList;

    // Adapter يربط بيانات الروابط مع ListView
    private SimpleAdapter adapter;

    /**
     * addLinkLauncher
     *
     * يفتح شاشة AddLinkActivity وينتظر النتيجة منها.
     * عند رجوع المستخدم من شاشة إضافة الرابط، يتم أخذ بيانات الرابط
     * وإضافتها إلى القائمة.
     */
    private final ActivityResultLauncher<Intent> addLinkLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == Activity.RESULT_OK
                                && result.getData() != null) {

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

    /**
     * onCreate
     *
     * يتم استدعاؤها عند فتح شاشة روابط الأعمال.
     * داخلها يتم ربط عناصر الواجهة، تجهيز القائمة،
     * وتحديد أوامر الأزرار والبحث.
     *
     * @param savedInstanceState يحفظ حالة الشاشة عند إعادة إنشائها
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);

        // ربط عناصر الواجهة
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

        // إنشاء Adapter لعرض اسم العمل ونوع العمل
        adapter = new SimpleAdapter(
                this,
                filteredLinksList,
                android.R.layout.simple_list_item_2,
                new String[]{"workName", "workType"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );

        listProjectsLinks.setAdapter(adapter);

        // فتح شاشة إضافة رابط جديد
        btnAddLink.setOnClickListener(v -> {
            Intent intent = new Intent(WorkActivity.this, AddLinkActivity.class);
            addLinkLauncher.launch(intent);
        });

        // فتح الرابط في المتصفح عند الضغط العادي على item
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

        // حذف الرابط عند الضغط المطوّل على item
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

        // الانتقال إلى شاشة الهوم
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(WorkActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        // الانتقال إلى شاشة المفضلة
        navFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(WorkActivity.this, FavoriteActivity.class);
            startActivity(intent);
            finish();
        });

        // الانتقال إلى شاشة البروفايل
        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(WorkActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        // المستخدم موجود أصلًا في شاشة روابط الأعمال
        navProjects.setOnClickListener(v -> {
            // لا حاجة لفتح شاشة جديدة
        });
    }

    /**
     * filterLinks
     *
     * تبحث داخل روابط الأعمال حسب:
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