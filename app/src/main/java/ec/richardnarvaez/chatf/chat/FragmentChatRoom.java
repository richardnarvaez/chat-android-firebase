package ec.richardnarvaez.chatf.chat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import java.util.Calendar;
import java.util.TimeZone;

import ec.richardnarvaez.chatf.R;
import ec.richardnarvaez.chatf.chat.adapters.FirebaseRecyclerAdapter;
import ec.richardnarvaez.chatf.chat.models.Author;
import ec.richardnarvaez.chatf.chat.models.Message;
import ec.richardnarvaez.chatf.chat.viewHolder.MessageViewHolder;
import ec.richardnarvaez.chatf.utils.FirebaseUtils;
import ec.richardnarvaez.chatf.utils.GlideUtils;

/**
 * Created by macbookpro on 2/27/18.
 */

public class FragmentChatRoom extends Fragment {
    // CONSTANTES
    public static final String TAG = "CommentsFragment";
    private static final String POST_REF_PARAM = "post_ref_param";
    private static final String URL_THUM = "url_thum";
    private static final String URL_FULL = "url_full";
    private static final String ID = "_id";
    private static final int DEFAULT_MSG_LENGTH_LIMIT = 256;
    // ELEMENTOS PARA PINTAR EL MENSAJE
    private EmojiEditText mEditText;
    private String url_thum, url_full;
    private LinearLayoutManager linearLayoutManager;
    private String uid;
    private boolean detail = false;
    private TextView tvName, tvUserName;
    private ImageView exit_mess, send_photo;
    // aqui se guardara la id del usuario el cual se ha ingresado en su chat
    private String IdUsuarioActivo;
    private String IdUsuarioAchatear;
    // Adapter firebase
    private FirebaseRecyclerAdapter<Message, MessageViewHolder> mAdapter;

    public FragmentChatRoom() {
    }

