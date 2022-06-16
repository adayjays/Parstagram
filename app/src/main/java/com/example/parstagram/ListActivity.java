package com.example.parstagram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private static final String TAG = "MainActivity";
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;
    LinearLayoutManager linearLayoutManager;
    public static final int REQUEST_CODE = 20;
    List<Post> posts = new ArrayList<>();
    PostAdapter adapter;
    int _page = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        progressBar = findViewById(R.id.loading);
        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light));
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "fetching new data!");
                _page = 0;
                posts = new ArrayList<>();
                queryPosts();
            }
        });
        recyclerView = findViewById(R.id.feeds);
        adapter = new PostAdapter(posts, this, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore" + page);
                _page ++;
                queryPosts();
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
        queryPosts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            ParseUser.logOut();
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            queryPosts();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void queryPosts() {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.setLimit(_page * 20);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                recyclerView.setVisibility(View.VISIBLE);
                if (e != null) {
                    Log.e(TAG, "Issue getting posts");
                    Toast.makeText(ListActivity.this, "", Toast.LENGTH_LONG).show();
                    return;
                }
                adapter = new PostAdapter(posts, ListActivity.this, false);

                // below line is for setting linear layout manager to our recycler view.
                linearLayoutManager = new LinearLayoutManager(ListActivity.this, RecyclerView.VERTICAL, false);

                // below line is to set layout manager to our recycler view.
                recyclerView.setLayoutManager(linearLayoutManager);

                // below line is to set adapter
                // to our recycler view.
                recyclerView.setAdapter(adapter);
                swipeContainer.setRefreshing(false);
            }
        });
    }
}