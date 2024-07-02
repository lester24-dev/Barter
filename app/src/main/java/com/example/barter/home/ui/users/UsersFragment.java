package com.example.barter.home.ui.users;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barter.R;
import com.example.barter.adapter.CustomAdapter;
import com.example.barter.adapter.ProductAdapter;
import com.example.barter.adapter.UpdateAdapter;
import com.example.barter.darkmode.SharedPref;
import com.example.barter.database.Product;
import com.example.barter.database.UserHelperClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    private View root;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    RecyclerView recyclerView;
    private Context mContext;
    private Activity mActivity;
    private ArrayList<Product> productsList;
    private UpdateAdapter productAdapter;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firebaseFirestore;
    String userID;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SharedPref sharedPref = new SharedPref(getActivity());
        if (sharedPref.loadNightModeState() == true) {
            getActivity().setTheme(R.style.ThemeDark);
        } else {
            getActivity().setTheme(R.style.ThemeLight);
        }
        root = inflater.inflate(R.layout.fragment_users, container,false);

        firebaseDatabase = FirebaseDatabase.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference  = FirebaseDatabase.getInstance().getReference("User");
        userID = firebaseUser.getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mActivity = getActivity();
        mContext = getContext();
        FirebaseApp.initializeApp(getActivity());

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Add Product Loading");
        progressDialog.setProgress(10);
        progressDialog.setMax(100);
        progressDialog.show();


        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 2, GridLayoutManager.VERTICAL, false));
        productsList = new ArrayList<>();

        firebaseFirestore.collection("Product")
                .whereEqualTo("product_seller", userID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (QueryDocumentSnapshot document : value) {
                            Product product = document.toObject(Product.class);
                            productsList.add(product);
                        }
                        productAdapter = new UpdateAdapter(mContext,mActivity, (ArrayList<Product>) productsList);
                        recyclerView.setAdapter(productAdapter);
                        productAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }
                });

        search();

        return root;
    }

    private void search(){
        SearchView searchView = (SearchView) root.findViewById(R.id.search_view);
        // below line is to call set on query text listener method.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                filter(newText);
                return false;
            }
        });
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
    public void onDestroyView() {
        super.onDestroyView();

    }
}