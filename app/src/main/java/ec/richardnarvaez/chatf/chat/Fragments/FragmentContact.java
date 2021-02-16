package ec.richardnarvaez.chatf.chat.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ec.richardnarvaez.chatf.R;
import ec.richardnarvaez.chatf.chat.adapters.ContactAdapter;
import ec.richardnarvaez.chatf.chat.Constants.Constants;
import ec.richardnarvaez.chatf.chat.models.Author;
import ec.richardnarvaez.chatf.chat.models.Friend;
import ec.richardnarvaez.chatf.Utils.FirebaseUtils;

public class FragmentContact extends Fragment {
    private DatabaseReference mRootReference;
    private List<Friend> friends;
    private RecyclerView rvContacts;

    public FragmentContact() {
    }

    public static Fragment newInstance() {
        return new FragmentContact();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Assignations
        mRootReference = FirebaseDatabase
                .getInstance()
                .getReference();

        Query query = mRootReference
                .child(Constants.USERS_DATABASE)
                .limitToLast(50);

        friends = new ArrayList<>();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friends.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Author author = dataSnapshot1.child(Constants.AUTHOR_DATABASE).getValue(Author.class);
                    assert author != null;
                    if(!author.getUid().equals(FirebaseUtils.getCurrentUserId())) {
                        friends.add(new Friend(author.getName(), author.getProfile_picture(), "", dataSnapshot1.getKey(),author.getIs_connected()));
                    }
                }

                ContactAdapter adapterContact = new ContactAdapter(getContext(), friends);
                rvContacts.setAdapter(adapterContact);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        rvContacts = v.findViewById(R.id.rView);
        rvContacts.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        return v;
    }

}
