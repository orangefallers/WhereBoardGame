package com.ofcat.whereboardgame.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ofcat.whereboardgame.R;

/**
 * 測試用Activity
 *
 * Created by orangefaller on 2017/2/11.
 */

public class LoginActivity extends AppCompatActivity {

    private final String TAG = LoginActivity.class.getSimpleName();

    private FirebaseAuth auth;

    private EditText etAccount;
    private EditText etPassword;

    private Button btnConfirm;
    private Button btnLogout;

    private FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
//                Log.i(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                Log.i(TAG, "onAuthStateChanged:signed_in:" + user.getEmail());
            } else {
                // User is signed out
                Log.i(TAG, "onAuthStateChanged:signed_out");
            }
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String account = etAccount.getText().toString();
            String password = etPassword.getText().toString();

            if (account.trim().equals("") || password.trim().equals("")) {
                return;
            }

            auth.signInWithEmailAndPassword(account, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


//                            Log.i(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
//                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                Toast.makeText(LoginActivity.this, "auth_failed",
                                        Toast.LENGTH_LONG).show();
                            }

                        }
                    });
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        initView();
    }

    private void initView() {
        etAccount = (EditText) findViewById(R.id.et_login_account);
        etPassword = (EditText) findViewById(R.id.et_login_password);

        btnConfirm = (Button) findViewById(R.id.btn_login_confirm);
        btnLogout = (Button) findViewById(R.id.btn_login_logout);
        btnConfirm.setOnClickListener(clickListener);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (auth != null) {
                    auth.signOut();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }
}
