package com.deone.xpro;

import static com.deone.xpro.tools.Constants.CAMERA_REQUEST_CODE;
import static com.deone.xpro.tools.Constants.GALLERY_TYPE;
import static com.deone.xpro.tools.Constants.IMAGE_DESCRIPTION;
import static com.deone.xpro.tools.Constants.IMAGE_PICK_CAMERA_CODE;
import static com.deone.xpro.tools.Constants.IMAGE_PICK_GALLERY_CODE;
import static com.deone.xpro.tools.Constants.IMAGE_TITLE;
import static com.deone.xpro.tools.Constants.STORAGE_REQUEST_CODE;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private String myUID;
    private String[] cameraPermissions;
    private String[] storagePermissions;
    private Uri imageUri;
    private ImageView imArticle;
    private AutoCompleteTextView actvCategorie;
    private EditText edtvItemtitre;
    private EditText edtvItemdescription;

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
        cameraPermissions = new String[]{Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        imArticle = findViewById(R.id.imArticle);
        actvCategorie = findViewById(R.id.actvCategorie);
        edtvItemtitre = findViewById(R.id.edtvItemtitre);
        edtvItemdescription = findViewById(R.id.edtvItemdescription);
        findViewById(R.id.btAddarticle).setOnClickListener(this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                imageUri = data.getData();
                imArticle.setImageURI(imageUri);
            }else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                imArticle.setImageURI(imageUri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){
                        pickFromCamera();
                    }else {
                        Toast.makeText(AddActivity.this,
                                getResources().getString(R.string.enable_camera_permission),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0){
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        pickFromGallery();
                    }else {
                        Toast.makeText(AddActivity.this,
                                getResources().getString(R.string.enable_storage_permission),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void addPhoto() {
        String[] options = {getResources().getString(R.string.camera),
                getResources().getString(R.string.gallery)};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
        builder.setTitle(getResources().getString(R.string.select_image));
        builder.setMessage(getResources().getString(R.string.question_image));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0 :
                        if (!checkCameraPermissions())
                            requestCameraPermissions();
                        else 
                            pickFromCamera();
                        break;
                    case 1:
                        if (!checkStoragePermissions())
                            requestStoragePermissions();
                        else
                            pickFromGallery();
                        break;
                    default:
                }
            }
        });
        builder.create().show();
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType(GALLERY_TYPE);
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, IMAGE_TITLE);
        values.put(MediaStore.Images.Media.DESCRIPTION, IMAGE_DESCRIPTION);
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void requestStoragePermissions() {
        ActivityCompat.requestPermissions(AddActivity.this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private void requestCameraPermissions() {
        ActivityCompat.requestPermissions(AddActivity.this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private boolean checkStoragePermissions() {
        return ContextCompat.checkSelfPermission(AddActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    private boolean checkCameraPermissions() {
        boolean result = ContextCompat.checkSelfPermission(AddActivity.this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(AddActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
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