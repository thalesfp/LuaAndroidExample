package me.thales.luaandroidexample;

import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;


public class MainActivity extends Activity {

	private String URL = "http://ip-webservice/";
	private String FILE = "is_rooted.lua";
	
	public LuaState L;

	TextView tvResult;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button btRun = (Button) findViewById(R.id.btRun);
		Button btDownload = (Button) findViewById(R.id.btUpdate);
		tvResult = (TextView) findViewById(R.id.tvResult);
		
		L = LuaStateFactory.newLuaState();
		L.openLibs();
		
		btRun.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				L.LdoFile(Environment.getExternalStorageDirectory().getPath() + "/" + FILE);
				
				L.getGlobal("name");
				
				L.getGlobal("result");
				L.pcall(0, 1, 0);
				
				String name = L.toString(-2);
				String result = L.toString(-1);
				
				tvResult.setText(name + ": " + result);
				
				stackDump(L);
				
				L.setTop(0);
			}
		});
		
		btDownload.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tvResult.setText("");
				
				DownloadTask downloadTask = new DownloadTask(MainActivity.this);
				downloadTask.execute(URL + FILE);
			}
		});
	}
	
	static void stackDump (LuaState L) {
		int i;
		int top = L.getTop();
		
		for (i = 1; i<=top; i++) {
			int t = L.type(i);
			
			if (t == LuaState.LUA_TSTRING) {
				Log.v("String", L.toString(i));
			} else if (t == LuaState.LUA_TBOOLEAN) {
				Log.v("Boolean", String.valueOf(L.toBoolean(i)));
			} else if (t == LuaState.LUA_TNUMBER) {
				Log.v("Number", String.valueOf(L.toNumber(i)));
			} else if (t == LuaState.LUA_TNIL) {
				Log.v("Nil", "nil");
			} else {
				Log.v("Other", String.valueOf(L.typeName(i)));
			}
		}
	}

}
