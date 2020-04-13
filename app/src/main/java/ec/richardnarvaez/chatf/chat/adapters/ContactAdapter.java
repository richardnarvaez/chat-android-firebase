package ec.richardnarvaez.chatf.chat.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import ec.richardnarvaez.chatf.R;
import ec.richardnarvaez.chatf.activities.ChatRoomActivity;
import ec.richardnarvaez.chatf.chat.Constants.Constants;
import ec.richardnarvaez.chatf.chat.Models.Friend;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private final List<Friend> friends;
    private Context context;

    public ContactAdapter(Context context, List<Friend> friends) {
        super();
        this.friends = friends;
        this.context =context;
    }
    @NonNull
    @Override


    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ViewHolder holder, final int position) {
        holder.contactName.setText(friends.get(position).getNombre());
        Picasso.get()
                .load(friends.get(position).getFoto())
                .into(holder.contactPicture);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent (context, ChatRoomActivity.class);
            intent.putExtra(Constants.FRIEND_KEY, friends.get(position).getKey());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return this.friends.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView contactName;
        ImageView contactPicture;

        ViewHolder(@NonNull View v) {
            super(v);
            contactName = v.findViewById(R.id.contactName);
            contactPicture = v.findViewById(R.id.contactPicture);
        }
    }

}
