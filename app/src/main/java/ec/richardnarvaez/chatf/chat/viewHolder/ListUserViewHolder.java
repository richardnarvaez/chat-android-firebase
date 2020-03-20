package ec.richardnarvaez.chatf.chat.viewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ec.richardnarvaez.chatf.R;

public class ListUserViewHolder extends RecyclerView.ViewHolder {
    public TextView itemNombre;

    public ListUserViewHolder(@NonNull View v) {
        super(v);
        itemNombre = v.findViewById(R.id.itemName);
    }
}
