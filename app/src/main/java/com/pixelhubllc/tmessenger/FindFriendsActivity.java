package com.pixelhubllc.tmessenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class FindFriendsActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView findFriendsRecyclerView;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        findFriendsRecyclerView = findViewById(R.id.find_friends_recyler_list);
        findFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = findViewById(R.id.find_friends_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");
    }

    @Override
    protected void onStart() {

        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(userRef, Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, FindFrinedsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, FindFrinedsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFrinedsViewHolder findFrinedsViewHolder, final int i, @NonNull Contacts contacts) {

                        findFrinedsViewHolder.userName.setText(contacts.getName());
                        findFrinedsViewHolder.userStatus.setText(contacts.getStatus());
                        Picasso.get().load(contacts.getImage()).into(findFrinedsViewHolder.profileImage);

                        findFrinedsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String visit_user_id = getRef(i).getKey();
                                Intent profileIntent = new Intent(FindFriendsActivity.this, ProfileActivity.class);
                                profileIntent.putExtra("visit_user_id", visit_user_id);
                                startActivity(profileIntent);

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FindFrinedsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                        FindFrinedsViewHolder viewHolder = new FindFrinedsViewHolder(view);
                        return viewHolder;
                    }
                };

        findFriendsRecyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    public static class FindFrinedsViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userStatus;
        CircleImageView profileImage;

        public FindFrinedsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = (TextView) itemView.findViewById(R.id.user_profile_name);
            userStatus = (TextView) itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
        }
    }
}
