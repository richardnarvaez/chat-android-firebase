package ec.richardnarvaez.chatf.chat.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import ec.richardnarvaez.chatf.R;
import ec.richardnarvaez.chatf.activities.ChatRoomActivity;
import ec.richardnarvaez.chatf.chat.Constants.Constants;
import ec.richardnarvaez.chatf.chat.Models.Friend;
import ec.richardnarvaez.chatf.chat.Models.Message;
import ec.richardnarvaez.chatf.Utils.FirebaseUtils;

public class
ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {
    private final List<Friend> friends;
    private Context context;
    DatabaseReference databaseReference;
    FirebaseDatabase database;

    public ChatsAdapter(Context context, List<Friend> friends) {
        super();
        this.friends = friends;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatsAdapter.ViewHolder holder, final int position) {
        final int[] numberOfMessages= {0};
        final String IdUserActive= FirebaseUtils.getCurrentUserId();
        if (IdUserActive != null) {
            final String friendKey = friends.get(position).getKey();
            if (friendKey != null) {
                final DatabaseReference messagesNodePrincipal = FirebaseUtils.getCommentsRef().child(IdUserActive).child(friendKey);
                final DatabaseReference messagesNodeSecondary = FirebaseUtils.getCommentsRef().child(friendKey).child(IdUserActive);

                messagesNodePrincipal.addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Message newMessage = dataSnapshot.getValue(Message.class);
                        if (newMessage != null) {
                            if (!newMessage.getState().equals("check")) {
                                setMessageStatus(IdUserActive,friends.get(position).getKey(),dataSnapshot.getKey(), Constants.MESSAGE_RECEIVED);
                            }
                            String mensaje = newMessage.getText();
                            if (checkMessageUser(newMessage,IdUserActive)) {
                                holder.imageMessageStatus.setVisibility(View.VISIBLE);
                                mensaje = String.format("Tú: %s", mensaje);
                            } else {
                                holder.layoutContadorMensajes.setVisibility(View.VISIBLE);
                                if (newMessage.getState().equals(Constants.MESSAGE_RECEIVED) || newMessage.getState().equals(Constants.MESSAGE_SENT))
                                    numberOfMessages[0]++;
                                try {
                                    int[] date = configureMessageDate(newMessage);
                                    holder.itemFechaMensaje.setText(String.format("%02d:%02d", date[1], date[0]));
                                    if (numberOfMessages[0] == 0) {
                                        holder.layoutContadorMensajes.setVisibility(View.INVISIBLE);
                                    } else {
                                        holder.layoutContadorMensajes.setVisibility(View.VISIBLE);
                                        holder.itemNumeroMensajes.setText(String.valueOf(numberOfMessages[0]));
                                    }
                                }catch (Exception e){
                                    Log.e("error: ", e.toString());
                                }
                            }

                            if (mensaje.length() > 22)
                                mensaje = String.format("%s...", mensaje.substring(0, 22));

                            holder.itemMensaje.setText(mensaje);
                            messagesNodeSecondary.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    Message newMensaje = dataSnapshot.getValue(Message.class);
                                    if (newMensaje != null && checkMessageUser(newMessage,IdUserActive)) {
                                        try {
                                            holder.imageMessageStatus.setVisibility(View.VISIBLE);
                                            if (newMensaje.getState().equals(Constants.MESSAGE_CHECK)) {
                                                holder.imageMessageStatus.setImageResource(R.drawable.vector_double_check);
                                                holder.imageMessageStatus.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
                                            } else if (newMensaje.getState().equals(Constants.MESSAGE_RECEIVED)) {
                                                holder.imageMessageStatus.setImageResource(R.drawable.vector_double_check);
                                                holder.imageMessageStatus.setColorFilter(ContextCompat.getColor(context, R.color.md_grey_500), PorterDuff.Mode.SRC_IN);
                                            } else if (newMensaje.getState().equals(Constants.MESSAGE_SENT)) {
                                                holder.imageMessageStatus.setImageResource(R.drawable.ic_message_sent);
                                                holder.imageMessageStatus.setColorFilter(ContextCompat.getColor(context, R.color.md_grey_500), PorterDuff.Mode.SRC_IN);
                                            }
                                        } catch (Exception e) {
                                            Log.e("error: ", e.toString());
                                        }
                                    }else{
                                        holder.imageMessageStatus.setVisibility(View.INVISIBLE);
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
                        }
                        ;
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
            
            holder.itemNombre.setText(friends.get(position).getNombre());
            try {
                if (friends.get(position) != null) {
                    if (friends.get(position).getIs_connected()) {
                        holder.linearConected.setBackgroundResource(R.drawable.chip_green);
                    }
                }
            } catch (Exception e) {
                Log.e("error: ", e.toString());
            }

            Picasso.get()
                    .load(friends.get(position).getFoto())
                    .into(holder.imageView);

            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, ChatRoomActivity.class);
                intent.putExtra(Constants.FRIEND_KEY, friends.get(position).getKey());
                context.startActivity(intent);
                numberOfMessages[0] = 0;
                holder.itemNumeroMensajes.setText(String.valueOf(numberOfMessages[0]));
                holder.layoutContadorMensajes.setVisibility(View.INVISIBLE);
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.friends.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemNombre;
        public ImageView imageView;
        public ImageView imageMessageStatus;
        public TextView itemMensaje;
        public FrameLayout linearConected;
        public FrameLayout layoutContadorMensajes;
        public TextView itemNumeroMensajes;
        public TextView itemFechaMensaje;

        public ViewHolder(@NonNull View v) {
            super(v);
            itemFechaMensaje = v.findViewById(R.id.tvFechaMensaje);
            itemNumeroMensajes = v.findViewById(R.id.itemContadorMensajes);
            itemNombre = v.findViewById(R.id.itemName);
            imageView = v.findViewById(R.id.itemFoto);
            itemMensaje = v.findViewById(R.id.itemMensaje);
            linearConected = v.findViewById(R.id.botonConectado);
            imageMessageStatus = v.findViewById(R.id.imMensajeVisto);
            layoutContadorMensajes = v.findViewById(R.id.FrameLayoutContadorMensajes);
        }
    }

    public void setMessageStatus(String currentUserKey,String friendKey,String messageKey,String messageStatus){
        Map<String, Object> hopperUpdates = new HashMap<>();
        hopperUpdates.put("state",messageStatus);
        DatabaseReference hopperRef = FirebaseUtils.getCommentsRef().child(currentUserKey).child(friendKey).child(messageKey);
        hopperRef.updateChildren(hopperUpdates);
    }

    public Boolean checkMessageUser(Message message, String idUser){
        return message.getUser_uid().equals(idUser);
    }

    private int[] configureMessageDate(Message message){
        long milliseconds = (long) message.getTimestamp();
        TimeZone tz = TimeZone.getDefault();
        milliseconds = milliseconds + tz.getOffset(Calendar.ZONE_OFFSET);
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        return new int[]{minutes, hours};
    }

}
