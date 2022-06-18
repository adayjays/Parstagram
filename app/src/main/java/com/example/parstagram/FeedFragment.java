package com.example.parstagram;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
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

    public FeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedFragment newInstance(String param1, String param2) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private boolean isInitialLoad = true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_feed, container, false);
        progressBar = root.findViewById(R.id.loading);
        swipeContainer = root.findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light));


        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "fetching new data!");
                _page = 1; // reset to first page
                posts = new ArrayList<>(); // clear display data
                queryPosts(); // fetch fresh data
            }
        });
        posts = new ArrayList<>(); // initialize the list to empty
        recyclerView = root.findViewById(R.id.feeds); // initialize the recyclerview control
        adapter = new PostAdapter(posts, getActivity(), false); // initialize the adapter

        // below line is for setting linear layout manager to our recycler view.
        linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);

        // below line is to set layout manager to our recycler view.
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter); // set the adapter to the recyclerview
        // set a listener for endofscroll activity
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            // upon end of scroll, it should refresh the data
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore" + page);
                _page ++;
                queryPosts();
            }
        };
        recyclerView.addOnScrollListener(scrollListener); // bind the onscrolllistener to the recyclerview
        recyclerView.setVisibility(View.GONE); // hide the recyclerview
        progressBar.setVisibility(View.VISIBLE); // show the progressbar
        queryPosts(); // make call to fetch data
        return root;
    }
    // method to query the data
    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.setLimit(_page * 20); // create the query and limit only first 20 elements depending on the page
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                progressBar.setVisibility(View.GONE);
                if (isInitialLoad)
                    recyclerView.setVisibility(View.VISIBLE);
                isInitialLoad = false;
                if (e != null) {
                    Log.e(TAG, "Issue getting posts");
                    Toast.makeText(getActivity(), "", Toast.LENGTH_LONG).show();
                    return;
                }


                // below line is to set adapter
                // to our recycler view.
                Log.e(TAG, "Posts fetched success1!");
                recyclerView.setAdapter(adapter);
                adapter.clear();
                adapter.addAll(posts);
                Log.e(TAG, "Posts fetched success2!");
                swipeContainer.setRefreshing(false);
            }
        });
    }
}