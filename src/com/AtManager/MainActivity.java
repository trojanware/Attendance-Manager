package com.AtManager;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {
	
	private ArrayList<Subject> lstSubjects;
	SubjectAdapter adapter;
	final MainActivity th=this; 
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.main);		
		
		boolean firstRun = checkFirstRun();
		System.out.println("firstrun = "+firstRun);
		if(firstRun){
			startActivityForResult(new Intent(MainActivity.this,SubjectsActivity.class),1);			
		}
		else
			populateData();
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setClickable(true);

		lv.setOnItemClickListener(new OnItemClickListener(){
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				Context context = getApplicationContext();
				CharSequence text = "Position = "+position;
				Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
				toast.show();
				System.out.println("Position = "+position);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode==RESULT_OK){
			//startActivity(getIntent());finish(); //Reload
			populateData();
			System.out.println("hello!");
		}
	}
	
	public void populateData(){
		System.out.println("in populatedata!");
		lstSubjects = new ArrayList<Subject>();
		adapter = new SubjectAdapter(this,R.layout.row,lstSubjects);
		setListAdapter(adapter);
		try{
			SQLiteDatabase db;
			String cols[] = {"name","total","present"}; 
			db = openOrCreateDatabase("Manager",Context.MODE_PRIVATE,null);			
			Cursor res = db.query("Summary", cols, null, null, null, null, null);
			int noSubjects = res.getCount();
			Subject subs[] = new Subject[noSubjects];
			System.out.println("noSubjects = "+noSubjects);
			res.moveToNext();
			for(int i=0;i<noSubjects;i++){
				subs[i] = new Subject();
				subs[i].setName(res.getString(0));
				subs[i].setTotalClases(res.getInt(1));
				subs[i].setNoPresent(res.getInt(2));
				res.moveToNext();
				lstSubjects.add(subs[i]);
			}
			db.close();
			adapter.notifyDataSetChanged();
		}catch(Exception e){
			System.out.println("Error while populating records: "+e.toString());
		}
	}
	
	public boolean checkFirstRun(){
		boolean ans = true;
		SQLiteDatabase db = openOrCreateDatabase("Manager",Context.MODE_PRIVATE,null);
		try{			
			db.query("Summary",null,null,null,null,null,null);
			db.close();
			ans = false;
		}catch(Exception e){			
			System.out.println("Error while opening db : "+e.toString());
			System.out.println("Open new activity");			
			db.close();
		}
		return ans;
	}	
	
	private class SubjectAdapter extends ArrayAdapter<Subject>{
		private ArrayList<Subject> items;		
		
		public SubjectAdapter(Context context, int viewRsrcId, ArrayList<Subject> items){
			super(context,viewRsrcId,items);
			this.items = items;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			View v = convertView;
			if(v==null){
				LayoutInflater in = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = in.inflate(R.layout.row, null);
				v.setClickable(true);
			}
			Subject s = items.get(position);
			if(s!=null){
				int total = s.getTotalClases();
				int present = s.getNoClasesPresent();
				float percent;
				if(total==0)
					percent = 0;
				else
					percent = ((float)present/(float)total)*(float)(100.0);
				boolean alert=false;
				if(percent<=75)
					alert = true;
				TextView lblSubject = (TextView)v.findViewById(R.id.lblSubject);
				lblSubject.setText(s.getSubjectName());
				TextView txtPercentage = (TextView)v.findViewById(R.id.txtPercentage);
				String strdisp = String.format("%.2f", percent); 
				txtPercentage.setText("    "+strdisp+"%");
				TextView txtPresent = (TextView)v.findViewById(R.id.txtPresent);
				txtPresent.setText("Present: "+s.getNoClasesPresent());
				TextView txtTotal = (TextView)v.findViewById(R.id.txtTotal);
				txtTotal.setText("Total: "+s.getTotalClases());
				
				if(alert)
					txtPercentage.setTextColor(Color.RED);
							
				lblSubject.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View view){
						final CharSequence[] items = {"Present","Absent"};
						final String strSubjectName=((TextView)view).getText().toString();
						AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
						builder.setTitle("Update");
						builder.setSingleChoiceItems(items,-1, new DialogInterface.OnClickListener() {
						    public void onClick(DialogInterface dialog, int item) {						        						       
						        int itemSelected = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
						        System.out.println("Selected = "+itemSelected);
						        dialog.dismiss();
						        
						        SQLiteDatabase db = openOrCreateDatabase("Manager",Context.MODE_PRIVATE,null);
						        Cursor ans = db.query("Summary", null, "name=\""+strSubjectName+"\"", null,null,null,null);
						        ans.moveToNext();
						        int noPresent, noTotal;
						        System.out.println("no present for "+strSubjectName+" = "+ans.getInt(1));
						        noPresent = ans.getInt(2);
						        noTotal = ans.getInt(1);
						        Calendar c = Calendar.getInstance();
						        int day = c.get(Calendar.DATE);
						        int month = c.get(Calendar.MONTH)+1;
						        int year = c.get(Calendar.YEAR);
						        if(itemSelected==0){
						        	noPresent++;
						        	noTotal++;
						        	//System.out.println("present on : "+c.get(Calendar.DATE)+"-"+c.get(Calendar.YEAR));
						        }
						        else
						        	noTotal++;
						        ContentValues values = new ContentValues(2);
						        values.put("total", noTotal);
						        values.put("present", noPresent);
						        int a = db.update("Summary", values, "name=\""+strSubjectName+"\"", null);
						        System.out.println("rows updated = "+a);
						        ans = db.query("Summary", null, "name=\""+strSubjectName+"\"", null,null,null,null);
						        ans.moveToNext();
						        System.out.println("new val(present) = "+ans.getInt(2));
						        System.out.println("new val(total) = "+ans.getInt(1));
						        
						        String query1 = "insert into "+strSubjectName+" values ("+day+","+month+","+year+");";
						        db.execSQL(query1);
						        db.close();
						        th.populateData();
						    }
						});
						AlertDialog alert = builder.create();
						alert.show();
						
					}
				});
			}
			return v;
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menulayout, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    
	    switch (item.getItemId()) {
	        case R.id.menuDelete:
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        	builder.setMessage("Are you sure you want to wipe all your data?")
	        	       .setCancelable(false)
	        	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	        	           public void onClick(DialogInterface dialog, int id) {
	        	        	   MainActivity.this.getApplicationContext().deleteDatabase("Manager");
	        	        	   startActivityForResult(new Intent(MainActivity.this,SubjectsActivity.class),1);
	        	           }
	        	       })
	        	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
	        	           public void onClick(DialogInterface dialog, int id) {
	        	                dialog.cancel();
	        	           }
	        	       });
	        	AlertDialog alert = builder.create();
	        	alert.show();
	                     	            
	            break;
	        case R.id.menuForceUpdate:
	        	startActivityForResult(new Intent(MainActivity.this,ForceUpdateActivity.class),1);
	            break;
	            
	        case R.id.menuDetails:
	        	SQLiteDatabase db = openOrCreateDatabase("Manager",Context.MODE_PRIVATE,null);
	        	String col[] = {"name"};	        	
	        	Cursor c = db.query("Summary", col, null, null, null, null, null);
	        	String subs[] = new String[c.getCount()];
	        	c.moveToNext();
	        	for(int i=0;i<c.getCount();i++){	        		
	        		subs[i] = c.getString(0);
	        		c.moveToNext();
	        	}
	        	final String s[] = subs;	        	
	        	builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("Details");
				builder.setSingleChoiceItems(subs,-1, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int item){
						int itemSelected = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
						Intent i = new Intent(MainActivity.this,DetailsActivity.class);
						i.putExtra("subjectName", s[itemSelected]);
						startActivity(i);
					}
				});
	        	alert = builder.create();
	        	alert.show();
	        	break;	        		       
	        
	        default:
	        	break;	       
	    }
	    return true;
	}
}
