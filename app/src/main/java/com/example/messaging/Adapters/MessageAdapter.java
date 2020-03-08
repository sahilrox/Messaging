package com.example.messaging.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.messaging.Chat;
import com.example.messaging.MessageActivity;
import com.example.messaging.R;
import com.example.messaging.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_LEFT = 0;
    public static final int MSG_RIGHT = 1;
    private Context context;
    private List<Chat> chats;
    private String imageURL;
    private FirebaseUser firebaseUser;

    public MessageAdapter(Context context, List<Chat> chats, String imageURL) {
        this.context = context;
        this.chats = chats;
        this.imageURL = imageURL;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat = chats.get(position);

        holder.chatText.setText(chat.getMessage());
        holder.chatPic.setImageResource(R.mipmap.ic_launcher_round);
        if (imageURL.equals("default")) {
            holder.chatPic.setImageResource(R.drawable.default_user_icon);
        }
        else {
            Glide.with(context).load(imageURL).into(holder.chatPic);
        }

        if (position == chats.size() - 1) {
            if (chat.isIsseen()) {
                holder.textSeen.setVisibility(View.VISIBLE);
            } else {
                holder.textSeen.setVisibility(View.GONE);
            }
        } else {
            holder.textSeen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chats.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_RIGHT;
        } else {
            return MSG_LEFT;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView chatText;
        public ImageView chatPic;
        public ImageView textSeen;


        public ViewHolder(View itemView) {
            super(itemView);

            chatText = itemView.findViewById(R.id.chat_text);
            chatPic = itemView.findViewById(R.id.chat_pic);
            textSeen = itemView.findViewById(R.id.seen_image);
        }

    }
}