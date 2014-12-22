package com.menil.rottenmovies;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

import com.facebook.AppEventsLogger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Main extends FragmentActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    final Context mContext = this;
    private CharSequence mTitle;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            Main.this.finish();
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getSupportFragmentManager().findFragmentByTag("HOME").isVisible()) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        mContext);
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
                //this is used to return the user to Home before asking him to quit the app. serves like a "double back to exit" option
                getSupportFragmentManager().popBackStack("HOME", 0);
            }


        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Logs 'app deactivate' App Event
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Logs 'install' and 'app active' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        /*if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            HomeFragment homeFragment = new HomeFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, homeFragment, "HOME")
                    .commit();
        } else {
            // Or set the fragment from restored state info
           HomeFragment homefragment = (HomeFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.container);
        }*/
        setContentView(R.layout.activity_main);

        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.menil.rottenmovies", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));

            }
        } catch (PackageManager.NameNotFoundException|NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
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
        FragmentManager fm = getSupportFragmentManager();
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

}
