package com.example.barter.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barter.R;
import com.example.barter.database.Product;
import com.example.barter.productDetails.ProductDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SuggestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Product> userList;
    private Context context;
    private Activity mActivity;

    public SuggestionAdapter(Context context, Activity mActivity, ArrayList<Product> userList){
        this.context = context;
        this.mActivity = mActivity;
        this.userList = userList;
    }

    public class SuggestionViewHolder extends RecyclerView.ViewHolder{
        public ImageView user_image;
        public TextView user_name, user_price;
        public CardView layout;
        public SuggestionViewHolder(View itemView, int viewType){
            super(itemView);
            user_image = (ImageView) itemView.findViewById(R.id.user_image);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            user_price = (TextView) itemView.findViewById(R.id.user_price);
            layout = itemView.findViewById(R.id.productcard);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_layout, parent, false);
        return new SuggestionViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            SuggestionViewHolder viewHolder = (SuggestionViewHolder) holder;

        viewHolder.user_name.setText(userList.get(position).getProduct_name());
        viewHolder.user_price.setText("₱"+userList.get(position).getProduct_price().trim());
        Picasso.with(viewHolder.user_image.getContext()).load(userList.get(position).getProduct_image()).into(viewHolder.user_image);

        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetails.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("product_name",userList.get(position).getProduct_name());
                intent.putExtra("product_des",userList.get(position).getProduct_des());
                intent.putExtra("product_color", userList.get(position).getProduct_color());
                intent.putExtra("product_price", userList.get(position).getProduct_price());
                intent.putExtra("product_size", userList.get(position).getProduct_size());
                intent.putExtra("product_brand", userList.get(position).getProduct_brand());
                intent.putExtra("product_year", userList.get(position).getProduct_year());
                intent.putExtra("product_image",userList.get(position).getProduct_image());
                intent.putExtra("product_seller",userList.get(position).getProduct_seller());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}