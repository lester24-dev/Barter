package com.example.barter.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barter.R;
import com.example.barter.database.Chat;
import com.example.barter.database.ChatUserHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter  extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context context;
    private List<ChatUserHelper> chatUserHelperList;
    FirebaseUser firebaseUser;
    String imgUrl;

    public MessageAdapter(Context context, List<ChatUserHelper> chatUserHelperList, String imgUrl) {
        this.chatUserHelperList = chatUserHelperList;
        this.context = context;
        this.imgUrl = imgUrl;
    }

    @NonNull
    @Override
    public MessageAdapter.MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new MessageHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new MessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageHolder holder, int position) {
        MessageHolder viewHolder = (MessageHolder) holder;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (chatUserHelperList.get(position).type.equals("text")){
            viewHolder.image_message.setVisibility(View.INVISIBLE);
            viewHolder.show_message.setVisibility(View.VISIBLE);
            viewHolder.show_message.setText(chatUserHelperList.get(position).getMessage());
        }
        else if(chatUserHelperList.get(position).type.equals("image")){
            viewHolder.image_message.setVisibility(View.VISIBLE);
            viewHolder.show_message.setVisibility(View.INVISIBLE);
            Picasso.with(holder.itemView.getContext()).load(chatUserHelperList.get(position).getMessage()).into(viewHolder.image_message);
        }else{
            viewHolder.image_message.setVisibility(View.INVISIBLE);
            viewHolder.show_message.setVisibility(View.INVISIBLE);
        }

        if (viewHolder.profile_image == null){

        }else{
            Picasso.with(holder.itemView.getContext()).load(imgUrl).into(viewHolder.profile_image);
        }

    }

    @Override
    public int getItemCount() {
        return chatUserHelperList.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder {
        public TextView show_message;
        public CircleImageView profile_image;
        public ImageView image_message;

        public MessageHolder(View itemView){
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            image_message = itemView.findViewById(R.id.image_message);
        }
    }

    @Override
    public int getItemViewType(int position){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String id = firebaseUser.getUid();
        if (chatUserHelperList.get(position).getSender().equals(id)){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }
}
