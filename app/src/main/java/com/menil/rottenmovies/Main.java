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



public class Main extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    final Context mContext=this;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        /**
         * ovo iznad je zlo!!@!!
         */
        setContentView(R.layout.activity_main);



        //TODO: make this work

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
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    mContext,R.style.CustomDialog);
            // set title
            alertDialogBuilder.setTitle("Exit application?");

            // set dialog message
            alertDialogBuilder
                    .setIcon(R.drawable.ic_action_warning)
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, close app
                            Main.this.finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, just close the dialog box
                            dialog.cancel();
                        }
                    });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();

        }
        return false;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        Fragment fragment;
        FragmentManager fragmentManager = getFragmentManager();
        Bundle args = new Bundle();
        args.putInt("position", position);
        //String tag=null;
        switch (position) {
            case 0://home fragment
                fragment = new HomeFragment();
                break;
            case 1://box office fragment
                /**
                 * CHANGE HERE FIRST
                 */
                //fragment = new BoxOfficeFragment();
            /*    fragment = new HomeFragment();
                break;*/
                fragment = new BoxOfficeFragment();
                break;
            case 2://in theaters
                fragment = new OthersFragment();
                break;
            case 3://opening
                fragment = new OthersFragment();
                break;
            case 4://upcoming
                fragment = new OthersFragment();
                break;
            case 5://favourites (currently displaying detail view)
                //TODO: make on click event on the images to store to favourites
                fragment = new HomeFragment();
                break;
            default:
                fragment = new HomeFragment();
                break;
        }
        fragment.setArguments(args);
        switchContent(fragment);

    }

    public void switchContent(Fragment fragment) {

        getFragmentManager().beginTransaction()
                .replace(R.id.container, fragment).addToBackStack(null).commit();
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
