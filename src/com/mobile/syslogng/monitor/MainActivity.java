/*

	This program is free software: you can redistribute it and/or modify

    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
 */

package com.mobile.syslogng.monitor;


import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String APP_NAME = "Syslog-ng Monitor";
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private SharedPreferences preference = null;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] menuItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*
		 * 
		 * Setting Shared Preferences for Identifying First run of the application 
		 * and creating Database
		 * 
		 */
		
		preference = getSharedPreferences("com.mobile.syslogng.monitor",MODE_PRIVATE);
		
		if(preference.getBoolean("firstRun",true))
		{
			SQLiteDatabase instanceDb = openOrCreateDatabase("instances.db",SQLiteDatabase.CREATE_IF_NECESSARY, null);
			
			instanceDb.execSQL("DROP TABLE IF EXISTS" + " INSTANCE_TABLE"); // Please Remove it during Production
			instanceDb.execSQL("CREATE TABLE if not exists INSTANCE_TABLE(_id INTEGER PRIMARY KEY, INSTANCE_NAME TEXT, INSTANCE_HOSTNAME TEXT, PORT_NUMBER TEXT)");
			
			
			preference.edit().putBoolean("firstRun", false).commit();
		}
		
		mTitle  = getTitle();
		mDrawerTitle = APP_NAME;
		menuItems = getResources().getStringArray(R.array.menu_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, menuItems));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(
				this,                  /* host Activity */
				mDrawerLayout,         /* DrawerLayout object */
				R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
				R.string.drawer_open,  /* "open drawer" description for accessibility */
				R.string.drawer_close  /* "close drawer" description for accessibility */
				) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			selectItem(0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {


		/*
		 * Add Menu Items to the right if needed
		 */

		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		// If the nav drawer is open, hide action items related to the content view
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons if need be
		return false;

	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		
		Integer caseNumber = position+1;
		Fragment fragment;
		Bundle args = new Bundle();
		FragmentManager fragmentManager = getFragmentManager();
		
		switch(caseNumber)
		{
			case 1:
				fragment = new HomeFragment();
				args.putInt(HomeFragment.ACTIONBAR_TITLE, position);
				fragment.setArguments(args);
				fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
				break;
				
			case 2:
				fragment = new RunCommandFragment(getApplicationContext());
				args.putInt(AddInstanceFragment.ACTIONBAR_TITLE, position);
				fragment.setArguments(args);
				fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
				break;
				
			case 3:
				fragment = new ImportCertificateFragment(getApplicationContext());
				args.putInt(ViewInstanceFragment.ACTIONBAR_TITLE, position);
				fragment.setArguments(args);
				fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
				break;
				
			case 4:
				fragment = new AddInstanceFragment();
				args.putInt(AddInstanceFragment.ACTIONBAR_TITLE, position);
				fragment.setArguments(args);
				fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
				break;
				
			case 5:
				fragment = new ViewInstanceFragment(getApplicationContext());
				args.putInt(ViewInstanceFragment.ACTIONBAR_TITLE, position);
				fragment.setArguments(args);
				fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
				break;
				
			
			
			default:
				break;
		}
		
		
		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(menuItems[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	

}
