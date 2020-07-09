package com.example.map_pa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOError;
import java.io.IOException;
import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    String ID="", pw="";
    EditText idText, pwText;
    private DatabaseReference loginReference;

    OkHttpClient client;
    private static String naverAPIUrl = "https://openapi.naver.com/v1/search/blog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getIntent().getExtras() != null){
            EditText username = (EditText)findViewById(R.id.userid);
            Intent signupIntent = getIntent();
            username.setText(signupIntent.getStringExtra("Username"));
        }

        TextView kodeforces = (TextView) findViewById(R.id.Title);
        kodeforces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchRequest("PS 코드포스란?");
            }
        });

        Button login = (Button)findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idText = (EditText)findViewById(R.id.userid);
                pwText = (EditText)findViewById(R.id.password);

                loginReference = FirebaseDatabase.getInstance().getReference();

                ID = idText.getText().toString();
                pw = pwText.getText().toString();

                Query query = loginReference.child("pp/user_list/").orderByChild("ID").equalTo(ID);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String cmppw = dataSnapshot.child(ID).child("pw").getValue().toString();
                            if(pw.equals(cmppw)){
                                Intent loginIntent = new Intent(MainActivity.this, postPage.class);
                                loginIntent.putExtra("Username", dataSnapshot.child(ID).child("ID").getValue().toString());
                                loginIntent.putExtra("pw", dataSnapshot.child(ID).child("pw").getValue().toString());
                                loginIntent.putExtra("tier", dataSnapshot.child(ID).child("tier").getValue().toString());
                                loginIntent.putExtra("rating", dataSnapshot.child(ID).child("rating").getValue().toString());
                                loginIntent.putExtra("hasPicture", dataSnapshot.child(ID).child("hasPicture").getValue().toString());
                                startActivity(loginIntent);
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Wrong Password", Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Wrong Username", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

        TextView signup = (TextView)findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(MainActivity.this, SignUp.class);
                startActivity(signupIntent);
            }
        });
    }

    private void SearchRequest(String keyword){
        client = new OkHttpClient();
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", "tcQAGjIZpNPfG0cSbOXe");
        requestHeaders.put("X-Naver-Client-Secret", "9cq516kanK");

        GET(naverAPIUrl, requestHeaders, keyword);
    }

    private void GET(String url, Map<String, String> header, String keyword){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("query", keyword);
        urlBuilder.addQueryParameter("display", String.valueOf(1));
        String reqUrl = urlBuilder.build().toString();

        Headers headerBuild = Headers.of(header);
        Request request = new Request.Builder().url(reqUrl).headers(headerBuild).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String myResponse = response.body().string();

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, myResponse, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
