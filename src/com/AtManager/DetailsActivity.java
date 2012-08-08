package com.AtManager;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

public class DetailsActivity extends ListActivity {
	Bundle extras;
	ArrayList<String> lstData;
	ArrayAdapter<String> adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview);
		
		extras = getIntent().getExtras();
		String subjName = extras.getString("subjectName");
		lstData = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,lstData);
		setListAdapter(adapter);
		SQLiteDatabase db = openOrCreateDatabase("Manager",Context.MODE_PRIVATE,null);
		Cursor c = db.query(subjName, null, null, null, "month", null, null);
		String dt;
		c.moveToNext();
		for(int i=0;i<c.getCount();i++){
			dt = c.getInt(0)+"/"+c.getInt(1)+"/"+c.getInt(2);
			lstData.add(dt);
		}
		adapter.notifyDataSetChanged();
		Context context = getApplicationContext();
		CharSequence text = "Enter Subject names";
		Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
		toast.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.detailsmenu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		System.out.println("itemSelected");
		final String sName = extras.getString("subjectName");
		
		switch(item.getItemId()){
		
		case R.id.menuSearchMonth:
								
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Search By Month");
			builder.setMessage("Enter the month(Integer): ");
			
			final EditText txtMonth = new EditText(this);
			builder.setView(txtMonth);
			
			builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	String value = txtMonth.getText().toString().trim();
                	SQLiteDatabase db = openOrCreateDatabase("Manager",Context.MODE_PRIVATE,null);
                	Cursor c = db.query(sName, null, "month="+value, null, null, null, null);
                	ArrayList<String> lstData = new ArrayList<String>();
                	ArrayAdapter<String> adapter = new ArrayAdapter(DetailsActivity.this,android.R.layout.simple_list_item_1,lstData);
                	DetailsActivity.this.setListAdapter(adapter);
                	if(c.getCount()==0)
                		lstData.add("Nothing Found");
                	else{
                		c.moveToNext();
                		String dt;
                		for(int i=0;i<c.getCount();i++){
                			dt = c.getInt(0)+"/"+c.getInt(1)+"/"+c.getInt(2);
                			System.out.println("dt = "+dt);
                			lstData.add(dt);
                		}
                		adapter.notifyDataSetChanged();
                	}
                	db.close();
                }
			});

			builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	dialog.cancel();
                        }
                });
				builder.show();

			break;
			
		case R.id.menuSearchDay:
			Toast.makeText(getApplicationContext(), "Processing", Toast.LENGTH_LONG);
			builder = new AlertDialog.Builder(this);
			builder.setTitle("Search By Day And Month");
			builder.setMessage("Enter in format: dd/m ");
			
			final EditText txtDate = new EditText(this);
			builder.setView(txtDate);
			
			builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	//String value = txtDate.getText().toString().trim();
                	System.out.println("Processing");
                	String vars[] = txtDate.getText().toString().split("/");
                	SQLiteDatabase db = openOrCreateDatabase("Manager",Context.MODE_PRIVATE,null);
                	Cursor c = db.query(sName, null, "day="+vars[0]+" and month="+vars[1], null, null, null, null);
                	ArrayList<String> lstData = new ArrayList<String>();                	
                	ArrayAdapter<String> adapter = new ArrayAdapter(DetailsActivity.this,android.R.layout.simple_list_item_1,lstData);
                	DetailsActivity.this.setListAdapter(adapter);
                	System.out.println("Count = "+c.getCount());
                	if(c.getCount()==0)
                		lstData.add("Nothing Found");
                	else{
                		c.moveToNext();
                		String dt;
                		for(int i=0;i<c.getCount();i++){
                			dt = c.getInt(0)+"/"+c.getInt(1)+"/"+c.getInt(2);
                			System.out.println("dt = "+dt);
                			lstData.add(dt);
                		}                		
                		adapter.notifyDataSetChanged();
                	}
                	db.close();
                }
			});

			builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	dialog.cancel();
                        }
                });
				builder.show();

			break;
		}
		return true;
	}
}
