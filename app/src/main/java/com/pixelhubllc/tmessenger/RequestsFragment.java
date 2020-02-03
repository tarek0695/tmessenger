package com.pixelhubllc.tmessenger;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestsFragment extends Fragment {

    private View requestsFragmentView;
    private RecyclerView myRequestsList;
    private DatabaseReference chatRequestRefernce, usersRef, contactsRef;
    private FirebaseAuth mAuth;
    private String currentUser;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestsFragmentView = inflater.inflate(R.layout.fragment_requests, container, false);
        chatRequestRefernce = FirebaseDatabase.getInstance().getReference().child("Chat Requests");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        myRequestsList = requestsFragmentView.findViewById(R.id.friend_request_list);
        myRequestsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return requestsFragmentView;
    }

    @Override
    public void onStart() {

        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatRequestRefernce.child(currentUser), Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, requestViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, requestViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final requestViewHolder requestViewHolder, int i, @NonNull Contacts contacts) {

                        requestViewHolder.itemView.findViewById(R.id.request_accept_btn).setVisibility(View.VISIBLE);
                        requestViewHolder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.VISIBLE);

                        final String list_user_id = getRef(i).getKey();
                        DatabaseReference getTypeRef = getRef(i).child("request_type").getRef();
                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    
                                    String type = dataSnapshot.getValue().toString();

                                    if (type.equals("received")){
                                        usersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChild("image")){

                                                    final String requestProfileImage = dataSnapshot.child("image").getValue().toString();
                                                    Picasso.get().load(requestProfileImage).into(requestViewHolder.profileImage);

                                                }
                                                    final String requestUserName = dataSnapshot.child("name").getValue().toString();
                                                    final String requestUserStatus = dataSnapshot.child("status").getValue().toString();

                                                    requestViewHolder.userName.setText(requestUserName);
                                                    requestViewHolder.userStatus.setText("Wants to connect with you.");

                                                    requestViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            CharSequence options[] = new CharSequence[]{
                                                              "Accept", "Cancel"
                                                            };

                                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                            builder.setTitle(requestUserName  + "  Chat Request");

                                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int i) {
                                                                    if (i==0){
                                                                        contactsRef.child(currentUser).child(list_user_id).setValue("Saved")
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()){
                                                                                            contactsRef.child(list_user_id).child(currentUser).setValue("Saved")
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()){
                                                                                                                chatRequestRefernce.child(currentUser).child(list_user_id)
                                                                                                                        .removeValue()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                if (task.isSuccessful()){
                                                                                                                                    chatRequestRefernce.child(list_user_id).child(currentUser)
                                                                                                                                            .removeValue()
                                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                @Override
                                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                    if (task.isSuccessful()){
                                                                                                                                                        Toast.makeText(getContext(), "New Contact Saved", Toast.LENGTH_SHORT).show();

                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                            });
                                                                                                                                }
                                                                                                                            }
                                                                                                                        });
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                    if (i==1){
                                                                        chatRequestRefernce.child(currentUser).child(list_user_id)
                                                                                .removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()){
                                                                                            chatRequestRefernce.child(list_user_id).child(currentUser)
                                                                                                    .removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()){
                                                                                                                Toast.makeText(getContext(), "Friend Request Deleted", Toast.LENGTH_SHORT).show();

                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }
                                                                                });

                                                                    }
                                                                }
                                                            });

                                                            builder.show();
                                                        }
                                                    });
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public requestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent,false);
                        requestViewHolder holder = new requestViewHolder(view);
                        return holder;
                    }
                };
        myRequestsList.setAdapter(adapter);
        adapter.startListening();

    }

    public static class requestViewHolder extends RecyclerView.ViewHolder{

        TextView userName, userStatus;
        CircleImageView profileImage;
        Button acceptButton, cancelButton;

        public requestViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.users_profile_image);
            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            acceptButton = itemView.findViewById(R.id.request_accept_btn);
            cancelButton = itemView.findViewById(R.id.request_cancel_btn);
        }
    }
}
