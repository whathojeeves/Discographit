package com.example.cs571app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class ProcessJSONData extends ListActivity {
	
	String jsonLines;
	JSONArray jsonArray;
	JSONObject[] jsonObjects;
	MediaPlayer mediaPlayer;
	String nameInfo, captInfo, descInfo, linkInfo, picInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_process_jsondata);

		Intent recdIntent = getIntent();
		jsonLines = recdIntent.getStringExtra(GetJsonData.JSON);
		
		try {
			jsonArray = new JSONArray(jsonLines);
			jsonObjects = new JSONObject[jsonArray.length()];

			for(int i=0; i<jsonArray.length(); i++)
			{
				jsonObjects[i] = jsonArray.getJSONObject(i);
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		setListAdapter(new CustomAdapter(this, jsonObjects));
	}
	
	protected void onListItemClick(final ListView l, View v, final int position, long id)
	{
		AlertDialog.Builder nextStepQ = new AlertDialog.Builder(this);
		
		nextStepQ.setTitle("Choose");
		
		if(MainActivity.qType.equalsIgnoreCase("songs"))
		{
			nextStepQ.setMessage("What do you want to do?")
			 .setCancelable(false)
			 .setNeutralButton("Post to Facebook",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						System.out.println("Post to facebook");
						doFacebookDialog(l, (JSONObject) getListView().getItemAtPosition(position));
					}})
			 .setPositiveButton("Listen to Sample", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						System.out.println("Sample Music");
						String msg = "No Sample Available";
						try {
							JSONObject rowData = (JSONObject) getListView().getItemAtPosition(position);
							String sampleLink = rowData.getString("@sample");
							final boolean sampleAbsent = sampleLink.equalsIgnoreCase("NA");
							
							if(!sampleAbsent)
							{
							msg = "Sample Playing";
							String url = sampleLink;
							mediaPlayer = new MediaPlayer();
							mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
							mediaPlayer.setDataSource(l.getContext(), Uri.parse(url));
							mediaPlayer.prepare(); 
							}
							
							AlertDialog.Builder stopMusic = new AlertDialog.Builder(l.getContext());
							stopMusic.setTitle(msg).setCancelable(false);

							stopMusic.setNeutralButton("Stop", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int id) {
									System.out.println("Stopping music");
									if(!sampleAbsent) {
									mediaPlayer.stop();
									mediaPlayer.release();
								}
								}});
							AlertDialog alertDialogStop = stopMusic.create();

							alertDialogStop.show();
							
							if(!sampleAbsent) {
							mediaPlayer.start();
							}
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}} 
					)
					.setNegativeButton("Nothing, thanks!",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							System.out.println("Don't do anything");
						}});
		}
		else 
		{
			nextStepQ.setMessage("What do you want to do?")
			 .setCancelable(true)
			 .setNeutralButton("Post to Facebook",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						System.out.println("Post to facebook");
						JSONObject rowData = (JSONObject) getListView().getItemAtPosition(position);
						doFacebookDialog(l, rowData);
					}})
					.setNegativeButton("Nothing, thanks!",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						System.out.println("Don't post to facebook");
					}});
		}
		
		
		
		AlertDialog alertDialog = nextStepQ.create();

		alertDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.process_jsondata, menu);
		return true;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    Session.getActiveSession()
	        .onActivityResult(this, requestCode, resultCode, data);
	}
	
	public void doFacebookDialog(final ListView curView, final JSONObject rowData)
	{
		
		if(MainActivity.qType.equalsIgnoreCase("artists"))
		{
			try {
			 nameInfo = rowData.getString("@name");
			 captInfo = "I like "+rowData.getString("@name")+" who is/are active since "+rowData.getString("@year");
			 descInfo = "Genre of music is : "+rowData.getString("@genre");
			 linkInfo = rowData.getString("@details");
			 picInfo = rowData.getString("@cover");
			} catch(JSONException e) {
				e.printStackTrace();
			}
		}
		else if(MainActivity.qType.equalsIgnoreCase("albums"))
		{
			try {
			 nameInfo = rowData.getString("@title");
			 captInfo = "I like "+rowData.getString("@title")+" released in "+rowData.getString("@year");
			 descInfo = "Artist : "+rowData.getString("@artist")+ " Genre :"+rowData.getString("@genre");
			 linkInfo = rowData.getString("@details");
			 picInfo = rowData.getString("@cover");
			} catch(JSONException e) {
				e.printStackTrace();
			}
		}
		else if(MainActivity.qType.equalsIgnoreCase("songs"))
		{
			try {
			 nameInfo = rowData.getString("@title");
			 captInfo = "I like "+rowData.getString("@title")+" composed by "+rowData.getString("@composer");
			 descInfo = "Performer : "+rowData.getString("@performer");
			 linkInfo = rowData.getString("@details");
			 picInfo = "http://cs-server.usc.edu:26798/play_song.png";
			} catch(JSONException e) {
				e.printStackTrace();
			}
		}
		/* Facebook feed dialogue */
		
		/* Have to log in the user first */
		// start Facebook Login
	    Session.openActiveSession(this, true, new Session.StatusCallback() {
	    	
	      // callback when session changes state
	      @Override
	      public void call(Session session, SessionState state, Exception exception) {
	    	  System.out.println("In call");
	        if (session.isOpened()) {
	        	
	        	System.out.println("In session is opened");
	        	
	          // make request to the /me API
	          Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

	            // callback after Graph API response with user object
	            @Override
	            public void onCompleted(GraphUser user, Response response) {
	            	System.out.println("In onCompleted");
	              if (user != null) {
	            	  
	            	  System.out.println("In user not null");
	            	  
	            	  Bundle params = new Bundle();
	            	  try {
	          	    params.putString("name", nameInfo.trim());
	          	    params.putString("caption", captInfo.trim());
	          	    params.putString("description", descInfo.trim());
	          	    params.putString("picture", picInfo.trim());
	          	    System.out.println(picInfo);
	          	    if(!linkInfo.equalsIgnoreCase("NA"))
	          	    {
	          	    params.putString("link", linkInfo.trim());
	          	    params.putString("properties", " {'Look at details':{'text':'here','href':'"+rowData.getString("@details").trim()+"'}}");
	          	    }
	          	        	    
	            	  } catch(JSONException e) {
	            		  e.printStackTrace();
	            	  }

	          	    WebDialog feedDialog = (
	          	        new WebDialog.FeedDialogBuilder(curView.getContext(),
	          	            Session.getActiveSession(),
	          	            params))
	          	        .setOnCompleteListener(new OnCompleteListener() {

	          	            @Override
	          	            public void onComplete(Bundle values,
	          	                FacebookException error) {
	          	                if (error == null) {
	          	                    // When the story is posted, echo the success
	          	                    // and the post Id.
	          	                    final String postId = values.getString("post_id");
	          	                    if (postId != null) {
	          	                        Toast.makeText(curView.getContext(),
	          	                            "Posted story, id: "+postId,
	          	                            Toast.LENGTH_SHORT).show();
	          	                    } else {
	          	                        // User clicked the Cancel button
	          	                        Toast.makeText(curView.getContext().getApplicationContext(), 
	          	                            "Publish cancelled", 
	          	                            Toast.LENGTH_SHORT).show();
	          	                    }
	          	                } else if (error instanceof FacebookOperationCanceledException) {
	          	                    // User clicked the "x" button
	          	                    Toast.makeText(curView.getContext().getApplicationContext(), 
	          	                        "Publish cancelled", 
	          	                        Toast.LENGTH_SHORT).show();
	          	                } else {
	          	                    // Generic, ex: network error
	          	                    Toast.makeText(curView.getContext().getApplicationContext(), 
	          	                        "Error posting story", 
	          	                        Toast.LENGTH_SHORT).show();
	          	                }
	          	            }

	          	        })
	          	        .build();
	          	    feedDialog.show();
	          	    
	          	    /* End facebook feed */
	          	    
	              }
	            }
	          });
	        }
	        else
	        {
	        	System.out.println("Session not opened");
	        }
	      }
	    });
		
		
		
	}

}
