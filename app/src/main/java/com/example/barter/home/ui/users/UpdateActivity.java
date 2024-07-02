package com.example.barter.home.ui.users;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.barter.R;
import com.example.barter.darkmode.SharedPref;
import com.example.barter.database.ProductHelperClass;
import com.example.barter.database.UpdateProductWIthI;
import com.example.barter.database.UpdateProductWIthoutI;
import com.example.barter.database.UserHelperClass;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UpdateActivity extends AppCompatActivity {
    private TextInputLayout product_name, product_des, product_category, product_size, product_color, product_year, product_brand, product_price, regAdd;
    private Button btn_choose_image, update_product;
    private AutoCompleteTextView category, color;
    private ArrayAdapter<String> ad;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    private int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private FirebaseStorage firebaseStore;
    private StorageReference storageReference;
    ImageView imagePreview;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private String userID;
    private FirebaseFirestore firebaseFirestore;
    AutoCompleteTextView reg_address;
    private String address, hash;
    String uid, saveName, saveDate,savePrice,userIDs, imageUri, Messagename, message;
    Uri saveImage;

    String[] categories = {
            "Men Clothes", "Women Clothes", "Men Accessories And Shoes", "Women Accessories And Shoes", "Sport Accessories", "Sport Clothes And Shoes", "Gadget Accessories",
            "Appliances", "Unisex Clothes", "Unisex Accessories And Shoes", "Rentals", "Furniture", "Baby", "Auto Parts",
            "Books, Movies & Music", "Healthy and Beauty", "Patio and Garden", "Pet Supplies", "Toy and Games", "Vehicles",
            "Music and Intruments", "Miscellaneous", "Kids Fashion and Accessories", "Foods"
    };

    String[] colors = {
            "Red", "Yellow", "Blue", "Orange", "Brown", "Green", "Gray", "Yellow Green",
            "Black", "White", "Sky Blue", "Maroon", "Aqua Marine", "Mustartd", "Dirty White",
            "Pink", "Violet"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPref sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.ThemeDark);
        } else {
            setTheme(R.style.ThemeLight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        firebaseStore = FirebaseStorage.getInstance();
        storageReference = firebaseStore.getReferenceFromUrl("gs://striped-harbor-351908.appspot.com/productImage");
        category = findViewById(R.id.productCats);
        category.setAdapter(new ArrayAdapter<String>(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categories));
        color = findViewById(R.id.productColors);
        color.setAdapter(new ArrayAdapter<String>(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, colors));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = firebaseUser.getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        rootNode = FirebaseDatabase.getInstance();
        product_name = findViewById(R.id.product_name);
        product_category = findViewById(R.id.productCat);
        product_des = findViewById(R.id.product_des);
        product_color = findViewById(R.id.productColor);
        product_size = findViewById(R.id.product_size);
        imagePreview = findViewById(R.id.image_preview);
        update_product = findViewById(R.id.update_product);
        product_year = findViewById(R.id.product_year);
        btn_choose_image = findViewById(R.id.btn_choose_image);
        product_brand = findViewById(R.id.product_brand);
        product_price = findViewById(R.id.product_price);

        Query query = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperClass userHelperClass = snapshot.getValue(UserHelperClass.class);

                if (userHelperClass != null){
                    address = userHelperClass.getAddress().trim();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        saveName = getIntent().getExtras().getString("product_name");
        savePrice = getIntent().getExtras().getString("product_price");
        saveImage = Uri.parse(getIntent().getExtras().getString("product_image"));
        String product_colors = getIntent().getExtras().getString("product_color");
        String product_dess = getIntent().getExtras().getString("product_des");
        String product_prices = getIntent().getExtras().getString("product_price");
        String product_sizes = getIntent().getExtras().getString("product_size");
        String product_brands = getIntent().getExtras().getString("product_brand");
        String product_years = getIntent().getExtras().getString("product_year");
        String product_sellers = getIntent().getExtras().getString("product_seller");
        String product_categorys = getIntent().getExtras().getString("product_category");

        product_name.getEditText().setText(saveName);
        product_category.getEditText().setText(product_categorys);
        product_color.getEditText().setText(product_colors);
        product_des.getEditText().setText(product_dess);
        product_price.getEditText().setText(product_prices);
        product_size.getEditText().setText(product_sizes);
        product_brand.getEditText().setText(product_brands);
        product_year.getEditText().setText(product_years);

        update_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProduct();
            }
        });


        btn_choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent.createChooser(galleryIntent, "Select Image") , 2);
                Toast.makeText(getApplicationContext(), "Image Button!!", Toast.LENGTH_LONG).show();
            }
        });


    }

    public void updateProduct(){

        Geocoder coder = new Geocoder(getApplication());
        List<Address> addressList;

        try {
            addressList = coder.getFromLocationName(address, 5);
            Address location = addressList.get(0);
            location.getLatitude();
            location.getLongitude();

            hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(location.getLatitude(), location.getLongitude()));

        } catch (IOException e) {
            e.printStackTrace();
        }


        if (filePath != null){

            NumberFormat formatter = new DecimalFormat("#,###.##");
            double prices = Double.parseDouble(product_price.getEditText().getText().toString());
            String name = product_name.getEditText().getText().toString();
            String category = product_category.getEditText().getText().toString();
            String des = product_des.getEditText().getText().toString();
            String color = product_color.getEditText().getText().toString();
            String size = product_size.getEditText().getText().toString();
            String year = product_year.getEditText().getText().toString();
            String brand = product_brand.getEditText().getText().toString();
            String price = formatter.format(prices);

            progressDialog = new ProgressDialog(UpdateActivity.this);
            progressDialog.setMessage("Update Product Loading");
            progressDialog.setProgress(10);
            progressDialog.setMax(100);
            progressDialog.show();

            storageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(filePath));
            storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            SimpleDateFormat s = new SimpleDateFormat("MM-dd-yyyy");
                            String date = s.format(new Date());
                            UpdateProductWIthI productHelperClass = new UpdateProductWIthI(name,category,color,des,uri.toString(),brand,size,year,price);
                            //rootNode.getReference().child("Product").child(saveName).setValue(productHelperClass);
                            firebaseFirestore.collection("Product").whereEqualTo("product_name", saveName).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot: value){
                                        queryDocumentSnapshot.getReference().update("product_name",name);
                                        queryDocumentSnapshot.getReference().update("product_category",category);
                                        queryDocumentSnapshot.getReference().update("product_color",color);
                                        queryDocumentSnapshot.getReference().update("product_des",des);
                                        queryDocumentSnapshot.getReference().update("product_image",uri.toString());
                                        queryDocumentSnapshot.getReference().update("product_brand",brand);
                                        queryDocumentSnapshot.getReference().update("product_size",size);
                                        queryDocumentSnapshot.getReference().update("product_year",year);
                                        queryDocumentSnapshot.getReference().update("product_price",price);
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Update Your Product is Success!!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, usersFragment).commit();
                        }
                    });
                }
            });
        }else{

            NumberFormat formatter = new DecimalFormat("#,###.##");
            double prices = Double.parseDouble(product_price.getEditText().getText().toString());
            String name = product_name.getEditText().getText().toString();
            String category = product_category.getEditText().getText().toString();
            String des = product_des.getEditText().getText().toString();
            String color = product_color.getEditText().getText().toString();
            String size = product_size.getEditText().getText().toString();
            String year = product_year.getEditText().getText().toString();
            String brand = product_brand.getEditText().getText().toString();
            String price = formatter.format(prices);

            progressDialog = new ProgressDialog(UpdateActivity.this);
            progressDialog.setMessage("Update Product Loading");
            progressDialog.setProgress(10);
            progressDialog.setMax(100);
            progressDialog.show();
            DatabaseReference mDatabaseRef = rootNode.getReference();
            UpdateProductWIthoutI productHelperClass = new UpdateProductWIthoutI(name,category,color,des,brand,size,year,price);
            //mDatabaseRef.child("Product").child(saveName).setValue(productHelperClass);

            firebaseFirestore.collection("Product").whereEqualTo("product_name", saveName).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for (QueryDocumentSnapshot queryDocumentSnapshot: value){
                        queryDocumentSnapshot.getReference().update("product_name",name);
                        queryDocumentSnapshot.getReference().update("product_category",category);
                        queryDocumentSnapshot.getReference().update("product_color",color);
                        queryDocumentSnapshot.getReference().update("product_des",des);
                        queryDocumentSnapshot.getReference().update("product_brand",brand);
                        queryDocumentSnapshot.getReference().update("product_size",size);
                        queryDocumentSnapshot.getReference().update("product_year",year);
                        queryDocumentSnapshot.getReference().update("product_price",price);
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Update Your Product is Success!!", Toast.LENGTH_LONG).show();
                    }
                }
            });

            mDatabaseRef.child("Product").equalTo("product_name", saveName).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot queryDocumentSnapshot: snapshot.getChildren()){
                        queryDocumentSnapshot.getRef().setValue("product_name",name);
                        queryDocumentSnapshot.getRef().setValue("product_category",category);
                        queryDocumentSnapshot.getRef().setValue("product_color",color);
                        queryDocumentSnapshot.getRef().setValue("product_des",des);
                        queryDocumentSnapshot.getRef().setValue("product_brand",brand);
                        queryDocumentSnapshot.getRef().setValue("product_size",size);
                        queryDocumentSnapshot.getRef().setValue("product_year",year);
                        queryDocumentSnapshot.getRef().setValue("product_price",price);
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Update Your Product is Success!!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    private String getFileExtension(Uri mUri){

        ContentResolver cr = getApplicationContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==2 && resultCode == RESULT_OK && data != null && data.getData()!=null){

            filePath = data.getData();
            imagePreview.setImageURI(filePath);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        switch (menu.getItemId()) {
            case android.R.id.home:
                // addSomething();
                finish();
                break;
            default:
        }
        return true;
    }
}