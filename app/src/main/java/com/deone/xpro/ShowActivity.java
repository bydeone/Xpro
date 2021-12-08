package com.deone.xpro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.deone.xpro.adapters.MainAdapter;
import com.deone.xpro.fragments.ArticleFragment;
import com.deone.xpro.fragments.CommentsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ShowActivity extends AppCompatActivity {
    // A lire impérativement
    //https://www.geeksforgeeks.org/how-to-collapse-toolbar-layout-in-android/

    private FirebaseAuth mAuth;
    private String myUID;
    private String aId;
    private Toolbar toolbar;
    private TextView tvLegende;

    private TabLayout tab_layout;
    private ViewPager view_pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        Intent intent = getIntent();
        aId = intent.getStringExtra("aId");
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.pocket_expert));
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
            startActivity(new Intent(ShowActivity.this, MainActivity.class));
            finish();
        }
    }

    private void initUI() {
        tvLegende = findViewById(R.id.tvLegende);

        tab_layout = findViewById(R.id.tab_layout);
        view_pager = findViewById(R.id.view_pager);

        MainAdapter adapter = new MainAdapter(getSupportFragmentManager());
        adapter.addFragment(new ArticleFragment(), "Article");
        adapter.addFragment(new CommentsFragment(), "Commentaires");

        view_pager.setAdapter(adapter);

        tab_layout.setupWithViewPager(view_pager);
    }
}