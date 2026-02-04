package com.example.laracin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.laracin.data.AppDatabase;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUser;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUserQuery;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Activity مسؤولة عن تسجيل مستخدم جديد (Sign Up).
 * تقوم بجمع بيانات المستخدم، التحقق من صحتها،
 * ثم حفظها في قاعدة البيانات باستخدام Room.
 */
public class SignUp extends AppCompatActivity {

    /** زر إنشاء حساب جديد */
    private Button btnSignUp;

    /** حقول إدخال بيانات المستخدم */
    private TextInputEditText etFullName, etEmail2, etPhone, etPassword2,
            etPortfolio, etExperienceYears, etSkills;

    /** قائمة منسدلة لاختيار دور المستخدم */
    private AutoCompleteTextView acRole;

    /** نص للانتقال إلى شاشة تسجيل الدخول */
    private TextView tvSignIn;

    /** كائن DAO للتعامل مع جدول المستخدمين */
    private MyCinemaUserQuery dao;

    /**
     * يتم استدعاؤها عند إنشاء الـ Activity.
     * مسؤولة عن:
     * - ربط الواجهة بالـ Activity
     * - ربط العناصر (Views)
     * - تعريف الأحداث (Click Listeners)
     *
     * @param savedInstanceState الحالة المحفوظة سابقاً (إن وُجدت)
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        // ربط عناصر الواجهة
        btnSignUp = findViewById(R.id.btSignUp);
        etFullName = findViewById(R.id.etFullname);
        etPhone = findViewById(R.id.etPhone);
        etEmail2 = findViewById(R.id.eiEmail2);
        etPassword2 = findViewById(R.id.edPassword2);
        etPortfolio = findViewById(R.id.etPortfolio);
        etExperienceYears = findViewById(R.id.etExperienceYears);
        etSkills = findViewById(R.id.etSkills);
        acRole = findViewById(R.id.acRole);
        tvSignIn = findViewById(R.id.tvSignIn);

        // حدث الضغط على زر التسجيل
        btnSignUp.setOnClickListener(v -> validateAndInsertRecord());

        // حدث الانتقال إلى شاشة تسجيل الدخول
        tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, signInActivity.class);
            startActivity(intent);
        });
    }

    /**
     * تتحقق من صحة البيانات المدخلة من المستخدم،
     * ثم تُدخل المستخدم الجديد إلى قاعدة البيانات إذا كانت البيانات صحيحة.
     *
     * @return true إذا كانت جميع البيانات صحيحة وتم الإدخال، false إذا وُجد خطأ
     */
    private boolean validateAndInsertRecord() {

        // قراءة القيم من الحقول
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail2.getText().toString().trim();
        String password = etPassword2.getText().toString().trim();
        String role = acRole.getText().toString().trim();
        String portfolio = etPortfolio.getText().toString().trim();
        String experienceYears = etExperienceYears.getText().toString().trim();
        String skills = etSkills.getText().toString().trim();

        boolean isValid = true;

        // التحقق من الاسم الكامل
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Full name is required");
            isValid = false;
        }

        // التحقق من رقم الهاتف
        if (!Patterns.PHONE.matcher(phone).matches()) {
            etPhone.setError("Invalid phone number");
            isValid = false;
        }

        // التحقق من الإيميل (فارغ)
        if (TextUtils.isEmpty(email)) {
            etEmail2.setError("Email is required");
            isValid = false;
        }

        // التحقق من صيغة الإيميل
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail2.setError("Invalid email");
            isValid = false;
        }

        // التحقق من طول كلمة المرور
        if (password.length() < 6) {
            etPassword2.setError("Password must be at least 6 characters long");
            isValid = false;
        }


        // التحقق إذا كان الإيميل موجود مسبقاً في قاعدة البيانات
        MyCinemaUser user =
                AppDatabase.getDb(this).myCinemaUserQuery().getUserByEmail(email);

        if (user != null) {
            etEmail2.setError("Email already exists");
            isValid = false;
        }

        // إذا كانت جميع البيانات صحيحة
        if (isValid) {

            // إنشاء كائن مستخدم جديد
            MyCinemaUser myuser = new MyCinemaUser();
            myuser.setFullName(fullName);
            myuser.setEmail(email);
            myuser.setPassword(password);
            myuser.setPhone(phone);
            myuser.setRole("myuser");

            // إدخال المستخدم إلى قاعدة البيانات
            AppDatabase.getDb(this)
                    .myCinemaUserQuery()
                    .insertUser(myuser);

            // رسالة نجاح
            Toast.makeText(this,
                    "Record inserted successfully",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        return isValid;
    }
}
