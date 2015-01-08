package com.sinch.messagingtutorial.app;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.parse.LogInCallback;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

public class LoginActivity extends Activity {

    private Button signUpButton;
    private Button loginButton;
    private EditText usernameField;
    private EditText passwordField;
    private String username;
    private String password;
    private Intent intent;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());

        class RegisterGcmTask extends AsyncTask<Void, Void, String> {

            String msg = "";

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    msg = gcm.register("your-project-number");
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                intent = new Intent(getApplicationContext(), ListUsersActivity.class);
                serviceIntent = new Intent(getApplicationContext(), MessageService.class);

                serviceIntent.putExtra("regId", msg);

                startActivity(intent);
                startService(serviceIntent);
            }
        }

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            (new RegisterGcmTask()).execute();
        }

        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.loginButton);
        signUpButton = (Button) findViewById(R.id.signupButton);
        usernameField = (EditText) findViewById(R.id.loginUsername);
        passwordField = (EditText) findViewById(R.id.loginPassword);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameField.getText().toString();
                password = passwordField.getText().toString();

                ParseUser.logInInBackground(username, password, new LogInCallback() {
                    public void done(ParseUser user, com.parse.ParseException e) {
                        if (user != null) {
                            (new RegisterGcmTask()).execute();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                "Wrong username/password combo",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                username = usernameField.getText().toString();
                password = passwordField.getText().toString();

                ParseUser user = new ParseUser();
                user.setUsername(username);
                user.setPassword(password);

                user.signUpInBackground(new SignUpCallback() {
                    public void done(com.parse.ParseException e) {
                        if (e == null) {
                            (new RegisterGcmTask()).execute();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                "There was an error signing up."
                                    , Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, MessageService.class));
        super.onDestroy();
    }
}
