package com.deone.xpro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtvLogin;
    private EditText edtvPassword;
    private EditText edtvConfPassword;
    private Button btLogCreate;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        mAuth = FirebaseAuth.getInstance();
        checkUser();
    }

    private void checkUser() {
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser != null){
            startActivity(new Intent(CreateActivity.this, DashActivity.class));
            finish();
        }else {
            initUI();
        }
    }

    private void initUI() {
        progressDialog = new ProgressDialog(this);
        edtvLogin = findViewById(R.id.edtvLogin);
        edtvPassword = findViewById(R.id.edtvPassword);
        edtvConfPassword = findViewById(R.id.edtvConfPassword);
        btLogCreate = findViewById(R.id.btLogCreate);
        btLogCreate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btLogCreate){
            verifSaisie();
        }
    }

    private void verifSaisie() {
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getResources().getString(R.string.app_name));
        String email = edtvLogin.getText().toString().trim();
        String password = edtvPassword.getText().toString().trim();
        if (email.isEmpty()){
            Toast.makeText(CreateActivity.this,
                    getResources().getString(R.string.email_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()){
            Toast.makeText(CreateActivity.this,
                    getResources().getString(R.string.password_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String confirm = edtvConfPassword.getText().toString().trim();
        createNewAccount(
                ""+email,
                ""+password,
                ""+confirm
        );
    }

    private void createNewAccount(String email, String password, String confirm) {
        if (!password.equals(confirm)){
            Toast.makeText(CreateActivity.this,
                    getResources().getString(R.string.password_conf_error),
                    Toast.LENGTH_SHORT).show();
            btLogCreate.setEnabled(true);
            return;
        }
        progressDialog.setMessage(getResources().getString(R.string.pd_create_user));
        createAccount(
                ""+email,
                ""+password
        );
    }

    private void createAccount(String email, String password) {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        progressDialog.dismiss();
                        Toast.makeText(CreateActivity.this,
                                ""+getResources().getString(R.string.create_account_ok),
                                Toast.LENGTH_LONG).show();
                        startActivity(new Intent(CreateActivity.this, ProfileActivity.class));
                        finish();
                    }else {
                        Toast.makeText(CreateActivity.this,
                                ""+getResources().getString(R.string.create_account_error),
                                Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CreateActivity.this, MainActivity.class));
        finish();
        super.onBackPressed();
    }
}