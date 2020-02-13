package com.pixelhubllc.tmessenger;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatsFragment extends Fragment {

    private View privateChatsView;
    private RecyclerView chatsList;
    private DatabaseReference chatsRef, usersRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        privateChatsView = inflater.inflate(R.layout.fragment_chats, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        chatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        chatsList = privateChatsView.findViewById(R.id.chats_list);
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return privateChatsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatsRef, Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, chatsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, chatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final chatsViewHolder chatsViewHolder, int i, @NonNull Contacts contacts) {

                        final String usersIds = getRef(i).getKey();
                        final String[] retImage = {"default_image"};

                        usersRef.child(usersIds).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()){

                                    if (dataSnapshot.hasChild("image")){

                                        retImage[0] = dataSnapshot.child("image").getValue().toString();
                                        Picasso.get().load(retImage[0]).into(chatsViewHolder.profileImage);
                                    }

                                    final String retName = dataSnapshot.child("name").getValue().toString();
                                    final String retStatus = dataSnapshot.child("status").getValue().toString();

                                    chatsViewHolder.userName.setText(retName);
                                    chatsViewHolder.userStatus.setText("Last seen: " + "\n" + "Date " + " Time");

                                    if (dataSnapshot.child("userState").hasChild("state")){

                                        String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                        String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                        String time = dataSnapshot.child("userState").child("time").getValue().toString();

                                        Log.d("state", state);
                                 
                                        if (state.equals("online"))
                                        {
                                            chatsViewHolder.userStatus.setText("online");
                                            Log.d("state", state + " online ");
                                           // chatsViewHolder.onlineIcon.setVisibility(View.VISIBLE);
                                        }
                                        else if (state.equals("offline"))
                                        {
                                            chatsViewHolder.userStatus.setText("Last Seen: " + date + " " + time);
                                            Log.d("state", state + " ofline ");
                                           // chatsViewHolder.onlineIcon.setVisibility(View.INVISIBLE);

                                        }
                                    }
                                    else {
                                        chatsViewHolder.userStatus.setText("offline");
                                    }

                                    chatsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            Intent chatIntent =new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("visit_user_id", usersIds);
                                            chatIntent.putExtra("visit_user_name", retName);
                                            chatIntent.putExtra("visit_user_image", retImage[0]);
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

                    @NonNull
                    @Override
                    public chatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                        return new chatsViewHolder(view);
                    }
                };

        chatsList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class chatsViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profileImage;
        TextView userName, userStatus;

        public chatsViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.users_profile_image);
            userStatus = itemView.findViewById(R.id.user_status);
            userName = itemView.findViewById(R.id.user_profile_name);
        }
    }
}
