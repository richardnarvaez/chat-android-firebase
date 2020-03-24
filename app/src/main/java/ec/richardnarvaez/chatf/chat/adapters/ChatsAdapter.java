package ec.richardnarvaez.chatf.chat.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
import ec.richardnarvaez.chatf.chat.constantes.Constantes;
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
        final int[] numeroMensajes = {0};
        final String IdUsuarioActivo = FirebaseUtils.getCurrentUserId();
        if(IdUsuarioActivo!=null) {
            // busca cambios asincronos para mostrar el ultimo mensaje
            final Query commentsRefNodoPrincipal = FirebaseUtils.getCommentsRef().child(IdUsuarioActivo).child(list.get(position).getKey());
            /*final Query commentsRefNodoSecundario = FirebaseUtils.getCommentsRef().child(list.get(position).getKey()).child(IdUsuarioActivo);*/
// Listener para la llegada de nuevos mensajes
            commentsRefNodoPrincipal.addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Message nuevoMensaje = dataSnapshot.getValue(Message.class);

                    if (nuevoMensaje != null) {
                        if(!nuevoMensaje.getState().equals("check")) {
                            Map<String, Object> hopperUpdates = new HashMap<>();
                            hopperUpdates.put("state", "received");
                            DatabaseReference hopperRef = FirebaseUtils.getCommentsRef().child(IdUsuarioActivo).child(list.get(position).getKey()).child(dataSnapshot.getKey());
                            hopperRef.updateChildren(hopperUpdates);
                        }
                        String mensaje = nuevoMensaje.getText();
                        // verificar el autor del mensaje
                        if (nuevoMensaje.getUser_uid().equals(IdUsuarioActivo)) {
                            mensaje = "Tú: " + mensaje;
                        } else {
                            if(nuevoMensaje.getState().equals("received")||nuevoMensaje.getState().equals("sent")) {
                                numeroMensajes[0]++;
                                holder.layoutContadorMensajes.setVisibility(View.VISIBLE);
                            }
                        }
                        // Controlador de mensajes largos
                        if (mensaje.length() > 22) {
                            mensaje = mensaje.substring(0, 22) + "...";
                        }
                        // Setear los mensajes en las vistas
                        holder.itemMensaje.setText(mensaje);
                        holder.itemNumeroMensajes.setText(String.valueOf(numeroMensajes[0]));
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
        }


        //se pinta en la interfaz el nombre obteniendolo de la lista
        holder.itemNombre.setText(list.get(position).getNombre());
        try {
            if(list.get(position)!=null) {
                if (list.get(position).getIs_connected()) {
                    holder.linearConected.setBackgroundResource(R.drawable.chip_green);
                }
            }
        }catch (Exception e){
            Log.e("error: ",e.toString());
        }

        Picasso.get()
                .load(list.get(position).getFoto())
                .into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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
                // Resetear el contador de mensajes
                numeroMensajes[0] = 0;
                holder.itemNumeroMensajes.setText(String.valueOf(numeroMensajes[0]));
                holder.layoutContadorMensajes.setVisibility(View.INVISIBLE);
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
        public FrameLayout linearConected;
        public FrameLayout layoutContadorMensajes;
        public TextView itemNumeroMensajes;

        public ViewHolder(@NonNull View v) {
            super(v);
            itemNumeroMensajes = v.findViewById(R.id.itemContadorMensajes);
            itemNombre = v.findViewById(R.id.itemName);
            imageView = v.findViewById(R.id.itemFoto);
            itemMensaje = v.findViewById(R.id.itemMensaje);
            linearConected = v.findViewById(R.id.botonConectado);
            layoutContadorMensajes = v.findViewById(R.id.FrameLayoutContadorMensajes);
        }
    }

}
