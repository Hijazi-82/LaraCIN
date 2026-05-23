package com.example.laracin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.laracin.data.MyCinemaUserTable.MyCinemAdapter;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUser;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerativeBackend;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.Executor;

/**
 * HomeActivity
 *
 * الشاشة الرئيسية في تطبيق LaraCIN.
 *
 * تعرض جميع المستخدمين من Firebase داخل ListView.
 * تحتوي على بحث عادي حسب الاسم أو الدور.
 * تحتوي أيضًا على بحث ذكي باستخدام Gemini AI.
 * توفر تنقلًا إلى صفحات Profile و Work و Favorite.
 */
public class HomeActivity extends AppCompatActivity {

    // عناصر التنقل أسفل الشاشة
    private TextView navProfile, navProjects, navFavorite;

    // حقل البحث وزر البحث الذكي
    private EditText etSearch;
    private Button btnAiSearch;

    // القائمة التي تعرض المستخدمين
    private ListView listusers;

    // Adapter مسؤول عن عرض المستخدمين داخل ListView
    private MyCinemAdapter adapteruser;

    // قائمة تحفظ جميع المستخدمين القادمين من Firebase
    private ArrayList<MyCinemaUser> allUsers = new ArrayList<>();

    // متغيرات خاصة بتجهيز Gemini AI
    private GenerativeModel ai;
    private GenerativeModelFutures model;

    // مرجع Firebase لمسار users
    private DatabaseReference usersRef;

    // Listener لمراقبة تغييرات المستخدمين في Firebase
    private ValueEventListener usersListener;

    /**
     * يتم استدعاؤها عند فتح الشاشة.
     *
     * تقوم بربط عناصر XML مع Java،
     * وتجهيز Adapter، والبحث، والتنقل، و Gemini AI.
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_screen);

        // ضبط حدود الشاشة حتى لا تدخل العناصر تحت شريط النظام
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ربط عناصر الواجهة
        navProfile = findViewById(R.id.navProfile);
        navProjects = findViewById(R.id.navProjects);
        navFavorite = findViewById(R.id.navFavorite);
        etSearch = findViewById(R.id.etSearch);
        btnAiSearch = findViewById(R.id.btnAiSearch);
        listusers = findViewById(R.id.listusers);

        // تجهيز Adapter وربطه مع ListView
        adapteruser = new MyCinemAdapter(this, R.layout.actor_item_layout);
        listusers.setAdapter(adapteruser);

        // تجهيز Gemini AI للبحث الذكي
        try {
            ai = FirebaseAI.getInstance(GenerativeBackend.googleAI())
                    .generativeModel("gemini-3-flash-preview");

            model = GenerativeModelFutures.from(ai);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // عند الضغط على زر البحث الذكي يتم إرسال النص إلى Gemini
        btnAiSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();

            if (query.isEmpty()) {
                etSearch.setError("Write what you are looking for");
                return;
            }

            if (model == null) {
                Toast.makeText(this, "AI is not available", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "AI is searching...", Toast.LENGTH_SHORT).show();
            askGemini(query);
        });

        // الانتقال إلى صفحة البروفايل
        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // الانتقال إلى صفحة الأعمال
        navProjects.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, WorkActivity.class);
            startActivity(intent);
        });

        // الانتقال إلى صفحة المفضلة
        navFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, FavoriteActivity.class);
            startActivity(intent);
        });

        // البحث العادي أثناء الكتابة حسب الاسم أو الدور
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // غير مستخدمة
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // غير مستخدمة
            }
        });

        // تحديد مسار المستخدمين داخل Firebase
        usersRef = FirebaseDatabase.getInstance().getReference("users");
    }

    /**
     * تعمل كل مرة ترجع الشاشة تظهر للمستخدم.
     *
     * يتم فيها تحميل المستخدمين من Firebase حتى تبقى القائمة محدثة.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadUsersFromFirebase();
    }

    /**
     * تعمل عند الخروج من الشاشة.
     *
     * يتم فيها إزالة Firebase Listener حتى لا يبقى يعمل بالخلفية.
     */
    @Override
    protected void onPause() {
        super.onPause();

        if (usersRef != null && usersListener != null) {
            usersRef.removeEventListener(usersListener);
        }
    }

