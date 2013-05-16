package com.example.cs571app;

import java.net.URI;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
//import net.sf.json.*;
//import net.sf.json.JSONArray;

public class MainActivity extends Activity {
	
	public final static String QUERY = "cs571App.query";
	public final static String QUERY_TYPE = "cs571App.querytype";
	
	public static String qTextString;
	public static String qType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.cs571app", 
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                System.out.println("KeyHash:" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
        } catch (NameNotFoundException e) {

        }
        catch (NoSuchAlgorithmException e) {

        }
        
        setContentView(R.layout.activity_main);
        
    
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void queryServlet(View view)
    {
    	EditText qText = (EditText) findViewById(R.id.edit_message);
    	qTextString = qText.getText().toString();
    	
    	Spinner sqType = (Spinner) findViewById(R.id.spinner1);
    	qType = sqType.getSelectedItem().toString();
    	String constructedURL = null;
    	try {
    	constructedURL = "http://cs-server.usc.edu:26799/discographit/HelloWorld?sQuery="+URLEncoder.encode(qTextString, "UTF-8")+"&qT="+URLEncoder.encode(qType, "UTF-8");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	try {
    		new GetJsonData().execute(this,new URI(constructedURL));
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
}
