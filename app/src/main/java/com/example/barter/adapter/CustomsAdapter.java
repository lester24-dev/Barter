package com.example.barter.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.barter.R;
import com.example.barter.database.Notif;
import com.example.barter.notification.NotifInfoActivity;

import java.util.ArrayList;

public class CustomsAdapter extends ArrayAdapter<Notif> {

    public CustomsAdapter(@NonNull Context context, ArrayList<Notif> notifArrayList) {
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
