package com.example.duplom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.JsonReader;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private static final String USER_API_URL = "http://localhost:5000";

    /*public static void login(String email, String password) {
        OutputStream out = null;
        List<String> params = new ArrayList<>();
        params.add(email);
        params.add(password);
        try {
            URL userLog = new URL(USER_API_URL + "/api/user/login");
            HttpsURLConnection myConnection = (HttpsURLConnection) userLog.openConnection();
            if (myConnection.getResponseCode() == 200) {
                out = new BufferedOutputStream(myConnection.getOutputStream());

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(String.valueOf(params));
                writer.flush();
                writer.close();
                out.close();
                myConnection.connect();

            }
        } catch (IOException e) {
            throw new RuntimeException();
        }

    }*/

    public MainActivity() throws IOException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView username = (TextView) findViewById(R.id.username);
        TextView password = (TextView) findViewById(R.id.password);

        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        MaterialButton loginbtn = (MaterialButton) findViewById(R.id.loginbtn);



        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getText().toString().equals("admin") && password.getText().toString().equals("admin")){
                    Intent myIntent = new Intent(view.getContext(), MapActivity.class);
                    startActivity(myIntent);
                }
                else{
                    //incorrect
                    Toast.makeText(MainActivity.this,"LOGFAILED",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}