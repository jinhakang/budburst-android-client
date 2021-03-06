package edu.ucla.cens.budburst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.ucla.cens.budburst.data.Row;
import edu.ucla.cens.budburst.helper.ImageAdapter;
import edu.ucla.cens.budburst.models.PlantRow;

public class PlantList extends ListActivity {

	private ImageAdapter adapter;
	private static final String TAG = "PlantList";

	private static final String ITEM_COMMON_NAME = "name";
	private static final String ITEM_SPECIES_NAME = "description";
	private static final String ITEM_IMG = "icon";

	private static final int MENU_ADD = 0;
	private static final int MENU_ONE = 1;
	private static final int MENU_SETTINGS = 2;
	private static final int MENU_SYNC = 3;

	protected static final int CONTEXT_REMOVE = 0;
	private static final int LOGIN_FINISHED = 0;

	Button button1;
	Button button2;
	Button button3;
	private BudburstDatabaseManager databaseManager;
	private ArrayList<HashMap<String, String>> data;

	
	private final BroadcastReceiver mLoggedInReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plant_list);

		//To display hello, user id
		//TextView textView = (TextView) this.findViewById(R.id.hello_text);
		//String username_string = new String(PreferencesManager.currentUser(this));
		//textView.setText("Hello " + username_string + "!");
		////////////////////////

		databaseManager = Budburst.getDatabaseManager();

		button1 = (Button) this.findViewById(R.id.button1);
		button1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// currentTask = new
				// GrabCampaigns().execute("http://t5l-kullect.appspot.com/list?query=featured");
			}
		});

		button2 = (Button) this.findViewById(R.id.button2);
		button2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(PlantList.this, "Coming soon..!", Toast.LENGTH_SHORT).show();
				// currentTask = new
				// GrabCampaigns().execute("http://t5l-kullect.appspot.com/list?query=new");
			}
		});
		button1.setSelected(true);
		registerReceiver(mLoggedInReceiver, new IntentFilter(Constants.INTENT_ACTION_LOGGED_OUT));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		unregisterReceiver(mLoggedInReceiver);
	}

	@Override
	public void onResume() {
		super.onResume();


		ArrayList<Row> plants = databaseManager.getDatabase("plant").all();
		data = new ArrayList<HashMap<String, String>>();
		for (Iterator<Row> i = plants.iterator(); i.hasNext();) {
			HashMap<String, String> map = new HashMap<String, String>();
			PlantRow current = (PlantRow) i.next();
			map.put("name", current.species().common_name);
			map.put("description", current.species().species_name);
			map.put("icon", current.species().getImagePath());
			map.put("_id", current._id.toString());
			data.add(map);
		}

		adapter = new ImageAdapter(this, data, R.layout.list_item, 
				new String[] { ITEM_COMMON_NAME, ITEM_SPECIES_NAME, ITEM_IMG }, 
				new int[] { R.id.name,
				R.id.description, R.id.icon });
		setListAdapter(this.adapter);

		// set for long clicks
		getListView().setOnCreateContextMenuListener(ContextMenuListener);

		//showUserName();
	}


	private final OnCreateContextMenuListener ContextMenuListener = new OnCreateContextMenuListener() {

		public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
			menu.setHeaderTitle("Plant Actions");
			// menu.add(0, CONTEXT_REMOVE, 0, "Remove");
			// menu.add(0, CONTEXT_SHOW_ON_MAP, 1, "Show on map");
			// menu.add(0, CONTEXT_VIEW, 2, "View tag");
		}
	};

	// @Override
	// public boolean onContextItemSelected(MenuItem item) {
	// AdapterView.AdapterContextMenuInfo menuInfo =
	// (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	//
	// switch (item.getItemId()) {
	// case CONTEXT_REMOVE:
	// databaseManager.getDatabase("plant").find(item.id).remove();
	// this.adapter.notifyDataSetChanged();
	//			
	// break;
	// }
	//
	// return super.onContextItemSelected(item);
	// }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		Intent launchPlantIntent = new Intent(this, PlantInfo.class);
		launchPlantIntent.putExtra("PlantID", Long.parseLong(data.get(position).get("_id")));
		launchPlantIntent.putExtra("phase", "leaves");
		startActivity(launchPlantIntent);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_ADD, 0, "Add Plant").setIcon(android.R.drawable.ic_menu_add);
		menu.add(0, MENU_SETTINGS, 2, "Settings").setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(0, MENU_SYNC, 3, "Sync").setIcon(android.R.drawable.ic_menu_rotate);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;

		switch (item.getItemId()) {
		case MENU_ADD:
			intent = new Intent(this, AddPlant.class);
			this.startActivity(intent);

			break;
		case MENU_SYNC:
			if(checkNetwork()){
				intent = new Intent(this, SyncDatabases.class);
				this.startActivity(intent);
			}
			else{
				Toast.makeText(PlantList.this, "Please check network status.", Toast.LENGTH_SHORT).show();
			}
			break;
		 case MENU_SETTINGS:
		 intent = new Intent(this, SettingsScreen.class);
		 this.startActivity(intent);
		
		 break;
		case MENU_ONE:
			// intent = new Intent(this, Help.class);
			// this.startActivity(intent);
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private boolean checkNetwork(){
		ConnectivityManager mgr = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
		
		//Check if wifi is available
		NetworkInfo ni = mgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(ni.isConnected())
			return true;
		
		//Check if mobile network is available
		ni = mgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if(ni.isConnected())
			return true;
		
		return false;
	}
	
	// added by EG to try to learn how to do this... add name after "Hello"
	protected void showUserName() {
		// Display user name at top of screen
		TextView textView = (TextView) this.findViewById(R.id.hello_text);

		String username_string = new String(PreferencesManager.currentUser(this));
		textView.setText("Hello " + username_string + "!");

	}
}