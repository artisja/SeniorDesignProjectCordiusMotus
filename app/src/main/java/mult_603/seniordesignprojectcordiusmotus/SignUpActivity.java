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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.tapadoo.alerter.Alerter;
import com.tapadoo.alerter.OnHideAlertListener;
import com.tapadoo.alerter.OnShowAlertListener;
import android.net.Uri;
import org.hashids.Hashids;

/**
 * This class handles the sign up of new users
 * The sign up is handled by Firebase
 */
public class SignUpActivity extends AppCompatActivity{
    public final String TAG = SignUpActivity.class.getSimpleName();
    private EditText setPasswordEdit,setEmailEdit, setUserNameEdit, confirmPassword;
    private Button submitButton;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ImageView profileImage;
    private Uri userImageUri;
    private AccountHeader headerResult;
    private Drawer drawerResult;
    private NavigationDrawerHandler navHandler;
    private StorageReference storageRef;
    private StorageReference userImageReference;
    private FirebaseStorage mFirebaseStorage;
    private String userImageReferenceString;
    private FirebaseUser currentUser;
    private String uName;
    private String email;
    private String password;
    private String cPassword;
    private String userImageRefToSave;
    public static DeviceUser newUser = new DeviceUser();
    public static DatabaseReference userDictRef = FirebaseDatabase.getInstance().getReference("UserDictionary");;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Set up the navigation handler
        navHandler = new NavigationDrawerHandler(this, savedInstanceState, getApplicationContext(), toolbar);
        headerResult = navHandler.setAccountHeader(this, savedInstanceState, getApplicationContext());
        drawerResult = navHandler.setUserDrawer(this, headerResult, toolbar);

        // Get firebase storage reference as well as auth and storage instances
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        storageRef = mFirebaseStorage.getReference();
        userImageReference = storageRef.child("UserImages");

        findViews();
        setUpClick();


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.i(TAG, "Auth state listener");