    /**
     * تحميل جميع المستخدمين من Firebase وعرضهم في Home.
     *
     * تقرأ البيانات من المسار users،
     * تحول كل DataSnapshot إلى MyCinemaUser،
     * تحفظ مفتاح المستخدم،
     * ثم تعرض القائمة داخل Adapter.
     */
    private void loadUsersFromFirebase() {

        usersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // تفريغ البيانات القديمة حتى لا تتكرر
                allUsers.clear();
                adapteruser.clear();

                // المرور على جميع المستخدمين داخل Firebase
                for (DataSnapshot data : snapshot.getChildren()) {

                    MyCinemaUser user = data.getValue(MyCinemaUser.class);

                    if (user != null) {
                        // حفظ مفتاح المستخدم من Firebase لاستخدامه في Profile و Favorite
                        user.setKey(data.getKey());

                        // إضافة المستخدم للقائمة الأصلية
                        allUsers.add(user);
                    }
                }

                // عرض جميع المستخدمين في ListView
                adapteruser.addAll(allUsers);
                adapteruser.notifyDataSetChanged();

                if (allUsers.isEmpty()) {
                    Toast.makeText(HomeActivity.this,
                            "No users found in Firebase",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this,
                        "Failed to load users: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        };

        // تشغيل listener على مسار users
        usersRef.addValueEventListener(usersListener);
    }

    /**
     * البحث العادي داخل قائمة المستخدمين.
     *
     * يتم البحث حسب اسم المستخدم أو الدور.
     *
     * @param text النص الذي يكتبه المستخدم في حقل البحث
     */
    private void filterUsers(String text) {
        ArrayList<MyCinemaUser> filteredList = new ArrayList<>();
        String searchText = text.toLowerCase().trim();

        for (MyCinemaUser user : allUsers) {
            String fullName = user.getFullName() != null
                    ? user.getFullName().toLowerCase()
                    : "";

            String role = user.getRole() != null
                    ? user.getRole().toLowerCase()
                    : "";

            // إذا الاسم أو الدور يحتوي على نص البحث، يتم عرض المستخدم
            if (fullName.contains(searchText) || role.contains(searchText)) {
                filteredList.add(user);
            }
        }

        adapteruser.clear();
        adapteruser.addAll(filteredList);
        adapteruser.notifyDataSetChanged();
    }

    /**
     * البحث الذكي باستخدام Gemini AI.
     *
     * ترسل الدالة بيانات المستخدمين وطلب البحث إلى Gemini.
     * Gemini يرجع أسماء المستخدمين المناسبين فقط.
     *
     * @param userQuery طلب البحث الذي كتبه المستخدم
     */
    private void askGemini(String userQuery) {
        if (allUsers.isEmpty()) {
            Toast.makeText(this, "No users available", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder usersInfo = new StringBuilder();

        // تجهيز معلومات المستخدمين لإرسالها إلى Gemini
        for (MyCinemaUser user : allUsers) {
            usersInfo.append("- Name: ")
                    .append(user.getFullName() != null ? user.getFullName() : "")
                    .append(", Role: ")
                    .append(user.getRole() != null ? user.getRole() : "")
                    .append(", Skills: ")
                    .append(user.getSkills() != null ? user.getSkills() : "")
                    .append(", Experience: ")
                    .append(user.getExperienceYears())
                    .append(" years\n");
        }

        // نص التعليمات المرسل إلى Gemini
        String promptStr =
                "You are a casting assistant for a cinema app.\n" +
                        "Here is a list of users:\n" +
                        usersInfo +
                        "\nUser request: " + userQuery + "\n" +
                        "Return ONLY the exact full names of matching users, separated by commas. " +
                        "If no one matches, return None. Do not explain.";

        Content prompt = new Content.Builder()
                .addText(promptStr)
                .build();

        // إرسال الطلب إلى Gemini
        ListenableFuture<GenerateContentResponse> response =
                model.generateContent(prompt);

        // تنفيذ النتيجة على واجهة المستخدم
        Executor executor = this::runOnUiThread;

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String aiNames = result.getText();

                if (aiNames == null
                        || aiNames.trim().isEmpty()
                        || aiNames.trim().equalsIgnoreCase("None")) {

                    Toast.makeText(HomeActivity.this,
                            "AI: No matches found",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // عرض المستخدمين الذين رجعهم Gemini
                filterListByAI(aiNames);
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(HomeActivity.this,
                        "AI Error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }, executor);
    }

    /**
     * عرض نتائج Gemini فقط داخل القائمة.
     *
     * تقارن أسماء المستخدمين التي رجعها AI مع قائمة allUsers،
     * ثم تعرض المستخدمين المطابقين فقط.
     *
     * @param namesFromAI الأسماء التي رجعت من Gemini
     */
    private void filterListByAI(String namesFromAI) {
        ArrayList<MyCinemaUser> filtered = new ArrayList<>();
        String namesLower = namesFromAI.toLowerCase();

        for (MyCinemaUser user : allUsers) {
            String fullName = user.getFullName() != null
                    ? user.getFullName().toLowerCase()
                    : "";

            // إذا كان اسم المستخدم موجودًا في جواب AI، يتم عرضه
            if (!fullName.isEmpty() && namesLower.contains(fullName)) {
                filtered.add(user);
            }
        }

        adapteruser.clear();
        adapteruser.addAll(filtered);
        adapteruser.notifyDataSetChanged();

        Toast.makeText(this,
                "AI found " + filtered.size() + " result(s)",
                Toast.LENGTH_SHORT).show();
    }
}