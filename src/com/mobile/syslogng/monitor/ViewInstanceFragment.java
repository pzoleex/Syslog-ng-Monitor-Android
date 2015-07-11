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



import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.MultiChoiceModeListener;


public class ViewInstanceFragment extends Fragment{

	public static final String ACTIONBAR_TITLE = "menu_title";
	
	
	private Map<Integer,String> itemsDisplayed = new LinkedHashMap<Integer, String>();
	private ArrayList<String> itemsSelected = new ArrayList<String>();
	private Context context;
	
	ListView listViewInstances;
	ImageView bImg;
	
	Integer sdk = android.os.Build.VERSION.SDK_INT;
	
	public ViewInstanceFragment(Context context){
		this.context = context;
	}
	
    public ViewInstanceFragment() {
        // Empty constructor required for fragment subclasses
    }

    ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	
    	
    	SQLiteManager instanceDataObject = new SQLiteManager(context);
        list = instanceDataObject.getInstancesData();
    	
        
        /*
    	 * Storing the items displayed in the List - Position and Primary Key.
    	 * Position starting from 0 and incremented. 
    	 * Position and Corresponding record's primary key is stored as key,value.
    	 *  
    	 */
        Integer iterator = 0;
        for(HashMap<String,String> temp : list){
        	itemsDisplayed.put(iterator++, temp.get("Key"));
        }
        
    	View rootView = inflater.inflate(R.layout.fragment_view_instance, container, false);
        
    	listViewInstances	 = (ListView) rootView.findViewById(R.id.listview_view_instance);
    	listViewInstances.setAdapter(getListViewAdapter(list));
    	
    	
    	
    	
    	
    	int i = getArguments().getInt(ACTIONBAR_TITLE);
        String actionbarTitle = getResources().getStringArray(R.array.menu_array)[i];
        
        getActivity().setTitle(actionbarTitle);
        
        
        
        this.listViewInstances.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        this.listViewInstances.setMultiChoiceModeListener(new MultiChoiceModeListener() {
        	
        	@Override
        	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        		MenuInflater inflater = mode.getMenuInflater();
        	        inflater.inflate(R.menu.context_menu, menu);
        	       
        	        return true;
        	}


        	@Override
        	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        		// TODO Auto-generated method stub
        		return false;
        	}


        	@Override
        	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        		//Responds to clicked item.
        		switch (item.getItemId()) {
        		
        			case R.id.delete_list_item:
        				if(deleteInstancesData()){
        					reloadCurrentFragment();
        					mode.finish();
        					Toast.makeText(context, context.getString(R.string.delete_success), Toast.LENGTH_LONG).show();
        				}
        				else{
        					Toast.makeText(context, context.getString(R.string.delete_failure), Toast.LENGTH_LONG).show();
        				}
        				
        				break;
        			case R.id.edit_list_item:
        				if(itemsSelected.size() == 1){
        					loadAddUpdateInstanceFragment(list);
        					mode.finish();
        				}
        				else{
        					Toast.makeText(context, context.getString(R.string.edit_validation_failure), Toast.LENGTH_LONG).show();
        				}
        				break;
        		
        		}
        		
        		return false;
        	}


        	@Override
        	public void onDestroyActionMode(ActionMode mode) {
        		// TODO Auto-generated method stub
        		listViewInstances.setActivated(false);
        	}


        	
        	@Override
        	public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        		if(checked == true){
        			
        			itemsSelected.add(Integer.toString(position));
        			listViewInstances.getChildAt(position).setActivated(true);
        			
        		}
        		else if(checked == false){
        			Integer removalPosition = itemsSelected.indexOf(position);
        			itemsSelected.remove(removalPosition);
        			listViewInstances.getChildAt(position).setActivated(false);

        		}
        		
        	}
        	
        });
        
       
        
        return rootView;
    }
    
    public Boolean deleteInstancesData(){
    	ArrayList<String> itemsToDelete = new ArrayList<String>();
    	SQLiteManager sManager = new SQLiteManager(context);
    	for(String item : itemsSelected){
    		itemsToDelete.add(itemsDisplayed.get(Integer.parseInt(item))); 
    	}
    	return sManager.deleteInstancesData(itemsToDelete);
    }
    
    public SimpleAdapter getListViewAdapter(ArrayList<HashMap<String, String>> list){
    	return new SimpleAdapter(getActivity().getBaseContext(), list, R.layout.rows_design, 
    			new String[] { "InstanceName", "HostName", "PortNumber"}, 
    			new int[] { R.id.textview_instance_name, R.id.textview_hostname, R.id.textview_portnumber });
    }
    
    public void reloadCurrentFragment(){
    	Bundle args = new Bundle();
    	Fragment viewInstanceFragment = new ViewInstanceFragment(context);
    	args.putInt(ViewInstanceFragment.ACTIONBAR_TITLE, 4);
    	viewInstanceFragment.setArguments(args);
    	getActivity().getFragmentManager().beginTransaction().replace(R.id.container, viewInstanceFragment).commit();
    	
    }
    
    public void loadAddUpdateInstanceFragment(ArrayList<HashMap<String,String>> instancesList){
    	
    	String key = null;
    	String instanceName = null;
    	String hostName = null;
    	String portNumber = null;
    	String certificateFileName = null;
    	String certificatePassword = null;
    	
    	for(HashMap<String,String> instanceData : instancesList){
    		key = instanceData.get("Key");
    		instanceName = instanceData.get("InstanceName");
    		hostName = instanceData.get("HostName");
    		portNumber = instanceData.get("PortNumber");
    		certificateFileName = instanceData.get("CertPath");
    		certificatePassword = instanceData.get("CertPass");
    		Log.i("instanceData", instanceData.toString());
    	}
    	
    	Bundle args = new Bundle();
    	Fragment fragment = new AddUpdateInstanceFragment(context, instanceName, hostName, portNumber, certificateFileName, certificatePassword, key);
		args.putInt(AddUpdateInstanceFragment.ACTIONBAR_TITLE, 3);
		fragment.setArguments(args);
		getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
		MainActivity.updateDrawer(3);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
    }


	

}
