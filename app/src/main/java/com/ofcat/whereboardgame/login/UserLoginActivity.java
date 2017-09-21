package com.ofcat.whereboardgame.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ofcat.whereboardgame.R;
import com.ofcat.whereboardgame.config.AppConfig;
import com.ofcat.whereboardgame.findperson.CustomFindPersonActivity;
import com.ofcat.whereboardgame.findperson.FindPersonActivity;
import com.ofcat.whereboardgame.firebase.dataobj.LocationDTO;
import com.ofcat.whereboardgame.firebase.dataobj.UserInfoDTO;
import com.ofcat.whereboardgame.firebase.model.FireBaseUrl;
import com.ofcat.whereboardgame.util.FirebaseTableKey;
import com.ofcat.whereboardgame.util.MyLog;


/**
 * Created by orangefaller on 2017/3/4.
 */

public class UserLoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = UserLoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;

    private static final String PROVIDER_FACEBOOK = "facebook.com";
    private static final String PROVIDER_GOOGLE = "google.com";

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private DatabaseReference roomDatabaseRef;
    private DatabaseReference customRoomDatabaseRef;


    private CallbackManager mCallbackManager;

    private GoogleApiClient mGoogleApiClient;

    private LinearLayout llLoginButtonArea;
    private RelativeLayout rlUserInfoArea;
    private ProgressBar progressBar;
    private LoginButton btnFacebookLogin;
    private Button btnGoogleLogin, btnSignOut;
    private TextView tvLoginDescription;
    private TextView tvUserInfoPlayerRoom;
    private TextView tvUserInfoPlayerRoomDate;
    private SwitchCompat playerRoomStatus;
    private Spinner spinnerCurrentPerson, spinnerNeedPerson;


    private String userAccountProvider = "";
    private String userId = "";
    private String userStoreId = "";
    private String userStoreName = "";
    private String userStoreAddressTag = "";
    private String userStoreAddress = "";
    private double userStoreLat, userStoreLng;

    private String roomDate = "目前揪團日期：%s";

    /**
     * 用來判斷roomlist下該user開的揪團房有沒有過期被刪掉
     * false為空資料
     */
    private boolean isRoomHasChildern;

    private FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            isLoading(false);
            if (user != null) {
                // User is signed in
                MyLog.i(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                userId = user.getUid();

                try {
                    userAccountProvider = user.getProviders().get(0);
                } catch (NullPointerException e) {
                    userAccountProvider = "";
                }

                llLoginButtonArea.setVisibility(View.INVISIBLE);
                showLoginStatusDescription(true);

                databaseReference = database.getReferenceFromUrl(AppConfig.FIREBASE_URL + "/" + FirebaseTableKey.TABLE_USER_INFO + "/" + user.getUid());
                databaseReference.addValueEventListener(userInfoValueEventListener);
            } else {
                // User is signed out
                MyLog.i(TAG, "onAuthStateChanged:signed_out");
                showLoginStatusDescription(false);
                showUserInfoRoomStatus(false);
                cleanData();

            }
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_facebook_sign_in:
                    isLoading(true);
                    break;
                case R.id.btn_google_sign_in:
                    isLoading(true);
                    gmailSignIn();
                    break;
                case R.id.btn_sign_out:
                    signOut();
                    break;
                case R.id.rl_user_info_area:

                    if (userStoreId.equals("") || userStoreName.equals("") || userStoreAddressTag.equals("")) {
                        break;
                    }

                    if (userStoreId.equals(FirebaseTableKey.CUSTOM_STORE_ID)) {

                        Intent intent = new Intent(UserLoginActivity.this, CustomFindPersonActivity.class);
                        startActivity(intent);

                    } else {

                        Intent intent = new Intent(UserLoginActivity.this, FindPersonActivity.class);
                        intent.putExtra(FindPersonActivity.KEY_FINDPERSON_BGS_PLACE, userStoreName);
                        intent.putExtra(FindPersonActivity.KEY_FINDPERSON_BGS_ID, userStoreId);
                        intent.putExtra(FindPersonActivity.KEY_FINDPERSON_BGS_PLACE_TAG, userStoreAddressTag);
                        intent.putExtra(FindPersonActivity.KEY_FINDPERSON_BGS_LAT, userStoreLat);
                        intent.putExtra(FindPersonActivity.KEY_FINDPERSON_BGS_LNG, userStoreLng);
                        intent.putExtra(FindPersonActivity.KEY_FINDPERSON_BGS_ADDRESS, userStoreAddress);
                        startActivity(intent);
                    }

                    break;
            }

        }
    };

    private ValueEventListener userInfoValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            UserInfoDTO userInfoDTO = dataSnapshot.getValue(UserInfoDTO.class);
            if (userInfoDTO != null) {

                if (dataSnapshot.hasChild("waitPlayerRoomDTO")) {
                    tvUserInfoPlayerRoom.setText(userInfoDTO.getWaitPlayerRoomDTO().getInitiator());
                    tvUserInfoPlayerRoomDate.setText(String.format(roomDate, userInfoDTO.getWaitPlayerRoomDTO().getDate()));

                    userStoreName = userInfoDTO.getWaitPlayerRoomDTO().getStoreName();
                    userStoreAddressTag = userInfoDTO.getWaitPlayerRoomDTO().getAddressTag();

                    if (userInfoDTO.getWaitPlayerRoomDTO().getRoomStatus() == 2) { //滿團
                        playerRoomStatus.setChecked(true);
                    } else {
                        playerRoomStatus.setChecked(false);
                    }

                    spinnerCurrentPerson.setSelection(userInfoDTO.getWaitPlayerRoomDTO().getCurrentPerson());
                    spinnerNeedPerson.setSelection(userInfoDTO.getWaitPlayerRoomDTO().getNeedPerson());

                    //補上店家地址和經緯度資訊
                    userStoreAddress = userInfoDTO.getWaitPlayerRoomDTO().getStoreAddress();
                    LocationDTO locationDTO = userInfoDTO.getWaitPlayerRoomDTO().getLocation();
                    if (locationDTO != null) {
                        userStoreLat = locationDTO.getLatitude();
                        userStoreLng = locationDTO.getLongitude();
                    }
                }

                userStoreId = userInfoDTO.getStoreId();

                showUserInfoRoomStatus(dataSnapshot.hasChild("storeId"));

                if (userStoreId.equals(FirebaseTableKey.CUSTOM_STORE_ID)) {

                    if (customRoomDatabaseRef == null) {
                        FireBaseUrl customRoomListUrl = new FireBaseUrl.Builder()
                                .addUrlNote(FirebaseTableKey.TABLE_CUSTOM_WAITPLAYERROOM)
                                .addUrlNote(userId)
                                .build();
                        customRoomDatabaseRef = database.getReferenceFromUrl(customRoomListUrl.getUrl());
                        customRoomDatabaseRef.addValueEventListener(customRoomValueListener);

                    }

                } else {

                    if (roomDatabaseRef == null) {
                        FireBaseUrl roomListUrl = new FireBaseUrl.Builder()
                                .addUrlNote(FirebaseTableKey.TABLE_WAITPLYERROOM)
                                .addUrlNote(userStoreId)
                                .addUrlNote(userId)
                                .build();

                        roomDatabaseRef = database.getReferenceFromUrl(roomListUrl.getUrl());
                        roomDatabaseRef.addValueEventListener(roomValueListener);
                    }

                }

            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ValueEventListener roomValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            MyLog.i(TAG, " room list has children = " + dataSnapshot.hasChildren());
            isRoomHasChildern = dataSnapshot.hasChildren();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ValueEventListener customRoomValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            MyLog.i(TAG, " custom room list has children = " + dataSnapshot.hasChildren());
            isRoomHasChildern = dataSnapshot.hasChildren();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_other);
        initActionBar();
        initView();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
