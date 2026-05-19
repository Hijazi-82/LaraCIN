package com.example.laracin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.laracin.data.AppDatabase;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * SignUpActivity
 *
 * شاشة إنشاء حساب جديد.
 *
 * وظيفة الشاشة:
 * 1. قراءة البريد الإلكتروني وكلمة المرور من المستخدم.
 * 2. التحقق من صحة المدخلات.
 * 3. إنشاء حساب جديد في Firebase Authentication.
 * 4. حفظ المستخدم في Room Database.
 * 5. حفظ المستخدم في Firebase Realtime Database.
 * 6. نقل المستخدم إلى شاشة إنشاء البروفايل.
 */
public class SignUpActivity extends AppCompatActivity {
    // حقول إدخال البريد الإلكتروني وكلمة المرور
    private TextInputEditText etEmail2 , etPassword2;
    // نص للانتقال إلى شاشة تسجيل الدخول
    private TextView tvSignIn;
    // كائن FirebaseAuth المسؤول عن إنشاء الحساب
    private FirebaseAuth auth;
private Button button ;
    /**
     * onCreate
     *
     * يتم استدعاؤها عند فتح شاشة التسجيل.
     * داخلها يتم ربط عناصر الواجهة، تهيئة Firebase،
     * وتجهيز أزرار التسجيل والانتقال لتسجيل الدخول.
     *
     * @param savedInstanceState يحفظ حالة الشاشة عند إعادة إنشائها
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        // ضبط الحواف حتى لا تدخل العناصر تحت أشرطة النظام
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // تهيئة Firebase Authentication
        auth = FirebaseAuth.getInstance();
        // ربط عناصر الواجهة
        etEmail2 = findViewById(R.id.eiEmail2);
        etPassword2 = findViewById(R.id.edPassword2);
        tvSignIn = findViewById(R.id.tvSignIn);
        // عند الضغط على زر التسجيل يتم فحص البيانات وإنشاء الحساب
        findViewById(R.id.button).setOnClickListener(v -> validateAndInsertRecord());
        // الانتقال إلى شاشة تسجيل الدخول
        tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * validateAndInsertRecord
     *
     * تفحص البريد الإلكتروني وكلمة المرور.
     * إذا كانت البيانات صحيحة، يتم إنشاء حساب جديد في Firebase.
     * عند نجاح التسجيل، يتم إنشاء كائن MyCinemaUser وحفظه في Room وFirebase Database.
     *
     * @return true إذا كانت المدخلات صحيحة، false إذا وُجد خطأ في المدخلات
     */
    private boolean validateAndInsertRecord() {

        String email = etEmail2.getText().toString().trim();
        String password = etPassword2.getText().toString().trim();

        boolean isValid = true;

        // فحص أن البريد غير فارغ
        if (TextUtils.isEmpty(email)) {
            etEmail2.setError("Email is required");
            isValid = false;
        }

        // فحص صيغة البريد الإلكتروني
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail2.setError("Invalid email");
            isValid = false;
        }

        // فحص طول كلمة المرور
        if (password.length() < 6) {
            etPassword2.setError("Password must be at least 6 characters long");
            isValid = false;
        }

        if (!isValid) {
            return false;
        }

        // إنشاء حساب جديد في Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    /**
                     * onComplete
                     *
                     * يتم استدعاؤها بعد انتهاء محاولة إنشاء الحساب في Firebase.
                     * إذا نجحت العملية، يتم حفظ بيانات المستخدم ونقله إلى شاشة البروفايل.
                     */
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            // إنشاء كائن مستخدم جديد
                            MyCinemaUser myuser = new MyCinemaUser();
                            myuser.setEmail(email);
                            myuser.setPassword(password);
                            myuser.setRole("myuser");

                            // حفظ UID من Firebase كمفتاح للمستخدم
                            if (task.getResult() != null && task.getResult().getUser() != null) {
                                myuser.setKey(task.getResult().getUser().getUid());
                            }

                            // حفظ المستخدم في Firebase Realtime Database
                            saveOrUpdateUserToFirebase(myuser);

                            // حفظ المستخدم في Room Database
                            AppDatabase.getDb(SignUpActivity.this)
                                    .myCinemaUserQuery()
                                    .insertUser(myuser);

                            Toast.makeText(SignUpActivity.this,
                                    "Signing up Succeeded",
                                    Toast.LENGTH_SHORT).show();

                            // الانتقال إلى شاشة إنشاء البروفايل
                            Intent intent = new Intent(SignUpActivity.this, SaveProfileActivity.class);
                            startActivity(intent);
                            finish();

                        } else {

                            Toast.makeText(SignUpActivity.this,
                                    "Signing up Failed",
                                    Toast.LENGTH_SHORT).show();

                            if (task.getException() != null) {
                                etEmail2.setError(task.getException().getMessage());
                            }
                        }
                    }
                });

        return true;
    }
    /**
     * saveOrUpdateUserToFirebase
     *
     * تحفظ بيانات المستخدم في Firebase Realtime Database داخل المسار users.
     * إذا لم يكن للمستخدم key، يتم إنشاء key جديد.
     *
     * @param user المستخدم المراد حفظه في Firebase
     */
    private void saveOrUpdateUserToFirebase(MyCinemaUser user) {

        DatabaseReference myRef = FirebaseDatabase
                .getInstance()
                .getReference("users");

        String key = "";

        // إذا لم يكن هناك key، يتم إنشاء key جديد
        if (user.getKey() == null || user.getKey().isEmpty()) {
            key = myRef.push().getKey();
            user.setKey(key);
        }

        myRef.child(user.getKey()).setValue(user).addOnCompleteListener(fbTask -> {

            if (fbTask.isSuccessful()) {
                Toast.makeText(getApplicationContext(),
                        "User Saved Successfully",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Saving Failed",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}