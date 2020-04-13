package ec.richardnarvaez.chatf.chat.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import ec.richardnarvaez.chatf.R;
import ec.richardnarvaez.chatf.activities.ChatRoomActivity;
import ec.richardnarvaez.chatf.chat.Models.Author;
import ec.richardnarvaez.chatf.chat.Models.Friend;
import ec.richardnarvaez.chatf.chat.ViewHolder.ListUserViewHolder;
import ec.richardnarvaez.chatf.chat.adapters.FirebaseRecyclerAdapter;
import ec.richardnarvaez.chatf.Utils.FirebaseUtils;


public class FragmentUsers extends Fragment {

    private FirebaseRecyclerAdapter<Author, ListUserViewHolder> mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference mRootReference;
    private List<Friend> friendsList;

    public FragmentUsers() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        return new FragmentUsers();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_call, container, false);
        RecyclerView rv = v.findViewById(R.id.rvView);

        mAdapter = new FirebaseRecyclerAdapter<Author, ListUserViewHolder>(
                Author.class,
                1,
                ListUserViewHolder.class, FirebaseUtils.getCurrentUserRef()) {
            @Override
            public ListUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = null;
                /*if (viewType == 1) {
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_list_user, parent, false);
                    return new ListUserViewHolder(view);
                } else if (viewType == 2) {
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_list_user, parent, false);
                    return new ListUserViewHolder(view);
                }*/

                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_list_user, parent, false);
                return new ListUserViewHolder(view);
            }

            @Override
            protected void populateViewHolder(ListUserViewHolder holder, Author data, int position) {
                //logica y eventos del item
                final Context ctx=getContext();
                //obtengo el nombre de firebase
                holder.itemName.setText(data.getName());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent i = new Intent (ctx, ChatRoomActivity.class);
                        //i.putExtra("");
                        ctx.startActivity(i);
                        //Toast.makeText(ctx, "TOUCH "+position, Toast.LENGTH_SHORT).show();
                    }
                });

            };
        };
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setSmoothScrollbarEnabled(false);

        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(mAdapter);
        return v;
    }


}
