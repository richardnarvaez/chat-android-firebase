package ec.richardnarvaez.chatf.chat.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import ec.richardnarvaez.chatf.R;
import ec.richardnarvaez.chatf.chat.adapters.ChatsAdapter;
import ec.richardnarvaez.chatf.chat.Constants.Constants;
import ec.richardnarvaez.chatf.chat.models.Author;
import ec.richardnarvaez.chatf.chat.models.Friend;
import ec.richardnarvaez.chatf.Utils.FirebaseUtils;


public class FragmentChat extends Fragment {
    private DatabaseReference mRootReference;
    private String IdUserActive;
    private List<Friend> list;
    private RecyclerView rvChats;

    public FragmentChat() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        return new FragmentChat();
    }

    ChatsAdapter adapterChat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        IdUserActive = FirebaseUtils.getCurrentUserId();
        mRootReference = FirebaseDatabase
                .getInstance()
                .getReference();

        list = new ArrayList<>();

        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        rvChats = v.findViewById(R.id.rView);
        rvChats.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        adapterChat = new ChatsAdapter(getContext(), list);
        rvChats.setAdapter(adapterChat);

        FirebaseUtils.getBaseRef()
                .child("users_chats/" + IdUserActive)
                .limitToLast(3)
                .orderByChild("date")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dts, @Nullable String s) {
                        Log.e("Nuevo chat: ", dts.getRef().toString());
                        String key = dts.getKey();
                        assert key != null;
                        final DatabaseReference databaseReference = FirebaseUtils.getPeopleRef().child(key);
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Author author = dataSnapshot.getChildren().iterator().next().getValue(Author.class);
                                try {
                                    if (author != null) {
                                        Friend friend = new Friend(author.getName(), author.getProfile_picture(), "", author.getUid(), author.getIs_connected());
                                        int n = findFriendById(friend);
                                        if (n != -1) {
                                            list.set(n, friend);
                                        } else {
                                            list.add(0, friend);
                                        }
                                        adapterChat.notifyDataSetChanged();
                                    }
                                } catch (
                                        Exception e) {
                                    Log.e("error: ", e.toString());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        Log.e("nu", dataSnapshot.getKey());
                        for (Friend f : list) {
                            if (f.getKey().equals(dataSnapshot.getKey())) {
                                int a = list.indexOf(f);
                                Collections.swap(list, a, 0);
                                adapterChat.notifyDataSetChanged();
                                return;
                            }
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        return v;
    }

    public int findFriendById(Friend friend) {
        int n = -1;
        for (Friend x : list) {
            if (friend.equals(x)) {
                n = list.indexOf(x);
            }
        }
        return n;
    }


}
