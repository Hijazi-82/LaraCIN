package com.example.laracin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * SaveProfileActivity
 *
 * شاشة إنشاء وتحديث ملف المستخدم.
 *
 * الوظائف:
 * 1. قراءة بيانات البروفايل.
 * 2. اختيار صورة من الهاتف.
 * 3. تحويل الصورة إلى Base64 String.
 * 4. تحديث المستخدم في Room.
 * 5. إرسال المستخدم إلى CinemaUserService للحفظ في Firebase.
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

    private ImageView ivSelectedImage;
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

        if (getIntent() != null
                && getIntent().getExtras() != null
                && getIntent().getExtras().get("cinmaUser") != null) {

            cinmaUser = (MyCinemaUser) getIntent().getExtras().get("cinmaUser");
        }

        auth = FirebaseAuth.getInstance();

        btnSignUp = findViewById(R.id.btCreatePro);
        etFullName = findViewById(R.id.etFullname);
        etPhone = findViewById(R.id.etPhone);
        etPortfolio = findViewById(R.id.etPortfolio);
        etExperienceYears = findViewById(R.id.etExperienceYears);
        etSkills = findViewById(R.id.etSkills);
        acRole = findViewById(R.id.acRole);
        tvSignIn = findViewById(R.id.tvIfSignIn);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);

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

        // اختيار صورة من الجهاز وعرضها
        pickImage = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            selectedImageUri = result;
                            ivSelectedImage.setImageURI(result);
                            ivSelectedImage.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );

        ivSelectedImage.setOnClickListener(v -> pickImage.launch("image/*"));

        btnSignUp.setOnClickListener(v -> validateAndInsertRecord());

        if (cinmaUser != null) {
            etFullName.setText(cinmaUser.getFullName());
            etPhone.setText(cinmaUser.getPhone());
            acRole.setText(cinmaUser.getRole());
            etPortfolio.setText(cinmaUser.getPortfolio());
            etExperienceYears.setText(String.valueOf(cinmaUser.getExperienceYears()));
            etSkills.setText(cinmaUser.getSkills());

            btnSignUp.setText("Update");

            // إذا كان عند المستخدم صورة محفوظة، اعرضها
            Bitmap bitmap = stringToBitmap(cinmaUser.getProfileImageUri());
            if (bitmap != null) {
                ivSelectedImage.setImageBitmap(bitmap);
                ivSelectedImage.setVisibility(View.VISIBLE);
            }
        }

        tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SaveProfileActivity.this, SignInActivity.class);
            startActivity(intent);
        });
    }

    /**
     * validateAndInsertRecord
     *
     * تفحص البيانات، تحفظ الصورة إذا تم اختيارها،
     * تحدث المستخدم في Room، ثم ترسله إلى Service للحفظ في Firebase.
     */
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

        MyCinemaUser user =
                AppDatabase.getDb(this).myCinemaUserQuery().getUserByEmail(currentEmail);

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

        // حفظ الصورة داخل المستخدم إذا تم اختيار صورة
        if (selectedImageUri != null) {
            String imageString = convertImageToString(selectedImageUri);

            if (imageString != null) {
                user.setProfileImageUri(imageString);
            }
        }

        AppDatabase.getDb(this).myCinemaUserQuery().updateUser(user);

        Intent serviceIntent = new Intent(this, CinemaUserService.class);
        serviceIntent.putExtra(CinemaUserService.EXTRA_USER, user);
        startService(serviceIntent);

        return true;
    }

    /**
     * convertImageToString
     *
     * تحول الصورة من Uri إلى String بصيغة Base64
     * حتى يتم حفظها في Room و Firebase.
     */
    public String convertImageToString(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            if (bitmap == null) {
                Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
                return null;
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // ضغط الصورة حتى لا تكون كبيرة جدًا
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream);

            byte[] imageBytes = outputStream.toByteArray();

            return Base64.encodeToString(imageBytes, Base64.DEFAULT);

        } catch (Exception e) {
            Toast.makeText(this, "Failed to convert image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    /**
     * stringToBitmap
     *
     * تحول الصورة من Base64 String إلى Bitmap لعرضها داخل ImageView.
     */
    private Bitmap stringToBitmap(String imageString) {
        if (imageString == null || imageString.isEmpty()) {
            return null;
        }

        try {
            byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception e) {
            return null;
        }
    }

    public void saveCinemaUser(MyCinemaUser user) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = database.child("CinemaProfiles");
        DatabaseReference newUserRef = usersRef.push();

        if (user.getKeyId() == 0) {
            user.setKeyId(System.currentTimeMillis());
        }

        newUserRef.setValue(user).addOnSuccessListener(aVoid -> {
            Toast.makeText(SaveProfileActivity.this,
                    "Succeeded to add User",
                    Toast.LENGTH_SHORT).show();

            finish();

        }).addOnFailureListener(e -> {
            // يمكن إضافة رسالة خطأ هنا إذا احتجت
        });
    }
}