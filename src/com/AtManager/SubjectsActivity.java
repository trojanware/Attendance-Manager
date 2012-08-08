package com.AtManager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.*;
import android.view.ViewGroup.LayoutParams;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.database.sqlite.*;
import android.graphics.Color;

public class SubjectsActivity extends Activity{
	
	EditText txtSubs[];
	Button btnMore;
	LinearLayout txtLayout;
	int index;	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subjectllayout);
		System.out.println("Enter");		
		index = 0;		
		
		txtLayout = (LinearLayout)findViewById(R.id.txtLayout);
		txtSubs = new EditText[10];
		click_btnMore((Button)findViewById(R.id.btnMore));			
	}
	
	public void click_btnMore(View v){
		EditText txt;
		for(int i=0;i<3;i++){
			if((index)!=10){
				txtSubs[index] = new EditText(this);
				txtSubs[index].setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
				//txtSubs[index].setText("Subject "+(index+1));
				txtSubs[index].setTextColor(Color.GRAY);
				txtSubs[index].setHint("Subject No. "+(index+1));											
				txtLayout.addView(txtSubs[index++]);					
			}
			else
				break;
		}
	}
	
	public void click_btnDone(View v){		
		SQLiteDatabase db = openOrCreateDatabase("Manager",Context.MODE_PRIVATE,null);
		String query = "create table Summary ( name varchar, total int, present int );";
		db.execSQL(query);
		
		
		for(int i=0;i<index;i++){
			query = "insert into Summary (name,total,present) values ";
			String sub_name = txtSubs[i].getText().toString();
			if(!sub_name.equals("")&&!sub_name.matches("Subject \\d+")){
				query += "(\""+sub_name+"\",0,0)";			
				query += ";";
				db.execSQL(query);
				
				String query1 = "create table "+sub_name+" ( day int, month int, year int);";
				db.execSQL(query1);
			}
		}		
		System.out.println("query = "+query);		
		db.close();
		setResult(RESULT_OK);
		finish();
	}

}
