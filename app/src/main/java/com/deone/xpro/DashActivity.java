package com.deone.xpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.deone.xpro.adapters.ArticlesAdapter;
import com.deone.xpro.adapters.XClicklistener;
import com.deone.xpro.models.Article;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private List<Article> articleList;
    private ArticlesAdapter articlesAdapter;
    private RecyclerView rvItems;
    private String myUID;
    private String mySEARCH;
    private boolean checkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        configAppTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.pocket_expert));
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        checkUser();
    }

    private void configAppTheme() {
        SharedPreferences mPrefs = getSharedPreferences("Theme", Context.MODE_PRIVATE);
        checkMode = mPrefs.getBoolean("ThemeMode", false);

        if (checkMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            Toast.makeText(this, "DARK MODE", Toast.LENGTH_SHORT).show();
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Toast.makeText(this, "LIGHT MODE", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        checkUser();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        /*MenuItem  mSearch = menu.findItem(R.id.seach);
        SearchView searchView = (SearchView) mSearch.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.search));
        manageSearchView(searchView);*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings){
            startActivity(new Intent(DashActivity.this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkUser() {
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser != null){
            myUID = mUser.getUid();
            initUI();
            allArticles();
        }else {
            startActivity(new Intent(DashActivity.this, MainActivity.class));
            finish();
        }
    }

    private void allArticles() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Xpro");
        reference.child("Articles").addValueEventListener(valArticles);
    }

    private void searchArticles(String query) {
        mySEARCH = query;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Xpro");
        reference.child("Articles").addValueEventListener(valSearchArticles);
    }

    private void initUI() {
        articleList = new ArrayList<>();
        rvItems = findViewById(R.id.rvItems);
        findViewById(R.id.fabAddItem).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fabAddItem){
            addNewItem();
        }
    }

    private void addNewItem() {
        startActivity(new Intent(DashActivity.this, AddActivity.class));
    }

    private void manageSearchView(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)){
                    searchArticles(""+query);
                }else {
                    allArticles();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    searchArticles(""+newText);
                }else {
                    allArticles();
                }
                return false;
            }
        });
    }

    private final ValueEventListener valArticles = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            articleList.clear();
            for (DataSnapshot ds : snapshot.getChildren()){
                Article article = ds.getValue(Article.class);
                articleList.add(article);
                articlesAdapter = new ArticlesAdapter(DashActivity.this, articleList);
                rvItems.setAdapter(articlesAdapter);
                articlesAdapter.setListener(new XClicklistener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(DashActivity.this, ShowActivity.class);
                        intent.putExtra("aId", articleList.get(position).getaId());
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                });
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private final ValueEventListener valSearchArticles = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            articleList.clear();
            for (DataSnapshot ds : snapshot.getChildren()){
                Article article = ds.getValue(Article.class);
                if (article.getaTitre().toLowerCase().contains(mySEARCH.toLowerCase()) ||
                        article.getaDescription().toLowerCase().contains(mySEARCH.toLowerCase()) ||
                        article.getaCategorie().toLowerCase().contains(mySEARCH.toLowerCase())){
                    articleList.add(article);
                    articlesAdapter = new ArticlesAdapter(DashActivity.this, articleList);
                    rvItems.setAdapter(articlesAdapter);
                    articlesAdapter.setListener(new XClicklistener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent = new Intent(DashActivity.this, ShowActivity.class);
                            intent.putExtra("aId", articleList.get(position).getaId());
                            startActivity(intent);
                        }

                        @Override
                        public void onLongItemClick(View view, int position) {

                        }
                    });
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
}