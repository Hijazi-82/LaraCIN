package com.example.laracin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.laracin.data.AppDatabase;
import com.example.laracin.data.MyCinemaUserTable.CinemaUserService;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUser;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUserQuery;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
/**
 * SaveProfileActivity
 *
 * شاشة إنشاء أو تحديث بروفايل المستخدم.
 *
 * وظيفة الشاشة:
 * 1. قراءة بيانات البروفايل من المستخدم.
 * 2. عرض قائمة أدوار باستخدام AutoCompleteTextView.
 * 3. السماح باختيار صورة من الجهاز.
 * 4. التحقق من صحة الاسم ورقم الهاتف وسنوات الخبرة.
 * 5. تحديث بيانات المستخدم في Room Database.
 * 6. إرسال بيانات المستخدم إلى CinemaUserService لحفظها في Firebase.
 */
public class SaveProfileActivity extends AppCompatActivity {
    // زر حفظ أو تحديث البروفايل
    private Button btnSignUp;
    // حقول إدخال بيانات البروفايل
    private TextInputEditText etFullName ,etPhone , etPortfolio , etExperienceYears ,etSkills;
    // قائمة اختيار دور المستخدم
    private AutoCompleteTextView acRole;
    // نص للانتقال إلى شاشة أخرى
    private TextView tvSignIn;
    // زر/صورة لاختيار صورة البروفايل
    private ImageButton ivSelectedImage;

    // URI للصورة التي يختارها المستخدم
    private Uri selectedImageUri;

    // أداة فتح معرض الصور واستقبال الصورة المختارة
    private ActivityResultLauncher<String> pickImage;

    // DAO للتعامل مع جدول MyCinemaUser في Room
    private MyCinemaUserQuery dao;

    // FirebaseAuth لمعرفة المستخدم الحالي
    private FirebaseAuth auth;

    // المستخدم القادم من شاشة أخرى عند تعديل البروفايل
    private MyCinemaUser cinmaUser;
    /**
     * onCreate
     *
     * يتم استدعاؤها عند فتح شاشة Save Profile.
     * داخلها يتم ربط عناصر الواجهة، تجهيز قائمة الأدوار،
     * تجهيز اختيار الصورة، وملء البيانات إذا كان المستخدم يعدّل بروفايل موجود.
     *
     * @param savedInstanceState يحفظ حالة الشاشة عند إعادة إنشائها
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_save_profile);

        // استقبال المستخدم إذا تم فتح الشاشة للتعديل
        if (getIntent() != null
                && getIntent().getExtras() != null
                && getIntent().getExtras().get("cinmaUser") != null) {

            cinmaUser = (MyCinemaUser) getIntent().getExtras().get("cinmaUser");
        }

        // تهيئة FirebaseAuth
        auth = FirebaseAuth.getInstance();
        // ربط عناصر الواجهة
        btnSignUp = findViewById(R.id.btSignUp);
        etFullName = findViewById(R.id.etFullname);
        etPhone = findViewById(R.id.etPhone);
        etPortfolio = findViewById(R.id.etPortfolio);
        etExperienceYears = findViewById(R.id.etExperienceYears);
        etSkills = findViewById(R.id.etSkills);
        acRole = findViewById(R.id.acRole);
        tvSignIn = findViewById(R.id.tvSignIn);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);

        // تجهيز قائمة الأدوار
        String[] roles = {
                "Director",
                "Actor",
                "Producer",
                "Editor",
                "Cinematographer",
                "Screenwriter",
                "Sound Designer"
        };

        ArrayAdapter<String> roleAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roles);
        acRole.setAdapter(roleAdapter);
        acRole.setOnClickListener(v -> acRole.showDropDown());

        // اختيار صورة من الجهاز وعرضها في الشاشة
        pickImage = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                selectedImageUri = result;
                ivSelectedImage.setImageURI(result);
                ivSelectedImage.setVisibility(View.VISIBLE);
            }
        });

        ivSelectedImage.setOnClickListener(v -> pickImage.launch("image/*"));
        // حفظ أو تحديث بيانات البروفايل
        btnSignUp.setOnClickListener(v -> validateAndInsertRecord());
        // إذا كان المستخدم موجودًا، يتم ملء الحقول ببياناته للتعديل
        if (cinmaUser != null) {
            etFullName.setText(cinmaUser.getFullName());
            etPhone.setText(cinmaUser.getPhone());
            acRole.setText(cinmaUser.getRole());
            etPortfolio.setText(cinmaUser.getPortfolio());
            etExperienceYears.setText(String.valueOf(cinmaUser.getExperienceYears()));
            etSkills.setText(cinmaUser.getSkills());
            btnSignUp.setText("Update");
        }
        // الانتقال إلى
        tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SaveProfileActivity.this, SignInActivity.class);
            startActivity(intent);
        });
    }

    /**
     * validateAndInsertRecord
     *
     * تقرأ بيانات البروفايل من الحقول، ثم تفحص صحتها.
     * إذا كانت البيانات صحيحة، يتم تحديث المستخدم في Room،
     * ثم تشغيل CinemaUserService لحفظ المستخدم في Firebase.
     *
     * @return true إذا نجحت عملية الفحص والتحديث، false إذا كان هناك خطأ
     */
    private boolean validateAndInsertRecord() {
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String role = acRole.getText().toString().trim();
        String portfolio = etPortfolio.getText().toString().trim();
        String experienceYears = etExperienceYears.getText().toString().trim();
        String skills = etSkills.getText().toString().trim();
        boolean isValid = true;

        // فحص الاسم الكامل
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Full name is required");
            isValid = false;
        }

