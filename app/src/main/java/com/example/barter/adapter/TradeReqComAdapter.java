package com.example.barter.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barter.R;
import com.example.barter.database.TradeReq;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TradeReqComAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<TradeReq> tradeList;
    private Context context;
    private Activity mActivity;
    DatabaseReference reference;
    FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;

    public TradeReqComAdapter(Context context, Activity mActivity, ArrayList<TradeReq> tradeList){
        this.context = context;
        this.mActivity = mActivity;
        this.tradeList = tradeList;
    }

    public class TradeViewHolder extends RecyclerView.ViewHolder{
        public ImageView productePic, productePic2;
        public TextView name, name2, product_price, product_price2, status;
        public CardView layout;
        public TradeViewHolder(View itemView, int viewType){
            super(itemView);
            productePic = (ImageView) itemView.findViewById(R.id.productePic);
            productePic2 = (ImageView) itemView.findViewById(R.id.productePic2);
            name = (TextView) itemView.findViewById(R.id.name);
            name2 = (TextView) itemView.findViewById(R.id.name2);
            product_price = (TextView) itemView.findViewById(R.id.product_price);
            product_price2 = (TextView) itemView.findViewById(R.id.product_price2);
            status = itemView.findViewById(R.id.status);
            layout = itemView.findViewById(R.id.tradelist);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trade_request_complete, parent, false);
        return new TradeViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TradeViewHolder tradeViewHolder = (TradeViewHolder) holder;

        Picasso.with(tradeViewHolder.productePic.getContext()).load(tradeList.get(position).getProduct_image_seller()).into(tradeViewHolder.productePic);
        Picasso.with(tradeViewHolder.productePic2.getContext()).load(tradeList.get(position).getProduct_image_user()).into(tradeViewHolder.productePic2);
        tradeViewHolder.name.setText(tradeList.get(position).getProduct_name_seller());
        tradeViewHolder.name2.setText(tradeList.get(position).getProduct_name_user());
        tradeViewHolder.product_price.setText("₱"+tradeList.get(position).getProduct_price_seller());
        tradeViewHolder.product_price2.setText("₱"+tradeList.get(position).getProduct_price_user());
        tradeViewHolder.status.setText(tradeList.get(position).getStatus());
    }

    @Override
    public int getItemCount() {
        return tradeList.size();
    }
}
