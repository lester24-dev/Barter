package com.example.barter.register;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.ColorSpace;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.barter.R;
import com.example.barter.database.UserHelperClass;
import com.example.barter.database.Users;
import com.example.barter.home.Home;
import com.example.barter.login.LoginActivity;
import com.example.barter.verify.VerifyEmail;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Register extends AppCompatActivity {
    TextInputLayout regName, regUsername, regEmail, regPhoneNo, regPassword, regAge, regAdd;
    Button regBtn, regToLoginBtn, btn_choose_image, btn_choose_id_image;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    private int PICK_IMAGE_REQUEST = 71;
    private Uri filePath, filePath2;
    private FirebaseStorage firebaseStore;
    private StorageReference storageReference, storageReference2;
    ImageView imagePreview, logo, image_preview_id;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firebaseFirestore;
    String users = "";
    AutoCompleteTextView reg_address;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Hooks to all xml elements in activity_sign_up.xml
        regName = findViewById(R.id.reg_name);
        regUsername = findViewById(R.id.reg_username);
        regEmail = findViewById(R.id.reg_email);
        regPhoneNo = findViewById(R.id.reg_phoneNo);
        regPassword = findViewById(R.id.reg_password);
        regBtn = findViewById(R.id.reg_btn);
        regToLoginBtn = findViewById(R.id.reg_login_btn);
        btn_choose_image = findViewById(R.id.btn_choose_image);
        imagePreview = findViewById(R.id.image_preview);
        mAuth = FirebaseAuth.getInstance();
        firebaseStore = FirebaseStorage.getInstance();
        storageReference = firebaseStore.getReferenceFromUrl("gs://striped-harbor-351908.appspot.com/userProfile");
        storageReference2 = firebaseStore.getReferenceFromUrl("gs://striped-harbor-351908.appspot.com/govtID");
        regAge = findViewById(R.id.reg_age);
        regAdd = findViewById(R.id.regAdd);
        logo = findViewById(R.id.logo);
        reg_address = findViewById(R.id.reg_address);

        //Save data in FireBase on button click

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            startActivity(new Intent(Register.this, LoginActivity.class));
        }
        firebaseFirestore = FirebaseFirestore.getInstance();

        Places.initialize(getApplicationContext(),"AIzaSyBA8fJdBXxFJpo7Hj7mSgPMq0w34vdNeHs");

        regAdd.setFocusable(false);
        reg_address.setFocusable(false);

        reg_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG,Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .setCountry("PH").build(Register.this);
                startActivityForResult(intent,100);
            }
        });


        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference("User");
                //Get all the values
                String name = regName.getEditText().getText().toString();
                String username = regUsername.getEditText().getText().toString();
                String email = regEmail.getEditText().getText().toString();
                String phoneNo = regPhoneNo.getEditText().getText().toString();
                String password = regPassword.getEditText().getText().toString();
                String address = regAdd.getEditText().getText().toString();
                String age = regAge.getEditText().getText().toString();

                progressDialog = new ProgressDialog(Register.this);
                progressDialog.setMessage("Register Loading");
                progressDialog.setProgress(10);
                progressDialog.setMax(100);
                progressDialog.show();

                if (name.isEmpty()){
                    regName.getEditText().getHint();
                    regName.setError("Must be filled");
                    return;
                }

                if (username.isEmpty()){
                    regUsername.setError("Must be filled");;
                    return;
                }

                if (email.isEmpty()){
                    regEmail.getEditText().getHint();
                    regEmail.setError("Must be filled");
                    return;
                }

                if (phoneNo.isEmpty()){
                    regPhoneNo.getEditText().getHint();
                    regPhoneNo.setError("Must be filled");
                    return;
                }

                if (password.isEmpty()){
                    regPassword.setError("Must be filled");;
                    return;
                }

                if (password.length() < 6 ){
                    regPassword.setError("Must be 6 or above characters");;
                    return;
                }

                else{
                    storageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(filePath));
                    storageReference2 = storageReference2.child(System.currentTimeMillis() + " " + getFileExtension(filePath2));

                    if (filePath != null){
                        storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                       mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                           @Override
                                           public void onComplete(@NonNull Task<SignInMethodQueryResult> task)   {
                                               if (task.getResult().getSignInMethods().size() == 0){
                                                   mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<AuthResult> task) {
                                                           String id = task.getResult().getUser().getUid();
                                                           SimpleDateFormat s = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss a");
                                                           String date = s.format(new Date());
                                                           Users helperClass = new Users(name, username, email, phoneNo, password, uri.toString(), address, age, id, "online", "", date, "disapproved");
                                                           rootNode.getReference().child("User").child(id).setValue(helperClass);
                                                           firebaseFirestore.collection("User").document(id).set(helperClass);
                                                           progressDialog.hide();
                                                           Toast.makeText(getApplicationContext(), "Data Saved ", Toast.LENGTH_LONG).show();

                                                           mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                               @Override
                                                               public void onComplete(@NonNull Task<AuthResult> task) {
                                                                   startActivity(new Intent(Register.this, VerifyEmail.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                                           .putExtra("email", email).putExtra("id", id).putExtra("password", password));
                                                                   finish();
                                                               }
                                                           });

                                                       }
                                                   });
                                               }else {
                                                   progressDialog.hide();
                                                   Toast.makeText(getApplicationContext(),"Email is already existed",Toast.LENGTH_SHORT);


                                               }
                                           }
                                       });
                                    }
                                });
                            }
                        });
                    }else {
                        Toast.makeText(getApplicationContext(), "Please Select Image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });//Register Button method end

        btn_choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent , 2);
            }
        });

        regToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, LoginActivity.class));
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
            filePath2 = data.getData();

        }else if (requestCode ==100 && resultCode == RESULT_OK){
                Place place = Autocomplete.getPlaceFromIntent(data);
                reg_address.setText(place.getAddress());

        }else if (resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            Log.i("TAG", status.getStatusMessage());
        }
    }

}