package com.deone.xpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private String myUID;
    private ImageView imArticle;
    private AutoCompleteTextView actvCategorie;
    private EditText edtvItemtitre;
    private EditText edtvItemdescription;
    private Button btAddarticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        checkUser();
    }

    private void checkUser() {
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser != null){
            myUID = mUser.getUid();
            initUI();
        }else {
            startActivity(new Intent(AddActivity.this, MainActivity.class));
            finish();
        }
    }

    private void initUI() {
        imArticle = findViewById(R.id.imArticle);
        actvCategorie = findViewById(R.id.actvCategorie);
        edtvItemtitre = findViewById(R.id.edtvItemtitre);
        edtvItemdescription = findViewById(R.id.edtvItemdescription);
        btAddarticle = findViewById(R.id.btAddarticle);
        btAddarticle.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addphoto){
            addPhoto();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addPhoto() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btAddarticle){
            addArticle();
        }
    }

    private void addArticle() {
        String categorie = actvCategorie.getText().toString().trim();
        String titre = edtvItemtitre.getText().toString().trim();
        String description = edtvItemdescription.getText().toString().trim();

        if (categorie.isEmpty()){
            Toast.makeText(
                    AddActivity.this,
                    getResources().getString(R.string.category_error),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (titre.isEmpty()){
            Toast.makeText(
                    AddActivity.this,
                    getResources().getString(R.string.titre_error),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (description.isEmpty()){
            Toast.makeText(
                    AddActivity.this,
                    getResources().getString(R.string.description_error),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        saveArticle(
                ""+categorie,
                ""+titre,
                ""+description);
    }

    private void saveArticle(String categorie, String titre, String description) {

    }
}