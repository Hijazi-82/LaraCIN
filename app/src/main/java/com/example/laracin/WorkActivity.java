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

import androidx.annotation.NonNull;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * WorkActivity
 *
 * شاشة روابط الأعمال.
 *
 * وظيفة الشاشة:
 * 1. عرض روابط الأعمال الخاصة بالمستخدم الحالي من Firebase.
 * 2. إضافة رابط جديد عن طريق AddLinkActivity.
 * 3. حفظ الرابط داخل Firebase.
 * 4. البحث داخل الروابط حسب الاسم أو النوع أو الوصف.
 * 5. فتح الرابط عند الضغط العادي.
 * 6. حذف الرابط من Firebase عند الضغط المطوّل.
 */
public class WorkActivity extends AppCompatActivity {

    private EditText etProjectsSearch;
    private Button btnAddLink;
    private ListView listProjectsLinks;

    private TextView navHome, navProjects, navFavorite, navProfile;

    private ArrayList<HashMap<String, String>> allLinksList;
    private ArrayList<HashMap<String, String>> filteredLinksList;

    private SimpleAdapter adapter;

    private FirebaseAuth auth;
    private DatabaseReference worksRef;
    private DatabaseReference currentUserRef;

    private final ActivityResultLauncher<Intent> addLinkLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {

                            String workName = result.getData().getStringExtra("workName");
                            String workType = result.getData().getStringExtra("workType");
                            String workDescription = result.getData().getStringExtra("workDescription");
                            String workLink = result.getData().getStringExtra("workLink");

                            saveWorkToFirebase(workName, workType, workDescription, workLink);
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);

        auth = FirebaseAuth.getInstance();

        etProjectsSearch = findViewById(R.id.etProjectsSearch);
        btnAddLink = findViewById(R.id.btnAddLink);
        listProjectsLinks = findViewById(R.id.listProjectsLinks);

        navHome = findViewById(R.id.navHome);
        navProjects = findViewById(R.id.navProjects);
        navFavorite = findViewById(R.id.navFavorite);
        navProfile = findViewById(R.id.navProfile);

        allLinksList = new ArrayList<>();
        filteredLinksList = new ArrayList<>();

        adapter = new SimpleAdapter(
                this,
                filteredLinksList,
                android.R.layout.simple_list_item_2,
                new String[]{"workName", "workType"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );

        listProjectsLinks.setAdapter(adapter);

        prepareFirebaseReferences();

        btnAddLink.setOnClickListener(v -> {
            Intent intent = new Intent(WorkActivity.this, AddLinkActivity.class);
            addLinkLauncher.launch(intent);
        });

        listProjectsLinks.setOnItemClickListener((parent, view, position, id) -> {
            HashMap<String, String> item = filteredLinksList.get(position);
            openWorkLink(item.get("workLink"));
        });

        listProjectsLinks.setOnItemLongClickListener((parent, view, position, id) -> {
            HashMap<String, String> item = filteredLinksList.get(position);
            String workId = item.get("workId");

            if (workId != null && worksRef != null) {
                deleteWorkFromFirebase(workId);
            }

            return true;
        });

        etProjectsSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterLinks(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(WorkActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        navFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(WorkActivity.this, FavoriteActivity.class);
            startActivity(intent);
            finish();
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(WorkActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        navProjects.setOnClickListener(v -> {
            // المستخدم موجود أصلًا في شاشة الأعمال
        });
    }

    /**
     * prepareFirebaseReferences
     *
     * تجهز مسار المستخدم الحالي في Firebase.
     * كل الأعمال تحفظ تحت:
     * users / uid / works
     */
    private void prepareFirebaseReferences() {

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "No signed in user", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        currentUserRef = FirebaseDatabase
                .getInstance()
                .getReference("users")
                .child(uid);

        worksRef = currentUserRef.child("works");

        loadWorksFromFirebase();
    }

    /**
     * loadWorksFromFirebase
     *
     * تجلب روابط الأعمال الخاصة بالمستخدم الحالي من Firebase.
     */
    private void loadWorksFromFirebase() {

        if (worksRef == null) {
            return;
        }

        worksRef.get().addOnSuccessListener(snapshot -> {

            allLinksList.clear();

            for (DataSnapshot data : snapshot.getChildren()) {

                String workId = data.getKey();
                String workName = data.child("workName").getValue(String.class);
                String workType = data.child("workType").getValue(String.class);
                String workDescription = data.child("workDescription").getValue(String.class);
                String workLink = data.child("workLink").getValue(String.class);

                HashMap<String, String> item = new HashMap<>();
                item.put("workId", workId);
                item.put("workName", workName);
                item.put("workType", workType);
                item.put("workDescription", workDescription);
                item.put("workLink", workLink);

                allLinksList.add(item);
            }

            filterLinks(etProjectsSearch.getText().toString().trim());
            updateWorksCountInFirebase();

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load works", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * saveWorkToFirebase
     *
     * تحفظ رابط عمل جديد في Firebase.
     */
    private void saveWorkToFirebase(String workName,
                                    String workType,
                                    String workDescription,
                                    String workLink) {

        if (worksRef == null) {
            Toast.makeText(this, "Firebase not ready", Toast.LENGTH_SHORT).show();
            return;
        }

        String workId = worksRef.push().getKey();

        if (workId == null) {
            Toast.makeText(this, "Failed to create work id", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> workMap = new HashMap<>();
        workMap.put("workName", workName);
        workMap.put("workType", workType);
        workMap.put("workDescription", workDescription);
        workMap.put("workLink", workLink);

        worksRef.child(workId).setValue(workMap).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                Toast.makeText(this, "Work saved", Toast.LENGTH_SHORT).show();

                // حفظ آخر عمل أيضًا داخل بيانات المستخدم للتوافق مع الكلاسات القديمة
                currentUserRef.child("workName").setValue(workName);
                currentUserRef.child("workType").setValue(workType);
                currentUserRef.child("workDescription").setValue(workDescription);
                currentUserRef.child("workLink").setValue(workLink);

                loadWorksFromFirebase();

            } else {
                Toast.makeText(this, "Failed to save work", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * deleteWorkFromFirebase
     *
     * تحذف رابط عمل من Firebase حسب workId.
     */
    private void deleteWorkFromFirebase(String workId) {

        worksRef.child(workId).removeValue().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                Toast.makeText(this, "Work deleted", Toast.LENGTH_SHORT).show();
                loadWorksFromFirebase();
            } else {
                Toast.makeText(this, "Failed to delete work", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * updateWorksCountInFirebase
     *
     * تحفظ عدد الأعمال داخل بيانات المستخدم.
     * لاحقًا ProfileActivity يقدر يعرض هذا الرقم.
     */
    private void updateWorksCountInFirebase() {
        if (currentUserRef != null) {
            currentUserRef.child("workCount").setValue(allLinksList.size());
        }
    }

    /**
     * openWorkLink
     *
     * تفتح رابط العمل في المتصفح.
     */
    private void openWorkLink(String link) {

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
    }

    /**
     * filterLinks
     *
     * تبحث داخل روابط الأعمال حسب الاسم أو النوع أو الوصف.
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