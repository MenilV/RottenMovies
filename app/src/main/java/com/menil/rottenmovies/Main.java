package com.menil.rottenmovies;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;


public class Main extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        //this does nothing
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        // e ovo cudo mi daje mogucnost mjenjanja Viewa na osnovu oznacene opcije
        // 3 days later... bra'o Menile, sad si se sjetio da pocnes citati svoje komentare!


        Fragment fragment;
        FragmentManager fragmentManager = getFragmentManager();
        Bundle args = new Bundle();
        args.putInt("position", position);

        switch(position){
            case 0://home fragment
                fragment = new HomeFragment();
                break;
            case 1://box office fragment
                /**
                 * CHANGE HERE FIRST
                 */
                //fragment = new BoxOfficeFragment();
                fragment = new HomeFragment();
                break;
                //fragment = new BoxOfficeFragment();
                //break;
            case 2://in theaters
            case 3://opening
                fragment = new OthersFragment();
                break;
            case 4://upcoming (currently displaying detail view) !fix api deserialization
            case 5://favourites (currently displaying detail view) !fix on long click event
                fragment = new DetailsFragment();
                break;
            default:
                fragment= new HomeFragment();
                break;
        }
        fragment.setArguments(args);
        switchContent(fragment);

    }
    public void switchContent(Fragment fragment) {

            getFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment).commit();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
}
