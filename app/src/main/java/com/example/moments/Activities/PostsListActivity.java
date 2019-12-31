package com.example.moments.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.moments.Data.MomentRecyclerAdapter;
import com.example.moments.Model.Moment;
import com.example.moments.Model.UserInfor;
import com.example.moments.R;
import com.example.moments.util.MomentsApi;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostsListActivity extends AppCompatActivity implements View.OnClickListener {

    // ========== PopUp ==========
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    // ========== FireBase ==========
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mUserReference;
    private DatabaseReference mUserInfo;

    private StorageReference mStorage;

    // ========== the rest ==========
    private ProgressDialog mProgressDialog;
    private RecyclerView recyclerView;
    private MomentRecyclerAdapter momentRecyclerAdapter;
    private List<Moment> postList;


    private ImageButton postImage;
    private EditText    postTitle;
    private EditText    postDescription;
    private Button      shareBtn;
    private Button      cancelBtn;


    private Uri mImageUri;
    private static final int GALLERY_CODE = 1;


    // ==================  onCreate =================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_list);

        // get user reference
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // get DataBase reference
        // get child reference
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = mDatabase.getReference().child("MPosts");



        mUserInfo = FirebaseDatabase.getInstance().getReference();
        mUserReference = mUserInfo.child("MUsers").child(mUser.getUid());


        mDatabaseReference.keepSynced(true);
        mProgressDialog = new ProgressDialog(this);
        postList = new ArrayList<>();


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button personalPageBtn   = findViewById(R.id.personalPageBtnID);
        Button signOutBtn        = findViewById(R.id.homeSignOutBtnID);
        FloatingActionButton fab = findViewById(R.id.homeFabAddPost);


        personalPageBtn.setOnClickListener(this);
        fab.setOnClickListener(this);
        signOutBtn.setOnClickListener(this);



    }


    // ==================  onClick  =================
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.homeFabAddPost:
                addPostPopUp();
                break;

            case R.id.personalPageBtnID:
                Intent intent1 = new Intent(PostsListActivity.this, PersonalPageActivity.class);
                startActivity(intent1);
                finish();
                break;

            case R.id.homeSignOutBtnID:
                mAuth.signOut();
                mProgressDialog.setMessage("Signing Out...");
                mProgressDialog.show();
                Intent intent2 = new Intent(PostsListActivity.this, SignInActivity.class);
                startActivity(intent2);
                finish();
                break;


            //============= popUp Clicks ==============

            case R.id.popUpImgBtnID:
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
                break;

            case R.id.popUpShareBtnID:
                posting();
                break;

            case R.id.popUpcancelBtnID:

                postTitle.setText(" ");
                postDescription.setText(" ");
                dialog.dismiss();

        }
    }


    // ================== upload data to fire base =================
    private void posting() {

        final String titleVal = postTitle.getText().toString().trim();
        final String descVal = postDescription.getText().toString().trim();

        mProgressDialog.show();

        if (!TextUtils.isEmpty(titleVal) && !TextUtils.isEmpty(descVal)) {

            if (mImageUri != null) {
                StorageReference filepath = mStorage.child("MPosts_images").
                        child(mImageUri.getLastPathSegment());

                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String link = uri.toString();

                                MomentsApi momentsApi = MomentsApi.getInstance();

                                DatabaseReference newPost = mDatabaseReference.push();
                                final Map<String, String> dataToSave = new HashMap<>();

                                dataToSave.put("userName", momentsApi.getUserName());
                                dataToSave.put("profileImgUrl", momentsApi.getProfileImgUrl());
                                dataToSave.put("image", link);
                                dataToSave.put("title", titleVal);
                                dataToSave.put("description", descVal);

                                dataToSave.put("timestamp", String.valueOf(System.currentTimeMillis()));
                                dataToSave.put("userId", mUser.getUid());

                                newPost.setValue(dataToSave);
                            }
                        });
                    }
                });
            }else{

                MomentsApi momentsApi = MomentsApi.getInstance();
                DatabaseReference newPost = mDatabaseReference.push();
                final Map<String, String> dataToSave = new HashMap<>();

                dataToSave.put("userName", momentsApi.getUserName());
                dataToSave.put("profileImgUrl", momentsApi.getProfileImgUrl());
                dataToSave.put("image", "null");
                dataToSave.put("title", titleVal);
                dataToSave.put("description", descVal);

                dataToSave.put("timestamp", String.valueOf(System.currentTimeMillis()));
                dataToSave.put("userId", mUser.getUid());

                newPost.setValue(dataToSave);
            }

            mProgressDialog.dismiss();
            dialog.dismiss();

        }else{
            mProgressDialog.dismiss();
            Toast.makeText(getApplicationContext(),"Insert Data..", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }


    // ================== Add Post  =================
    public void addPostPopUp(){

        //create an AlertDialog
        dialogBuilder = new AlertDialog.Builder(this);                  // انستينشيت للدايلوك بلدر
        View view = getLayoutInflater().inflate(R.layout.popp, null);

        postImage   = (ImageButton) view.findViewById(R.id.popUpImgBtnID);
        postTitle   = (EditText) view.findViewById(R.id.popUpEdtTitleID);
        postDescription = (EditText) view.findViewById(R.id.popUpEdtDescID);
        shareBtn    = (Button) view.findViewById(R.id.popUpShareBtnID);
        cancelBtn   = (Button) view.findViewById(R.id.popUpcancelBtnID);


        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        cancelBtn.setOnClickListener(this);
        shareBtn.setOnClickListener(this);
        postImage.setOnClickListener(this);

    }


    //==================  Activity result for Image Button  =================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            postImage.setImageURI(mImageUri);


        }
    }


     // ==================  onStart =================
    @Override
    protected void onStart() {
        super.onStart();

        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UserInfor userInfor = dataSnapshot.getValue(UserInfor.class);
                if((userInfor != null)){

                    MomentsApi momentsApi = MomentsApi.getInstance();
                    momentsApi.setUserName(userInfor.getFullUserName());
                    momentsApi.setProfileImgUrl(userInfor.getProfilePhotoUrl());
                    momentsApi.setUserId(mUser.getUid());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                // mapped to Diary class
                Moment moment = dataSnapshot.getValue(Moment.class);
                // adding to adapter
                postList.add(moment);
                Collections.reverse(postList);

                momentRecyclerAdapter = new MomentRecyclerAdapter(PostsListActivity.this, postList);
                recyclerView.setAdapter(momentRecyclerAdapter);
                momentRecyclerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
