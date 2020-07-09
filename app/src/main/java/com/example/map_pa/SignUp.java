package com.example.map_pa;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private DatabaseReference registerReference;
    String ID="", pw="", handle, tier = "", rating ="", hasPicture="false";
    EditText idText, pwText, handleText, tierText, ratingText;

    Document doc = null;
    String testing = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button login = (Button)findViewById(R.id.signupButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                idText = (EditText)findViewById(R.id.signupUsername);
                pwText = (EditText)findViewById(R.id.signupPassword);

                registerReference = FirebaseDatabase.getInstance().getReference();

                ID = idText.getText().toString();
                pw = pwText.getText().toString();
                tier = "N/A";
                rating = "0";
                handleText = (EditText)findViewById(R.id.signupTier);
                handle = handleText.getText().toString();
                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute();
//                if(ID.length()*pw.length()*tier.length()*rating.length()==0){
//                    Toast.makeText(SignUp.this, "Please fill all blanks", Toast.LENGTH_LONG).show();
//                }
//
//                else{
//                    Query query = registerReference.child("pp/user_list/").orderByChild("ID").equalTo(ID);
//                    query.addListenerForSingleValueEvent(new ValueEventListener() {
//
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            if(dataSnapshot.exists()){
//                                Toast.makeText(SignUp.this, "Please use another username", Toast.LENGTH_LONG).show();
//                            }
//                            else{
//                                postFirebaseDatabase(true);
//                                EditText username = (EditText)findViewById(R.id.signupUsername);
//                                Intent signupIntent = new Intent(SignUp.this, MainActivity.class);
//                                signupIntent.putExtra("Username", username.getText().toString());
//                                startActivity(signupIntent);
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//
//                    });
//                }
            }
        });
    }

    public void postFirebaseDatabase(boolean add){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            RegisterUser post = new RegisterUser(ID, pw, tier, rating, hasPicture);
            postValues = post.toMap();
        }
        childUpdates.put("pp/user_list/"+ID, postValues);
        registerReference.updateChildren(childUpdates);
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect("https://www.codeforces.com/profile/"+handle).get();
                Elements userdata = doc.select("div[id=body]").select("div").select("div[id=pageContent]").select("div[class=roundbox]").select("div[class=userbox]").select("div[class=info]").select("ul").select("li").select("span[class=smaller]");
                String usertier = userdata.select("span").text();
                int flag = 0;
                for(int i = 0;i<usertier.length();i++){
                    if(usertier.charAt(i)==')'){
                        flag = 1;
                    }
                    else if(flag==1){
                        flag =2;
                    }
                    else if(flag==2){
                        if(usertier.charAt(i)==','){
                            flag = 3;
                        }
                        else{
                            if(tier.equals("N/A")){
                                tier = "";
                            }
                            tier = tier+usertier.charAt(i);
                        }
                    }
                    else if (flag==3){
                        flag = 4;
                    }
                    else if (flag==4){
                        if(rating.equals("0")){
                            rating = "";
                        }
                        rating = rating+usertier.charAt(i);
                    }
                }
                Log.e("alskdjfl;kasjdflka", tier);
                Log.e("asdfasdfasd", rating);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Query query = registerReference.child("pp/user_list/").orderByChild("ID").equalTo(ID);
            query.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        Toast.makeText(SignUp.this, "Please use another username", Toast.LENGTH_LONG).show();
                    }
                    else{
                        postFirebaseDatabase(true);
                        EditText username = (EditText)findViewById(R.id.signupUsername);
                        Intent signupIntent = new Intent(SignUp.this, MainActivity.class);
                        signupIntent.putExtra("Username", username.getText().toString());
                        startActivity(signupIntent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }
    }
}