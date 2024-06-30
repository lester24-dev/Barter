package com.example.barter.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barter.R;
import com.example.barter.database.Notif;
import com.example.barter.database.NotifHelperClass;
import com.example.barter.database.User;
import com.example.barter.database.UserHelperClass;
import com.example.barter.notification.NotifInfoActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<Notif> {

    public CustomAdapter(@NonNull Context context, ArrayList<Notif> notifArrayList) {
        super(context,0,notifArrayList);
    }

    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {

        View listView = convertView;
        if (listView == null){
            listView = LayoutInflater.from(getContext()).inflate(R.layout.notif_holder,parent, false);
        }

        Notif sd = getItem(position);

        TextView lvTvSectionCode = (TextView) listView.findViewById(R.id.title);
        TextView lvTvSectionSeats = (TextView) listView.findViewById(R.id.message);
        CardView layout = (CardView) listView.findViewById(R.id.layout);

        lvTvSectionSeats.setText(sd.getMessage());
        lvTvSectionCode.setText(sd.getTitle());

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notif = new Intent(getContext(), NotifInfoActivity.class);

            }
        });

        return listView;
    }

}
