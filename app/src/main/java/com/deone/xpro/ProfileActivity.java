package com.deone.xpro;

import static com.deone.xpro.tools.Constants.CAMERA_REQUEST_CODE;
import static com.deone.xpro.tools.Constants.GALLERY_TYPE;
import static com.deone.xpro.tools.Constants.IMAGE_DESCRIPTION;
import static com.deone.xpro.tools.Constants.IMAGE_PICK_CAMERA_CODE;
import static com.deone.xpro.tools.Constants.IMAGE_PICK_GALLERY_CODE;
import static com.deone.xpro.tools.Constants.IMAGE_TITLE;
import static com.deone.xpro.tools.Constants.STORAGE_REQUEST_CODE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private String myUID;
    private String myEMAIL;
    private ImageView ivLogo;
    private EditText edtvNom;
    private EditText edtvTelephone;
    private String[] cameraPermissions;
    private String[] storagePermissions;
    private Uri imageUri;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.utilisateur_profile));
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        checkUser();
    }

    @Override
    protected void onStart() {
        checkUser();
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btCreateProfile){
            saveProfile();
        }
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
                ivLogo.setImageURI(imageUri);
            }else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                ivLogo.setImageURI(imageUri);
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
                        Toast.makeText(ProfileActivity.this,
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
                        Toast.makeText(ProfileActivity.this,
                                getResources().getString(R.string.enable_storage_permission),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void checkUser() {
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser != null){
            myUID = mUser.getUid();
            myEMAIL = mUser.getEmail();
            initUI();
        }else {
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish();
        }
    }

    private void initUI() {
        imageUri = null;
        cameraPermissions = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        progressDialog = new ProgressDialog(this);
        ivLogo = findViewById(R.id.ivLogo);
        edtvNom = findViewById(R.id.edtvNom);
        edtvTelephone = findViewById(R.id.edtvTelephone);
        findViewById(R.id.btCreateProfile).setOnClickListener(this);
    }

    private void addPhoto() {
        String[] options = {getResources().getString(R.string.camera),
                getResources().getString(R.string.gallery)};
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
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

    private boolean checkCameraPermissions() {
        boolean result = ContextCompat.checkSelfPermission(ProfileActivity.this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(ProfileActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermissions() {
        ActivityCompat.requestPermissions(ProfileActivity.this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private boolean checkStoragePermissions() {
        return ContextCompat.checkSelfPermission(ProfileActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestStoragePermissions() {
        ActivityCompat.requestPermissions(ProfileActivity.this, storagePermissions, STORAGE_REQUEST_CODE);
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

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType(GALLERY_TYPE);
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    private void saveProfile() {
        String nom = edtvNom.getText().toString().trim();
        String telephone = edtvTelephone.getText().toString().trim();
        if (nom.isEmpty()){
            Toast.makeText(
                    ProfileActivity.this,
                    getResources().getString(R.string.name_error),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        if (telephone.isEmpty()){
            Toast.makeText(
                    ProfileActivity.this,
                    getResources().getString(R.string.phone_error),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        progressDialog.setTitle(getResources().getString(R.string.user_profile));
        progressDialog.setMessage(getResources().getString(R.string.user_message));
        progressDialog.setCancelable(false);
        prepareData(
                ""+nom,
                ""+telephone);
    }

    private void prepareData(String nom, String telephone) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uId", myUID);
        hashMap.put("uEmail", myEMAIL);
        hashMap.put("uDate", timestamp);
        hashMap.put("uNom", nom);
        hashMap.put("uTelephone", telephone);
        saveUserInfo(hashMap);
    }

    private void saveUserInfo(HashMap<String, String> hashMap) {
        progressDialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(myUID).setValue(hashMap)
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileActivity.this,
                            getResources().getString(R.string.save_user_info_successfully),
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ProfileActivity.this, DashActivity.class));
                    finish();
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileActivity.this,
                            ""+e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

}