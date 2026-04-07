package com.example.laracin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * AddLinkActivity
 *
 * شاشة إضافة رابط عمل جديد
 *
 * الهدف من الشاشة
 * - تخلي المستخدم يدخل معلومات عمل جديد
 * - تتأكد إن الحقول المطلوبة مش فاضية
 * - ترجع البيانات للشاشة السابقة عن طريق Intent
 *
 * الحقول الموجودة
 * - etWorkName اسم العمل
 * - etWorkType نوع العمل
 * - etWorkDescription وصف العمل
 * - etWorkLink رابط العمل
 *
 * الأزرار
 * - btnBackToLinks للرجوع للشاشة السابقة
 * - btnSaveLink لحفظ البيانات وإرجاعها
 */
public class AddLinkActivity extends AppCompatActivity {

    // زر الرجوع للشاشة السابقة
    private ImageView btnBackToLinks;

    // حقول إدخال بيانات العمل
    private EditText etWorkName;
    private EditText etWorkType;
    private EditText etWorkDescription;
    private EditText etWorkLink;

    // زر حفظ الرابط
    private Button btnSaveLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // تحميل واجهة الشاشة
        setContentView(R.layout.activity_add_link);

        // ربط عناصر الواجهة بالمتغيرات
        btnBackToLinks = findViewById(R.id.btnBackToLinks);
        etWorkName = findViewById(R.id.etWorkName);
        etWorkType = findViewById(R.id.etWorkType);
        etWorkDescription = findViewById(R.id.etWorkDescription);
        etWorkLink = findViewById(R.id.etWorkLink);
        btnSaveLink = findViewById(R.id.btnSaveLink);

        // الرجوع للشاشة السابقة
        btnBackToLinks.setOnClickListener(v -> finish());

        // حفظ الرابط وإرجاع البيانات للشاشة السابقة
        btnSaveLink.setOnClickListener(v -> {

            String workName = etWorkName.getText().toString().trim();
            String workType = etWorkType.getText().toString().trim();
            String workDescription = etWorkDescription.getText().toString().trim();
            String workLink = etWorkLink.getText().toString().trim();

            if (TextUtils.isEmpty(workName)) {
                etWorkName.setError("Enter work name");
                etWorkName.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(workType)) {
                etWorkType.setError("Enter work type");
                etWorkType.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(workDescription)) {
                etWorkDescription.setError("Enter description");
                etWorkDescription.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(workLink)) {
                etWorkLink.setError("Paste link here");
                etWorkLink.requestFocus();
                return;
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("workName", workName);
            resultIntent.putExtra("workType", workType);
            resultIntent.putExtra("workDescription", workDescription);
            resultIntent.putExtra("workLink", workLink);

            setResult(RESULT_OK, resultIntent);
            Toast.makeText(this, "Link saved", Toast.LENGTH_SHORT).show();

            finish();
        });
    }
}