package me.thales.luaandroidexample;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

public class DownloadTask extends AsyncTask<String, Integer, String> {

	private Context context;
	private ProgressDialog pDialog;
	
    public DownloadTask(Context context) {
        this.context = context;
    }
    
    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(this.context);
        pDialog.setMessage("Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }
    
	@Override
	protected String doInBackground(String... params) {
		InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
		        
        try {
        	URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            
            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }
            
        	// this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();
            
            // download the file
            String baseName = params[0].substring( params[0].lastIndexOf('/')+1, params[0].length() );
                
            input = connection.getInputStream();
            output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/" + baseName);
            
            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
            
        }
        
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
        pDialog.dismiss();
    }
	
}
