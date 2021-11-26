package com.deone.xpro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtvLogin;
    private EditText edtvPassword;
    private EditText edtvConfPassword;
    private TextView tvNewaccount;
    private Button btLogCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkUser();
    }

    private void checkUser() {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
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
        tvNewaccount.setOnClickListener(this);
        btLogCreate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvNewaccount : checkTvVisibility();
                break;
            case R.id.btLogCreate : checkBtVisibility();
                break;
            default:
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
        String email = edtvLogin.getText().toString().trim();
        String password = edtvPassword.getText().toString().trim();
        if (edtvConfPassword.getVisibility() == View.VISIBLE){
            String confirm = edtvConfPassword.getText().toString().trim();
            createNewAccount(
                    ""+email,
                    ""+password,
                    ""+confirm
            );
        } else if (edtvConfPassword.getVisibility() == View.GONE){
            connectUser(
                    ""+email,
                    ""+password
            );
        }
    }

    private void createNewAccount(String email, String password, String confirm) {

    }

    private void connectUser(String email, String password) {

    }
}