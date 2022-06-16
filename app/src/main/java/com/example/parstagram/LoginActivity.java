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
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (ParseUser.getCurrentUser() != null) {
            goToListActivity();
        }
        progressBar = findViewById(R.id.loading);
        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.login);
        btnLogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String username = etUsername.getText().toString();
                        String password = etPassword.getText().toString();
                        if (username.isEmpty() || password.isEmpty()) {
                            Toast.makeText(LoginActivity.this, "Username and password are required", Toast.LENGTH_LONG).show();
                            return;
                        }
                        loginUser(username, password);
                    }
                }
        );
    }

    private void loginUser(String username, String password) {
        progressBar.setVisibility(View.VISIBLE);
        Log.i(TAG, "Attempting to login user " + username);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                progressBar.setVisibility(View.GONE);
                if (e != null) {
                    Log.e(TAG, "Issue with login", e);
                    Toast.makeText(LoginActivity.this, "Issue with login. " + e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                goToListActivity();
                Toast.makeText(LoginActivity.this, "Login success!", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void goToListActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
