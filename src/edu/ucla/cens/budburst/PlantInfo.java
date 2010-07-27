
package edu.ucla.cens.budburst;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.ucla.cens.budburst.data.Row;
import edu.ucla.cens.budburst.models.ObservationRow;
import edu.ucla.cens.budburst.models.PhenophaseRow;
import edu.ucla.cens.budburst.models.PlantRow;


public class PlantInfo extends Activity {

	private static final String TAG = "plant_info";
	protected static final int PHOTO_CAPTURE_CODE = 0;
	private static final int MENU_ADD_NOTE = 0;

	private ObservationRow observation;

	ArrayList<Button> buttonBar = new ArrayList<Button>();
	private BudburstDatabaseManager databaseManager;
	protected Long image_id;

	private ImageView img;
	private Bitmap bmOverlay;
	
	//Observation in editing before user submit.
	public class Temporary_obs{
		private boolean img_replaced = false; //A flag to show if image has been replaced
		private boolean img_removed = false; //A flag to show if image has been replaced
		private boolean note_edited = false; //A flag to show if image has been replaced
		private boolean saved = true; //A flag to show if it is saved or not
		private Long unsaved_image_id; //temporarily saved image id
		private String unsaved_note; //temporarily stored notes		
	}
	private Temporary_obs temporary_obs;
	
	private Intent global_intent;

