package ec.richardnarvaez.chatf.chat.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import ec.richardnarvaez.chatf.R;
import ec.richardnarvaez.chatf.activities.ChatRoomActivity;
import ec.richardnarvaez.chatf.chat.Fragments.FragmentChat;
import ec.richardnarvaez.chatf.chat.models.Author;
import ec.richardnarvaez.chatf.chat.models.Friends;
import ec.richardnarvaez.chatf.chat.models.Message;
import ec.richardnarvaez.chatf.utils.FirebaseUtils;

public class
ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {
    /*Constructor: aqui se crea la lista*/
    private final List<Friends> list;
    private Context ctx;
    DatabaseReference databaseReference;
    FirebaseDatabase database;

    public ChatsAdapter(Context ctx, List<Friends> list) {
        super();
        this.list = list;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    /*inflate del item.xml*/
    public ChatsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;

    }

    /*acciones de cada item*/
    @Override
    public void onBindViewHolder(@NonNull final ChatsAdapter.ViewHolder holder, final int position) {
        final String IdUsuarioActivo = FirebaseUtils.getCurrentUserId();
        //se pinta en la interfaz el nombre obteniendolo de la lista
        final Query commentsRefNodoPrincipal = FirebaseUtils.getCommentsRef().child(IdUsuarioActivo).child(list.get(position).getKey()).limitToLast(1);
        commentsRefNodoPrincipal.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Message nuevoMensaje = null;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    nuevoMensaje = dataSnapshot1.getValue(Message.class);
                }
                if (nuevoMensaje != null) {
                    String mensaje = nuevoMensaje.getText();
                    if (nuevoMensaje.getUser_uid().equals(IdUsuarioActivo)) {
                        mensaje = "Tú: "+mensaje;
                    }
                    // Si el mensaje es muy largo la vista previa saldra recortada
                    if(mensaje.length()>22){
                        mensaje = mensaje.substring(0,22)+"...";
                    }
                    holder.itemMensaje.setText(mensaje);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.itemNombre.setText(list.get(position).getNombre());
        Picasso.get()
                .load(list.get(position).getFoto())
                .into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//HEAD
                //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

//                Toast.makeText(ctx, ""+list.get(position).getKey(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ctx, ChatRoomActivity.class);
                //mando el key
                i.putExtra("keyreceptor", list.get(position).getKey());
                //i.putExtra("");

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                Toast.makeText(ctx, ""+list.get(position).getKey(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ctx, ChatRoomActivity.class);
                intent.putExtra("keyreceptor", list.get(position).getKey());
// ed574b80d4953298f9d3c12ab8be11f71966c1f9
                ctx.startActivity(i);
            }
        });
    }


    /*tamaño de la lista*/
    @Override
    public int getItemCount() {
        return this.list.size();
    }

    /*referencias (id) de cada elemento del xml*/
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemNombre;
        public ImageView imageView;
        public TextView itemMensaje;

        public ViewHolder(@NonNull View v) {
            super(v);
            itemNombre = v.findViewById(R.id.itemName);
            imageView = v.findViewById(R.id.itemFoto);
            itemMensaje = v.findViewById(R.id.itemMensaje);
        }
    }

}
