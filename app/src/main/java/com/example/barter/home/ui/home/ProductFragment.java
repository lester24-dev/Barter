package com.example.barter.home.ui.home;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.barter.R;
import com.example.barter.adapter.ProductAdapter;
import com.example.barter.darkmode.SharedPref;
import com.example.barter.database.Match;
import com.example.barter.database.Product;
import com.example.barter.database.UserHelperClass;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryBounds;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import uk.co.mgbramwell.geofire.android.model.Distance;
import uk.co.mgbramwell.geofire.android.model.DistanceUnit;
import uk.co.mgbramwell.geofire.android.model.QueryLocation;


public class ProductFragment extends Fragment {

    View root;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private DatabaseReference matchDatabase, productDatabase;
    private FirebaseStorage firebaseStore;
    private FirebaseDatabase firebaseDatabase;
    private StorageReference storageReference;
    RecyclerView recyclerView,latestView;
    private Context mContext;
    private Activity mActivity;
    private ArrayList<Product> productsList;
    private ProductAdapter productAdapter;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firebaseFirestore;
    LatLng p1 = null;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    String address;

    public ProductFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPref sharedPref = new SharedPref(getActivity());
        if (sharedPref.loadNightModeState() == true) {
            getActivity().setTheme(R.style.ThemeDark);
        } else {
            getActivity().setTheme(R.style.ThemeLight);
        }
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_product, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStore = FirebaseStorage.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference  = FirebaseDatabase.getInstance().getReference("User");
        matchDatabase = FirebaseDatabase.getInstance().getReference("Match");
        productDatabase = FirebaseDatabase.getInstance().getReference().child("Product");
        String userID = firebaseUser.getUid();

        firebaseStore = FirebaseStorage.getInstance();
        storageReference = firebaseStore.getReferenceFromUrl("gs://striped-harbor-351908.appspot.com/userProfile");

        mActivity = getActivity();
        mContext = getContext();
        FirebaseApp.initializeApp(getActivity());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 2, GridLayoutManager.VERTICAL, false));

        productsList = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Product Loading");
        progressDialog.setProgress(10);
        progressDialog.setMax(100);
        progressDialog.show();

        Query query = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperClass userHelperClass = snapshot.getValue(UserHelperClass.class);

                if (userHelperClass != null){
                    address = userHelperClass.getAddress();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        if (ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                        currentLocation = location;
                        Toast.makeText(getContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

                        final GeoLocation center = new GeoLocation(currentLocation.getLatitude(), currentLocation.getLongitude());
                        final double radiusInM = 25 * 1000;
                        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM);

                    final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
                    for (GeoQueryBounds b : bounds) {
                        com.google.firebase.firestore.Query q = firebaseFirestore.collection("Product")
                                .orderBy("hash")
                                .startAt(b.startHash)
                                .endAt(b.endHash);
                        tasks.add(q.get());
                    }

// Collect all the query results together into a single list
                    Tasks.whenAllComplete(tasks)
                            .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                                @Override
                                public void onComplete(@NonNull Task<List<Task<?>>> t) {
                                    List<DocumentSnapshot> matchingDocs = new ArrayList<>();

                                    for (Task<QuerySnapshot> task : tasks) {
                                        QuerySnapshot snap = task.getResult();
                                        for (DocumentSnapshot doc : snap.getDocuments()) {
                                            Product product = doc.toObject(Product.class);
                                            productsList.add(product);
                                            productAdapter = new ProductAdapter(mContext,mActivity, (ArrayList<Product>) productsList);
                                            recyclerView.setAdapter(productAdapter);
                                            productAdapter.notifyDataSetChanged();
                                            progressDialog.dismiss();
                                        }
                                    }

                                    // matchingDocs contains the results
                                    // ...
                                }
                            });

                }
            }
        });

        search();

        return root;
    }



    private void search(){
//        SearchView searchView = (SearchView) root.findViewById(R.id.search_view);
//        // below line is to call set on query text listener method.
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                // inside on query text change method we are
//                // calling a method to filter our recycler view.
//                filter(newText);
//                return false;
//            }
//        });
    }

    private void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<Product> filteredlist = new ArrayList<Product>();

        // running a for loop to compare elements.
        for (Product item : productsList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getProduct_name().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(getActivity(), "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            productAdapter.filterList(filteredlist);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(
                            getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                        return;
                    }
                }
                break;
        }
    }

}