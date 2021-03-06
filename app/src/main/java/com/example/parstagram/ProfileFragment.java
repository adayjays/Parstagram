package com.example.parstagram;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    SwipeRefreshLayout swipeContainer;
    private static final String TAG = "ProfileFragment";
    EndlessRecyclerViewScrollListener scrollListener;
    GridLayoutManager gridLayoutManager;
    public static final int REQUEST_CODE = 20;
    List<Post> posts = new ArrayList<>();
    PostAdapter adapter;
    int _page = 1;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

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
                _page = 1;
                posts = new ArrayList<>();
                queryPosts();
            }
        });
        posts = new ArrayList<>();
        recyclerView = root.findViewById(R.id.feeds);
        adapter = new PostAdapter(posts, getActivity(), true);

        // below line is for setting linear layout manager to our recycler view.
        gridLayoutManager = new GridLayoutManager(getActivity(), 4);

        // below line is to set layout manager to our recycler view.
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore" + page);
                _page ++;
                queryPosts();
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
        queryPosts();
        return root;
    }
    //private boolean isInitialLoad = true;
    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.setLimit(_page * 20);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue getting posts");
                    Toast.makeText(getActivity(), "", Toast.LENGTH_LONG).show();
                    return;
                }

                Log.e(TAG, "Posts fetched success!");

                // below line is to set adapter
                // to our recycler view.
                recyclerView.setAdapter(adapter);
                adapter.clear();
                adapter.addAll(posts);
                recyclerView.getAdapter().notifyDataSetChanged();
                Log.e(TAG, "Posts fetched success2!");
                swipeContainer.setRefreshing(false);
            }
        });
    }
}