package com.talhazk.islah.httpcontrol;

/**
 * Created by Talhazk on 25-Mar-16.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.talhazk.islah.R;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class HttpAudio extends AsyncTask<String, Integer, String> {

    Context context;
    ProgressDialog pDialog;
    String decision;
    String position;

    public HttpAudio() {
    }

    public HttpAudio(Context con, String decision) {
        // TODO Auto-generated constructor stub

        context = con;
        this.decision = decision;
        position = "";

    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        if (decision.equals("share")) {
            showDialog();
        }
    }

    @Override
    protected String doInBackground(String... url) {
        int count;
        try {
            URL mUrl = new URL(url[0]);

            File mydir = context.getDir("audio", Context.MODE_PRIVATE);
            Log.e("Debug", "" + mydir.toString());
            File fileWithinMyDir = new File(mydir ,"/"+ url[1] + ".mp3");
            // FileOutputStream out = new FileOutputStream(fileWithinMyDir);
            URLConnection conexion = mUrl.openConnection();
            conexion.connect();
            // this will be useful so that you can show a tipical 0-100%
            // progress bar
            int lenghtOfFile = conexion.getContentLength();

            // downlod the file
            InputStream input = new BufferedInputStream(mUrl.openStream());
            OutputStream output = new FileOutputStream(fileWithinMyDir);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                publishProgress((int) (total * 100 / lenghtOfFile));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {

            Log.e("Debug", "Couldn't get any data from the url" + e);
        }
        if (url.length > 2)
            if (url[2].equals("share"))
                return "share";
            else
                position = url[2];
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        Log.e("Debug", "Countttttttttttt");
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        Intent intent = new Intent("playAudio");

        if (result != null) {
            if (result.equals("share"))
                intent.putExtra("action", "share");
        } else{
            intent.putExtra("action", "play");
            intent.putExtra("position", position);
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }

    public void showDialog() {
        this.pDialog = new ProgressDialog(this.context, R.style.MyTheme);
        this.pDialog.setCancelable(false);
        this.pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        this.pDialog.show();
    }
}