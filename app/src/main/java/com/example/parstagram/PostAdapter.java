package com.example.parstagram;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.ParseException;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<Post> posts;
    private Context context;
    private boolean useGridView;

    public PostAdapter(List<Post> posts, Context context, boolean useGridView) {
        this.posts = posts;
        this.context = context;
        this.useGridView = useGridView;
    }

    public void clear(){
        this.posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> posts){
        Log.d("TAG", "Posts: " + posts.size());
        this.posts.addAll(posts);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflating our layout for item of recycler view item.
        View view;
        if (useGridView) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_feed_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        }
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);
        try {
            holder.username.setText(post.getUser().fetchIfNeeded().getUsername());
            Picasso.get().load(post.getImage().getFile()).into(holder.postImage);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.postImageDescription.setText(post.getDescription());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private ImageView postImage;
        private TextView postImageDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            postImage = itemView.findViewById(R.id.postImage);
            postImageDescription = itemView.findViewById(R.id.postImageDescription);
        }
    }
}