    public static FragmentChatRoom newInstance(String postRef, String _url_thum, String _url_full, String id) {
        // Crea una instancia del fragmento y lo devuelve con los parametros llenados por el bundle
        FragmentChatRoom fragment = new FragmentChatRoom();
        Bundle args = new Bundle();
        args.putString(ID, id);
        args.putString(POST_REF_PARAM, postRef);
        args.putString(URL_THUM, _url_thum);
        args.putString(URL_FULL, _url_full);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Comprueba si el fragmento tiene argumentos
        if (getArguments() != null) {
            uid = getArguments().getString(ID);
            IdUsuarioActivo = getArguments().getString(POST_REF_PARAM);
            url_thum = getArguments().getString(URL_THUM);
            url_full = getArguments().getString(URL_FULL);
            IdUsuarioAchatear = getArguments().getString(ID);
        } else {
            throw new RuntimeException("You must specify a post reference.");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Manejador de emojis
        EmojiManager.install(new IosEmojiProvider());
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_chat_room, container,
                false);
        final LinearLayout linearLayout = rootView.findViewById(R.id.like_box);

        final RecyclerView mCommentsView = rootView.findViewById(R.id.comment_list);
        mEditText = rootView.findViewById(R.id.commenttext);
        final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(mEditText);
        final ImageView emoji = rootView.findViewById(R.id.emoji);

        tvName = rootView.findViewById(R.id.tvName);
        tvUserName = rootView.findViewById(R.id.tvUserName);
        exit_mess = rootView.findViewById(R.id.exit_mess);
        exit_mess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.removeAllViewsInLayout();
            }
        });

        send_photo = rootView.findViewById(R.id.send_photo);
        send_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Proximamente...", Toast.LENGTH_SHORT).show();
            }
        });


        final ImageView profileThumbnail = rootView.findViewById(R.id.profileThumbnail);
        //Obtener sugerencias de amistad
        FirebaseUtils.getPeopleRef().child(uid + "/author").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Author author = dataSnapshot.getValue(Author.class);
                if (author != null) {
                    tvName.setText(author.getName());
                    tvUserName.setText("@" + author.getUser_name());
                    GlideUtils.loadProfileIcon(getActivity(), author.getProfile_picture(), profileThumbnail);
                }else{
                    Toast.makeText(getContext(), "No hay usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        // Escucha cambios en el recycler view
        //Controla que los chats aparezcan en orden
        mCommentsView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, final int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom && mAdapter.getItemCount() > 0) {
                    mCommentsView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int bottomPosition = mCommentsView.getAdapter().getItemCount() - 1;
                            mCommentsView.smoothScrollToPosition(bottomPosition);
                        }
                    }, 100);
                }
            }
        });

        // click en el icono de emojis
        emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiPopup.toggle();
                if (emojiPopup.isShowing()) {
                    emoji.setImageResource(R.drawable.vector_keyboard);
                } else {
                    emoji.setImageResource(R.drawable.vector_emoji);
                }
            }
        });

        //emojiPopup.toggle(); // Toggles visibility of the Popup.
        //emojiPopup.dismiss(); // Dismisses the Popup.
        //emojiPopup.isShowing(); // Returns true when Popup is showing.
        final ImageView sendButton = rootView.findViewById(R.id.send_comment);

        // Se obtiene todos los chats del nodo id:UsarioActivo/id:UsuarioAchatear de este modo solo ellos dos prodran ver los mensajes

        final DatabaseReference commentsRefNodoPrincipal = FirebaseUtils.getCommentsRef().child(IdUsuarioActivo).child(IdUsuarioAchatear);
        final DatabaseReference commentsRefNodoSecundario = FirebaseUtils.getCommentsRef().child(IdUsuarioAchatear).child(IdUsuarioActivo);


        mAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                Message.class,
                1,
                MessageViewHolder.class, commentsRefNodoPrincipal) {
            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = null;
                Log.e(TAG, "ITEM NUM" + viewType);
                // Se verifica si el mensaje es nuestro
                if (viewType == 1) {
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_message_send, parent, false);
                    return new MessageViewHolder(view);
                } else if (viewType == 2) {
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_message, parent, false);
                    return new MessageViewHolder(view);
                }

                return new MessageViewHolder(view);
            }

            @SuppressLint("DefaultLocale")
            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder, Message comment, int position) {
                //CommentViewHolder viewHolder = (CommentViewHolder) holder;
                long milliseconds = (long) comment.getTimestamp();
                TimeZone tz = TimeZone.getDefault();
                milliseconds = milliseconds + tz.getOffset(Calendar.ZONE_OFFSET);
                int seconds = (int) (milliseconds / 1000) % 60;
                int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
                int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

                viewHolder.commentTime
                        .setText(String.format("%02d:%02d", hours, minutes));

                viewHolder.commentText.setText(comment.getText());
                viewHolder.authorRef = comment.getUser_uid();

                switch (viewHolder.getItemViewType()) {
                    // si el mensaje es nuestro es el tipo 1
                    case 1:
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.commentText.getLayoutParams();
                        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                        if (position != 0 && mAdapter.getItem(position - 1) != null &&
                                mAdapter.getItem(position - 1).getUser_uid().equals(FirebaseUtils.getCurrentUserId())) {
                            Log.e(TAG, "Position: " + position);
                            //mAdapter.getItemViewType(position-1) == 1
                            //params.setMargins(0,0,16,0);
                            //viewHolder.commentTime.setVisibility(View.GONE);
                            //viewHolder.commentText.setBackgroundResource(R.drawable.rounded_rectangle_mess_send_up);
                        }

                        viewHolder.commentText.setLayoutParams(params);
                        break;
                        // si el tipo es dos se procedera a llenar los datos de nombre y foto de perfil
                    case 2:
                        FirebaseUtils.getPeopleRef().child(comment.getUser_uid() + "/author").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                GlideUtils.loadProfileIcon(getActivity(), dataSnapshot.child("profile_picture").getValue(String.class), viewHolder.commentPhoto);
                                viewHolder.commentAuthor.setText(dataSnapshot.child("name").getValue(String.class));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        break;
                }


            }


        };

        if (mAdapter.getItemCount() > 0) {
            int bottomPosition = mCommentsView.getAdapter().getItemCount() - 1;
            mCommentsView.smoothScrollToPosition(bottomPosition);
        }
        //mAdapter.startListening();

        sendButton.setEnabled(false);

        mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    sendButton.setEnabled(true);
                    sendButton.setColorFilter(getResources().getColor(R.color.blue_d));
                } else {
                    sendButton.setEnabled(false);
                    sendButton.setColorFilter(getResources().getColor(R.color.colorNoSelect));
                }
            }
        });

        //aqui se envian nuevos mensajes
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Editable commentText = mEditText.getText();
                mEditText.setText("");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Toast.makeText(getActivity(), R.string.user_logged_out_error,
                            Toast.LENGTH_SHORT).show();
                }

                Message comment = new Message(FirebaseUtils.getCurrentUserId(), commentText.toString(),
                        ServerValue.TIMESTAMP);
                commentsRefNodoPrincipal.push().setValue(comment, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError error, DatabaseReference firebase) {
                        if (error != null) {
                            Log.w(TAG, "Error posting comment: " + error.getMessage());
                            Toast.makeText(getActivity(), "Error posting comment.", Toast
                                    .LENGTH_SHORT).show();
                            mEditText.setText(commentText);
                        }
                        int bottomPosition = mCommentsView.getAdapter().getItemCount() - 1;
                        mCommentsView.smoothScrollToPosition(bottomPosition);
                    }
                });
                 commentsRefNodoSecundario.push().setValue(comment, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.w(TAG, "Error posting comment: " + databaseError.getMessage());
                            Toast.makeText(getActivity(), "Error posting comment.", Toast
                                    .LENGTH_SHORT).show();
                            mEditText.setText(commentText);
                        }
                        int bottomPosition = mCommentsView.getAdapter().getItemCount() - 1;
                        mCommentsView.smoothScrollToPosition(bottomPosition);
                    }
                });
            }
        });

        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setSmoothScrollbarEnabled(false);

        mCommentsView.setLayoutManager(linearLayoutManager);
        mCommentsView.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            //mAdapter.stopListening();
        }
    }


}
