package com.example.parstagram;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private File photoFile;
    private ComposeFragment composeFragment;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setup the main layout
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostsFragment()).commit();
    }
    // public method to be called by child fragments
    public void setPhotoFile(File photoFile) {
        this.photoFile = photoFile;
    }

    // method to report the result of the image capture activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if the request code is the one for capture image via camera
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            // if the result is ok
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                composeFragment.setImage(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override // method to create menu items (top bar)
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override // method to listen to menu item clicks
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // if logout button is tapped
        if (id == R.id.action_logout) {
            ParseUser.logOut(); // log the person out from the api
            this.finish(); // exit the application
        } else if (id == R.id.action_compose) { // if compose button is tapped
            composeFragment = new ComposeFragment(); // initialize capture fragement
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, composeFragment).commit(); // display the fragment
        }

        return super.onOptionsItemSelected(item);
    }
    // method to listen to bottom navigation click actions
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        // By using switch we can easily get
        // the selected fragment
        // by using there id.
        Fragment selectedFragment = null;
        // get the tapped menu item by id
        switch (item.getItemId()) {
            case R.id.feeds: // if it's the feeds menu item
                selectedFragment = new PostsFragment(); // select the feeds fragment for display
                break;
            case R.id.capture: // if it's the capture menu item
                selectedFragment = new ComposeFragment(); // select the capture fragment for display
                composeFragment = (ComposeFragment) selectedFragment; // keep this for later use whenever we return from capture activity (camera)
                break;
            case R.id.profile: // if it's the profile menu item
                selectedFragment = new ProfileFragment(); // select the profile fragment for display
                break;
        }
        // if we have the selected fragment
        if (selectedFragment != null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit(); // display it
        return true;
    };
}