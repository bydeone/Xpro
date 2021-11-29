package com.deone.xpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtvLogin;
    private EditText edtvPassword;
    private EditText edtvConfPassword;
    private TextView tvNewaccount;
    private Button btLogCreate;
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
        edtvLogin = findViewById(R.id.edtvLogin);
        edtvPassword = findViewById(R.id.edtvPassword);
        edtvConfPassword = findViewById(R.id.edtvConfPassword);
        tvNewaccount = findViewById(R.id.tvNewaccount);
        btLogCreate = findViewById(R.id.btLogCreate);
        progressDialog = new ProgressDialog(this);
        tvNewaccount.setOnClickListener(this);
        btLogCreate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.tvNewaccount){
            checkTvVisibility();
        }else if(v.getId() == R.id.btLogCreate){
            checkBtVisibility();
        }
    }

    private void checkTvVisibility() {
        if (edtvConfPassword.getVisibility() == View.VISIBLE){
            edtvConfPassword.setVisibility(View.GONE);
            tvNewaccount.setText(getResources().getString(R.string.newaccount));
            btLogCreate.setText(getResources().getString(R.string.creationaccount));
        } else if (edtvConfPassword.getVisibility() == View.GONE){
            edtvConfPassword.setVisibility(View.VISIBLE);
            tvNewaccount.setText(getResources().getString(R.string.ihaveaccount));
            btLogCreate.setText(getResources().getString(R.string.conn));
        }
    }

    private void checkBtVisibility() {
        btLogCreate.setEnabled(false);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getResources().getString(R.string.app_name));
        String email = edtvLogin.getText().toString().trim();
        String password = edtvPassword.getText().toString().trim();
        if (email.isEmpty()){
            Toast.makeText(MainActivity.this,
                    getResources().getString(R.string.email_error),
                    Toast.LENGTH_SHORT).show();
            btLogCreate.setEnabled(true);
            return;
        }
        if (password.isEmpty()){
            Toast.makeText(MainActivity.this,
                    getResources().getString(R.string.password_error),
                    Toast.LENGTH_SHORT).show();
            btLogCreate.setEnabled(true);
            return;
        }
        if (edtvConfPassword.getVisibility() == View.VISIBLE){
            String confirm = edtvConfPassword.getText().toString().trim();
            createNewAccount(
                    ""+email,
                    ""+password,
                    ""+confirm
            );
        } else if (edtvConfPassword.getVisibility() == View.GONE){
            progressDialog.setMessage(getResources().getString(R.string.pd_conn_user));
            connectUser(
                    ""+email,
                    ""+password
            );
        }
    }

    private void createNewAccount(String email, String password, String confirm) {
        if (!password.equals(confirm)){
            Toast.makeText(MainActivity.this,
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
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this,
                                    ""+getResources().getString(R.string.create_account_ok),
                                    Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();

                            startActivity(new Intent(MainActivity.this, DashActivity.class));
                            finish();
                        }else {
                            Toast.makeText(MainActivity.this,
                                    ""+getResources().getString(R.string.create_account_error),
                                    Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    private void connectUser(String email, String password) {
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
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
                    }
                });
    }
}