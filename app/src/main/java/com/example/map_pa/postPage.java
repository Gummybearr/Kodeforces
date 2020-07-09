package com.example.map_pa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class postPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    String username, pw, tier, rating, hasPicture;
    private static final int PICK_IMAGE = 777;
    Uri currentImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_page);

        if(getIntent().getExtras() != null){
            Intent signupIntent = getIntent();
            username = signupIntent.getStringExtra("Username");
            pw = signupIntent.getStringExtra("pw");
            tier = signupIntent.getStringExtra("tier");
            rating = signupIntent.getStringExtra("rating");
            hasPicture = signupIntent.getStringExtra("hasPicture");
        }

        ImageButton newPost = (ImageButton)findViewById(R.id.newPost);
        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newPostIntent = new Intent(postPage.this, NewPost.class);
                newPostIntent.putExtra("Username", username);
                newPostIntent.putExtra("pw", pw);
                newPostIntent.putExtra("tier", tier);
                newPostIntent.putExtra("rating", rating);
                newPostIntent.putExtra("hasPicture", hasPicture);
                startActivity(newPostIntent);
            }
        });

        /* to use toolbar,
        1. add implementation
                implementation 'com.android.support:appcompat-v7:29.0.3'
           into build.gradle(Module:app)  !!!! version is same with buildToolsversion

        2. add toolbar in your layout
        3. Set toolbar when onCreate (you must import androidx.appcompat.widget.Toolbar
        4. If not Refactor -> Migrate to Androidx
         */
        Toolbar tb = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ViewPager2 viewPager2 = findViewById(R.id.viewpager);
        viewPager2.setAdapter(new myFragmentStateAdapter(this, username));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.TabLayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setText("Personal");
                        break;
                    case 1:
                        tab.setText("Public");
                        break;
                }
            }
        });
        tabLayoutMediator.attach();


        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.drawer);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.drawer_username);
        navUsername.setText(username);

        Menu navMenu = navigationView.getMenu();
        MenuItem navFullname = navMenu.findItem(R.id.navigationFullname);
        navFullname.setTitle(tier);
        MenuItem navBirthday = navMenu.findItem(R.id.navigationBirthday);
        navBirthday.setTitle(rating);
//        MenuItem navEmail = navMenu.findItem(R.id.navigationEmail);
//        navEmail.setTitle(email);

        ImageView profileImage = (ImageView)headerView.findViewById(R.id.drawer_user_image);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });

        if(hasPicture.equals("true")){
            updatePic();
        }

        ImageView shareTwitter = (ImageView)headerView.findViewById(R.id.drawer_share_twitter);
        shareTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String twitterURL = "";
                twitterURL = String.format("http://twitter.com/intent/tweet?text=%s",
                        URLEncoder.encode(username+"\n"+tier+"\n"+rating));
                Intent twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterURL));
                startActivity(twitterIntent);
            }
        });

        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, tb, R.string.app_name, R.string.app_name);
        drawerToggle.syncState();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()){
            case R.id.navigationBirthday:
                break;
            case R.id.navigationFullname:
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE){
            NavigationView navigationView = (NavigationView) findViewById(R.id.drawer);
            View headerView = navigationView.getHeaderView(0);
            ImageView img = (ImageView)headerView.findViewById(R.id.drawer_user_image);
            if(data!=null){
                currentImageUri = data.getData();
                img.setImageURI(data.getData());
                StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("pp/profileImages/");
                StorageReference ref = mStorageRef.child(username+".jpg");
                UploadTask uploadTask = ref.putFile(currentImageUri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(postPage.this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        hasPicture = "true";
                        postFirebaseDatabaseProfile(true);
                        Toast.makeText(postPage.this, "Upload success!", Toast.LENGTH_LONG).show();
//                        updatePic();
                    }
                });
            }
        }
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

    public void postFirebaseDatabaseProfile(boolean add){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            RegisterUser post = new RegisterUser(username, pw, tier, rating, hasPicture);
            postValues = post.toMap();
        }
        childUpdates.put("pp/user_list/"+username, postValues);
        DatabaseReference registerReference = FirebaseDatabase.getInstance().getReference();
        registerReference.updateChildren(childUpdates);
    }
}
