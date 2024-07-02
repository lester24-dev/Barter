package com.example.barter.home.ui.gallery;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.barter.R;
import com.example.barter.darkmode.SharedPref;
import com.example.barter.database.ProductHelperClass;
import com.example.barter.database.UserHelperClass;
import com.example.barter.home.ui.users.UsersFragment;
import com.example.barter.login.LoginActivity;
import com.example.barter.register.Register;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GalleryFragment extends Fragment {

    private View root;
    private TextInputLayout product_name, product_des, product_category, product_size, product_color, product_year, product_brand, product_price, regAdd;
    private Button product_image, product_add;
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
    UsersFragment usersFragment;
    private FirebaseFirestore firebaseFirestore;
    AutoCompleteTextView reg_address;
    private String address, hash;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        SharedPref sharedPref = new SharedPref(getActivity());
        if (sharedPref.loadNightModeState() == true) {
            getActivity().setTheme(R.style.ThemeDark);
        } else {
            getActivity().setTheme(R.style.ThemeLight);
        }
        root = inflater.inflate(R.layout.fragment_gallery, container,false);
        firebaseStore = FirebaseStorage.getInstance();
        storageReference = firebaseStore.getReferenceFromUrl("gs://striped-harbor-351908.appspot.com/productImage");
        category = root.findViewById(R.id.productCats);
        category.setAdapter(new ArrayAdapter<String>(root.getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categories));
        color = root.findViewById(R.id.productColors);
        color.setAdapter(new ArrayAdapter<String>(root.getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, colors));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = firebaseUser.getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        rootNode = FirebaseDatabase.getInstance();
        product_name = root.findViewById(R.id.product_name);
        product_category = root.findViewById(R.id.productCat);
        product_des = root.findViewById(R.id.product_des);
        product_color = root.findViewById(R.id.productColor);
        product_size = root.findViewById(R.id.product_size);
        imagePreview = root.findViewById(R.id.image_preview);
        product_add = root.findViewById(R.id.add_product);
        product_year = root.findViewById(R.id.product_year);
        product_image = root.findViewById(R.id.btn_choose_image);
        product_brand = root.findViewById(R.id.product_brand);
        product_price = root.findViewById(R.id.product_price);


        usersFragment = new UsersFragment();

        addProduct();

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



        return root;
    }

    public void addProduct(){

        product_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Geocoder coder = new Geocoder(getActivity());
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

                progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Add Product Loading");
                progressDialog.setProgress(10);
                progressDialog.setMax(100);
                progressDialog.show();


                if (name.isEmpty()){
                    product_name.getEditText().getHint();
                    product_name.setError("Must be filled");
                    progressDialog.dismiss();
                    return;
                }

                if (category.isEmpty()){
                    product_category.getEditText().getHint();
                    product_category.setError("Must be filled");
                    progressDialog.dismiss();
                    return;
                }

                if (des.isEmpty()){
                    product_des.getEditText().getHint();
                    product_des.setError("Must be filled");
                    progressDialog.dismiss();
                    return;
                }

                if (color.isEmpty()){
                    product_color.getEditText().getHint();
                    product_color.setError("Must be filled");
                    progressDialog.dismiss();
                    return;
                }

                if (size.isEmpty()){
                    product_size.getEditText().getHint();
                    product_size.setError("Must be filled");
                    progressDialog.dismiss();
                    return;
                }

                if (year.isEmpty()){
                    product_year.getEditText().getHint();
                    product_year.setError("Must be filled");
                    progressDialog.dismiss();
                    return;
                }

                if (brand.isEmpty()){
                    product_brand.getEditText().getHint();
                    product_brand.setError("Must be filled");
                    progressDialog.dismiss();
                    return;
                }

                if (filePath != null){
                    storageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(filePath));
                    storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    SimpleDateFormat s = new SimpleDateFormat("MM-dd-yyyy");
                                    String date = s.format(new Date());
                                    ProductHelperClass productHelperClass = new ProductHelperClass(name,category,color,des,uri.toString(),brand,size,year,price,userID,date,address,hash);
                                    rootNode.getReference().child("Product").push().setValue(productHelperClass);
                                    firebaseFirestore.collection("Product").add(productHelperClass);
                                    progressDialog.hide();
                                    Toast.makeText(getActivity(), "Adding Your Product is Success!!", Toast.LENGTH_LONG).show();
                                    product_name.getEditText().setText("");
                                    product_category.getEditText().setText("");
                                    product_price.getEditText().setText("");
                                    product_color.getEditText().setText("");
                                    product_size.getEditText().setText("");
                                    product_des.getEditText().setText("");
                                    product_brand.getEditText().setText("");
                                    product_year.getEditText().setText("");
                                    imagePreview.setImageResource(0);
                                    //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, usersFragment).commit();
                                }
                            });
                        }
                    });
                }
            }
        });

        product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent , 2);
            }
        });


    }

    private String getFileExtension(Uri mUri){

        ContentResolver cr = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==2 && resultCode == RESULT_OK && data != null){

            filePath = data.getData();
            imagePreview.setImageURI(filePath);

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}