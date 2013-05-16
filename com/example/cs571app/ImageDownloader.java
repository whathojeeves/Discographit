package com.example.cs571app;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageDownloader extends AsyncTask<Object, Void, Bitmap> {
		
		public ImageView current;
		public String qURL;
		public static final String JSON="cs571App.jsonstirng";
		
	    protected Bitmap doInBackground(Object... params) {
	        current = (ImageView)params[0];
	        qURL = (String)params[1];
	        Bitmap ret=null;
	        try {
	        		ret = loadBitmap(qURL);
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	        
	        return ret;
	    }

	    protected void onProgressUpdate() {
	        // Stub
	    }

	    protected void onPostExecute(Bitmap result) {
	    	
	        System.out.println("Downloaded Image");
	        current.setImageBitmap(result);
	    }
	    
	    
		public static Bitmap loadBitmap(String url) {
			InputStream inImgUrl = null;

			Bitmap outImg = null;
			try {
				inImgUrl = new java.net.URL(url).openStream();
				outImg = BitmapFactory.decodeStream(inImgUrl);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return outImg;
		}
	}
