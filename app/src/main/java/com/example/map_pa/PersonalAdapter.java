package com.example.map_pa;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class PersonalAdapter extends RecyclerView.Adapter<PersonalAdapter.PersonalViewHolder> {

    private ArrayList<Metadata> arrayList;
    private Context context;

    PersonalAdapter(ArrayList<Metadata> arrayList, Context context){
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public PersonalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        PersonalViewHolder holder = new PersonalViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PersonalViewHolder holder, int position) {
        final String username = arrayList.get(position).username;
        String content = arrayList.get(position).Content;
        String tier = arrayList.get(position).tier;

        holder.profileUser.setText(username);
        holder.content.setText(content);
        String colorval = getcolor(tier);
        holder.tier.setText(tier);
        holder.tier.setTextColor(Color.parseColor(colorval));

        DatabaseReference mStorageRef = FirebaseDatabase.getInstance().getReference();
        Query query = mStorageRef.child("pp/user_list/").orderByChild("ID").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String hasPicture = dataSnapshot.child(username).child("hasPicture").getValue().toString();
                    if (hasPicture.equals("true")) {
                        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("pp/profileImages/");
                        StorageReference ref = mStorageRef.child(username + ".jpg");
                        final long ONE_MEGABYTE = 1024 * 1024;
                        ref.getBytes(ONE_MEGABYTE).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) { }
                        }).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                holder.profileImage.setImageBitmap(bitmap);
                            }
                        });
                    } else { }
                } else { }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList!=null?arrayList.size():0;
    }

    public class PersonalViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImage;
        TextView profileUser, tier, content;

        public PersonalViewHolder(@NonNull View itemView) {
            super(itemView);
            this.profileUser = itemView.findViewById(R.id.profileUsername);
            this.profileImage = itemView.findViewById(R.id.profilePicture);
            this.tier = itemView.findViewById(R.id.postTier);
            this.content = itemView.findViewById(R.id.postContent);
        }
    }

    String getcolor(String qtier){
        if(qtier.equals("legendary grandmaster")){
            return "#000000";
        }
        else if(qtier.equals("international grandmaster")){
            return "#ff0000";
        }
        else if (qtier.equals("grandmaster")){
            return "#ff0000";
        }
        else if (qtier.equals("international master")){
            return "#ff8c00";
        }
        else if (qtier.equals("master")){
            return "#ff8c00";
        }
        else if (qtier.equals("candidate master")){
            return "#aa00aa";
        }
        else if (qtier.equals("expert")){
            return "#0000ff";
        }
        else if (qtier.equals("specialist")){
            return "#03a8ab";
        }
        else if (qtier.equals("pupil")){
            return "#008000";
        }
        else if (qtier.equals("newbie")){
            return "#808080";
        }
        return "#03a8ab";
    }
}
