package com.example.android.multithreading;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private EditText editText;
    private ListView listView;
    private String[] listOfImages;
    private ProgressBar progressBar;
    private LinearLayout loadingSection = null;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.downloadURL);
        listView = (ListView) findViewById(R.id.urlList);
        listView.setOnItemClickListener(this);
        listOfImages = getResources().getStringArray(R.array.imageUrls);
        progressBar = (ProgressBar) findViewById(R.id.downloadProgress);
        loadingSection = (LinearLayout) findViewById(R.id.loadingSection);
        handler = new Handler();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void downloadImage(View view) {

       String url = editText.getText().toString();

       Thread myThread = new Thread(new DownloadImagesThread(url));
        myThread.start();

        /*File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String url= listOfImages[0];
        Uri uri = Uri.parse(url);
        Log.v("file", uri.getLastPathSegment().toString());*/
    }

    public boolean downloadImageUsingThreads(String url) {

        boolean successful = false;
        URL download = null;
        HttpURLConnection connection= null;
        InputStream inputStream = null;

        FileOutputStream fileOutputStream = null;
        File file = null;

        try {
            download = new URL(url);
            connection= (HttpURLConnection) download.openConnection();
            inputStream= connection.getInputStream();

            file = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
            +"/"+Uri.parse(url).getLastPathSegment());

            fileOutputStream = new FileOutputStream(file);

            Log.v("downloadImage", file.getAbsolutePath().toString());

            int read = -1;
            byte[] buffer = new byte[1024];

            while ((read=inputStream.read(buffer)) != -1) {

                fileOutputStream.write(buffer, 0, read);
                //Log.v("WhileLoopRead", Integer.toString(read) );
             }

            successful = true;

        } catch (MalformedURLException e) {
            Log.v("MalformedURLEx", e.toString());

        } catch (IOException e) {
            Log.v("IOEx", e.toString());
        } finally {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    loadingSection.setVisibility(View.GONE);
                   // MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            });
            if (connection !=null) {
                connection.disconnect();
            }
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.v("IOEx", e.toString());
                }
            }

            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    Log.v("IOEx", e.toString());
                }
            }
        }

        return successful;
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         editText.setText(listOfImages[position]);
    }


    private class DownloadImagesThread implements Runnable {

        private String url;
        public DownloadImagesThread(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    loadingSection.setVisibility(View.VISIBLE);
                }
            });


            downloadImageUsingThreads(url);

            /*MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });*/
        }
    }

}
