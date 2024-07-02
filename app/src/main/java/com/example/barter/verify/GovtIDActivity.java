package com.example.barter.verify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;

import com.example.barter.R;
import com.example.barter.home.Home;
import com.example.barter.login.LoginActivity;
import com.example.barter.register.Register;
import com.example.barter.termandcon.TermAConActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class GovtIDActivity extends AppCompatActivity {
Button btn_choose_image, btn_upload_image;
ImageView imagePreview;
    private Uri filePath;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_govt_idactivity);

        btn_choose_image = findViewById(R.id.btn_choose_image);
        btn_upload_image = findViewById(R.id.btn_upload_image);
        imagePreview = findViewById(R.id.image_preview);

        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://striped-harbor-351908.appspot.com/govtID");

        String id = getIntent().getExtras().getString("id");
        String password = getIntent().getExtras().getString("password");
        String emails = getIntent().getExtras().getString("email");

        btn_choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent , 2);
            }
        });

        btn_upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(filePath));
                progressDialog = new ProgressDialog(GovtIDActivity.this);
                progressDialog.setMessage("Uploading Gov't ID Loading");
                progressDialog.setProgress(10);
                progressDialog.setMax(100);
                progressDialog.show();


              if (filePath != null){
                  storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                      @Override
                      public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                          storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                              @Override
                              public void onSuccess(Uri uri) {

                                  DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(id);

                                  HashMap<String, Object> hashMap = new HashMap<>();
                                  hashMap.put("govt_id",uri.toString());

                                  reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                      @Override
                                      public void onDataChange(@NonNull DataSnapshot snapshot) {
                                          if (snapshot.exists()){
                                              reference.updateChildren(hashMap);
                                              startActivity(new Intent(GovtIDActivity.this, TermAConActivity.class)
                                                      .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("email", emails).putExtra("password", password));
                                              progressDialog.dismiss();
                                              finish();
                                          }
                                      }

                                      @Override
                                      public void onCancelled(@NonNull DatabaseError error) {

                                      }
                                  });

                              }
                          });
                      }
                  });
              }


            }
        });

    }

    private String getFileExtension(Uri mUri){

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==2 && resultCode == RESULT_OK && data != null){

            filePath = data.getData();
            imagePreview.setImageURI(filePath);

        }
    }
}