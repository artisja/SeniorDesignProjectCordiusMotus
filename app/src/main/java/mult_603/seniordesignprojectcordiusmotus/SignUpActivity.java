package mult_603.seniordesignprojectcordiusmotus;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import android.net.Uri;

import java.io.File;

public class SignUpActivity extends AppCompatActivity{
    public final String TAG = SignUpActivity.class.getSimpleName();
    private EditText setPasswordEdit,setEmailEdit, setUserNameEdit;
    private Button createdPasswordButton;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ApplicationController appController;
    private ImageView profileImage;
    private Uri userImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        findViews();
        mFirebaseAuth = appController.firebaseAuth;
        setUpClick();

        // Auth state listener should it take into account multiple accounts with the same email?

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.i(TAG, "Current User " + user);

                if(user != null){
                    Log.i(TAG, "Current User is not null");
                    String uName    = setUserNameEdit.getText().toString().trim();

                    // Need to check that the image button is not null also
                    if(uName != null){
                        Log.i(TAG, "User Name is not null");
                        // Can use this to give them an image with their profile as well
                        // Giving the user a username
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(uName)
                                .setPhotoUri(userImagePath)
                                .build();

                        user.updateProfile(userProfileChangeRequest);
                        Log.i(TAG, "User Get Display Name " + user.getDisplayName());
                        Log.i(TAG, "User Get Email " + user.getEmail());
                        Log.i(TAG, "User UUID " + user.getUid());
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
        createdPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = setPasswordEdit.getText().toString().trim();
                String email    = setEmailEdit.getText().toString().trim();
                String uName    = setUserNameEdit.getText().toString().trim();
                final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                if(email.isEmpty() || password.isEmpty() || uName.isEmpty()){
                    // Close the keyboard and notify the user that one of the fields was empty
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    Toast.makeText(SignUpActivity.this, "No Password or Email was entered", Toast.LENGTH_SHORT).show();
                }else{
                    // If all the fields have input then create a new user
                    mFirebaseAuth.createUserWithEmailAndPassword(email,password);
                    Boolean truth = mFirebaseAuth.signInWithEmailAndPassword(email,password).isSuccessful();
                    onBackPressed();
                }
            }
        });

        // User has granted permission to use the camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            Log.i(TAG, "The user has granted permission to use the camera ");
//            userImage.setEnabled(true);
        }
        else{
            Log.i(TAG, "User has not given us permission to use their camera ");
//            userImage.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }

        // Set the users image for their profile
//        userImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent cameraGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                cameraGalleryIntent.setType("image/*");
//                startActivityForResult(cameraGalleryIntent, 0);
//            }
//        });

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
        String path = new String();

        if (requestCode == 0) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a photo.
                // The Intent's data Uri identifies which photo was selected.
                Log.i(TAG, "Get Photo from users camera");
                // Do something with the photo here (bigger example below)
                userImagePath = data.getData();
                Log.i(TAG, "Selected Image to String " + profileImage.toString());
                Log.i(TAG, "Selected Image Path " + userImagePath.getPath());

                //Bitmap bitmap = BitmapFactory.decodeFile(userImagePath.toString().trim());
                //Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, 48, 48, false);
                //userImage.setImageBitmap(bitmap);
                //profileImage.setImageBitmap(bitmap);

//                userImage.setImageURI(userImagePath);
                profileImage.setImageURI(userImagePath);
//                userImage.setScaleX(48);
//                userImage.setScaleY(48);
            }
        }
    }

    public static int calcSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        final int height = options.outHeight;
        final int width  = options.outWidth;

        int inSampleSize = 1;

        if(height > reqHeight || width > reqWidth){
            final int halfHeight = height/2;
            final int halfWidth  = width/2;

            while((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth){
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    // Set up the resources of the views
    private void findViews() {
        setPasswordEdit       = (EditText) findViewById(R.id.input_password_edit);
        setEmailEdit          = (EditText) findViewById(R.id.input_email_edit);
        setUserNameEdit       = (EditText) findViewById(R.id.input_username_edit);
//        userImage             = (ImageButton) findViewById(R.id.user_login_image);
        profileImage          = (ImageView) findViewById(R.id.profile_image);
        createdPasswordButton = (Button) findViewById(R.id.submit_signup_button);
        appController         = (ApplicationController) getApplicationContext();
    }
}