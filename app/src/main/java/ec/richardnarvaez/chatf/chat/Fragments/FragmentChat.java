package ec.richardnarvaez.chatf.chat.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

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

import ec.richardnarvaez.chatf.R;
import ec.richardnarvaez.chatf.activities.ChatRoomActivity;
import ec.richardnarvaez.chatf.chat.adapters.ChatsAdapter;
import ec.richardnarvaez.chatf.chat.constantes.Constantes;
import ec.richardnarvaez.chatf.chat.models.Author;
import ec.richardnarvaez.chatf.chat.models.Friends;
import ec.richardnarvaez.chatf.utils.FirebaseUtils;


public class FragmentChat extends Fragment {
    //Referencia a la base de datos
    DatabaseReference mRootReference;
    //FirebaseAuth mAuth;
    //Lista
    List<Friends> list;
    RecyclerView rvChats;

    public FragmentChat() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        return new FragmentChat();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootReference = FirebaseDatabase
                .getInstance()
                .getReference();
        Query query = mRootReference
                .child(Constantes.USERS_DATABASE)
                .limitToLast(50);

        list = new ArrayList<>();
// Se procede a llenar la lista con los nombres de los usuarios de firebase
       // mRootReference = FirebaseDatabase.getInstance().getReference();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Author author = dataSnapshot1.child(Constantes.AUTHOR_DATABASE).getValue(Author.class);
                    if(!author.getUid().equals(FirebaseUtils.getCurrentUserId())) {
                        list.add(new Friends(author.getName(), author.getProfile_picture(), "", dataSnapshot1.getKey()));
                    }
                }

                ChatsAdapter adapterChat = new ChatsAdapter(getContext(), list);
                rvChats.setAdapter(adapterChat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        rvChats = v.findViewById(R.id.rView);
        rvChats.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        return v;
    }
}
