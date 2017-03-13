package mult_603.seniordesignprojectcordiusmotus;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

/**
 * Created by Wes on 1/30/17.
 */
public class NavigationDrawerHandler implements
        AccountHeader.OnAccountHeaderListener,
        FirebaseAuth.AuthStateListener{

    private static final String TAG = NavigationDrawerHandler.class.getSimpleName();
    // Specific to the user
    public static Drawer             userDrawer;
    private static AccountHeader     currentUserAccountHeader;
    private static ProfileDrawerItem userProfile;
    private static PrimaryDrawerItem signOut;
    private static PrimaryDrawerItem deleteAccount;
    private static PrimaryDrawerItem forgotPassword;
    private static PrimaryDrawerItem changePassword;
    private static PrimaryDrawerItem changeEmail;
    private static PrimaryDrawerItem login;
    // Regular activities
    private static PrimaryDrawerItem home;
    private static PrimaryDrawerItem signUp;
    private static PrimaryDrawerItem addContact;
    private static PrimaryDrawerItem contactList;
    private static PrimaryDrawerItem map;
    private static PrimaryDrawerItem bluetooth;
    private static PrimaryDrawerItem blueChart;
    // Tags
    private final static String LOGIN_TAG           = "Login";
    private final static String CHANGE_EMAIL_TAG    = "ChangeEmail";
    private final static String CHANGE_PASSWORD_TAG = "ChangePassword";
    private final static String SIGN_OUT_TAG        = "SignOut";
    private final static String FORGOT_PASSWORD_TAG = "ForgotPassword";
    private final static String DELETE_ACCOUNT_TAG  = "DeleteAccount";
    private final static String HOME_TAG = "Home";
    private final static String SIGN_UP_TAG = "SignUp";
    private final static String ADD_CONTACT_TAG = "AddContact";
    private final static String CONTACT_TAG = "Contact";
    private final static String MAP_TAG = "Map";
    private final static String BLUETOOTH_TAG = "Bluetooth";
    private final static String BLUETOOTH_CHART_TAG = "BluetoothChart";
    private static FirebaseUser         currentUser;
    private static Context              context;
    private static AppCompatActivity    activity;
    private static Bundle               savedInstanceState;
    private static Toolbar              toolbar;

    public NavigationDrawerHandler(AppCompatActivity activity, Bundle savedInstanceState, Context context, Toolbar toolbar){
        this.activity = activity;
        this.savedInstanceState = savedInstanceState;
        this.context  = context;
        this.toolbar = toolbar;
    }

    // return the account header
    public AccountHeader getHeader(){
        return currentUserAccountHeader;
    }

    // return the drawer
    public Drawer getDrawer(){
        return userDrawer;
    }

    // Refresh the drawer after something is clicked
    public static void refreshDrawer(){
        currentUserAccountHeader = setAccountHeader(activity, savedInstanceState, context);
        userDrawer = setUserDrawer(activity,currentUserAccountHeader, toolbar);
    }

    public static Drawer.OnDrawerItemClickListener handleOnClick(final Drawer drawer, final AppCompatActivity activity){
        return new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                if(drawerItem != null){
                    Log.i("Drawer Item", " Is not null");
                    final String str = drawerItem.getTag().toString();

                    switch (str) {
                        case HOME_TAG:
                            Log.i(TAG, "Home Tag Pressed");
                            userDrawer.closeDrawer();
                            Intent homeIntent = new Intent(context, UserDefinitionActivity.class);
                            homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(homeIntent);
                            break;

//                        case ADD_CONTACT_TAG:
//                            Log.i(TAG, "Add Contact Tag Pressed");
//                            userDrawer.closeDrawer();
//                            Intent addContactIntent = new Intent(context, ContactSimpleActivity.class);
//                            addContactIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(addContactIntent);
//                            break;

                        case SIGN_UP_TAG:
                            Log.i(TAG, "Sign Up Tag Pressed");
                            userDrawer.closeDrawer();
                            Intent signUpIntent = new Intent(context, SignUpActivity.class);
                            signUpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(signUpIntent);
                            break;

//                        case CONTACT_TAG:
//                            Log.i(TAG, "Contact Tag Pressed");
//                            userDrawer.closeDrawer();
//                            Intent contactIntent = new Intent(context, ContactListActivity.class);
//                            contactIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(contactIntent);
//                            break;

                        case MAP_TAG:
                            Log.i(TAG, "Map Tag Pressed");
                            userDrawer.closeDrawer();
                            Intent mapIntent = new Intent(context, UserMapsActivity.class);
                            mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(mapIntent);
                            break;

//                        case BLUETOOTH_TAG:
//                            Log.i(TAG, "Bluetooth Tag Pressed");
//                            userDrawer.closeDrawer();
//                            Intent bluetoothIntent = new Intent(context, BluetoothActivity.class);
//                            bluetoothIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(bluetoothIntent);
//                            break;
//
//                        case BLUETOOTH_CHART_TAG:
//                            Log.i(TAG, "Bluetooth Chart Tag Pressed");
//                            userDrawer.closeDrawer();
//                            Intent bluetoothChartIntent = new Intent(context, BluetoothChartActivity.class);
//                            bluetoothChartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(bluetoothChartIntent);
//                            break;

                        case LOGIN_TAG:
                            Log.i(TAG, "Login Tag Pressed");
                            userDrawer.closeDrawer();
                            Intent loginIntent = new Intent(context, LoginActivity.class);
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(loginIntent);
                            break;

                        case CHANGE_EMAIL_TAG:
                            Log.i(TAG, "Change Email Tag Pressed");
                            userDrawer.closeDrawer();
                            Intent changeEmail = new Intent(context, ChangeEmailActivity.class);
                            changeEmail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(changeEmail);
                            break;

                        case CHANGE_PASSWORD_TAG:
                            Log.i(TAG, "Change Password Tag Pressed");
                            userDrawer.closeDrawer();
                            Intent changePassword = new Intent(context, ChangePasswordActivity.class);
                            changePassword.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(changePassword);
                            break;

                        case SIGN_OUT_TAG:
                            Log.i(TAG, "Sign Out Tag Pressed");
                            if (currentUser != null) {
                                FirebaseAuth.getInstance().signOut();
                            }
                            userDrawer.closeDrawer();
                            break;

                        case DELETE_ACCOUNT_TAG:
                            Log.i(TAG, "Delete Account Tag Pressed");
                            userDrawer.closeDrawer();
                            de.hdodenhof.circleimageview.CircleImageView img = new de.hdodenhof.circleimageview.CircleImageView(context);
                            img.setImageURI(currentUser.getPhotoUrl());

                            new AlertDialog.Builder(context)
                                    .setMessage("Are you sure you want to delete your account with the following Info : "
                                            + "\n\nEmail : "  + currentUser.getEmail()
                                            + "\nUser Name: " + currentUser.getDisplayName())
                                    .setCustomTitle(img)
                                    .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (currentUser != null) {
                                                Log.i(TAG, "Deleting the user with display name " + currentUser.getDisplayName());
                                                Log.i(TAG, "Deleting the user with email address " + currentUser.getEmail());


                                                currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.i(TAG, "User has been Deleted");
                                                        } else {
                                                            Log.i(TAG, "Something went wrong deleting the user");
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    })
                                    .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .create()
                                    .show();
                            break;
                    }
                }
                refreshDrawer();
                return true;
            }
        };
    }

    public static AccountHeader setAccountHeader(final AppCompatActivity activity, final Bundle savedInstanceState, final Context uContext){
        // Get the current user and set the context
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        context = uContext;

        if (currentUser != null){
            final IProfile userProfile = new ProfileDrawerItem()
                    .withEmail(currentUser.getEmail())
                    .withName(currentUser.getDisplayName())
                    .withIcon(currentUser.getPhotoUrl());

            return new AccountHeaderBuilder()
                    .withActivity(activity)
                    .withHeaderBackground(R.drawable.header_nav_background_adjusted)
                    .addProfiles(userProfile)
                    .withTextColorRes(R.color.colorPrimaryDark)
                    .build();
        }
        else{
            return new AccountHeaderBuilder()
                    .withActivity(activity)
                    .withHeaderBackground(R.drawable.header_nav_background_adjusted)
                    .withTextColorRes(R.color.colorPrimaryDark)
                    .build();
        }
    }


    public static Drawer setUserDrawer(final AppCompatActivity activity, AccountHeader accountHeader, Toolbar toolbar){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // Instantiate all the drawer items
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        home = new PrimaryDrawerItem();
        addContact = new PrimaryDrawerItem();
        contactList = new PrimaryDrawerItem();
        map = new PrimaryDrawerItem();
        bluetooth = new PrimaryDrawerItem();
        blueChart = new PrimaryDrawerItem();

        // User account specific
        changeEmail = new PrimaryDrawerItem();
        changePassword = new PrimaryDrawerItem();
        signOut = new PrimaryDrawerItem();
        deleteAccount = new PrimaryDrawerItem();
        login = new PrimaryDrawerItem();
        signUp = new PrimaryDrawerItem();

        home.withDescription(R.string.home_activity)
                .withSelectedColorRes(R.color.lightGrayWithRed)
                .withIcon(R.drawable.ic_home_black_24dp)
                .withTag(HOME_TAG);

        map.withDescription(R.string.map_activity)
                .withSelectedColorRes(R.color.lightGrayWithRed)
                .withIcon(R.drawable.ic_place_black_24dp)
                .withTag(MAP_TAG);


        if(currentUser != null) {

            // User add contact
            addContact.withDescription("Add Contact")
                    .withSelectedColorRes(R.color.lightGrayWithRed)
                    .withIcon(R.drawable.ic_add_circle_black_24dp)
                    .withTag(ADD_CONTACT_TAG);

            // Users contact list
            contactList.withDescription(R.string.contact_activity)
                    .withSelectedColorRes(R.color.lightGrayWithRed)
                    .withIcon(R.drawable.ic_list_black_24dp)
                    .withTag(CONTACT_TAG);

            // Users bluetooth connections
            bluetooth.withDescription(R.string.bluetooth_activity)
                    .withSelectedColorRes(R.color.lightGrayWithRed)
                    .withIcon(R.drawable.ic_settings_bluetooth_black_24dp)
                    .withTag(BLUETOOTH_TAG);

            // Users Bluetooth Chart
            blueChart.withDescription(R.string.bluetooth_chart_activity)
                    .withSelectedColorRes(R.color.lightGrayWithRed)
                    .withIcon(R.drawable.ic_show_chart_black_24dp)
                    .withTag(BLUETOOTH_CHART_TAG);

            // Change email
            changeEmail.withDescription(R.string.change_email)
                    .withDescriptionTextColorRes(R.color.material_drawer_dark_selected)
                    .withSelectedColorRes(R.color.lightGrayWithRed)
                    .withTag(CHANGE_EMAIL_TAG);

            // Change password
            changePassword.withDescription(R.string.change_password)
                    .withDescriptionTextColorRes(R.color.material_drawer_dark_selected)
                    .withSelectedColorRes(R.color.lightGrayWithRed)
                    .withTag(CHANGE_PASSWORD_TAG);

            // Log out drawer item
            signOut.withDescriptionTextColorRes(R.color.wordColorRed)
                    .withDescription("Sign Out")
                    .withSelectedColorRes(R.color.lightGrayWithRed)
                    .withSelectedTextColor(Color.WHITE)
                    .withTag(SIGN_OUT_TAG);

            // Delete account drawer item
            deleteAccount.withDescriptionTextColorRes(R.color.wordColorRed)
                    .withDescription("Delete Account")
                    .withSelectedColorRes(R.color.lightGrayWithRed)
                    .withSelectedTextColor(Color.WHITE)
                    .withTag(DELETE_ACCOUNT_TAG);


            userDrawer =  new DrawerBuilder()
                    .withActivity(activity)
                    .withAccountHeader(accountHeader)
                    .withTranslucentStatusBar(false)
                    .withDisplayBelowStatusBar(true)
                    .withToolbar(toolbar)
                    .addDrawerItems(home,
                                    addContact,
                                    contactList,
                                    map,
                                    bluetooth,
                                    blueChart,
                                    new DividerDrawerItem(),
                                    changeEmail,
                                    changePassword,
                                    signOut,
                                    deleteAccount)
                    .withSelectedItem(-1)
                    .withOnDrawerListener(new Drawer.OnDrawerListener() {
                        @Override
                        public void onDrawerOpened(View drawerView) {
                            Log.i(TAG, "Drawer Opened");
                        }

                        @Override
                        public void onDrawerClosed(View drawerView) {
                            Log.i(TAG, "Drawer Closed");
                        }

                        @Override
                        public void onDrawerSlide(View drawerView, float slideOffset) {
                            Log.i(TAG, "Drawer Slide");
                        }
                    })
                    .build();

            // Set up the on click listener
            userDrawer.setOnDrawerItemClickListener(handleOnClick(userDrawer, activity));
            return userDrawer;
        }
        else{

            userProfile = new ProfileDrawerItem()
                    .withEmail("Email")
                    .withName("Name")
                    .withIcon(R.drawable.ic_account)
                    .withTextColor(Color.BLACK);

            // Login Drawer Item
            login = new PrimaryDrawerItem()
                    .withDescription("Login")
                    .withDescriptionTextColorRes(R.color.colorPrimaryDark)
                    .withTag(LOGIN_TAG);

            signUp = new PrimaryDrawerItem()
                    .withDescription("Sign Up")
                    .withDescriptionTextColorRes(R.color.colorPrimaryDark)
                    .withTag(SIGN_UP_TAG);

            currentUserAccountHeader = new AccountHeaderBuilder()
                    .withActivity(activity)
                    .withHeaderBackground(R.drawable.header_nav_background_adjusted)
                    .addProfiles(userProfile)
                    .build();


            userDrawer = new DrawerBuilder()
                    .withActivity(activity)
                    .withAccountHeader(accountHeader)
                    .withAccountHeader(currentUserAccountHeader)
                    .withTranslucentStatusBar(false)
                    .withDisplayBelowStatusBar(true)
                    .withToolbar(toolbar)
                    .addDrawerItems(
                            home,
                            map,
                            new DividerDrawerItem(),
                            login,
                            signUp)
                    .withSelectedItem(-1)
                    .build();

            // Set up the on click listener for the navigation drawer
            userDrawer.setOnDrawerItemClickListener(handleOnClick(userDrawer, activity));
            return userDrawer;
        }
    }

    @Override
    public boolean onProfileChanged(View view, IProfile profile, boolean current) {

        Log.i(TAG, "On Profile changed was called");
        return true;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        Log.i(TAG, "On Auth State Changed");
    }
}
