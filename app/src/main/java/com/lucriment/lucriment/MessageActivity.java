package com.lucriment.lucriment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {

    private Button sendButton;
    private EditText messageField;
    private TextView conversation;
    private String userName, chatName;
    private DatabaseReference root;
    private String tempKey;
    private String chatString, displayNameString;
    private Button backButton;
    private String chatID;
    private String receiverID;
    private String senderID;
    private String sender;
    private String receiver;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();
        sendButton = (Button) findViewById(R.id.sentButton);
        messageField = (EditText) findViewById(R.id.messageField);
        conversation = (TextView) findViewById(R.id.convo);
        backButton = (Button) findViewById(R.id.prevButton);
        UserInfo tutor = getIntent().getParcelableExtra("user");
            sender = firebaseAuth.getCurrentUser().getDisplayName();
            receiver = tutor.getFullName();
            senderID = firebaseAuth.getCurrentUser().getUid().toString();
            receiverID = tutor.getId();
            getMessageFromFirebaseUser(senderID,receiverID);
           // chatID = getIntent().getExtras().get("chatID").toString();
            userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName().toString();

           //userName = getIntent().getExtras().get("userName").toString();
      //  chatName = getIntent().getExtras().get("chatName").toString();
       // DatabaseReference check = FirebaseDatabase.getInstance().getReference().child("Chats");

      //  root = FirebaseDatabase.getInstance().getReference().child("Chats").child(chatID);

       // setTitle(chatID);

        backButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);
/*
        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                appendChatCovnversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                appendChatCovnversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/

    }


    public void getMessageFromFirebaseUser(String senderUid, String receiverUid) {
        final String room_type_1 = senderUid + "_" + receiverUid;
        final String room_type_2 = receiverUid + "_" + senderUid;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference();

        databaseReference.child("chats")
                .getRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(room_type_1)) {
                            //Log.e(TAG, "getMessageFromFirebaseUser: " + room_type_1 + " exists");
                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("chats")
                                    .child(room_type_1)
                                    .addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            // Chat message is retreived.
                                            Chat chat = dataSnapshot.getValue(Chat.class);
                                            chatString = chat.text;

                                            displayNameString = chat.senderName;
                                            conversation.append(displayNameString + " : " + chatString + " \n");
                                        }

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            // Unable to get message.
                                        }
                                    });
                        } else if (dataSnapshot.hasChild(room_type_2)) {
                           // Log.e(TAG, "getMessageFromFirebaseUser: " + room_type_2 + " exists");
                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("chats")
                                    .child(room_type_2)
                                    .addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            // Chat message is retreived.
                                            Chat chat = dataSnapshot.getValue(Chat.class);
                                            chatString = chat.text;

                                            displayNameString = chat.senderName;
                                            conversation.append(displayNameString + " : " + chatString + " \n");
                                        }

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            // Unable to get message.
                                        }
                                    });
                        } else {
                          //  Log.e(TAG, "getMessageFromFirebaseUser: no such room available");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Unable to get message
                    }
                });
    }

    private void appendChatCovnversation(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();
        while(i.hasNext()){
                chatString = (String) ((DataSnapshot)i.next()).getValue();

                displayNameString = (String) ((DataSnapshot)i.next()).getValue();
                conversation.append(chatString + " : " + displayNameString + " \n");
        }
    }

    public void sendMessageToFirebaseUser(
            final Chat chat
            ) {
        final String room_type_1 = chat.senderId + "_" + chat.receiverId;
        final String room_type_2 = chat.receiverId + "_" + chat.senderId;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference();

        databaseReference.child("chats")
                .getRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(room_type_1)) {
                            //  Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_1 + " exists");
                            databaseReference.child("chats")
                                    .child(room_type_1)
                                    .child(FirebaseDatabase.getInstance().getReference().push().getKey())
                                    .setValue(chat);
                        } else if (dataSnapshot.hasChild(room_type_2)) {
                            //  Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_2 + " exists");
                            databaseReference.child("chats")
                                    .child(room_type_2)
                                    .child(FirebaseDatabase.getInstance().getReference().push().getKey())
                                    .setValue(chat);
                        } else {
                            // Log.e(TAG, "sendMessageToFirebaseUser: success");
                            databaseReference.child("chats")
                                    .child(room_type_1)
                                    .child(FirebaseDatabase.getInstance().getReference().push().getKey())
                                    .setValue(chat);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Unable to send message.
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        startActivity(new Intent(MessageActivity.this, ViewMessagesActivity.class));
        return true;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.sentButton:
                Calendar cc = Calendar.getInstance();
                Date date = cc.getTime();
                // SimpleDateFormat format1 = new SimpleDateFormat("dd MMM");
                SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                String tsLong = FirebaseDatabase.getInstance().getReference().push().getKey();
                Map<String, String> testM  = ServerValue.TIMESTAMP;

              ///long timeStamp = (long) ServerValue.TIMESTAMP;
               Chat chat = new Chat(sender, receiver, senderID, receiverID,messageField.getText().toString(), date.getTime());
                sendMessageToFirebaseUser(chat);
                messageField.setText("");

                /*
                Map<String,Object> map = new HashMap<String,Object>();
                tempKey = root.push().getKey();
                root.updateChildren(map);

                DatabaseReference messageRoot = root.child(tempKey);
                Map<String,Object> map2 = new HashMap<String,Object>();
                map2.put("msg", messageField.getText().toString());
                map2.put("User Name", userName);
                messageRoot.updateChildren(map2);
                messageField.setText("");
                */
                break;
            case R.id.prevButton:
                finish();
                startActivity(new Intent(MessageActivity.this, ViewMessagesActivity.class));


        }
    }
}
