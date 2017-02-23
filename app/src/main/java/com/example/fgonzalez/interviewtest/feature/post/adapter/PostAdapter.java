package com.example.fgonzalez.interviewtest.feature.post.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fgonzalez.domain.Post;
import com.example.fgonzalez.interviewtest.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

// Create the basic Post adapter extending from RecyclerView.Adapter
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    // Store a member variable for the Posts
    private List<Post> mPostList = new ArrayList<>();
    // Store the context for easy access
    private Context mContext;

    // General constructor
    public PostAdapter(Context context) {
        mContext = context;
    }

    public void setPostList(List<Post> postList) {
        this.mPostList = postList;
    }

    public Post getItemByPosition(int position) {
        return mPostList.get(position);
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.post_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(PostAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Post post = mPostList.get(position);

        // Set item views based on your views and data model
        Uri uri = Uri.parse(post.user.avatar);
        viewHolder.avatar.setImageURI(uri);
        viewHolder.title.setText(post.title);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.avatar)
        SimpleDraweeView avatar;
        @BindView(R.id.title)
        TextView title;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
