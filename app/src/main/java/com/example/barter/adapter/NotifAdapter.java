package com.example.barter.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barter.R;
import com.example.barter.database.Notif;
import com.example.barter.database.NotifHelperClass;

import java.util.ArrayList;

public class NotifAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Notif> notifArrayList;
    private Context context;
    private Activity mActivity;
    private Application application;

    public NotifAdapter(Context context, Application application, ArrayList<Notif> notifArrayList){
        this.context = context;
        this.application = application;
        this.notifArrayList = notifArrayList;
    }

    public class NotifHolder extends RecyclerView.ViewHolder{
        public TextView title, message;
        public CardView layout;

        public NotifHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            message = (TextView) itemView.findViewById(R.id.message);
            layout = (CardView) itemView.findViewById(R.id.layout);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, @SuppressLint("RecyclerView") int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notif_holder, parent, false);
        return new NotifHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        NotifHolder notifHolder = (NotifHolder) holder;
        notifHolder.title.setText(notifArrayList.get(position).getTitle().trim());
        notifHolder.message.setText(notifArrayList.get(position).getMessage().trim());
    }

    @Override
    public int getItemCount() {
        return notifArrayList.size();
    }
}