	private final BroadcastReceiver mLoggedInReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	};

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
	    	
			//Check if note has been edited
			EditText note = (EditText) findViewById(R.id.notes);
			String current_note = note.getText().toString();
			if(!current_note.equals(observation.note))
				temporary_obs.saved = false;	
			
			if(temporary_obs.saved == false){
				new AlertDialog.Builder(PlantInfo.this)
				.setTitle("Question")
				.setMessage(getString(R.string.submit_question))
				.setPositiveButton("Yes",mClick) //BUTTON1
				.setNeutralButton("No",mClick) //BUTTON3
				.setNegativeButton("Cancel",mClick) //BUTTON2
				.show();
				return true;
			}
	    }
		return super.onKeyDown(keyCode, event);
	}
	
	DialogInterface.OnClickListener mClickSaveSubQuestion = 
		new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// TODO Auto-generated method stub
				if(whichButton == DialogInterface.BUTTON1){

					//Check if note has been edited
					EditText note = (EditText) findViewById(R.id.notes);
					String current_note = note.getText().toString();
					observation.note = current_note;
		
					//Check if img has been replaced
					if(temporary_obs.img_replaced == true){
						//Delete older picture file
						File file = new File(Budburst.OBSERVATION_PATH + temporary_obs.unsaved_image_id + ".jpg");
						if(file != null)
							file.delete();
						temporary_obs.img_replaced = false;
					}
					
					//Check if img has been removed
					if(temporary_obs.img_removed == true){
						//Delete older picture file
						File file = new File(Budburst.OBSERVATION_PATH + temporary_obs.unsaved_image_id + ".jpg");
						if(file != null)
							file.delete();
						temporary_obs.img_removed = false;
					}
					
					observation.time = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
					observation.put();
					
					temporary_obs.saved = true;
					Toast.makeText(PlantInfo.this,"Thank you for your observation!", Toast.LENGTH_SHORT).show();
					
					if(global_intent!=null)
						startActivity(global_intent);
					finish();	

				}
				else if(whichButton == DialogInterface.BUTTON3){

					//Check if note has been edited
					EditText note = (EditText) findViewById(R.id.notes);
					String current_note = note.getText().toString();
					observation.note = current_note;
		
					//Check if img has been replaced
					if(temporary_obs.img_replaced == true){
						//Delete older picture file
						File file = new File(Budburst.OBSERVATION_PATH + temporary_obs.unsaved_image_id + ".jpg");
						if(file != null)
							file.delete();
						temporary_obs.img_replaced = false;
					}
					
					//Check if img has been removed
					if(temporary_obs.img_removed == true){
						//Delete older picture file
						File file = new File(Budburst.OBSERVATION_PATH + temporary_obs.unsaved_image_id + ".jpg");
						if(file != null)
							file.delete();
						temporary_obs.img_removed = false;
					}
					
					observation.put();
					
					temporary_obs.saved = true;
					Toast.makeText(PlantInfo.this,"Thank you for your observation!", Toast.LENGTH_SHORT).show();
					
					if(global_intent!=null)
						startActivity(global_intent);
					finish();	

				}
				else{
					
				}
				
			}
		};
	
	DialogInterface.OnClickListener mClick = 
		new DialogInterface.OnClickListener(){
		public void onClick(DialogInterface dialog, int whichButton){
			if (whichButton == DialogInterface.BUTTON1){ 
				//Submit
				//Check the previous observation date is same as today
				if(observation.time == null || 
						observation.time.equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))){
					//Check if note has been edited
					EditText note = (EditText) findViewById(R.id.notes);
					String current_note = note.getText().toString();
					observation.note = current_note;
		
					//Check if img has been replaced
					if(temporary_obs.img_replaced == true){
						//Delete older picture file
						File file = new File(Budburst.OBSERVATION_PATH + temporary_obs.unsaved_image_id + ".jpg");
						if(file != null)
							file.delete();
						temporary_obs.img_replaced = false;
					}
					
					//Check if img has been removed
					if(temporary_obs.img_removed == true){
						//Delete older picture file
						File file = new File(Budburst.OBSERVATION_PATH + temporary_obs.unsaved_image_id + ".jpg");
						if(file != null)
							file.delete();
						temporary_obs.img_removed = false;
					}
					observation.time = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
					observation.put();
					temporary_obs.saved = true;
					Toast.makeText(PlantInfo.this,"Thank you for your observation!", Toast.LENGTH_SHORT).show();
					
					if(global_intent!=null)
						startActivity(global_intent);
					finish();	
				}
				else{
					new AlertDialog.Builder(PlantInfo.this)
					.setTitle("Question")
					.setMessage("Do you want to change the observation date to today?")
					.setPositiveButton("Yes",mClickSaveSubQuestion)
					.setNeutralButton("No", mClickSaveSubQuestion)
					.setNegativeButton("Cance", mClickSaveSubQuestion)
					.show();
				}
				
			}else if(whichButton == DialogInterface.BUTTON3){ 
				//No submit
				
				//Restore note
				EditText note = (EditText) findViewById(R.id.notes);
				note.setText(observation.note);
				
				//Restore replaced image 
				if(temporary_obs.img_replaced == true){
					
					//Delete new photo file
					File file = new File(Budburst.OBSERVATION_PATH + observation.image_id + ".jpg");
					if(file != null)
						file.delete();
					
					//Put new photo file
					observation.image_id = temporary_obs.unsaved_image_id;
					temporary_obs.img_replaced = false; //necessary?
				}
				
				//Restore removed image
				if(temporary_obs.img_removed == true){
					observation.image_id = temporary_obs.unsaved_image_id;
					temporary_obs.img_removed = false;
				}
				
				temporary_obs.saved = true;	
				if(global_intent != null)
					startActivity(global_intent);
				finish();	
				
			}else{ //Cancel
				temporary_obs.saved = true;
			}				
		}
	};
	
	
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		temporary_obs = new Temporary_obs(); 
		
		Log.d(TAG, "PlantInfoActivity created");		
		setContentView(R.layout.sampleinfo);
		
		img = (ImageView) this.findViewById(R.id.image);

		databaseManager = Budburst.getDatabaseManager();
		Bundle extras = getIntent().getExtras();
		int chrono = extras.getInt("chrono", 0);
		int stageID = extras.getInt("StageID", BudburstDatabaseManager.LEAVES);


		// get plant, phenophases, and observation models
		PlantRow plant = (PlantRow) databaseManager.getDatabase("plant").find(extras.getLong("PlantID"));
		ArrayList<Row> phenophases = plant.species().phenophases(stageID);
		PhenophaseRow phenophase = (PhenophaseRow) phenophases.get(chrono);
		observation = plant.observations(phenophase);

		
		//set name
		//TextView name = (TextView) this.findViewById(R.id.name);
		//name.setText(plant.species().common_name+" - "+phenophase.name);
		setTitle(plant.species().common_name+" - "+phenophase.name);
		//set phenophase description
		TextView phenophase_comment = (TextView) this.findViewById(R.id.phenophase_info_text);
		phenophase_comment.setText(phenophase.getAboutText(plant.species().protocol_id));

		//set phenophase state text
		//TextView state = (TextView) this.findViewById(R.id.state);
		//state.setText(phenophase.name);
		
		View take_photo = this.findViewById(R.id.take_photo);
		take_photo.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				File ld = new File(Budburst.OBSERVATION_PATH);
				if (ld.exists()) {
					if (!ld.isDirectory()) {
						// Should probably inform user ... hmm!
						Toast.makeText(PlantInfo.this, "Error: Please check your sdcard.", Toast.LENGTH_SHORT).show();
						PlantInfo.this.finish();
					}
				} else {
					if (!ld.mkdir()) {
						Toast.makeText(PlantInfo.this, "Error: Please check your sdcard.", Toast.LENGTH_SHORT).show();
						PlantInfo.this.finish();
					}
				}

				image_id = new Date().getTime();

				Intent mediaCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				mediaCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, 
						Uri.fromFile(new File(Budburst.OBSERVATION_PATH, image_id + ".jpg")));
				startActivityForResult(mediaCaptureIntent, PHOTO_CAPTURE_CODE);
			}
		});
		
		View remove_photo = this.findViewById(R.id.no_photo);
		remove_photo.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				temporary_obs.unsaved_image_id = observation.image_id;
				temporary_obs.saved = false;
				temporary_obs.img_removed = true;

				observation.image_id = new Long(0);
				showReplaceRemovePhotoButtons();
			}
		});
		
		//show replace image/add image/remove image stuff
		showReplaceRemovePhotoButtons();
		
		if(observation != null) {
			
			//Set Pheonphase text
			TextView phenophase_text = (TextView) this.findViewById(R.id.phenophase_text);
			phenophase_text.setText(phenophase.name);
			
			//set date
			TextView timestamp = (TextView) this.findViewById(R.id.timestamp_text);
			timestamp.setText(observation.time + " ");
			
			//put the note in the edittext
			EditText note = (EditText) this.findViewById(R.id.notes);
			note.setText(observation.note);
			
			//Make make_obs_text unvisible
			this.findViewById(R.id.make_obs_text).setVisibility(View.GONE);
			
			//show image
			
		} else {
			this.findViewById(R.id.timestamp_text).setVisibility(View.GONE);
			this.findViewById(R.id.timestamp_label).setVisibility(View.GONE);
			
			//Set Phenophase text
			TextView phenophase_text = (TextView) this.findViewById(R.id.phenophase_text);
			phenophase_text.setText(phenophase.name);
			
			observation = new ObservationRow();
			observation.species_id = plant.species_id;
			observation.phenophase_id = phenophase._id;
			observation.site_id = plant.site_id;
		}

		
		//////////////Stage Button Bar////////////////////
		//Leave button
		buttonBar.add((Button) this.findViewById(R.id.button1));
		buttonBar.get(0).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Intent intent = getIntent();
				intent.putExtra("StageID", BudburstDatabaseManager.LEAVES);
				intent.putExtra("chrono", 0);
				
				//Check if note has been edited
				EditText note = (EditText) findViewById(R.id.notes);
				String current_note = note.getText().toString();
				if(!current_note.equals(observation.note))
					temporary_obs.saved = false;					
				
				//Check if there is unsaved data
				if(temporary_obs.saved == true){
					startActivity(intent);
					finish();
				}
				else{ //Prompts users where save it or not
					global_intent = intent;				
					
					new AlertDialog.Builder(PlantInfo.this)
					.setTitle("Question")
					.setMessage(getString(R.string.submit_question))
					.setPositiveButton("Yes",mClick) //BUTTON1
					.setNeutralButton("No",mClick) //BUTTON3
					.setNegativeButton("Cancel",mClick) //BUTTON2
					.show();
				}
			}
		});

		
		//Flower button
		buttonBar.add((Button) this.findViewById(R.id.button2));
		buttonBar.get(1).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = getIntent();
				intent.putExtra("StageID", BudburstDatabaseManager.FLOWERS);
				intent.putExtra("chrono", 0);

				//Check if note has been edited
				EditText note = (EditText) findViewById(R.id.notes);
				String current_note = note.getText().toString();
				if(!current_note.equals(observation.note))
					temporary_obs.saved = false;
				
				//Check if there is unsaved data
				if(temporary_obs.saved == true){
					startActivity(intent);
					finish();
				}
				else{ //Prompts users where save it or not
					global_intent = intent;
				
					new AlertDialog.Builder(PlantInfo.this)
					.setTitle("Question")
					.setMessage(getString(R.string.submit_question))
					.setPositiveButton("Yes",mClick) //BUTTON1
					.setNeutralButton("No",mClick) //BUTTON3
					.setNegativeButton("Cancel",mClick) //BUTTON2	
					.show();
				}

			}
		});

		//Fruits button
		buttonBar.add((Button) this.findViewById(R.id.button3));
		buttonBar.get(2).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = getIntent();
				intent.putExtra("StageID", BudburstDatabaseManager.FRUITS);
				intent.putExtra("chrono", 0);

				//Check if note has been edited
				EditText note = (EditText) findViewById(R.id.notes);
				String current_note = note.getText().toString();
				if(!current_note.equals(observation.note))
					temporary_obs.saved = false;
				
				//Check if there is unsaved data
				if(temporary_obs.saved == true){
					startActivity(intent);
					finish();
				}
				else{ //Prompts users where save it or not
					global_intent = intent;
					
					new AlertDialog.Builder(PlantInfo.this)
					.setTitle("Question")
					.setMessage(getString(R.string.submit_question))
					.setPositiveButton("Yes",mClick) //BUTTON1
					.setNeutralButton("No",mClick) //BUTTON3
					.setNegativeButton("Cancel",mClick) //BUTTON2
					.show();
				}
			}
		});

		// set selected button
		buttonBar.get(stageID).setSelected(true);

		//populate the buttonbar
		LinearLayout phenophaseBar = (LinearLayout) this.findViewById(R.id.phenophase_bar);

		for (Iterator<Row> i = phenophases.iterator(); i.hasNext();) {
			final PhenophaseRow current = (PhenophaseRow) i.next();
			final int phenophaseChrono = phenophases.indexOf(current);
			ImageView button = new ImageView(this);
			// button.setImageBitmap(Drawable.createFromPath(pathName)
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent intent = getIntent();
					intent.putExtra("chrono", phenophaseChrono);
					
					//Check if note has been edited
					EditText note = (EditText) findViewById(R.id.notes);
					String current_note = note.getText().toString();
					if(!current_note.equals(observation.note))
						temporary_obs.saved = false;	

					//Check if there is unsaved data
					if(temporary_obs.saved == true){
						startActivity(intent);
						finish();
					}
					else{ //Prompts users where save it or not
						global_intent = intent;
						new AlertDialog.Builder(PlantInfo.this)
						.setTitle("Question")
						.setMessage(getString(R.string.submit_question))
						.setPositiveButton("Yes",mClick) //BUTTON1
						.setNeutralButton("No",mClick) //BUTTON3
						.setNegativeButton("Cancel",mClick) //BUTTON2
						.show();
					}
				}
			});
			button.setPadding(0, 0, 5, 0);

			Bitmap icon = overlay(BitmapFactory.decodeStream(
					current.getImageStream(this,plant.species().protocol_id)));

			if (chrono != phenophaseChrono)
				icon = overlay(icon, BitmapFactory.decodeResource(
					getResources(),R.drawable.translucent_gray35));

			ObservationRow current_obs = plant.observations(current);
			if (current_obs != null && current_obs.isSaved())
				icon = overlay(icon, BitmapFactory.decodeResource(
					getResources(),R.drawable.check_mark));

			button.setImageBitmap(icon);
			phenophaseBar.addView(button);
		}

		//Save Button
		final int final_chrono = chrono;
		Button save = (Button) this.findViewById(R.id.save);
		save.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) { 
				
				Intent intent = getIntent();
				intent.putExtra("chrono", final_chrono);
				
				//Check the previous observation date is same as today
				if(observation.time == null || 
						observation.time.equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))){
					Toast.makeText(PlantInfo.this, 
							"Thank you for your observation!", Toast.LENGTH_SHORT).show();				

					EditText note = (EditText) findViewById(R.id.notes);
					//Check if img has been replaced
					if(temporary_obs.img_replaced == true){
						//Delete older picture file
						File file = new File(Budburst.OBSERVATION_PATH + 
								temporary_obs.unsaved_image_id + ".jpg");
						if(file != null)
							file.delete();
						temporary_obs.img_replaced = false;
					}
					
					//Check if img has been removed
					if(temporary_obs.img_removed == true){
						//Delete older picture file
						File file = new File(Budburst.OBSERVATION_PATH + 
								temporary_obs.unsaved_image_id + ".jpg");
						if(file != null)
							file.delete();
						temporary_obs.img_removed = false;
					}
					
					observation.note = note.getText().toString();
					observation.time = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
					observation.put();
					
					startActivity(intent);
					finish();
				}
				else{
					global_intent = intent;
					
					new AlertDialog.Builder(PlantInfo.this)
					.setTitle("Question")
					.setMessage("Do you want to change the observation date to today?")
					.setPositiveButton("Yes",mClickSaveSubQuestion)
					.setNeutralButton("No", mClickSaveSubQuestion)
					.setNegativeButton("Cance", mClickSaveSubQuestion)
					.show();					
				}
				
				/*
				EditText note = (EditText) findViewById(R.id.notes);
				
				Intent intent = getIntent();
				intent.putExtra("chrono", final_chrono);
				startActivity(intent);
				
				//Check if img has been replaced
				if(temporary_obs.img_replaced == true){
					//Delete older picture file
					File file = new File(Budburst.OBSERVATION_PATH + temporary_obs.unsaved_image_id + ".jpg");
					if(file != null)
						file.delete();
					temporary_obs.img_replaced = false;
				}
				
				//Check if img has been removed
				if(temporary_obs.img_removed == true){
					//Delete older picture file
					File file = new File(Budburst.OBSERVATION_PATH + temporary_obs.unsaved_image_id + ".jpg");
					if(file != null)
						file.delete();
					temporary_obs.img_removed = false;
				}
				
				observation.note = note.getText().toString();
				observation.time = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
				observation.put();
				
				Toast.makeText(PlantInfo.this, "Thank you for your observation!", Toast.LENGTH_SHORT).show();				

				finish();
				*/
			}
		});

		//Cancel Button
		Button cancel = (Button) this.findViewById(R.id.cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) { 
				
				Intent intent = new Intent(PlantInfo.this, PlantList.class);
				startActivity(intent);
				startActivity(PlantInfo.this.getIntent());
				finish();
			}
		});

		registerReceiver(mLoggedInReceiver, new IntentFilter(Constants.INTENT_ACTION_LOGGED_OUT));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.d(TAG, "PlantInfoActivity destroy");
		img.setImageBitmap(null);
		unregisterReceiver(mLoggedInReceiver);	
	}
	
	private Bitmap overlay(Bitmap... bitmaps) {
		if (bitmaps.length == 0)
			return null;

		bmOverlay = Bitmap.createBitmap(bitmaps[0].getWidth(), bitmaps[0].getHeight(), bitmaps[0].getConfig());

		Canvas canvas = new Canvas(bmOverlay);
		for (int i = 0; i < bitmaps.length; i++)
			canvas.drawBitmap(bitmaps[i], new Matrix(), null);
		
		return bmOverlay;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		//if (image_id != null)
			//savedInstanceState.putLong("image_id", image_id);

		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Log.d(TAG, "restore instance state");
		//image_id = savedInstanceState.getLong("image_id");
		//if(image_id != null)
			//observation.image_id = image_id;
		
		//showReplaceRemovePhotoButtons();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// You can use the requestCode to select between multiple child
		// activities you may have started. Here there is only one thing
		// we launch.
		Log.d(TAG, "onActivityResult");
		if (requestCode == PHOTO_CAPTURE_CODE) {

			// This is a standard resultCode that is sent back if the
			// activity doesn't supply an explicit result. It will also
			// be returned if the activity failed to launch.
			if (resultCode == RESULT_CANCELED) {
				Log.d(TAG, "Photo returned canceled code.");
				Toast.makeText(this, "Picture cancelled.", Toast.LENGTH_SHORT).show();
			} else {
				
				if (image_id != null) {
					temporary_obs.saved = false;
					observation.image_id = image_id;
					showReplaceRemovePhotoButtons();
				}
			}
		}
	}

	private void showReplaceRemovePhotoButtons() {
		TextView no_photo_text = (TextView) this.findViewById(R.id.no_photo_text);
		TextView replace_photo_text = (TextView) this.findViewById(R.id.take_photo_text);
		View remove_photo = this.findViewById(R.id.no_photo);

		img.setImageBitmap(null);
		
		if(observation != null && observation.hasImage()) {
			File file = new File(observation.getImagePath());
			
			//In order to reduce bitmap size for high resolution camera like nexus one. 
			//In this case, limit is 100kb.
			BitmapFactory.Options options = new BitmapFactory.Options();
			if(file.length() > 500000)
 				options.inSampleSize=4;
			else if(file.length() > 100000)
				options.inSampleSize=2;
			else
				options.inSampleSize=1;
			
			img.setImageBitmap(BitmapFactory.decodeFile(observation.getImagePath(),options));

			no_photo_text.setText("Remove Photo");
			remove_photo.setVisibility(View.VISIBLE);
			replace_photo_text.setText("Replace Photo");
		} else {
			remove_photo.setVisibility(View.GONE);
			replace_photo_text.setText("Add Photo");
		}
	}
	
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		menu.add(0, MENU_ADD_NOTE, 0, "Set Note").setIcon(android.R.drawable.ic_menu_edit);
//
//		return super.onCreateOptionsMenu(menu);
//	}


}
