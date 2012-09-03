package com.example.lectorfeed;

import java.util.HashMap;
import java.util.LinkedList;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity {
	static final String DATA_TITLE = "T";
	static final String DATA_LINK  = "L";
	static final String FEED_URL = "http://www.maestrosdelweb.com/index.xml";
	static LinkedList<HashMap<String, String>> data;
	private ProgressDialog progressDialog;
	private final Handler progressHandler = new Handler() {
    @SuppressWarnings("unchecked")
    public void handleMessage(Message msg) {
        if (msg.obj != null) {
            data = (LinkedList<HashMap<String, String>>)msg.obj;
            setData(data);
                }
                progressDialog.dismiss();
         }
};
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        setTitle("Lector de feed Maestros del Web");
        
        Button btn = (Button) findViewById(R.id.btnLoad);
        btn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
            	ListView lv = (ListView) findViewById(R.id.lstData);
            	if (lv.getAdapter() != null) {
            	    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            	    builder.setMessage("ya ha cargado datos, ¿Está seguro de hacerlo de nuevo?")
            	               .setCancelable(false)
            	               .setPositiveButton("Si", new DialogInterface.OnClickListener() {
            	                               public void onClick(DialogInterface dialog, int id) {
            	                                     loadData();
            	                               }
            	                })
            	               .setNegativeButton("No", new DialogInterface.OnClickListener() {
            	                              public void onClick(DialogInterface dialog, int id) {
            	                                    dialog.cancel();
            	                              }
            	               })
            	               .create()
            	           .show();
            	} else {
            	    loadData();
            	}
            	
            }
        });
        
        ListView lv = (ListView) findViewById(R.id.lstData);
        /**
         * Cuando el usuario haga click en algœn elemento de la lista, lo llevaremos al 
         * enlace del elemento a travŽs del navegador.
         */
        lv.setOnItemClickListener(new OnItemClickListener() {

    		public void onItemClick(AdapterView<?> av, View v, int position,
    				long id) {
		        /**
		         * Obtenemos el elemento sobre el que se presion—
		         */
    			HashMap<String, String> entry = data.get(position);

		        /**
		         * Preparamos el intent ACTION_VIEW y luego iniciamos la actividad (navegador en este caso)
		         */
    			Intent browserAction = new Intent(Intent.ACTION_VIEW, 
    					Uri.parse(entry.get(DATA_LINK)));
    			startActivity(browserAction);				
    		}
    	});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    private void setData(LinkedList<HashMap<String, String>> data){
    SimpleAdapter sAdapter = new SimpleAdapter(getApplicationContext(), data,
            android.R.layout.two_line_list_item,
            new String[] { DATA_TITLE, DATA_LINK },
            new int[] { android.R.id.text1, android.R.id.text2 });
    ListView lv = (ListView) findViewById(R.id.lstData);
    lv.setAdapter(sAdapter);
    }
    
    private void loadData() {
    progressDialog = ProgressDialog.show(
            MainActivity.this,
            "",
            "Por favor espere mientras se cargan los datos...",
            true);
    	new Thread(new Runnable(){
        public void run() {
            XMLParser parser = new XMLParser(FEED_URL);
                        Message msg = progressHandler.obtainMessage();
                       msg.obj = parser.parse();
               progressHandler.sendMessage(msg);
        }}).start();
}
}
