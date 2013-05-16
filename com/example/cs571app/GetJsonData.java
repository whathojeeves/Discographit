package com.example.cs571app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

public class GetJsonData extends AsyncTask<Object, Integer, String> {

	public Context current;
	public URI qURL;
	public static final String JSON="cs571App.jsonstirng";
	public Object mainObj;

	protected String doInBackground(Object... params) {
		mainObj = params[0];
		current = (Context)params[0];
		qURL = (URI)params[1];

		StringBuffer jsonLines = new StringBuffer("");

		try {
			HttpClient myClient= new DefaultHttpClient();

			myClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "android");
			HttpGet request = new HttpGet();
			request.setHeader("Content-Type", "text; charset=utf-8");
			request.setURI(qURL);
			HttpResponse response = myClient.execute(request);
			response.setHeader("Content-Type", "text; charset=utf-8");
			BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));


			String inLine = "";


			while ((inLine = in.readLine()) != null) 
			{
				jsonLines.append(inLine);
				/* Do something */
				publishProgress((int) (((inLine.length())) / (float) response.getEntity().getContentLength()) * 100);
				// Escape early if cancel() is called
				if (isCancelled()) break;
			}

			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonLines.toString();
	}

	protected void onProgressUpdate(Integer... progress) {
		System.out.println(progress[0]);
	}

	protected void onPostExecute(String result) {

		System.out.println("Downloaded " + result);

		if(!result.equalsIgnoreCase("null"))
		{
			final Intent intent = new Intent(current, ProcessJSONData.class);
			intent.putExtra(JSON, result);

			current.startActivity(intent);
		}
		else
		{
			AlertDialog.Builder noData = new AlertDialog.Builder(current);
			noData.setTitle("No Discography Found! Please search again.").setCancelable(true);

			noData.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					System.out.println("No data, not starting listview activity");
				}});
			AlertDialog alertDialogNoData = noData.create();

			alertDialogNoData.show();
		}
	}
}
