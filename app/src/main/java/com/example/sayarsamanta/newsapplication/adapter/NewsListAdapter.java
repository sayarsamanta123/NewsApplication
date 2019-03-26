package com.example.sayarsamanta.newsapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sayarsamanta.newsapplication.activity.NewsDetail;
import com.example.sayarsamanta.newsapplication.R;
import com.example.sayarsamanta.newsapplication.model.Post;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.NewsViewHolder> {
    private List<Post> postList=new ArrayList<>();
    Context context;
    private static Post post;
    public static Map<String, Object> cache = new HashMap<String, Object>();
    public NewsListAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    public NewsListAdapter(Context context){
        this.context=context;
    }

    public void addGridItemsToView(int position, Post data) {
        postList.add(position, data);
        internalNotifyItemInserted(position);
    }

    public void internalNotifyItemInserted(int position) {
        notifyItemInserted(position);
    }
    @Override
    public int getItemCount() {
        return postList.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final NewsViewHolder newsViewHolder, final int i) {


        final Post post=postList.get(i);

        newsViewHolder.textViewTitle.setText(post.newsTitle);
        if (post.newsContent.equals("null")){
            newsViewHolder.textViewContent.setText("");
        }
        else{
            newsViewHolder.textViewContent.setText(post.newsContent);
        }

        if (!post.newsSource.equals("null")){
            newsViewHolder.textViewSource.setText(post.newsSource);
        }

        newsViewHolder.textViewSource.setText(post.newsSource);
        Picasso.with(context).load(post.iamgeUrl).centerCrop().resize(100,100).placeholder(R.drawable.news_image).into(newsViewHolder.imageViewNews);

        newsViewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Post post1=postList.get(i);
                Intent intent=new Intent(view.getContext(),NewsDetail.class);
                intent.putExtra("title",post1.newsTitle);
                intent.putExtra("url",post1.newsUrl);
                view.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.news_list, viewGroup, false);
        return new NewsViewHolder(itemView);
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        View relativeLayout;
        ImageView imageViewNews;
        TextView textViewTitle,textViewContent,textViewSource;
        NewsViewHolder(View v) {
            super(v);
            relativeLayout=v.findViewById(R.id.newsRow);
            imageViewNews=v.findViewById(R.id.news_image);
            textViewTitle=v.findViewById(R.id.textView_news_title);
            textViewContent=v.findViewById(R.id.news_content);
            textViewSource=v.findViewById(R.id.news_source);

        }

    }
}
