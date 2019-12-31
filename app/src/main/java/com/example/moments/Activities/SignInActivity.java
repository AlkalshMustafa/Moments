package com.example.moments.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.moments.Model.Moment;
import com.example.moments.Model.UserInfor;
import com.example.moments.R;
import com.example.moments.util.MomentsApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText signInEmail;
    private EditText signInPassWord;
    private Button   signInBtn;
    private Button   signUpBtn;

    private FirebaseAuth                   mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser                   mUser;


    private ProgressDialog   mProgressDialog;


    //=============== onCreate ==================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        signInEmail = (EditText) findViewById(R.id.signInEmailEdt);
        signInPassWord = (EditText) findViewById(R.id.signInPswEdt);
        signInBtn = (Button) findViewById(R.id.signInBtn);
        signUpBtn = (Button) findViewById(R.id.signInSignUpBtn);

        mAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);

        signInBtn.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);

        //=============== AuthStateListener ==================

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                mUser = firebaseAuth.getCurrentUser();  // to  check the state of auth. user is connecting or not.
                if (mUser != null) {
                    Toast.makeText(SignInActivity.this, "Signed In", Toast.LENGTH_SHORT).show();


                    startActivity(new Intent(SignInActivity.this, PostsListActivity.class));
                    finish();
                }else {
                    Toast.makeText(SignInActivity.this, "Not Signed In", Toast.LENGTH_SHORT).show();
                }
            }
        };

    }


    //=============== onStart ==================
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);  // to  check the state of auth. user is connecting or not.

    }


    //=============== onClick ==================
    @Override
    public void onClick(View view){

        switch (view.getId()){
            case R.id.signInBtn:

                if (!TextUtils.isEmpty(signInEmail.getText().toString())
                        && !TextUtils.isEmpty(signInPassWord.getText().toString())
                        && signInPassWord.length() >= 6) {

                    mProgressDialog.setMessage("Signing In...");
                    mProgressDialog.show();
                    String email = signInEmail.getText().toString();
                    String pwd = signInPassWord.getText().toString();

                    signIn(email, pwd);
                }else{
                    Toast.makeText(SignInActivity.this, "Please, Enter valid Email or Password.",
                            Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
                    break;
            case R.id.signInSignUpBtn:
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();

        }

    }

    //=============== SignIn Method ==================
    private void signIn(String email, String pwd) {
            mAuth.signInWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(SignInActivity.this, "Signed in",
                                        Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                //updateUI(user);
                                Intent intent = new Intent(SignInActivity.this,
                                        PostsListActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                mProgressDialog.dismiss();
                                Toast.makeText(SignInActivity.this, "SignIn Unsuccessful",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
}


