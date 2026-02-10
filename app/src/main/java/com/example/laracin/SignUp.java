package com.example.laracin;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.laracin.data.AppDatabase;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUser;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUserQuery;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    private Button btnSignUp;

    private TextInputEditText etFullName, etEmail2, etPhone, etPassword2,
            etPortfolio, etExperienceYears, etSkills;

    private AutoCompleteTextView acRole;

    private TextView tvSignIn;

    private MyCinemaUserQuery dao;

    // 🔥 Firebase
    private FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        // 🔥 تهيئة Firebase
        auth = FirebaseAuth.getInstance();

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

        btnSignUp.setOnClickListener(v -> validateAndInsertRecord());

        tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, signInActivity.class);
            startActivity(intent);
        });
    }

    private boolean validateAndInsertRecord() {

        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail2.getText().toString().trim();
        String password = etPassword2.getText().toString().trim();
        String role = acRole.getText().toString().trim();
        String portfolio = etPortfolio.getText().toString().trim();
        String experienceYears = etExperienceYears.getText().toString().trim();
        String skills = etSkills.getText().toString().trim();

        boolean isValid = true;

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Full name is required");
            isValid = false;
        }

        if (!Patterns.PHONE.matcher(phone).matches()) {
            etPhone.setError("Invalid phone number");
            isValid = false;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail2.setError("Email is required");
            isValid = false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail2.setError("Invalid email");
            isValid = false;
        }

        if (password.length() < 6) {
            etPassword2.setError("Password must be at least 6 characters long");
            isValid = false;
        }

        // التحقق من وجود الإيميل في Room
        MyCinemaUser user =
                AppDatabase.getDb(this).myCinemaUserQuery().getUserByEmail(email);

        if (user != null) {
            etEmail2.setError("Email already exists");
            isValid = false;
        }

        // ✅ إذا البيانات صحيحة
        if (isValid) {

            // 🔥 تسجيل المستخدم في Firebase
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                // إنشاء المستخدم داخل Room
                                MyCinemaUser myuser = new MyCinemaUser();
                                myuser.setFullName(fullName);
                                myuser.setEmail(email);
                                myuser.setPassword(password);
                                myuser.setPhone(phone);
                                myuser.setRole("myuser");

                                AppDatabase.getDb(SignUp.this)
                                        .myCinemaUserQuery()
                                        .insertUser(myuser);

                                Toast.makeText(SignUp.this,
                                        "Signing up Succeeded",
                                        Toast.LENGTH_SHORT).show();

                                finish();

                            } else {

                                Toast.makeText(SignUp.this,
                                        "Signing up Failed",
                                        Toast.LENGTH_SHORT).show();

                                etEmail2.setError(task.getException().getMessage());
                            }
                        }
                    });
        }

        return isValid;
    }
    public void saveUser(MyCinemaUser user) {// الحصول على مرجع إلى عقدة "users" في قاعدة البيانات

        // تهيئة Firebase Realtime Database    //مؤشر لقاعدة البيانات
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
// ‏مؤشر لجدول المستعملين
        DatabaseReference usersRef = database.child("users");
        // إنشاء مفتاح فريد للمستخدم الجديد
        DatabaseReference newUserRef = usersRef.push();
        // تعيين معرف المستخدم في كائن MyUser
        user.setKeyId(Long.parseLong(newUserRef.getKey()));
        // حفظ بيانات المستخدم في قاعدة البيانات
        //اضافة كائن "لمجموعة" المستعملين ومعالج حدث لفحص نجاح المطلوب
        // حدث لفحص هل تم المطلوب من قاعدة البيانات معالم
        newUserRef.setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SignUp.this, "Succeeded to add User",  Toast.LENGTH_SHORT).show();
                        finish();




                        // تم حفظ البيانات بنجاح
                        Log.d(TAG, "تم حفظ المستخدم بنجاح: " + user.getKeyId());
                        // تحديث واجهة المستخدم أو تنفيذ إجراءات أخرى
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // معالجة الأخطاء
                        Log.e(TAG, "خطأ في حفظ المستخدم: " + e.getMessage(), e);
                        Toast.makeText(SignUp.this, "Failed to add User", Toast.LENGTH_SHORT).show();
                        // عرض رسالة خطأ للمستخدم
                    }
                });
        btnSignUp.setOnClickListener(v -> {
            String name = etFullName.getText().toString();
            String email = etEmail2.getText().toString();
            String Password= etPassword2.getText().toString();
            String phone = etPhone.getText().toString();
            String role = acRole.getText().toString();
            String portfolio = etPortfolio.getText().toString();
            String experienceYears = etExperienceYears.getText().toString();
            String skills = etSkills.getText().toString();





            if (!name.isEmpty() && !email.isEmpty()) {
                MyCinemaUser newUser = new MyCinemaUser(name, email);
                saveUser(newUser);


                // مسح حقول الإدخال
                etFullName.setText("");
                etEmail2.setText("");
            } else {
                Log.w(TAG, "الرجاء إدخال الاسم والبريد الإلكتروني.");
            }
        });


    }

}


