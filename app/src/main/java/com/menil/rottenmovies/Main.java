package com.menil.rottenmovies;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;


public class Main extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    final Context mContext = this;
    private CharSequence mTitle;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            Main.this.finish();
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getFragmentManager().findFragmentByTag("HOME").isVisible()) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        mContext);//, R.layout.custom_dialog);
                // set title
                alertDialogBuilder.setTitle("Exit application?");

                // set dialog message
                alertDialogBuilder
                        .setIcon(R.drawable.ic_action_warning)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close app
                                Main.this.finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close the dialog box
                                dialog.cancel();
                            }
                        });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            } else {
                getFragmentManager().popBackStack();
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        /**
         * ovo iznad je zlo!!@!! ali radi :D
         */
        setContentView(R.layout.activity_main);

        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        //this does nothing
    }

    public boolean isConnectedToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        Fragment fragment;

        String tag;
        switch (position) {
            case 0://home fragment
                fragment = new HomeFragment();
                tag = "HOME";
                break;
            case 1://box office fragment
                if (!isConnectedToInternet()) {
                    fragment = new HomeFragment();
                    tag = "HOME";
                    Toast.makeText(getApplicationContext(), "No Internet connection.\nReturning to Home...", Toast.LENGTH_LONG).show();
                } else {
                    fragment = new BoxOfficeFragment();
                    tag = "BOXOFFICE";
                }
                break;
            case 2://in theaters fragment
                if (!isConnectedToInternet()) {
                    fragment = new HomeFragment();
                    tag = "HOME";
                    Toast.makeText(getApplicationContext(), "No Internet connection.\nReturning to Home...", Toast.LENGTH_LONG).show();
                } else {
                    fragment = new InTheatersFragment();
                    tag = "INTHEATERS";
                }
                break;
            case 3://opening fragment
                if (!isConnectedToInternet()) {
                    fragment = new HomeFragment();
                    tag = "HOME";
                    Toast.makeText(getApplicationContext(), "No Internet connection.\nReturning to Home...", Toast.LENGTH_LONG).show();
                } else {
                    fragment = new BoxOfficeFragment();
                    tag = "OPENING";
                }
                break;
            case 4://upcoming fragment
                if (!isConnectedToInternet()) {
                    fragment = new HomeFragment();
                    tag = "HOME";
                    Toast.makeText(getApplicationContext(), "No Internet connection.\nReturning to Home...", Toast.LENGTH_LONG).show();
                } else {
                    fragment = new BoxOfficeFragment();
                    tag = "UPCOMING";
                }
                break;
            case 5://favourites fragment
                fragment = new FavouritesFragment();
                tag = "FAVOURITES";
                break;
            case 6://about fragment
                fragment = new AboutFragment();
                tag = "ABOUT";
                break;
            default://Home is the default view
                fragment = new HomeFragment();
                tag = "HOME";
                break;
        }
        Bundle args = new Bundle();

        args.putInt("position", position);
        args.putString("tag", tag);
        fragment.setArguments(args);
        switchContent(fragment, tag);

    }

    public void switchContent(Fragment fragment, String TAG) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Boolean found = false;
        for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
            if (fm.findFragmentByTag(TAG) == fm.findFragmentByTag(fm.getBackStackEntryAt(i).getName()))
                found = true;
        }
        if (found)
            fm.popBackStack(TAG, 0);
        else
            ft.replace(R.id.container, fragment, TAG).addToBackStack(TAG).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // if (!mNavigationDrawerFragment.isDrawerOpen()) {
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        getMenuInflater().inflate(R.menu.main, menu);
        //restoreActionBar();
        //  return true;
        // }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (item.getItemId() == R.id.action_exit)
            finish();
        else if (item.getItemId() == R.id.action_search) {

            SearchFragment fragment = new SearchFragment();
            switchContent(fragment, "SEARCH");
            //item.expandActionView();
        }
        //return id == R.id.action_settings || super.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

}
