package com.example.cs571app;

import java.io.InputStream;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<JSONObject> {
	
	Context currentContext;
	JSONObject[] jsonObjects;
	public CustomAdapter(Context curCtx, JSONObject[] jObjects)
	{
		super(curCtx,R.layout.row_main, jObjects);
		this.currentContext = curCtx;
		this.jsonObjects = jObjects;
	}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) currentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = null;
		
		if(MainActivity.qType.equals("artists"))
		{
			rowView = inflater.inflate(R.layout.row_main, parent, false);
			ImageView cover = (ImageView) rowView.findViewById(R.id.cover);
	        TextView nameField = (TextView) rowView.findViewById(R.id.name);
	        TextView genreField = (TextView) rowView.findViewById(R.id.genre);
	        TextView yearField = (TextView) rowView.findViewById(R.id.year);
	        
	        try{
	        	new ImageDownloader().execute(cover, jsonObjects[position].getString("@cover"));
	        	nameField.setText("Name:\n"+(jsonObjects[position].getString("@name")).replace("&amp;", "&"));
	        	genreField.setText("Genre:\n"+jsonObjects[position].getString("@genre"));
	        	yearField.setText("Year:\n"+jsonObjects[position].getString("@year"));
	        	
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
			
		}
		else if(MainActivity.qType.equals("albums"))
		{
			rowView = inflater.inflate(R.layout.row_albums, parent, false);
			ImageView cover = (ImageView) rowView.findViewById(R.id.cover_album);
	        TextView titleField = (TextView) rowView.findViewById(R.id.title_album);
	        TextView artistField = (TextView) rowView.findViewById(R.id.artist);
	        TextView genreField = (TextView) rowView.findViewById(R.id.genre_album);
	        TextView yearField = (TextView) rowView.findViewById(R.id.year_album);
	        
	        try{
	        	new ImageDownloader().execute(cover, jsonObjects[position].getString("@cover"));
	        	titleField.setText("Title:\n"+(jsonObjects[position].getString("@title")).replace("&amp;", "&"));
	        	artistField.setText("Artist:\n"+(jsonObjects[position].getString("@artist")).replace("&amp;", "&"));
	        	genreField.setText("Genre:\n"+jsonObjects[position].getString("@genre"));
	        	yearField.setText("Year:\n"+jsonObjects[position].getString("@year"));
	        	
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
			
		}
		else if(MainActivity.qType.equals("songs"))
		{
			rowView = inflater.inflate(R.layout.row_songs, parent, false);
			ImageView sample = (ImageView) rowView.findViewById(R.id.sample);
	        TextView titleField = (TextView) rowView.findViewById(R.id.title_songs);
	        TextView performerField = (TextView) rowView.findViewById(R.id.performer);
	        TextView composerField = (TextView) rowView.findViewById(R.id.composer);
	        
	        try{
	        	new ImageDownloader().execute(sample, "http://cs-server.usc.edu:26798/play_song.png");
	        	titleField.setText("Title:\n"+(jsonObjects[position].getString("@title")).replace("&amp;", "&"));
	        	performerField.setText("Performer:\n"+(jsonObjects[position].getString("@performer")).replace("&amp;", "&"));
	        	composerField.setText("Composer:\n"+(jsonObjects[position].getString("@composer")).replace("&amp;", "&"));
	        	
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	        
		}
		 return rowView;
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
