package ec.richardnarvaez.chatf.chat.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ec.richardnarvaez.chatf.R;
import ec.richardnarvaez.chatf.activities.ChatRoomActivity;
import ec.richardnarvaez.chatf.chat.models.Friend;


public class ListUserAdapter extends RecyclerView.Adapter<ListUserAdapter.ViewHolder> {
    /*Constructor: aqui se crea la lista*/
    private List<Friend> list;
    private Context ctx;
    public ListUserAdapter(Context ctx, List<Friend> list) {
        this.list = list;
        this.ctx=ctx;
    }


    @NonNull
    @Override
    /*inflate del item.xml*/
    public ListUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListUserAdapter.ViewHolder holder, final int position) {
        holder.itemNombre.setText("Friend: " + position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent (ctx, ChatRoomActivity.class);
                ctx.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemNombre;
        public ViewHolder(@NonNull View v) {
            super(v);
            itemNombre = v.findViewById(R.id.itemName);
        }
    }

}
