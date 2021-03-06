package com.deone.xpro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtvLogin;
    private EditText edtvPassword;
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
        edtvLogin = findViewById(R.id.edtvLogin);
        edtvPassword = findViewById(R.id.edtvPassword);
        findViewById(R.id.tvNewaccount).setOnClickListener(this);
        findViewById(R.id.tvResetPassword).setOnClickListener(this);
        findViewById(R.id.btLogCreate).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.tvNewaccount){
            startActivity(new Intent(MainActivity.this, CreateActivity.class));
            finish();
        }else if(v.getId() == R.id.tvResetPassword){
            resetPassword();
        }else if(v.getId() == R.id.btLogCreate){
            verifSaisie();
        }
    }

    private void resetPassword() {

    }

    private void verifSaisie() {
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getResources().getString(R.string.app_name));
        String email = edtvLogin.getText().toString().trim();
        String password = edtvPassword.getText().toString().trim();
        if (email.isEmpty()){
            Toast.makeText(MainActivity.this,
                    getResources().getString(R.string.email_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()){
            Toast.makeText(MainActivity.this,
                    getResources().getString(R.string.password_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage(getResources().getString(R.string.pd_conn_user));
        connectUser(
                ""+email,
                ""+password
        );
    }

    private void connectUser(String email, String password) {
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(MainActivity.this,
                                ""+getResources().getString(R.string.sign_in_ok),
                                Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();

                        startActivity(new Intent(MainActivity.this, DashActivity.class));
                        finish();
                    }else {
                        Toast.makeText(MainActivity.this,
                                ""+getResources().getString(R.string.sign_in_error),
                                Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });
    }

}