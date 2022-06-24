package com.example.parstagram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComposeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "MainActivity";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private EditText etDescription;
    private Button btnCaptureImage;
    private ImageView ivPostImage;
    private ProgressBar progressBar;
    private Button btnSubmit;
    private File photoFile;
    public String photoFileName = "photo.jpg";
    private String mParam1;
    private String mParam2;

    public ComposeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ComposeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComposeFragment newInstance(String param1, String param2) {
        ComposeFragment fragment = new ComposeFragment();
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
        View root = inflater.inflate(R.layout.fragment_capture, container, false);

        etDescription = root.findViewById(R.id.description); // initialize the post description control
        ivPostImage = root.findViewById(R.id.camera_image); // initialize the imageview control
        btnCaptureImage = root.findViewById(R.id.take_picture); // intialize the capture image button
        progressBar = root.findViewById(R.id.loading); // initialize the progress bar control
        // set listener to the capture image button (click)
        btnCaptureImage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        launchCamera();
                    }
                }
        );
        // initialize the submit button control
        btnSubmit = root.findViewById(R.id.submit);
        // set onclicklistener to the submit button control
        btnSubmit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String description = etDescription.getText().toString();
                        if (description.isEmpty()) {
                            Toast.makeText(getActivity(), "Description cannot be empty", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (photoFile == null || ivPostImage.getDrawable() == null) {
                            Toast.makeText(getActivity(), "Image cannot be empty", Toast.LENGTH_LONG).show();
                            return;
                        }
                        ParseUser currentUser = ParseUser.getCurrentUser();
                        savePost(description, currentUser, photoFile);
                    }
                }
        );
        return root;
    }
    public void setImage(Bitmap takenImage) {
        ivPostImage.setImageBitmap(takenImage);
    }
    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }
    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);
        ((MainActivity)getActivity()).setPhotoFile(photoFile);
        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getActivity(), "com.example.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            getActivity().startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }
    // method to submit a post to the API
    private void savePost(String description, ParseUser currentUser, File photoFile) {
        progressBar.setVisibility(View.VISIBLE); // display the progress bar
        // initialize a new post object and set it's associated parameters
        Post post = new Post();
        post.setDescription(description);
        post.setImage(new ParseFile(photoFile));
        post.setUser(currentUser);
        // run process in the background
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                // hide the progress bar upon completion
                progressBar.setVisibility(View.GONE);
                // check for error during execution of the same
                if (e != null) {
                    // alert the user if there was one
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getActivity(), "Error while saving", Toast.LENGTH_LONG).show();
                    // stop execution
                    return;
                }
                // otherwise alert the user of the success and clear the form
                Log.i(TAG, "Post was successfully saved");
                etDescription.getText().clear();
                ivPostImage.setImageResource(0);
                Toast.makeText(getActivity(), "Post was successfully saved", Toast.LENGTH_LONG).show();
            }
        });
    }
}