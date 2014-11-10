package com.menil.rottenmovies;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;


public class Main extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    final Context mContext = this;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode ==KeyEvent.KEYCODE_HOME){
            Main.this.finish();
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getFragmentManager().findFragmentByTag("HOME").isVisible()) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        mContext, R.style.CustomDialog);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        /**
         * ovo iznad je zlo!!@!!
         */
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

        Fragment fragment;
        FragmentManager fragmentManager = getFragmentManager();
        Bundle args = new Bundle();
        args.putInt("position", position);
        String tag=null;

        switch (position) {
            case 0://home fragment
                fragment = new HomeFragment();
                tag="HOME";
                break;
            case 1://box office fragment
                fragment = new BoxOfficeFragment();
                tag="BOXOFFICE";
                break;
            case 2://in theaters fragment
                fragment = new OthersFragment();
                tag="OTHERS";
                break;
            case 3://opening fragment
                fragment = new OthersFragment();
                tag="OTHERS";
                break;
            case 4://upcoming fragment
                fragment = new OthersFragment();
                tag="OTHERS";
                break;
            case 5://favourites (currently displaying detail view)
                //TODO: make on click event on the images to store to favourites
                fragment = new HomeFragment();
                tag="HOME";
                break;
            default:
                fragment = new HomeFragment();
                tag="OTHERS";
                break;
        }

        fragment.setArguments(args);
        switchContent(fragment,tag);
    }

    public void switchContent(Fragment fragment, String TAG) {

        getFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, TAG).addToBackStack(TAG).commit();
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
