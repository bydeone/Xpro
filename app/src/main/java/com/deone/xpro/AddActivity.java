package com.deone.xpro;

import static com.deone.xpro.tools.Constants.CAMERA_REQUEST_CODE;
import static com.deone.xpro.tools.Constants.GALLERY_TYPE;
import static com.deone.xpro.tools.Constants.IMAGE_DESCRIPTION;
import static com.deone.xpro.tools.Constants.IMAGE_PICK_CAMERA_CODE;
import static com.deone.xpro.tools.Constants.IMAGE_PICK_GALLERY_CODE;
import static com.deone.xpro.tools.Constants.IMAGE_TITLE;
import static com.deone.xpro.tools.Constants.STORAGE_REQUEST_CODE;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private String myUID;
    private String myNAME;
    private String myAVATAR;
    private String[] cameraPermissions;
    private String[] storagePermissions;
    private Uri imageUri;
    private ImageView imArticle;
    private AutoCompleteTextView actvCategorie;
    private EditText edtvItemtitre;
    private EditText edtvItemdescription;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null){
            if ("text/plain".equals(type)){
                handleSendText(intent);
            }else if (type.startsWith("image/*")){
                handleSendImage(intent);
            }
        }
        checkUser();
    }

    private void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null){
            edtvItemdescription.setText(sharedText);
        }
    }

    private void handleSendImage(Intent intent) {
        Uri imageURI = (Uri)intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageURI != null){
            imageUri = imageURI;
            imArticle.setImageURI(imageUri);
        }
    }

    @Override
    protected void onStart() {
        checkUser();
        super.onStart();
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btAddarticle){
            addArticle();
        }
    }

    private void checkUser() {
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser != null){
            myUID = mUser.getUid();
            Query query = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .orderByKey().equalTo(myUID);
            query.addValueEventListener(valUsers);
            initUI();
        }else {
            startActivity(new Intent(AddActivity.this, MainActivity.class));
            finish();
        }
    }

    private void initUI() {
        imageUri = null;
        cameraPermissions = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        progressDialog = new ProgressDialog(this);
        imArticle = findViewById(R.id.imArticle);
        actvCategorie = findViewById(R.id.actvCategorie);
        edtvItemtitre = findViewById(R.id.edtvItemtitre);
        edtvItemdescription = findViewById(R.id.edtvItemdescription);
        findViewById(R.id.btAddarticle).setOnClickListener(this);
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

        prepareData(
                ""+categorie,
                ""+titre,
                ""+description);
    }

    private void prepareData(String categorie, String titre, String description) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("aId", timestamp);
        hashMap.put("aDate", timestamp);
        hashMap.put("aCategorie", categorie);
        hashMap.put("aTitre", titre);
        hashMap.put("aDescription", description);
        hashMap.put("aUid", myUID);
        hashMap.put("aUnom", myNAME);
        hashMap.put("aUphoto", myAVATAR);
        saveArticle(hashMap, ""+timestamp);
    }

    private void saveArticle(HashMap<String, String> hashMap, String timestamp) {
        progressDialog.setTitle(getResources().getString(R.string.add_new_article));
        progressDialog.setMessage(getResources().getString(R.string.add_new_message_article));
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (imageUri != null){
            saveCoverDatabase(hashMap, ""+timestamp);
        }else{
            uploadData(hashMap, ""+timestamp);
        }
    }

    private void saveCoverDatabase(HashMap<String, String> hashMap, String timestamp) {
        String filePathName = "Articles/" + "article_" + myUID + "_" + timestamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathName);
        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());
                String downloadUri =uriTask.getResult().toString();
                if (uriTask.isSuccessful()){
                    hashMap.put("aCover", downloadUri);
                    uploadData(hashMap, ""+timestamp);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AddActivity.this,
                        ""+e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadData(HashMap<String, String> hashMap, String timestamp) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Xpro");
        reference.child("Articles").child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(AddActivity.this,
                                getResources().getString(R.string.operation_reussie),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AddActivity.this,
                        ""+e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final ValueEventListener valUsers = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot ds : snapshot.getChildren()){
                myNAME = ds.child("uName").getValue(String.class);
                myAVATAR = ds.child("uAvatar").getValue(String.class);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

}