                // If the user is not null then using the short hash set the image reference
                if(user != null){
                    Log.i(TAG, "Current User is not null");
                    if(newUser.getShortHash() != null) {
                        Log.i(TAG, "Current User short hash is not null");
                        userDictRef.child(newUser.getShortHash()).setValue(newUser);
                    }
                    else{
                        Log.i(TAG, "Current User short hash is null");
                    }

                }
                else{
                    Log.i(TAG, "Current User is null");
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

        // remove auth state listener
        if(mFirebaseAuth != null){
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
        headerResult = navHandler.getHeader();
        drawerResult = navHandler.getDrawer();
    }

    private void setUpClick() {

        // Create user in firebase auth
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uName    = setUserNameEdit.getText().toString().trim();
                email    = setEmailEdit.getText().toString().trim();
                password = setPasswordEdit.getText().toString().trim();
                cPassword= confirmPassword.getText().toString().trim();

                final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                if(email.isEmpty() || password.isEmpty() || uName.isEmpty() || cPassword.isEmpty()){
                    // Close the keyboard and notify the user that one of the fields was empty
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    Alerter.create(SignUpActivity.this)
                            .enableIconPulse(true)
                            .setBackgroundColor(R.color.colorPrimaryDark)
                            .setTitle("Error")
                            .setText("One of the following fields is empty: username, email, password, password confirmation. Please try again.")
                            .setDuration(5000)
                            .show();

                }else{
                    // Check to see that the passwords match
                    if(password.equals(cPassword)) {
                        // If all the fields have input then create a new user
                        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    // Create a new device user
                                    Log.i(TAG, "Creating a user with the following credientals");
                                    Log.i(TAG, "Email: " + email);
                                    Log.i(TAG, "UName: " + uName);
                                    Log.i(TAG, "Password: " + password);
                                    Log.i(TAG, "Confirm Password: " + cPassword);
                                    Log.i(TAG, "User Image Reference String: " + userImageRefToSave);

                                    // Set the user name, email, image path, and short hash
                                    Log.i(TAG, "User Name uName -> " + uName);
                                    newUser.setUserName(uName);
                                    newUser.setEmail(email);
                                    newUser.setUserImage(userImageReferenceString);

                                    // Set the short hash code
                                    Hashids shortHash = new Hashids(email);
                                    String hash = shortHash.encode(12345);
                                    Log.i(TAG, "Hash: " + hash);
                                    newUser.setShortHash(hash);

                                    // Sign the user in after allowing them to create an account
                                    mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                                            // If logging in is unsuccessful then hide keyboard and display toast
                                            if (!task.isSuccessful()) {
                                                Log.i(TAG, "Logging the user in after sign up was unsuccessful");
                                                try {
                                                    throw task.getException();
                                                }
                                                catch(Exception e){
                                                    Log.i(TAG, "Exception during login: " + e.getMessage());
                                                }
                                            } else {
                                                // Get the current user set the uuid of the user
                                                Log.i(TAG, "Successfully logged the user in after creating an account");
                                                currentUser = FirebaseAuth.getInstance().getCurrentUser();

                                                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(uName)
                                                        .setPhotoUri(userImageUri)
                                                        .build();

                                                // Update user profile, set device user's user name update user dictionary
                                                currentUser.updateProfile(userProfileChangeRequest);

//                                                // Update the user's profile information
                                                if (currentUser != null) {
                                                    newUser.setUuid(currentUser.getUid());
                                                }

                                                userDictRef.child(newUser.getShortHash()).setValue(newUser);

                                                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(currentUser.getUid());
                                                dbRef.child("CurrentUser").setValue(newUser);

                                                // Show success alert
                                                Alerter.create(SignUpActivity.this)
                                                        .enableIconPulse(true)
                                                        .setTitle("New User Created")
                                                        .setBackgroundColor(R.color.colorPrimaryDark)
                                                        .setText("New user with the following email address " + email + " was created. Logging in...")
                                                        .setDuration(5000).setOnShowListener(new OnShowAlertListener() {
                                                        @Override
                                                         public void onShow() {
                                                                Log.i(TAG, "Sign Up Alert Shown");
                                                            }
                                                         })
                                                        .setOnHideListener(new OnHideAlertListener() {
                                                        @Override
                                                        public void onHide() {
                                                                Log.i(TAG, "Sign Up Alert Hidden");

                                                                // Go to the add contact / bluetooth page
                                                                Intent userIntent = new Intent(SignUpActivity.this, UserTabActivity.class);
                                                                startActivity(userIntent);
                                                            }
                                                        })
                                                        .show();
                                            }
                                        }
                                    });

                                    // Clear the edit text fields
                                    setPasswordEdit.setText("");
                                    setEmailEdit.setText("");
                                    setUserNameEdit.setText("");
                                    confirmPassword.setText("");

                                } else {
                                    // Try to figure out why signing up is failing
                                    try {
                                        throw task.getException();

                                    }
                                    catch(FirebaseAuthWeakPasswordException e) {
                                        Alerter.create(SignUpActivity.this)
                                                .enableIconPulse(true)
                                                .setTitle("Error")
                                                .setBackgroundColor(R.color.colorPrimaryDark)
                                                .setText(e.getReason())
                                                .setDuration(5000)
                                                .show();
                                        Log.i(TAG, e.getReason());

                                    }
                                    catch(FirebaseAuthInvalidCredentialsException e) {
                                        Alerter.create(SignUpActivity.this)
                                                .enableIconPulse(true)
                                                .setTitle("Error")
                                                .setBackgroundColor(R.color.colorPrimaryDark)
                                                .setText(e.getLocalizedMessage())
                                                .setDuration(5000)
                                                .show();
                                        Log.i(TAG, e.getLocalizedMessage());

                                    }
                                    catch(FirebaseAuthUserCollisionException e) {
                                        Alerter.create(SignUpActivity.this)
                                                .enableIconPulse(true)
                                                .setTitle("Error")
                                                .setBackgroundColor(R.color.colorPrimaryDark)
                                                .setText(e.getMessage())
                                                .setDuration(5000)
                                                .show();
                                        Log.i(TAG, e.getMessage());

                                    }
                                    catch(Exception e) {
                                        Alerter.create(SignUpActivity.this)
                                                .enableIconPulse(true)
                                                .setTitle("Error")
                                                .setBackgroundColor(R.color.colorPrimaryDark)
                                                .setText(e.getMessage())
                                                .setDuration(5000)
                                                .show();
                                        Log.e(TAG, e.getMessage());
                                    }

                                    // Reset the password text fields
                                    setPasswordEdit.setText("");
                                    confirmPassword.setText("");
                                }
                            }
                        });
                    }
                    else{
                        // Passwords do not match
                        Alerter.create(SignUpActivity.this)
                                .enableIconPulse(true)
                                .setBackgroundColor(R.color.colorPrimaryDark)
                                .setTitle("Error")
                                .setText("Passwords do not match.")
                                .setDuration(5000)
                                .show();
                        setPasswordEdit.setText("");
                        confirmPassword.setText("");
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
                userImageUri = data.getData();
                Log.i(TAG, "Selected Image to String " + profileImage.toString());
                Log.i(TAG, "Selected Image Path "      + userImageUri.getPath());
                Log.i(TAG, "Selected Image Last Path " + userImageUri.getLastPathSegment());


                profileImage.setImageURI(userImageUri);

                // Store the user's image in fire base storage
                StorageReference newImageRef = userImageReference.child(userImageUri.getLastPathSegment());
                Log.i(TAG, "Storage Reference for Image " + newImageRef);
                userImageReferenceString = newImageRef.getPath();
                userImageRefToSave = newImageRef.toString();

                // Set new users image reference and update the user
                newUser.setUserImage(userImageReferenceString);

                newImageRef.putFile(userImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.i(TAG, "Uploaded file with image path " + userImageReferenceString);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Failed to upload file with image path " + userImageReferenceString);
                    }
                });

                Log.i(TAG, "User Image Reference Path String " + userImageReferenceString);

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
    }
}

