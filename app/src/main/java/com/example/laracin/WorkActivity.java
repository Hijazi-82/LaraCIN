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
 * 1. عرض كل الأعمال من كل المستخدمين.
 * 2. إضافة رابط عمل جديد عن طريق AddLinkActivity.
 * 3. حفظ الرابط الجديد تحت حساب المستخدم الحالي فقط.
 * 4. تحديث عدد الأعمال workCount للمستخدم الحالي.
 * 5. البحث داخل الأعمال حسب الاسم أو النوع أو الوصف أو الرابط.
 * 6. فتح الرابط عند الضغط عليه.
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

    // مكان المستخدم الحالي الحقيقي داخل Firebase
    private DatabaseReference currentUserRef;

    // مكان أعمال المستخدم الحالي فقط
    private DatabaseReference worksRef;

    // مفتاح المستخدم الحالي داخل users
    private String currentUserKey;

    /**
     * addLinkLauncher
     *
     * يفتح AddLinkActivity وينتظر النتيجة.
     * بعد رجوع البيانات، يحفظ العمل في Firebase.
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

        /*
         * هذا الـ Adapter يعرض العمل داخل item_work.xml.
         * لازم يكون item_work.xml يحتوي:
         * tvWorkName
         * tvWorkType
         * tvWorkDescription
         * tvWorkLink
         */
        adapter = new SimpleAdapter(
                this,
                filteredLinksList,
                R.layout.item_work,
                new String[]{"workName", "workType", "workDescription", "workLink"},
                new int[]{
                        R.id.tvWorkName,
                        R.id.tvWorkType,
                        R.id.tvWorkDescription,
                        R.id.tvWorkLink
                }
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

        /*
         * حذف العمل:
         * إذا العمل إلك، ينحذف.
         * إذا العمل لشخص آخر، ما بنحذفه.
         */
        listProjectsLinks.setOnItemLongClickListener((parent, view, position, id) -> {

            HashMap<String, String> item = filteredLinksList.get(position);

            String ownerKey = item.get("ownerKey");
            String workId = item.get("workId");

            if (ownerKey == null || workId == null) {
                Toast.makeText(this, "Cannot delete this work", Toast.LENGTH_SHORT).show();
                return true;
            }

            if (currentUserKey != null && currentUserKey.equals(ownerKey)) {
                deleteWorkFromFirebase(ownerKey, workId);
            } else {
                Toast.makeText(this, "You can delete only your own works", Toast.LENGTH_SHORT).show();
            }

            return true;
        });

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
     * تجهز مكان حفظ أعمال المستخدم الحالي.
     *
     * أولًا نحاول نلاقي البروفايل حسب الإيميل داخل users.
     * إذا لقيناه، نحفظ الأعمال تحته.
     *
     * إذا ما لقيناه، نستعمل uid كحل احتياطي حتى لا تفشل الإضافة.
     */
    private void prepareFirebaseReferences() {

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "No signed in user", Toast.LENGTH_SHORT).show();
            loadAllWorksFromFirebase();
            return;
        }

        String currentEmail = auth.getCurrentUser().getEmail();
        String uid = auth.getCurrentUser().getUid();

        DatabaseReference usersRef = FirebaseDatabase
                .getInstance()
                .getReference("users");

        if (currentEmail == null || currentEmail.isEmpty()) {
            useUidAsFallback(usersRef, uid, null);
            return;
        }

        usersRef.orderByChild("email")
                .equalTo(currentEmail)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (snapshot.exists()) {

                        for (DataSnapshot data : snapshot.getChildren()) {
                            currentUserKey = data.getKey();
                            currentUserRef = data.getRef();
                            worksRef = currentUserRef.child("works");

                            loadAllWorksFromFirebase();
                            return;
                        }
                    }

                    // إذا لم نجد البروفايل حسب الإيميل، نستعمل uid بدل ما نوقف الحفظ
                    useUidAsFallback(usersRef, uid, currentEmail);

                })
                .addOnFailureListener(e -> {
                    useUidAsFallback(usersRef, uid, currentEmail);
                });
    }

    /**
     * useUidAsFallback
     *
     * حل احتياطي:
     * إذا لم نستطع إيجاد بروفايل المستخدم داخل users حسب الإيميل،
     * نحفظ أعماله تحت users / uid.
     */
    private void useUidAsFallback(DatabaseReference usersRef, String uid, String email) {

        currentUserKey = uid;
        currentUserRef = usersRef.child(uid);
        worksRef = currentUserRef.child("works");

        if (email != null) {
            currentUserRef.child("email").setValue(email);
        }

        Toast.makeText(this, "Using current user account", Toast.LENGTH_SHORT).show();

        loadAllWorksFromFirebase();
    }

    /**
     * loadAllWorksFromFirebase
     *
     * تجلب كل الأعمال من كل المستخدمين.
     *
     * المسار:
     * users / userKey / works / workId
     */
    private void loadAllWorksFromFirebase() {

        DatabaseReference usersRef = FirebaseDatabase
                .getInstance()
                .getReference("users");

        usersRef.get().addOnSuccessListener(snapshot -> {

            allLinksList.clear();

            for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                String ownerKey = userSnapshot.getKey();
                String ownerName = userSnapshot.child("fullName").getValue(String.class);

                DataSnapshot worksSnapshot = userSnapshot.child("works");

                for (DataSnapshot workData : worksSnapshot.getChildren()) {

                    String workId = workData.getKey();
                    String workName = workData.child("workName").getValue(String.class);
                    String workType = workData.child("workType").getValue(String.class);
                    String workDescription = workData.child("workDescription").getValue(String.class);
                    String workLink = workData.child("workLink").getValue(String.class);

                    HashMap<String, String> item = new HashMap<>();

                    item.put("ownerKey", ownerKey != null ? ownerKey : "");
                    item.put("workId", workId != null ? workId : "");

                    item.put("workName", workName != null ? workName : "");
                    item.put("workType", workType != null ? workType : "");

                    String descriptionText = "";

                    if (ownerName != null && !ownerName.isEmpty()) {
                        descriptionText += "By: " + ownerName;
                    }

                    if (workDescription != null && !workDescription.isEmpty()) {
                        if (!descriptionText.isEmpty()) {
                            descriptionText += "\n";
                        }
                        descriptionText += workDescription;
                    }

                    item.put("workDescription", descriptionText);
                    item.put("workLink", workLink != null ? workLink : "");

                    allLinksList.add(item);
                }
            }

            filterLinks(etProjectsSearch.getText().toString().trim());

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load works", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * saveWorkToFirebase
     *
     * تحفظ العمل الجديد تحت المستخدم الحالي فقط.
     */
    private void saveWorkToFirebase(String workName,
                                    String workType,
                                    String workDescription,
                                    String workLink) {

        if (worksRef == null || currentUserRef == null) {
            Toast.makeText(this, "User profile not ready", Toast.LENGTH_SHORT).show();
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

                /*
                 * حفظ آخر عمل داخل بيانات المستخدم نفسه.
                 * هذا فقط للتوافق مع الكود القديم عندك.
                 */
                currentUserRef.child("workName").setValue(workName);
                currentUserRef.child("workType").setValue(workType);
                currentUserRef.child("workDescription").setValue(workDescription);
                currentUserRef.child("workLink").setValue(workLink);

                updateCurrentUserWorkCount();

                // نعيد تحميل كل الأعمال حتى يظهر الرابط مباشرة
                loadAllWorksFromFirebase();

            } else {
                Toast.makeText(this, "Failed to save work", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * deleteWorkFromFirebase
     *
     * يحذف العمل إذا كان ملك المستخدم الحالي.
     */
    private void deleteWorkFromFirebase(String ownerKey, String workId) {

        DatabaseReference workRef = FirebaseDatabase
                .getInstance()
                .getReference("users")
                .child(ownerKey)
                .child("works")
                .child(workId);

        workRef.removeValue().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                Toast.makeText(this, "Work deleted", Toast.LENGTH_SHORT).show();

                updateCurrentUserWorkCount();
                loadAllWorksFromFirebase();

            } else {
                Toast.makeText(this, "Failed to delete work", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * updateCurrentUserWorkCount
     *
     * يحسب عدد أعمال المستخدم الحالي فقط،
     * ويحفظه داخل:
     * users / currentUserKey / workCount
     */
    private void updateCurrentUserWorkCount() {

        if (worksRef == null || currentUserRef == null) {
            return;
        }

        worksRef.get().addOnSuccessListener(snapshot -> {
            currentUserRef.child("workCount").setValue(snapshot.getChildrenCount());
        });
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
     * تبحث داخل كل الأعمال المعروضة.
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

                String workLink = item.get("workLink") != null
                        ? item.get("workLink").toLowerCase()
                        : "";

                if (workName.contains(lowerQuery)
                        || workType.contains(lowerQuery)
                        || workDescription.contains(lowerQuery)
                        || workLink.contains(lowerQuery)) {

                    filteredLinksList.add(item);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }
}