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

public class SaveProfileActivity extends AppCompatActivity {

    private Button btnSignUp;

    private TextInputEditText etFullName,  etPhone,
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
        setContentView(R.layout.activity_save_profile);

        // 🔥 تهيئة Firebase
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

        btnSignUp.setOnClickListener(v -> validateAndInsertRecord());

        tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SaveProfileActivity.this, signInActivity.class);
            startActivity(intent);
        });
    }

    private boolean validateAndInsertRecord() {

        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
    //    String email = etEmail2.getText().toString().trim();
    //    String password = etPassword2.getText().toString().trim();
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



        // التحقق من وجود الإيميل في Room
        MyCinemaUser user =
                AppDatabase.getDb(this).myCinemaUserQuery().getUserByEmail(phone);



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

                                AppDatabase.getDb(SaveProfileActivity.this)
                                        .myCinemaUserQuery()
                                        .insertUser(myuser);

                                Toast.makeText(SaveProfileActivity.this,
                                        "Signing up Succeeded",
                                        Toast.LENGTH_SHORT).show();

                                finish();

                            } else {

                                Toast.makeText(SaveProfileActivity.this,
                                        "Signing up Failed",
                                        Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(SaveProfileActivity.this, "Succeeded to add User",  Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(SaveProfileActivity.this, "Failed to add User", Toast.LENGTH_SHORT).show();
                        // عرض رسالة خطأ للمستخدم
                    }
                });
        btnSignUp.setOnClickListener(v -> {
            String name = etFullName.getText().toString();
            String phone = etPhone.getText().toString();
            String role = acRole.getText().toString();
            String portfolio = etPortfolio.getText().toString();
            String experienceYears = etExperienceYears.getText().toString();
            String skills = etSkills.getText().toString();





            if (!name.isEmpty()) {
                MyCinemaUser newUser = new MyCinemaUser();
                saveUser(newUser);


                // مسح حقول الإدخال
                etFullName.setText("");
            } else {
                Log.w(TAG, "الرجاء إدخال الاسم والبريد الإلكتروني.");
            }
        });


    }

}