        // فحص رقم الهاتف
        if (!Patterns.PHONE.matcher(phone).matches()) {
            etPhone.setError("Invalid phone number");
            isValid = false;
        }

        if (!isValid) {
            return false;
        }

        // جلب إيميل المستخدم الحالي من FirebaseAuth
        String currentEmail = null;

        if (auth.getCurrentUser() != null) {
            currentEmail = auth.getCurrentUser().getEmail();
        }
        if (TextUtils.isEmpty(currentEmail)) {
            Toast.makeText(this, "User email not found", Toast.LENGTH_SHORT).show();
            return false;
        }

        // جلب المستخدم من Room حسب الإيميل
        MyCinemaUser user =
                AppDatabase.getDb(this).myCinemaUserQuery().getUserByEmail(currentEmail);

        if (user == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return false;
        }

        // فحص وتحويل سنوات الخبرة إلى رقم
        if (!TextUtils.isEmpty(experienceYears)) {
            try {
                user.setExperienceYears(Integer.parseInt(experienceYears));
            } catch (NumberFormatException e) {
                etExperienceYears.setError("Invalid experience years");
                return false;
            }
        }

        // تحديث بيانات المستخدم
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setPortfolio(portfolio);
        user.setSkills(skills);
        user.setRole(role);

        // تحديث المستخدم داخل Room
        AppDatabase.getDb(this).myCinemaUserQuery().updateUser(user);

        // تشغيل Service لحفظ المستخدم في Firebase
        Intent serviceIntent = new Intent(this, CinemaUserService.class);
        serviceIntent.putExtra(CinemaUserService.EXTRA_USER, user);
        startService(serviceIntent);

        return true;
    }

    /**
     * saveCinemaUser
     *
     * دالة بديلة لحفظ المستخدم مباشرة في Firebase.
     * في الكود الحالي لا يتم استخدامها لأن الحفظ يتم عن طريق CinemaUserService.
     *
     * @param user المستخدم المراد حفظه في Firebase
     */
    public void saveCinemaUser(MyCinemaUser user) {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = database.child("CinemaProfiles");
        DatabaseReference newUserRef = usersRef.push();

        if (user.getKeyId() == 0) {
            user.setKeyId(System.currentTimeMillis());
        }

        newUserRef.setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SaveProfileActivity.this,
                            "Succeeded to add User",
                            Toast.LENGTH_SHORT).show();

                    finish();
                })
                .addOnFailureListener(e -> {
                    // يمكن إضافة رسالة خطأ هنا إذا احتجت
                });
    }
}