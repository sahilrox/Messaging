package com.example.messaging.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.messaging.MessageActivity;
import com.example.messaging.R;
import com.example.messaging.UserProfile;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<UserProfile> users;

    public UserAdapter(Context context, List<UserProfile> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final UserProfile userProfile = users.get(position);
        holder.userName.setText(userProfile.getName());
        if (userProfile.getImageURL().equals("default")) {
            holder.profileImage.setImageResource(R.drawable.default_user_icon);
        }
        else {
            Glide.with(context).load(userProfile.getImageURL()).into(holder.profileImage);
        }

        holder.tag.setText("#".concat(userProfile.getTag()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("userid",userProfile.getUid());
                intent.putExtra("name",userProfile.getName());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userName, tag;
        public ImageView profileImage;


        public ViewHolder(View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_name);
            profileImage = itemView.findViewById(R.id.profile_image);
            tag = itemView.findViewById(R.id.user_tag);

        }

    }
}
