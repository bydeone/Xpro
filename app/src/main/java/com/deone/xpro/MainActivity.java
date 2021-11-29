package com.deone.xpro;

import static com.deone.xpro.tools.Constants.USER_EMAIL;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtvEmail;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        checkUser();
    }

    private void checkUser() {
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser != null){
            startActivity(new Intent(MainActivity.this, DashActivity.class));
            finish();
        }else {
            initUI();
        }
    }

    private void initUI() {
        progressDialog = new ProgressDialog(this);
        edtvEmail = findViewById(R.id.edtvEmail);
        findViewById(R.id.fabCheckEmail).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fabCheckEmail){
            verificationSaisies();
        }
    }

    private void verificationSaisies() {
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getResources().getString(R.string.app_name));
        String email = edtvEmail.getText().toString().trim();
        if (email.isEmpty()){
            Toast.makeText(MainActivity.this,
                    getResources().getString(R.string.email_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        checkEmail(""+email);
    }

    private void checkEmail(String email) {
        progressDialog.setMessage(getResources().getString(R.string.pd_create_user));
        progressDialog.show();
        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()){
                    boolean check = !task.getResult().getSignInMethods().isEmpty();
                    if (!check){ //envoyer un lien de connexion dns l'email
                        progressDialog.dismiss();
                        Intent intent = new Intent(MainActivity.this, AddActivity.class);
                        intent.putExtra(USER_EMAIL, email);
                        startActivity(intent);
                        finish();
                    }else {
                        progressDialog.dismiss();
                        // Email already exist
                    }
                }
            }
        });
    }
}