package ec.richardnarvaez.chatf.chat.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ec.richardnarvaez.chatf.R;

public class ListUserViewHolder extends RecyclerView.ViewHolder {
    public TextView itemName;

    public ListUserViewHolder(@NonNull View v) {
        super(v);
        itemName = v.findViewById(R.id.itemName);
    }
}
