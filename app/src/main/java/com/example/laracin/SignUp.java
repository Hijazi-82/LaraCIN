package com.example.laracin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUp extends AppCompatActivity {
    private Button btnSignUp;
    private EditText etFullName, etId, etPhone, etEmail2, etPassword2;
    private TextView tvRole , tvFullName , tvId , tvPhone , tvEmail2 , tvPassword2;
    private TextView textView5 , textView6 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnSignUp = findViewById(R.id.button);
        etFullName = findViewById(R.id.etFullname);
        etId = findViewById(R.id.edId);
        etPhone = findViewById(R.id.etPhone);
        etEmail2 = findViewById(R.id.edEmail2);
        etPassword2 = findViewById(R.id.edPassword2);
        tvRole = findViewById(R.id.tvRole);
        tvFullName = findViewById(R.id.tvFullName);
        tvId = findViewById(R.id.tvId);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail2 = findViewById(R.id.tvEmail2);
        tvPassword2 = findViewById(R.id.tvPassword2);
        textView5 = findViewById(R.id.textView5);
        textView6 = findViewById(R.id.textView6);
    }
}