package com.example.barter.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barter.R;
import com.example.barter.chat.MessageActivity;
import com.example.barter.database.ChatUserHelper;
import com.example.barter.database.User;
import com.example.barter.database.UserHelperClass;
import com.example.barter.database.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {
    private List<UserHelperClass> userHelperClasses;
    private Context context;
    private boolean ischat;
    private String theLastMessage;

    public UserAdapter(List<UserHelperClass> userHelperClasses, Context context,boolean ischat) {
        this.userHelperClasses = userHelperClasses;
        this.context = context;
        this.ischat = ischat;
    }
    @NonNull
    @Override
    public UserAdapter.UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_layout, parent, false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserHolder holder, @SuppressLint("RecyclerView") int position) {
        UserHolder viewHolder = (UserHolder) holder;

        viewHolder.name.setText(userHelperClasses.get(position).getName());
        //viewHolder.unseenMessage.setText(userHelperClasses.get(position).getMessage());
        Picasso.with(viewHolder.profileImg.getContext()).load(userHelperClasses.get(position).getFilepath()).into(viewHolder.profileImg);

        if (ischat){
            lastMesage(userHelperClasses.get(position).getId(), viewHolder.last_message);
        }else{
            viewHolder.last_message.setVisibility(View.GONE);
        }

        if (ischat){
            if (userHelperClasses.get(position).getStatus().equals("online")){
                viewHolder.img_on.setVisibility(View.VISIBLE);
                viewHolder.img_off.setVisibility(View.GONE);
            }else{
                viewHolder.img_on.setVisibility(View.GONE);
                viewHolder.img_off.setVisibility(View.VISIBLE);
            }
        }else {
            viewHolder.img_on.setVisibility(View.GONE);
            viewHolder.img_off.setVisibility(View.GONE);
        }

        viewHolder.chatMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("name",userHelperClasses.get(position).getName());
                intent.putExtra("id", userHelperClasses.get(position).getId());
                intent.putExtra("status", userHelperClasses.get(position).getStatus());
                intent.putExtra("filepath",userHelperClasses.get(position).getFilepath());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
     return  userHelperClasses.size();
    }

    public class UserHolder extends RecyclerView.ViewHolder {
        private CircleImageView profileImg, img_on, img_off;
        private TextView name, last_message;
        private CardView chatMessage;
        public UserHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.profilePic);
            name = itemView.findViewById(R.id.name);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            chatMessage = itemView.findViewById(R.id.chatMessage);
            last_message = itemView.findViewById(R.id.last_message);
        }
    }

    private void lastMesage(final String userid, final TextView last_message){
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Chat").orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                for (QueryDocumentSnapshot documentSnapshot: value){
                    ChatUserHelper chat = documentSnapshot.toObject(ChatUserHelper.class);
                    if (chat.getReciever().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                            chat.getReciever().equals(userid) && chat.getSender().equals(firebaseUser.getUid())){
                        theLastMessage = chat.getMessage();
                    }
                }

                switch (theLastMessage){
                    case "default":
                        last_message.setText("No Message");
                        break;

                    default:
                        last_message.setText(theLastMessage);
                        break;
                }
                theLastMessage = "default";
            }

        });

    }
}
