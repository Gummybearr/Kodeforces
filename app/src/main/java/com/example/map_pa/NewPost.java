package com.example.map_pa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.Map;

public class NewPost extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    String username="", pw="", tier="", rating="", hasPicture="", imageCheck="false";
    DrawerLayout drawerLayout;
    private static final int PICK_IMAGE = 778;
    Uri currentImageUri;
    EditText postContentText, postTagText;
    String postContent="", postTag="";
    int publicCheck = 0;
    String privateOrPersonal[] = {"private", "public"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        if(getIntent().getExtras() != null){
            Intent signupIntent = getIntent();
            username = signupIntent.getStringExtra("Username");
            pw = signupIntent.getStringExtra("pw");
            tier = signupIntent.getStringExtra("tier");
            rating = signupIntent.getStringExtra("rating");
            hasPicture = signupIntent.getStringExtra("hasPicture");
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navView = (NavigationView) findViewById(R.id.drawer);
        View headerView = navView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.drawer_username);
        navUsername.setText(username);

        Menu navMenu = navView.getMenu();
        MenuItem navFullname = navMenu.findItem(R.id.navigationFullname);
        navFullname.setTitle(tier);
        MenuItem navBirthday = navMenu.findItem(R.id.navigationBirthday);
        navBirthday.setTitle(rating);
//        MenuItem navEmail = navMenu.findItem(R.id.navigationEmail);
//        navEmail.setTitle(email);

        if(hasPicture.equals("true")){
            updatePic();
        }

        Button createPost = (Button)findViewById(R.id.createPost);
        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postContentText = (EditText)findViewById(R.id.postContent);
                postContent = postContentText.getText().toString();

                if(postContent.length()==0){
                    Toast.makeText(NewPost.this, "Please input contents", Toast.LENGTH_LONG).show();
                }
                else{
                    CheckBox postCheckBox = (CheckBox)findViewById(R.id.publicPost);
                    if(postCheckBox.isChecked()){
                        publicCheck = 1;
                    }

                    postFirebaseDatabase(true);

                    Intent createPostIntent = new Intent(NewPost.this, postPage.class);
                    createPostIntent.putExtra("Username", username);
                    createPostIntent.putExtra("pw", pw);
                    createPostIntent.putExtra("tier", tier);
                    createPostIntent.putExtra("rating", rating);
                    createPostIntent.putExtra("hasPicture", hasPicture);
                    startActivity(createPostIntent);
                }
            }
        });

        Toolbar tb = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.drawer);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, tb, R.string.app_name, R.string.app_name);
        drawerToggle.syncState();

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        closeDrawer();

        switch (item.getItemId()){
            case R.id.navigationBirthday:
                break;

            case R.id.navigationFullname:
                break;
        }


        return false;
    }

    private void closeDrawer(){
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void openDrawer(){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            closeDrawer();
        }
        super.onBackPressed();
    }

    public void updatePic(){
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("pp/profileImages/");
        StorageReference ref = mStorageRef.child(username+".jpg");
        final long ONE_MEGABYTE = 1024*1024;
        ref.getBytes(ONE_MEGABYTE).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                NavigationView navigationView = (NavigationView) findViewById(R.id.drawer);
                View headerView = navigationView.getHeaderView(0);
                ImageView profileImage = (ImageView)headerView.findViewById(R.id.drawer_user_image);
                profileImage.setImageBitmap(bitmap);
            }
        });
    }

    public void postFirebaseDatabase(boolean add){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            Metadata post = new Metadata(username, postContent, tier, imageCheck);
            postValues = post.toMap();
        }
        if(publicCheck==1){
            childUpdates.put("pp/"+privateOrPersonal[publicCheck]+"/"+postContent+"/", postValues);
        }
        else{
            childUpdates.put("pp/"+privateOrPersonal[publicCheck]+"/"+username+"/"+postContent+"/", postValues);
        }
        DatabaseReference newPostReference = FirebaseDatabase.getInstance().getReference();
        newPostReference.updateChildren(childUpdates);
        Toast.makeText(NewPost.this, "Post success!", Toast.LENGTH_LONG).show();
    }
}
