package com.example.laracin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class WorkActivity extends AppCompatActivity {

    private EditText etProjectsSearch;
    private Button btnAddLink;
    private ListView listProjectsLinks;

    private TextView navHome;
    private TextView navProjects;
    private TextView navFavorite;
    private TextView navProfile;

    private ArrayList<HashMap<String, String>> allLinksList;
    private ArrayList<HashMap<String, String>> filteredLinksList;
    private SimpleAdapter adapter;

    private final ActivityResultLauncher<Intent> addLinkLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {

                            String workName = result.getData().getStringExtra("workName");
                            String workType = result.getData().getStringExtra("workType");
                            String workDescription = result.getData().getStringExtra("workDescription");
                            String workLink = result.getData().getStringExtra("workLink");

                            HashMap<String, String> item = new HashMap<>();
                            item.put("workName", workName);
                            item.put("workType", workType);
                            item.put("workDescription", workDescription);
                            item.put("workLink", workLink);

                            allLinksList.add(item);
                            filterLinks(etProjectsSearch.getText().toString().trim());
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);

        etProjectsSearch = findViewById(R.id.etProjectsSearch);
        btnAddLink = findViewById(R.id.btnAddLink);
        listProjectsLinks = findViewById(R.id.listProjectsLinks);

        navHome = findViewById(R.id.navHome);
        navProjects = findViewById(R.id.navProjects);
        navFavorite = findViewById(R.id.navFavorite);
        navProfile = findViewById(R.id.navProfile);

        allLinksList = new ArrayList<>();
        filteredLinksList = new ArrayList<>();

        adapter = new SimpleAdapter(
                this,
                filteredLinksList,
                android.R.layout.simple_list_item_2,
                new String[]{"workName", "workType"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );

        listProjectsLinks.setAdapter(adapter);

        btnAddLink.setOnClickListener(v -> {
            Intent intent = new Intent(WorkActivity.this, AddLinkActivity.class);
            addLinkLauncher.launch(intent);
        });

        etProjectsSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterLinks(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(WorkActivity.this, Activity_main1.class);
            startActivity(intent);
            finish();
        });

        navFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(WorkActivity.this, FavoriteActivity.class);
            startActivity(intent);
            finish();
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(WorkActivity.this, SaveProfileActivity.class);
            startActivity(intent);
            finish();
        });

        navProjects.setOnClickListener(v -> {
        });
    }

    private void filterLinks(String query) {
        filteredLinksList.clear();

        if (query.isEmpty()) {
            filteredLinksList.addAll(allLinksList);
        } else {
            String lowerQuery = query.toLowerCase();

            for (HashMap<String, String> item : allLinksList) {
                String workName = item.get("workName") != null ? item.get("workName").toLowerCase() : "";
                String workType = item.get("workType") != null ? item.get("workType").toLowerCase() : "";
                String workDescription = item.get("workDescription") != null ? item.get("workDescription").toLowerCase() : "";

                if (workName.contains(lowerQuery) || workType.contains(lowerQuery) || workDescription.contains(lowerQuery)) {
                    filteredLinksList.add(item);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }
}