//        databaseReference = database.getReferenceFromUrl(AppConfig.FIREBASE_URL + "/" + FirebaseTableKey.TABLE_USER_INFO);


        mCallbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        btnFacebookLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                MyLog.i(TAG, "facebook:onSuccess:" + loginResult);
                isLoading(true);
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                MyLog.i(TAG, "facebook:onCancel");
                showErrorDataDialog(null);
                // ...
            }

            @Override
            public void onError(FacebookException error) {
//                Log.e(TAG, "facebook:onError", error);
                showErrorDataDialog(error.getMessage());
            }
        });


    }

    private void initActionBar() {
        try {
            getSupportActionBar().setTitle(R.string.login_title);
        } catch (NullPointerException e) {

        }
    }

    private void initView() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_green);
        llLoginButtonArea = (LinearLayout) findViewById(R.id.ll_login_button_area);
        btnFacebookLogin = (LoginButton) findViewById(R.id.btn_facebook_sign_in);
        btnFacebookLogin.setReadPermissions("email", "public_profile");
        btnGoogleLogin = (Button) findViewById(R.id.btn_google_sign_in);
        btnSignOut = (Button) findViewById(R.id.btn_sign_out);
        tvLoginDescription = (TextView) findViewById(R.id.login_description);

        rlUserInfoArea = (RelativeLayout) findViewById(R.id.rl_user_info_area);
        tvUserInfoPlayerRoom = (TextView) findViewById(R.id.tv_user_info_play_room_name);
        tvUserInfoPlayerRoomDate = (TextView) findViewById(R.id.tv_user_info_play_room_date);
        playerRoomStatus = (SwitchCompat) findViewById(R.id.switch_control_room_status);

        playerRoomStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                changeUserPlayerRoomStatus(b);
            }
        });

        spinnerCurrentPerson = (Spinner) findViewById(R.id.spinner_user_info_current_person);
        spinnerCurrentPerson.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (userStoreId != null && !userStoreId.equals("")) {

                    changeUserPlayerCurrentPerson(position);

                    if (userStoreId.equals(FirebaseTableKey.CUSTOM_STORE_ID)) {
                        changePlayerCustomRoomCurrentPerson(position);
                    } else {
                        changePlayerRoomCurrentPerson(position);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerNeedPerson = (Spinner) findViewById(R.id.spinner_user_info_need_person);
        spinnerNeedPerson.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (userStoreId != null && !userStoreId.equals("")) {

                    changeUserPlayerNeedPerson(position);

                    if (userStoreId.equals(FirebaseTableKey.CUSTOM_STORE_ID)) {
                        changePlayerCustomRoomNeedPerson(position);
                    } else {
                        changePlayerRoomNeedPerson(position);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnFacebookLogin.setOnClickListener(clickListener);
        btnGoogleLogin.setOnClickListener(clickListener);
        btnSignOut.setOnClickListener(clickListener);
        rlUserInfoArea.setOnClickListener(clickListener);
    }

    private void isLoading(boolean isLoad) {
        if (isLoad) {
            llLoginButtonArea.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            llLoginButtonArea.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showLoginStatusDescription(boolean isLogin) {

        if (isLogin) {

            if (userAccountProvider.equals(PROVIDER_FACEBOOK)) {
                tvLoginDescription.setText(getString(R.string.login_already_login_facebook));
            } else if (userAccountProvider.equals(PROVIDER_GOOGLE)) {
                tvLoginDescription.setText(getString(R.string.login_already_login_google));
            } else {
                tvLoginDescription.setText(getString(R.string.login_already_login));
            }

        } else {
            tvLoginDescription.setText(getString(R.string.login_description));
        }

    }

    private void showUserInfoRoomStatus(boolean isOpenRoom) {

        if (isOpenRoom) {
            rlUserInfoArea.setVisibility(View.VISIBLE);
        } else {
            rlUserInfoArea.setVisibility(View.GONE);
        }

    }

    private void changeUserPlayerRoomStatus(boolean isFull) {
        databaseReference = database.getReferenceFromUrl(AppConfig.FIREBASE_URL + "/" + FirebaseTableKey.TABLE_USER_INFO + "/" + userId);

        if (userId.equals("") || userStoreId.equals("")) {
            return;
        }

        if (isFull) {
            databaseReference.child("waitPlayerRoomDTO").child("roomStatus").setValue(2);
        } else {
            databaseReference.child("waitPlayerRoomDTO").child("roomStatus").setValue(1);
        }


        if (userStoreId.equals(FirebaseTableKey.CUSTOM_STORE_ID)) {
            changePlayerCustomRoomListRoomStatus(isFull);
        } else {
            changePlayerRoomListRoomStatus(isFull);
        }

    }

    private void changePlayerRoomListRoomStatus(boolean isFull) {
        databaseReference = database.getReferenceFromUrl(AppConfig.FIREBASE_URL + "/" + FirebaseTableKey.TABLE_WAITPLYERROOM);

        if (userStoreId.equals("") || userId.equals("") || !isRoomHasChildern) {
            return;
        }

        if (isFull) {
            databaseReference.child(userStoreId).child(userId).child("roomStatus").setValue(2);
        } else {
            databaseReference.child(userStoreId).child(userId).child("roomStatus").setValue(1);
        }

    }

    private void changePlayerCustomRoomListRoomStatus(boolean isFull) {
        databaseReference = database.getReferenceFromUrl(AppConfig.FIREBASE_URL + "/" + FirebaseTableKey.TABLE_CUSTOM_WAITPLAYERROOM);

        if (!userStoreId.equals(FirebaseTableKey.CUSTOM_STORE_ID) || userId.equals("") || !isRoomHasChildern) {
            return;
        }

        if (isFull) {
            databaseReference.child(userId).child("roomStatus").setValue(2);
        } else {
            databaseReference.child(userId).child("roomStatus").setValue(1);
        }

    }

    private void changeUserPlayerCurrentPerson(int person) {

        if (!isRoomHasChildern) {
            return;
        }

        databaseReference = database.getReferenceFromUrl(AppConfig.FIREBASE_URL + "/" + FirebaseTableKey.TABLE_USER_INFO + "/" + userId);

        databaseReference.child("waitPlayerRoomDTO").child("currentPerson").setValue(person);
    }

    private void changeUserPlayerNeedPerson(int person) {

        if (!isRoomHasChildern) {
            return;
        }

        databaseReference = database.getReferenceFromUrl(AppConfig.FIREBASE_URL + "/" + FirebaseTableKey.TABLE_USER_INFO + "/" + userId);

        databaseReference.child("waitPlayerRoomDTO").child("needPerson").setValue(person);
    }

    private void changePlayerRoomCurrentPerson(int person) {

        if (!isRoomHasChildern) {
            return;
        }

        databaseReference = database.getReferenceFromUrl(AppConfig.FIREBASE_URL + "/" + FirebaseTableKey.TABLE_WAITPLYERROOM);
        databaseReference.child(userStoreId).child(userId).child("currentPerson").setValue(person);

    }

    private void changePlayerCustomRoomCurrentPerson(int person) {

        if (!isRoomHasChildern) {
            return;
        }

        databaseReference = database.getReferenceFromUrl(AppConfig.FIREBASE_URL + "/" + FirebaseTableKey.TABLE_CUSTOM_WAITPLAYERROOM);
        databaseReference.child(userId).child("currentPerson").setValue(person);

    }

    private void changePlayerRoomNeedPerson(int person) {

        if (!isRoomHasChildern) {
            return;
        }

        databaseReference = database.getReferenceFromUrl(AppConfig.FIREBASE_URL + "/" + FirebaseTableKey.TABLE_WAITPLYERROOM);
        databaseReference.child(userStoreId).child(userId).child("needPerson").setValue(person);
    }

    private void changePlayerCustomRoomNeedPerson(int person) {

        if (!isRoomHasChildern) {
            return;
        }

        databaseReference = database.getReferenceFromUrl(AppConfig.FIREBASE_URL + "/" + FirebaseTableKey.TABLE_CUSTOM_WAITPLAYERROOM);
        databaseReference.child(userId).child("needPerson").setValue(person);

    }

    private void cleanData() {
        userAccountProvider = "";
        userId = "";
        userStoreId = "";
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseReference != null) {
            databaseReference.removeEventListener(userInfoValueEventListener);
            databaseReference = null;
        }

        if (roomDatabaseRef != null) {
            roomDatabaseRef.removeEventListener(roomValueListener);
            roomDatabaseRef = null;
        }

        if (customRoomDatabaseRef != null) {
            customRoomDatabaseRef.removeEventListener(customRoomValueListener);
            customRoomDatabaseRef = null;
        }


        isLoading(false);
        cleanData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            MyLog.i(TAG, "result.isSuccess = " + result.isSuccess());
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                MyLog.i(TAG, "result status message = " + result.getStatus().getStatusMessage());
                showErrorDataDialog(result.getStatus().getStatusMessage());
//                updateUI(null);
            }
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        MyLog.i(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        MyLog.i(TAG, "provider = " + credential.getProvider());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        MyLog.i(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
//                            MyLog.e(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(UserLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserLoginActivity.this, "登入成功",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }
                });

    }

    private void gmailSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        MyLog.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        MyLog.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
//                            MyLog.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(UserLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserLoginActivity.this, "登入成功",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        // ...
                    }
                });
    }

    public void signOut() {
        auth.signOut();
        LoginManager.getInstance().logOut();

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
//                        updateUI(null);
                    }
                });
        Toast.makeText(this, "已登出", Toast.LENGTH_SHORT).show();
    }

    private void showErrorDataDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.login_fail)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.i_know, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isLoading(false);
                        dialogInterface.dismiss();
                    }
                }).show();

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!connectionResult.isSuccess()) {
            showErrorDataDialog(connectionResult.getErrorMessage());
        }

    }

}
