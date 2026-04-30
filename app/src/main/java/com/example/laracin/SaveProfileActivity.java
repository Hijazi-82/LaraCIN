package com.example.laracin;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.laracin.data.AppDatabase;
import com.example.laracin.data.MyCinemaUserTable.CinemaUserService;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUser;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUserQuery;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * ============================================================
 * SaveProfileActivity
 * ============================================================
 * شاشة إنشاء وتحديث ملف المستخدم
 *
 * المسؤوليات
 * 1 قراءة بيانات المستخدم من الواجهة
 * 2 التحقق من صحة المدخلات
 * 3 تحديث المستخدم داخل Room Database
 * 4 حفظ نسخة من البيانات داخل Firebase Realtime Database
 * 5 دعم اختيار صورة بروفايل من الجهاز
 * 6 عرض قائمة Role باستخدام AutoCompleteTextView
 */

public class SaveProfileActivity extends AppCompatActivity {

    private Button btnSignUp;

    private TextInputEditText etFullName;
    private TextInputEditText etPhone;
    private TextInputEditText etPortfolio;
    private TextInputEditText etExperienceYears;
    private TextInputEditText etSkills;

    private AutoCompleteTextView acRole;

    private TextView tvSignIn;

    private ImageButton ivSelectedImage;

    private Uri selectedImageUri;

    private ActivityResultLauncher<String> pickImage;

    private MyCinemaUserQuery dao;

    private FirebaseAuth auth;
    private MyCinemaUser cinmaUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_save_profile);

        if(getIntent()!= null && getIntent().getExtras()!= null && getIntent().getExtras().get("cinmaUser")!=null)
         cinmaUser = (MyCinemaUser) getIntent().getExtras().get("cinmaUser");

        auth = FirebaseAuth.getInstance();

        btnSignUp = findViewById(R.id.btSignUp);
        etFullName = findViewById(R.id.etFullname);
        etPhone = findViewById(R.id.etPhone);
        etPortfolio = findViewById(R.id.etPortfolio);
        etExperienceYears = findViewById(R.id.etExperienceYears);
        etSkills = findViewById(R.id.etSkills);
        acRole = findViewById(R.id.acRole);
        tvSignIn = findViewById(R.id.tvSignIn);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);

        String[] roles = {"Director", "Actor", "Producer", "Editor", "Cinematographer", "Screenwriter", "Sound Designer"};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roles);
        acRole.setAdapter(roleAdapter);
        acRole.setOnClickListener(v -> acRole.showDropDown());

        pickImage = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                selectedImageUri = result;
                ivSelectedImage.setImageURI(result);
                ivSelectedImage.setVisibility(View.VISIBLE);
            }
        });
        ivSelectedImage.setOnClickListener(v -> pickImage.launch("image/*"));

        btnSignUp.setOnClickListener(v -> validateAndInsertRecord());

        if(cinmaUser!=null)
        {
            etFullName.setText(cinmaUser.getFullName());
            etPhone.setText(cinmaUser.getPhone());
            acRole.setText(cinmaUser.getRole());
            etPortfolio.setText(cinmaUser.getPortfolio());
            etExperienceYears.setText(String.valueOf(cinmaUser.getExperienceYears()));
            etSkills.setText(cinmaUser.getSkills());


            btnSignUp.setText("Update");
        }


        tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SaveProfileActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }

    private boolean validateAndInsertRecord() {
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
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

        if (!isValid) {
            return false;
        }

        String currentEmail = null;

        if (auth.getCurrentUser() != null) {
            currentEmail = auth.getCurrentUser().getEmail();
        }

        if (TextUtils.isEmpty(currentEmail)) {
            Toast.makeText(this, "User email not found", Toast.LENGTH_SHORT).show();
            return false;
        }

        MyCinemaUser user = AppDatabase.getDb(this).myCinemaUserQuery().getUserByEmail(currentEmail);

        if (user == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!TextUtils.isEmpty(experienceYears)) {
            try {
                user.setExperienceYears(Integer.parseInt(experienceYears));
            } catch (NumberFormatException e) {
                etExperienceYears.setError("Invalid experience years");
                return false;
            }
        }
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setPortfolio(portfolio);
        user.setSkills(skills);
        user.setRole(role);


        AppDatabase.getDb(this).myCinemaUserQuery().updateUser(user);

        ///saveCinemaUser(user);


        Intent serviceIntent = new Intent(this, CinemaUserService.class);
// 🔥 لازم تبعت ال user + نفس الاسم الموجود في الـ Service
        serviceIntent.putExtra(CinemaUserService.EXTRA_USER, user);
// 🔥 شغّل الخدمة
        startService(serviceIntent);
        return true;
    }

    public void saveCinemaUser(MyCinemaUser user) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = database.child("CinemaProfiles");
        DatabaseReference newUserRef = usersRef.push();

        if (user.getKeyId() == 0) {
            user.setKeyId(System.currentTimeMillis());
        }

        newUserRef.setValue(user).addOnSuccessListener(aVoid -> {
            Toast.makeText(SaveProfileActivity.this, "Succeeded to add User", Toast.LENGTH_SHORT).show();
            finish();
            // Log.d(TAG, "User saved successfully: " + user.getKeyId());
        }).addOnFailureListener(e -> {
            //Log.e(TAG, "Error saving user: " + e.getMessage(), e);
        });
    }
}