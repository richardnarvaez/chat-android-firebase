package ec.richardnarvaez.chatf.chat.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;


import ec.richardnarvaez.chatf.R;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    public final ImageView commentPhoto;
    public final TextView commentText;
    public final TextView commentAuthor;
    public final TextView commentTime;
    public String authorRef;
    public View view;

    public MessageViewHolder(View itemView) {
        super(itemView);
        this.view = itemView;
        commentPhoto = itemView.findViewById(R.id.comment_author_icon);
        commentText = itemView.findViewById(R.id.comment_text);
        commentAuthor = itemView.findViewById(R.id.comment_name);
        commentTime =  itemView.findViewById(R.id.comment_time);

        itemView.setOnLongClickListener(v -> {
            Toast.makeText(v.getContext(), "Long click -> Coming soon", Toast.LENGTH_SHORT).show();
            return true;
        });

        itemView.setOnClickListener(v -> {
            if (authorRef != null) {
                //Toast.makeText(v.getContext(), "Usuario real", Toast.LENGTH_SHORT).show();
                /*Context context = v.getContext();
                Intent userDetailIntent = new Intent(context, UserDetailActivity.class);
                userDetailIntent.putExtra(UserDetailActivity.USER_ID_EXTRA_NAME,
                        authorRef);
                context.startActivity(userDetailIntent);*/
            }
        });
    }
}
