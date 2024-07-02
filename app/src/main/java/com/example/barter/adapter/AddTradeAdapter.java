package com.example.barter.adapter;

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
import com.example.barter.database.AddTrade;
import com.example.barter.database.Product;
import com.example.barter.productDetails.ProductDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AddTradeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<AddTrade> userList;
    private Context context;
    private Activity mActivity;

    public AddTradeAdapter(Context context, Activity mActivity, ArrayList<AddTrade> userList){
        this.context = context;
        this.mActivity = mActivity;
        this.userList = userList;
    }

    public class CartViewHolder extends RecyclerView.ViewHolder{
        public ImageView user_image;
        public TextView user_name, user_price;
        public CardView layout;
        public CartViewHolder(View itemView, int viewType){
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_list_item, parent, false);
        return new CartViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
       CartViewHolder viewHolder = (CartViewHolder) holder;

        viewHolder.user_name.setText(userList.get(position).getProduct_name());
        viewHolder.user_price.setText("₱"+userList.get(position).getProduct_price());
        Picasso.with(viewHolder.user_image.getContext()).load(userList.get(position).getProduct_image()).into(viewHolder.user_image);

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
