package com.AtManager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ForceUpdateActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forcedatalayout);
		
		//Button btnUpdate = (Button)findViewById(R.id.btnUpdate);
	}
	
	public void click_btnUpdate(View v){
		EditText txtSubject = (EditText)findViewById(R.id.txtnewSubject);
		EditText txtPresent = (EditText)findViewById(R.id.txtnewPresent);
		EditText txtTotal = (EditText)findViewById(R.id.txtnewTotal);
		String strSubject = txtSubject.getText().toString();
		try{
		int noTotal = Integer.parseInt(txtTotal.getText().toString());
		int noPresent = Integer.parseInt(txtPresent.getText().toString());
		if(noPresent>noTotal){
			Toast.makeText(getApplicationContext(), "Invalid Data!!", Toast.LENGTH_LONG).show();
			return;
		}
		ContentValues values = new ContentValues(2);
        values.put("total", noTotal);
        values.put("present", noPresent);        
        	SQLiteDatabase db = openOrCreateDatabase("Manager",Context.MODE_PRIVATE,null);
        	db.update("Summary", values, "name=\""+strSubject+"\"", null);
        	Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_LONG).show();
        	db.close();
        	setResult(RESULT_OK);
        	finish();
        }catch(Exception e){
        	Toast.makeText(getApplicationContext(), "Please check the subject name!", Toast.LENGTH_LONG).show();
        }
	}
}
