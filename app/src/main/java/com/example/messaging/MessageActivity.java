package com.example.messaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.messaging.Adapters.MessageAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {


    private static final String TAG = "MESSAGE";
    CircleImageView messageImage;
    TextView messageName;

    FirebaseUser firebaseUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersReference = db.collection("Users");
    CollectionReference msgReference = db.collection("Messages");

    ImageButton btnSend;
    EditText textSend;
    String url = "default";

    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    List<Chat> chats;


    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageActivity.this, HomePage.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        messageImage = findViewById(R.id.message_image);
        messageName = findViewById(R.id.message_name);
        textSend = findViewById(R.id.text_send);
        btnSend = findViewById(R.id.btnSend);

        recyclerView = findViewById(R.id.messages_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        intent = getIntent();
        final String userId = getIntent().getStringExtra("userid");
        String name = getIntent().getStringExtra("name");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        messageName.setText(name);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = textSend.getText().toString().trim();
                if (!msg.equals("")) {
                    sendMessage(firebaseUser.getUid(), userId, msg);
                } else {
                    Toast.makeText(MessageActivity.this, "Can't send empty message", Toast.LENGTH_SHORT).show();
                }
                textSend.setText("");
            }
        });

        //readMessages(firebaseUser.getUid(), userId, "Image URL");

        usersReference.document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserProfile userProfile = documentSnapshot.toObject(UserProfile.class);
                        url = userProfile.getImageURL();
                        if (url.equals("default")) {
                            messageImage.setImageResource(R.drawable.default_user_icon);
                        }
                        else {
                            Glide.with(MessageActivity.this).load(url).into(messageImage);
                        }
                        readMessages(firebaseUser.getUid(), userId, url);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MessageActivity.this, "Error retrieving user info", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
                    }
                });

//        usersReference.document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    Toast.makeText(MessageActivity.this, "Error retrieving user info", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
//                    return;
//                }
//
//                UserProfile userProfile = documentSnapshot.toObject(UserProfile.class);
//                url = userProfile.getImageURL();
//                if (url.equals("default")) {
//                    messageImage.setImageResource(R.drawable.default_user_icon);
//                }
//                else {
//                    Glide.with(MessageActivity.this).load(url).into(messageImage);
//                }
//                readMessages(firebaseUser.getUid(), userId, url);
//            }
//        });



    }

    private void sendMessage(String sender, String receiver, String message) {


        Chat chat = new Chat(sender, receiver, message);

        msgReference.document(Double.toString(System.currentTimeMillis())).set(chat)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Message sent");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
                    }
                });
        readMessages(sender, receiver, url);
    }

    private void readMessages(final String myID, final String userID, final String imageURL) {
        chats = new ArrayList<>();

//        msgReference.get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        chats.clear();
//                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                            Chat chat = documentSnapshot.toObject(Chat.class);
//                            if (chat.getReceiver().equals(myID) && chat.getSender().equals(userID) ||
//                                    chat.getReceiver().equals(userID) && chat.getSender().equals(myID)) {
//                                chats.add(chat);
//                            }
//
//
//                        }
//                        messageAdapter = new MessageAdapter(MessageActivity.this, chats, imageURL);
//                        recyclerView.setAdapter(messageAdapter);
//                        messageAdapter.notifyDataSetChanged();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(MessageActivity.this, "Error retrieving chats", Toast.LENGTH_SHORT).show();
//                        Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
//                    }
//                });

        msgReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(MessageActivity.this, "Error retrieving chats", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
                    return;
                }
                chats.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Chat chat = documentSnapshot.toObject(Chat.class);
                    if (chat.getReceiver().equals(myID) && chat.getSender().equals(userID) ||
                            chat.getReceiver().equals(userID) && chat.getSender().equals(myID)) {
                        chats.add(chat);
                    }


                }
                messageAdapter = new MessageAdapter(MessageActivity.this, chats, imageURL);
                recyclerView.setAdapter(messageAdapter);
                messageAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setStatus(String status) {
        HashMap<String, Object>  hashMap = new HashMap<>();
        hashMap.put("status", status);

        usersReference.document(firebaseUser.getUid()).update(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        setStatus("offline");
    }
}
