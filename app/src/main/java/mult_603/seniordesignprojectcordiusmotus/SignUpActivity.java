package mult_603.seniordesignprojectcordiusmotus;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import android.net.Uri;

public class SignUpActivity extends AppCompatActivity{
    public final String TAG = SignUpActivity.class.getSimpleName();
    private EditText setPasswordEdit,setEmailEdit, setUserNameEdit, confirmPassword;
    private Button submitButton;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ApplicationController appController;
    private ImageView profileImage;
    private Uri userImagePath;
    private AccountHeader headerResult;
    private Drawer drawerResult;
    private DeviceUser newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        headerResult = NavigationDrawerHandler.getAccountHeader(this, savedInstanceState, getApplicationContext());
        drawerResult = NavigationDrawerHandler.getUserDrawer(this, headerResult, toolbar);

        findViews();
        mFirebaseAuth = FirebaseAuth.getInstance();
        setUpClick();

        // Auth state listener should it take into account multiple accounts with the same email?
        // TODO - Issues with the sign up now

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.i(TAG, "Current User " + user);

                if(user != null){
                    Log.i(TAG, "Current User is not null");
                    String uName    = setUserNameEdit.getText().toString().trim();

                    // Check that the user name is not null
                    if(!uName.isEmpty()){
                        // Can use this to give them an image with their profile as well
                        // Giving the user a username
                        Log.i(TAG, "User Image Path -> " + userImagePath);


                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(uName)
                                .setPhotoUri(userImagePath)
                                .build();

                        user.updateProfile(userProfileChangeRequest);

                        Log.i(TAG, "User Get Display Name " + user.getDisplayName());
                        Log.i(TAG, "User Get Email " + user.getEmail());
                        Log.i(TAG, "User UUID " + user.getUid());
                    }
                    else{
                        Log.i(TAG, "User Name: " + user.getDisplayName() + "User Photo Url" + user.getPhotoUrl());
                    }
                }
            }
        };

        // Add the auth state listener
        mFirebaseAuth.addAuthStateListener(authStateListener);

    }

    @Override
    public void onStop(){
        super.onStop();
        Log.i(TAG, "On Stop Entered");

        // If the auth listener exists remove it
        if(authStateListener != null){
            mFirebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.i(TAG, "On Pause Entered");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i(TAG, "On Resume Entered");
        // If the activity is resumed, add the auth listener
        mFirebaseAuth.addAuthStateListener(authStateListener);
    }

    private void setUpClick() {

        // Create user in firebase auth
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String password = setPasswordEdit.getText().toString().trim();
                final String email    = setEmailEdit.getText().toString().trim();
                final String uName    = setUserNameEdit.getText().toString().trim();
                final String cPassword= confirmPassword.getText().toString().trim();

                final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                if(email.isEmpty() || password.isEmpty() || uName.isEmpty() || cPassword.isEmpty()){
                    // Close the keyboard and notify the user that one of the fields was empty
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    Toast.makeText(SignUpActivity.this, "One of the following is blank Password, Email, UserName or Confirmed Password", Toast.LENGTH_SHORT).show();
                }else{
                    // Check to see that the passwords match
                    if(password == cPassword) {
                        // If all the fields have input then create a new user
                        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(appController, "Successfully created an account", Toast.LENGTH_SHORT).show();
                                    newUser = new DeviceUser(email, uName);
                                    Log.i(TAG, "Created a Device User: " + newUser.toString());

                                    try {
                                        // Try to push some initial info about the user to the database
                                        String uuid = mFirebaseAuth.getCurrentUser().getUid();
                                        newUser.setUuid(uuid);
                                        Log.i(TAG, "UUID -> " + uuid);
                                        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference(uuid);
                                        dRef.push().setValue(newUser);
                                    } catch (NullPointerException n) {
                                        Log.i(TAG, "Null Pointer Thrown while trying to get user's UUID");
                                    }

                                    // Clear the edit text fields
                                    setPasswordEdit.setText("");
                                    setEmailEdit.setText("");
                                    setUserNameEdit.setText("");

                                    // Press the back button to return to the login screen
                                    onBackPressed();
                                } else {
                                    Toast.makeText(appController, "Was unable to create an account. Please try again", Toast.LENGTH_SHORT).show();
                                    // Reset the text fields
                                    setPasswordEdit.setText("");
                                    setEmailEdit.setText("");
                                    setUserNameEdit.setText("");
                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "The Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // User has granted permission to use the camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            Log.i(TAG, "The user has granted permission to use the camera ");
        }
        else{
            Log.i(TAG, "User has not given us permission to use their camera ");
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                cameraGalleryIntent.setType("image/*");
                startActivityForResult(cameraGalleryIntent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        Log.i(TAG, "Request Code " + requestCode + " Result Code " + resultCode + " Data " + data);
        Log.i(TAG, "Result OK "  + RESULT_OK);

        if (requestCode == 0) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a photo.
                // The Intent's data Uri identifies which photo was selected.
                Log.i(TAG, "Get Photo from users camera");
                // Do something with the photo here (bigger example below)
                userImagePath = data.getData();
                Log.i(TAG, "Selected Image to String " + profileImage.toString());
                Log.i(TAG, "Selected Image Path "      + userImagePath.getPath());

                profileImage.setImageURI(userImagePath);
            }
        }
    }

    // Set up the resources of the views
    private void findViews() {
        confirmPassword       = (EditText) findViewById(R.id.input_password_confirmation);
        setPasswordEdit       = (EditText) findViewById(R.id.input_password_edit);
        setEmailEdit          = (EditText) findViewById(R.id.input_email_edit);
        setUserNameEdit       = (EditText) findViewById(R.id.input_username_edit);
        profileImage          = (ImageView) findViewById(R.id.profile_image);
        submitButton          = (Button) findViewById(R.id.submit_signup_button);
        appController         = (ApplicationController) getApplicationContext();
    }
}