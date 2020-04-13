package ec.richardnarvaez.chatf.chat.Fragments;

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

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import ec.richardnarvaez.chatf.R;
import ec.richardnarvaez.chatf.chat.Constants.Constants;
import ec.richardnarvaez.chatf.chat.adapters.FirebaseRecyclerAdapter;
import ec.richardnarvaez.chatf.chat.Models.Author;
import ec.richardnarvaez.chatf.chat.Models.Message;
import ec.richardnarvaez.chatf.chat.ViewHolder.MessageViewHolder;
import ec.richardnarvaez.chatf.Utils.FirebaseUtils;
import ec.richardnarvaez.chatf.Utils.GlideUtils;

public class FragmentChatRoom extends Fragment {
    // Items
    private EmojiEditText mEditText;
    private String url_thum, url_full;
    private LinearLayoutManager linearLayoutManager;
    private boolean detail = false;
    private TextView tvName, tvUserName;
    private ImageView exit_mess, send_photo;
    // Firebase ID's
    private String IdUserActive;
    private String IdFriendKey;
    // Adapters
    private FirebaseRecyclerAdapter<Message, MessageViewHolder> mAdapter;

    public FragmentChatRoom() {
    }

    public static FragmentChatRoom newInstance(String postRef, String _url_thum, String _url_full, String id) {
        FragmentChatRoom fragment = new FragmentChatRoom();
        Bundle arguments = new Bundle();
        arguments.putString(Constants.ID, id);
        arguments.putString(Constants.POST_REF_PARAM, postRef);
        arguments.putString(Constants.URL_THUM, _url_thum);
        arguments.putString(Constants.URL_FULL, _url_full);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            IdUserActive = getArguments().getString(Constants.POST_REF_PARAM);
            url_thum = getArguments().getString(Constants.URL_THUM);
            url_full = getArguments().getString(Constants.URL_FULL);
            IdFriendKey = getArguments().getString(Constants.ID);
        } else {
            throw new RuntimeException("You must specify a post reference.");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EmojiManager.install(new IosEmojiProvider());

        // Assignations

        final View rootView = inflater.inflate(R.layout.fragment_chat_room, container,
                false);
        final LinearLayout linearLayout = rootView.findViewById(R.id.like_box);
        final RecyclerView mCommentsView = rootView.findViewById(R.id.comment_list);
        mEditText = rootView.findViewById(R.id.commenttext);
        final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(mEditText);
        final ImageView emoji = rootView.findViewById(R.id.emoji);
        final ImageView profileThumbnail = rootView.findViewById(R.id.profileThumbnail);
        final ImageView sendButton = rootView.findViewById(R.id.send_comment);
        final DatabaseReference commentsRefNodePrincipal = FirebaseUtils.getCommentsRef().child(IdUserActive).child(IdFriendKey);
        final DatabaseReference commentsRefNodeSecondary = FirebaseUtils.getCommentsRef().child(IdFriendKey).child(IdUserActive);

        tvName = rootView.findViewById(R.id.tvName);
        tvUserName = rootView.findViewById(R.id.tvUserName);
        exit_mess = rootView.findViewById(R.id.exit_mess);
        send_photo = rootView.findViewById(R.id.send_photo);


        // Listeners
        exit_mess.setOnClickListener(v -> linearLayout.removeAllViewsInLayout());
        send_photo.setOnClickListener(v -> Toast.makeText(getActivity(), "Proximamente...", Toast.LENGTH_SHORT).show());

        emoji.setOnClickListener(v -> {
            emojiPopup.toggle();
            if (emojiPopup.isShowing()) {
                emoji.setImageResource(R.drawable.vector_keyboard);
            } else {
                emoji.setImageResource(R.drawable.vector_emoji);
            }
        });

        FirebaseUtils.getPeopleRef().child(IdFriendKey + "/author").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Author author = dataSnapshot.getValue(Author.class);
                if (author != null) {
                    tvName.setText(author.getName());
                    tvUserName.setText("@" + author.getUser_name());
                    GlideUtils.loadProfileIcon(getActivity(), author.getProfile_picture(), profileThumbnail);
                } else {
                    Toast.makeText(getContext(), "No hay usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Manager position messages
        mCommentsView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom && mAdapter.getItemCount() > 0) {
                mCommentsView.postDelayed(() -> {
                    int bottomPosition = Objects.requireNonNull(mCommentsView.getAdapter()).getItemCount() - 1;
                    mCommentsView.smoothScrollToPosition(bottomPosition);
                }, 100);
            }
        });

