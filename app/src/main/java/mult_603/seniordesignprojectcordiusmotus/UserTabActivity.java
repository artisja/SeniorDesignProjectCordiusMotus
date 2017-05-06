package mult_603.seniordesignprojectcordiusmotus;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import java.util.ArrayList;
import java.util.List;

/**
 * This Activity controls all the fragments that the user that logs in will see
 */
public class UserTabActivity extends AppCompatActivity {

    private static final String TAG = UserTabActivity.class.getSimpleName();
    private Drawer drawerResult;
    private AccountHeader headerResult;
    private NavigationDrawerHandler navHandler;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private int[] tabIcons = {R.drawable.ic_add_circle_black_24dp,
                                R.drawable.ic_list_black_24dp,
                                R.drawable.ic_settings_bluetooth_black_24dp,
                                R.drawable.ic_show_chart_black_24dp};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_tab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        navHandler = new NavigationDrawerHandler(this, savedInstanceState, getApplicationContext(), toolbar);
        headerResult = navHandler.setAccountHeader(this, savedInstanceState, getApplicationContext());
        drawerResult = navHandler.setUserDrawer(this, headerResult, toolbar);

        int defaultPage = 0;
        int page = getIntent().getIntExtra("Page", defaultPage);
        viewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager.setCurrentItem(page);
        tabLayout.setupWithViewPager(viewPager);
        // Icons make the text distort currently

    }

    /**
     * Add tab icons
     * Ended up not using this anymore because of limited space
     */
    private void setUpTabIcons(){
        try {
            tabLayout.getTabAt(0).setIcon(tabIcons[0]);
            tabLayout.getTabAt(1).setIcon(tabIcons[1]);
            tabLayout.getTabAt(2).setIcon(tabIcons[2]);
            tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        }
        catch(NullPointerException n){
            Log.i(TAG, "Null pointer exception: " + n.getMessage());
        }
    }

    /**
     * Set up view pager add fragments and set the adapter for it
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new UserAddContactFragment(), "Add Contact");
        adapter.addFragment(new UserContactListActivityFragment(), "Contact List");
        adapter.addFragment(new UserBluetoothListFragment(), "Bluetooth Devices");
        adapter.addFragment(new UserBluetoothChartFragment(), "Bluetooth Chart");
        viewPager.setAdapter(adapter);
    }

    /**
     * Class for holding the list of fragments that the tab bar controls
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public void onResume(){
        super.onResume();
        headerResult = navHandler.getHeader();
        drawerResult = navHandler.getDrawer();
    }
}