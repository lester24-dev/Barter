package com.example.barter.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.barter.R;
import com.example.barter.adapter.UserAdapter;
import com.example.barter.darkmode.SharedPref;
import com.example.barter.database.Chat;
import com.example.barter.database.ChatList;
import com.example.barter.database.ChatUser;
import com.example.barter.database.ChatUserHelper;
import com.example.barter.database.User;
import com.example.barter.database.UserHelperClass;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Attr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    private String userID;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private DatabaseReference ChatsRef, UsersRef;
    private String ids;
    private String theLastMessage;
    private String readMessage;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPref sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.ThemeDark);
        } else {
            setTheme(R.style.ThemeLight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Chat Message");

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userID = firebaseUser.getDisplayName();
        ids = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ChatsRef = firebaseDatabase.getReference().child("Chatlist").child(ids);
        UsersRef = firebaseDatabase.getReference().child("User");

        recyclerView = findViewById(R.id.chatList);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));

        readChats();

    }

    private void readChats(){

        FirebaseRecyclerOptions<ChatUser> options =
                new FirebaseRecyclerOptions.Builder<ChatUser>()
                        .setQuery(ChatsRef, ChatUser.class)
                        .build();


        FirebaseRecyclerAdapter<ChatUser, ChatsViewHolder> adapter =
                new FirebaseRecyclerAdapter<ChatUser, ChatsViewHolder>(options) {
                    @Override{
                        final String usersIDs = getRef(position).getKey();
                        final String[] retImage = {"default_image"};


                        UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.exists())
                                {
                                    if (dataSnapshot.hasChild("filepath"))
                                    {
                                        retImage[0] = dataSnapshot.child("filepath").getValue().toString();
                                        Picasso.with(getApplicationContext()).load(retImage[0]).into(holder.profileImage);
                                    }

                                    final String retName = dataSnapshot.child("name").getValue().toString();
                                    //final String retStatus = dataSnapshot.child("status").getValue().toString();

                                    holder.userName.setText(retName);

                                    if (dataSnapshot.child("status").getValue().equals("online")){
                                        holder.img_on.setVisibility(View.VISIBLE);
                                        holder.img_off.setVisibility(View.GONE);
                                    }else{
                                        holder.img_on.setVisibility(View.GONE);
                                        holder.img_off.setVisibility(View.VISIBLE);
                                    }

                                    lastMesage(usersIDs, holder.last_message);

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view)
                                        {
                                            firebaseFirestore.collection("Chat").whereEqualTo("sender",usersIDs).whereEqualTo("status", "unseen").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                                    for (QueryDocumentSnapshot document : value){
                                                        //  ChatUserHelper chatUserHelper = document.toObject(ChatUserHelper.class);
                                                        //  if (chatUserHelper.getStatus().equals("unseen")){
                                                        document.getReference().update("status", "seen");
                                                        //  }
                                                    }
                                                }
                                            });
                                            Intent chatIntent = new Intent(getApplicationContext(), MessageActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            chatIntent.putExtra("id", usersIDs);
                                            chatIntent.putExtra("name", retName);
                                            chatIntent.putExtra("filepath", retImage[0]);
                                            startActivity(chatIntent);

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull ChatUser model)


                    @NonNull
                    @Override
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_layout, viewGroup, false);
                        return new ChatsViewHolder(view);
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }


    public static class  ChatsViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView profileImage, img_on, img_off;
        TextView userName, last_message;


        public ChatsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profilePic);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            userName = itemView.findViewById(R.id.name);
            last_message = itemView.findViewById(R.id.last_message);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        switch (menu.getItemId()) {
            case android.R.id.home:
                // addSomething();
                finish();
                break;
            default:
        }
        return true;
    }

    private void lastMesage(final String userid, final TextView last_message){
        theLastMessage = "default";
        readMessage = "unseen";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Chat").orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                for (QueryDocumentSnapshot documentSnapshot: value){
                    Chat chat = documentSnapshot.toObject(Chat.class);
                    if (chat.getReciever().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                            chat.getReciever().equals(userid) && chat.getSender().equals(firebaseUser.getUid())){
                        theLastMessage = chat.getMessage();
                    }
                }

                switch (readMessage){
                    case "unseen":
                        last_message.setTextColor(Color.GRAY);
                        break;

                    default:

                        break;
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