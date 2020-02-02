package com.pixelhubllc.tmessenger;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

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

public class ContactsFragment extends Fragment {

    private View contactsView;
    private RecyclerView myContactsList;
    private DatabaseReference contactRef, userRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactsView = inflater.inflate(R.layout.fragment_contacts, container, false);

        myContactsList = contactsView.findViewById(R.id.contact_list);
        myContactsList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        contactRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        return contactsView;
    }

    @Override
    public void onStart() {

        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(contactRef, Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, contactsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, contactsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final contactsViewHolder contactsViewHolder, int i, @NonNull Contacts contacts) {

                        final String userIDs = getRef(i).getKey();
                        userRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild("image")) {
                                    String profileImage = dataSnapshot.child("image").getValue().toString();
                                    String profleStatus = dataSnapshot.child("status").getValue().toString();
                                    String profileName = dataSnapshot.child("name").getValue().toString();

                                    contactsViewHolder.userName.setText(profileName);
                                    contactsViewHolder.userStatus.setText(profleStatus);
                                    Picasso.get().load(profileImage).placeholder(R.drawable.profile_image).into(contactsViewHolder.profileImage);
                                } else {

                                    String profleStatus = dataSnapshot.child("status").getValue().toString();
                                    String profileName = dataSnapshot.child("name").getValue().toString();

                                    contactsViewHolder.userName.setText(profileName);
                                    contactsViewHolder.userStatus.setText(profleStatus);

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public contactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                        contactsViewHolder viewHolder = new contactsViewHolder(view);
                        return viewHolder;
                    }
                };
        myContactsList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class contactsViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userStatus;
        CircleImageView profileImage;


        public contactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
        }
    }
}
