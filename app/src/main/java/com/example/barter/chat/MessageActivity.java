package com.example.barter.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barter.R;
import com.example.barter.adapter.MessageAdapter;
import com.example.barter.darkmode.SharedPref;
import com.example.barter.database.ChatUserHelper;
import com.example.barter.database.UserHelperClass;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
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
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
TextView profileName;
CircleImageView profileImage, img_on, img_off ;
RecyclerView chatList;
MessageAdapter messageAdapter;
List<ChatUserHelper> chatUserHelperList;
ImageView sendBtn,fileBtn;
private FirebaseFirestore firebaseFirestore;
private FirebaseDatabase firebaseDatabase;
private FirebaseUser firebaseUser;
private EditText message;
String reciever_name;
String reciever_img;
String  onoff;
String toName;
private DatabaseReference databaseReference, chatDatabase, chatRef;
ValueEventListener seenListener;
String cname, cfilepath;
private String checker="", myUrl="";
private StorageTask uploadTask;
private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPref sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.ThemeDark);
        } else {
            setTheme(R.style.ThemeLight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        profileName = (TextView) findViewById(R.id.profileName);
        profileImage = (CircleImageView) findViewById(R.id.profilePics);
        img_on = (CircleImageView) findViewById(R.id.img_on);
        img_off = (CircleImageView) findViewById(R.id.img_off);
        chatList = (RecyclerView) findViewById(R.id.chatList);
        sendBtn = (ImageView) findViewById(R.id.sendBtn);
        message = (EditText) findViewById(R.id.message);
        fileBtn = (ImageView) findViewById(R.id.fileBtn);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userID = firebaseUser.getUid();
        databaseReference = firebaseDatabase.getReference("User");
        chatDatabase = firebaseDatabase.getReference("Chat");

        profileName.setText(getIntent().getExtras().getString("name"));
        Picasso.with(getApplicationContext()).load(Uri.parse(getIntent().getExtras().getString("filepath"))).into(profileImage);

        toName = getIntent().getExtras().getString("id");
        reciever_name = getIntent().getExtras().getString("name");
        reciever_img = getIntent().getExtras().getString("filepath");

//      onoff = getIntent().getExtras().getString("status");
//
//        if (onoff.equals("online")){
//            img_on.setVisibility(View.VISIBLE);
//            img_off.setVisibility(View.GONE);
//        }else if(onoff.equals("offline")) {
//            img_on.setVisibility(View.GONE);
//            img_off.setVisibility(View.VISIBLE);
//        }


        chatList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        chatList.setLayoutManager(linearLayoutManager);
        chatUserHelperList = new ArrayList<>();


        firebaseDatabase.getReference("User").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperClass userHelperClass = snapshot.getValue(UserHelperClass.class);

                cname = userHelperClass.getName().trim();
                cfilepath = userHelperClass.getFilepath().trim();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messages = message.getText().toString();
                SimpleDateFormat s = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss a");
                String date = s.format(new Date());

                if (!messages.equals("")){
                    ChatUserHelper chatUserHelper = new ChatUserHelper(toName,userID,messages,date,reciever_name,reciever_img,"unseen","text");
                    firebaseDatabase.getReference().child("Chat").push().setValue(chatUserHelper);
                    firebaseFirestore.collection("Chat").document().set(chatUserHelper);
                    final  DatabaseReference chatList = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid()).child(toName);
                    chatList.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()){
                                chatList.child("id").setValue(toName);
                                chatList.child("name").setValue(reciever_name);
                                chatList.child("filepath").setValue(reciever_img);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    final  DatabaseReference chatList2 = FirebaseDatabase.getInstance().getReference("Chatlist").child(toName).child(firebaseUser.getUid());
                    chatList2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()){
                                chatList2.child("id").setValue(firebaseUser.getUid());
                                chatList2.child("name").setValue(cname);
                                chatList2.child("filepath").setValue(cfilepath);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }else{
                    Toast.makeText(getApplicationContext(),
                                    "You can't send empty message !!",
                                    Toast.LENGTH_LONG)
                            .show();
                }
                message.setText("");
            }
        });

        final String ids = getIntent().getExtras().getString("id");
        firebaseDatabase.getReference("User").child(ids).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperClass userHelperClass = snapshot.getValue(UserHelperClass.class);
                readMessage(userID,ids,userHelperClass.getFilepath());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        String query = firebaseDatabase.getReference().child("Chat").getKey();

        fileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent.createChooser(galleryIntent,"Select Image"),438);
            }
        });


    }

    private void readMessage(final String userID, final String ids, final String img){
        firebaseDatabase.getReference("Chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatUserHelperList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    ChatUserHelper chat = dataSnapshot.getValue(ChatUserHelper.class);
                    if (userID.equals(chat.getReciever()) && ids.equals(chat.getSender()) ||
                            ids.equals(chat.getReciever()) && userID.equals(chat.getSender())) {
                        chatUserHelperList.add(chat);
                    }
                }
                messageAdapter = new MessageAdapter(MessageActivity.this,chatUserHelperList,img);
                chatList.setAdapter(messageAdapter);
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==438 && resultCode == RESULT_OK && data != null && data.getData()!=null){

            fileUri = data.getData();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");
            DatabaseReference messagePush = firebaseDatabase.getReference("Chat").push();
            StorageReference filePath = storageReference.child(fileUri.toString() + "." + "jpg");
            uploadTask = filePath.putFile(fileUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();
                        SimpleDateFormat s = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss a");
                        String date = s.format(new Date());

                        Map chatMessage = new HashMap();
                        chatMessage.put("message", myUrl);
                        chatMessage.put("reciever", toName);
                        chatMessage.put("reciever_img", reciever_img);
                        chatMessage.put("reciever_name", reciever_name);
                        chatMessage.put("sender", firebaseUser.getUid());
                        chatMessage.put("status", 0);
                        chatMessage.put("timestamp", date);
                        chatMessage.put("type", "image");

                        messagePush.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                messagePush.child("message").setValue(myUrl);
                                messagePush.child("reciever").setValue(toName);
                                messagePush.child("reciever_img").setValue(reciever_img);
                                messagePush.child("reciever_name").setValue(reciever_name);
                                messagePush.child("sender").setValue(firebaseUser.getUid());
                                messagePush.child("status").setValue("unseen");
                                messagePush.child("timestamp").setValue(date);
                                messagePush.child("type").setValue("image");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        firebaseFirestore.collection("Chat").document().set(chatMessage);
                    }
                }
            });

        }
    }
}