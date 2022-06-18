package com.example.parstagram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity  extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText etUsername; // username
    private EditText etPassword; // password
    private Button btnLogin; // login button
    private ProgressBar progressBar; // indeterminate progress bar
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // setup and display the login page
        // if the user is already logged in
        if (ParseUser.getCurrentUser() != null) {
            // abandon login page and go to main activity
            gotoMainActivity();
        }
        // otherwise, setup the login page
        progressBar = findViewById(R.id.loading); // initialize progress bar control
        etUsername = findViewById(R.id.username); // initialize username control
        etPassword = findViewById(R.id.password); // initialize password control
        btnLogin = findViewById(R.id.login); // initialize login button control
        // setup an onclick listener to fire whenever the login button is pressed (clicked)
        btnLogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String username = etUsername.getText().toString(); // get the entered username
                        String password = etPassword.getText().toString(); // get the entered password
                        // if either username or password is empty
                        if (username.isEmpty() || password.isEmpty()) {
                            // inform the user
                            Toast.makeText(LoginActivity.this, "Username and password are required", Toast.LENGTH_LONG).show();
                            // stop execution
                            return;
                        }
                        // otherwise, continue to process the login request
                        loginUser(username, password);
                    }
                }
        );
    }
    // method to log the user in
    private void loginUser(String username, String password) {
        progressBar.setVisibility(View.VISIBLE); // show the progress bar
        Log.i(TAG, "Attempting to login user " + username);
        // run the process in the background and wait for completion
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                // hide the progress back, i.e. when the login process is complete
                progressBar.setVisibility(View.GONE);
                // check if there's an error during login
                if (e != null) {
                    // report to the user if there was
                    Log.e(TAG, "Issue with login", e);
                    Toast.makeText(LoginActivity.this, "Issue with login. " + e.getMessage(), Toast.LENGTH_LONG).show();
                    // stop execution
                    return;
                }
                // otherwise, alert the user of the success in login
                Toast.makeText(LoginActivity.this, "Login success!", Toast.LENGTH_LONG).show();
                // proceed to main activity
                gotoMainActivity();
            }
        });
    }
    // method to take user to the main activity
    private void gotoMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