        // SetUp firebase adapter

        mAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                Message.class,
                1,
                MessageViewHolder.class, commentsRefNodePrincipal) {
            @NotNull
            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = null;
                Log.e(Constants.TAG, "ITEM NUM" + viewType);
                // Message current User
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
            protected void populateViewHolder(final MessageViewHolder viewHolder, Message message, int position) {
                int[] date = configureMessageDate(message);
                String keyMessage = getKey(position);
                final DatabaseReference refMessage = commentsRefNodePrincipal.child(keyMessage);
                refMessage.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (!Objects.equals(dataSnapshot.child("state").getValue(), "check")) {
                                setMessageStatus(refMessage,Constants.MESSAGE_CHECK);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                viewHolder.commentTime
                        .setText(String.format("%02d:%02d", date[1], date[0]));
                viewHolder.commentText.setText(message.getText());
                viewHolder.authorRef = message.getUser_uid();
                switch (viewHolder.getItemViewType()) {
                    case 1:
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.commentText.getLayoutParams();
                        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        if (position != 0 && mAdapter.getItem(position - 1) != null &&
                                mAdapter.getItem(position - 1).getUser_uid().equals(FirebaseUtils.getCurrentUserId())) {
                            Log.e(Constants.TAG, "Position: " + position);
                        }
                        viewHolder.commentText.setLayoutParams(params);
                        break;
                    case 2:
                        FirebaseUtils.getPeopleRef().child(message.getUser_uid() + "/author").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                                GlideUtils.loadProfileIcon(getActivity(), dataSnapshot.child("profile_picture").getValue(String.class), viewHolder.commentPhoto);
                                viewHolder.commentAuthor.setText(dataSnapshot.child("name").getValue(String.class));
                            }
                            @Override
                            public void onCancelled(@NotNull DatabaseError databaseError) {

                            }
                        });
                        break;
                }
            }
        };

        if (mAdapter.getItemCount() > 0) {
            int bottomPosition = Objects.requireNonNull(mCommentsView.getAdapter()).getItemCount() - 1;
            mCommentsView.smoothScrollToPosition(bottomPosition);
        }
        sendButton.setEnabled(false);
        mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Constants.DEFAULT_MSG_LENGTH_LIMIT)});
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

        sendButton.setOnClickListener(v -> {
            final Editable commentText = mEditText.getText();
            mEditText.setText("");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(getActivity(), R.string.user_logged_out_error,
                        Toast.LENGTH_SHORT).show();
            }
            assert commentText != null;

            Message comment = new Message(IdUserActive, commentText.toString(),
                    ServerValue.TIMESTAMP, "sent");
            commentsRefNodePrincipal.push().setValue(comment, (error, firebase) -> {
                if (error != null) {
                    Log.w(Constants.TAG, "Error posting comment: " + error.getMessage());
                    Toast.makeText(getActivity(), "Error posting comment.", Toast
                            .LENGTH_SHORT).show();
                    mEditText.setText(commentText);
                }
                int bottomPosition = Objects.requireNonNull(mCommentsView.getAdapter()).getItemCount() - 1;
                mCommentsView.smoothScrollToPosition(bottomPosition);
            });
            commentsRefNodeSecondary.push().setValue(comment, (databaseError, databaseReference) -> {
                if (databaseError != null) {
                    Log.w(Constants.TAG, "Error posting comment: " + databaseError.getMessage());
                    Toast.makeText(getActivity(), "Error posting comment.", Toast
                            .LENGTH_SHORT).show();
                    mEditText.setText(commentText);
                }
                int bottomPosition = Objects.requireNonNull(mCommentsView.getAdapter()).getItemCount() - 1;
                mCommentsView.smoothScrollToPosition(bottomPosition);
            });
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

    private int[] configureMessageDate(Message message){
        long milliseconds = (long) message.getTimestamp();
        TimeZone tz = TimeZone.getDefault();
        milliseconds = milliseconds + tz.getOffset(Calendar.ZONE_OFFSET);
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        return new int[]{minutes, hours};
    }

    private void setMessageStatus(DatabaseReference ref, String messageStatus){
        Map<String, Object> hopperUpdates = new HashMap<>();
        hopperUpdates.put("state",messageStatus);
        ref.updateChildren(hopperUpdates);
    }

    private void setUpListeners(){

    }
}
