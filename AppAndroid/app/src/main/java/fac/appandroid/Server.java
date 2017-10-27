package fac.appandroid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

public class Server extends AppCompatActivity {

    //for log
    private static final String TAG = "ServerActivity";

    //for save user preferences
    private SharedPreferences userPrefOnServer;
    public static final String prefName = "ServerPreferences";

    public final UUID MY_UUID = UUID.randomUUID();
    public BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private Button btDownload;
    private ProgressBar pbDownload;
    private TextView txtURL;

    private BluetoothService mService = null;

    // Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;


    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    private StringBuffer mOutStringBuffer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        btDownload = (Button) findViewById(R.id.btDownload);
        pbDownload = (ProgressBar) findViewById(R.id.pbDownload);
        txtURL = (TextView) findViewById(R.id.txtURL);

        btDownload.setEnabled(false);

        userPrefOnServer = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        txtURL.setText(userPrefOnServer.getString("url", ""));

        onClickBtDownload();

        if (mBluetoothAdapter != null)
        {
            setVisible(); //Bluetooth visible

            try {
                Thread.sleep(200);
            }
            catch (InterruptedException exception) {
                exception.printStackTrace();
            }

            setBluetooth(true); //Bluetooth enable

            try {
                Thread.sleep(200);
            }
            catch (InterruptedException exception) {
                exception.printStackTrace();
            }

            mService = new BluetoothService(this, mHandler);
            mOutStringBuffer = new StringBuffer("");
            mService.start();
        }
    }

    private void sendFile(byte[] buffer, String name) {
        try {
            // Input stream
            File inputFile = new File(name);
            InputStream input = new FileInputStream(inputFile);

            int n;
            while ((n=input.read(buffer))!=-1) {
                mService.write(buffer);
                mOutStringBuffer = new StringBuffer("");
                mOutStringBuffer.setLength(0);
                buffer = new byte[1024];
            }

            for (byte bit : buffer) {
                Log.d("file2", "\t" + bit);
            }

        } catch (Exception e) {
            Log.e(TAG, "sendFile - " + e.getMessage());
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Toast.makeText(getApplicationContext(), writeMessage, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Toast.makeText(getApplicationContext(), readMessage, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    Toast.makeText(getApplicationContext(), "Connected to " + msg.getData().getString(DEVICE_NAME), Toast.LENGTH_SHORT).show();
                    btDownload.setEnabled(true);
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }

            Log.d(TAG, "Handler - " + msg.what);
        }
    };

    private void setBluetooth(boolean enable) {
        boolean isEnabled = mBluetoothAdapter.isEnabled();

        if (enable && !isEnabled) {
            mBluetoothAdapter.enable();
        }
        else if(!enable && isEnabled) {
            mBluetoothAdapter.disable();
        }
        else if(enable && isEnabled) {
            Log.d(TAG, "setBluetooth - Bluetooth déja actif");
        }
    }

    private void setVisible() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

    @Override
    protected void onResume(){
        Log.d(TAG, "onResume");
        super.onResume();
        userPrefOnServer = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        txtURL.setText(userPrefOnServer.getString("url", ""));
    }

    @Override
    protected void onRestart(){
        Log.d(TAG, "onRestart");
        super.onRestart();
        userPrefOnServer = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        txtURL.setText(userPrefOnServer.getString("url", ""));
    }

    @Override
    protected void onPause(){
        Log.d(TAG, "onPause");
        SharedPreferences.Editor ed = userPrefOnServer.edit();
        ed.putString("url", txtURL.getText().toString());
        ed.commit();
        super.onPause();
    }

    @Override
    protected void onStop(){
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    private void onClickBtDownload() {
        btDownload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG, "bt download");

                pbDownload.setProgress(0);

                String file_url = txtURL.getText().toString();
                new DownloadFileFromURL().execute(file_url);
            }
        });
    }

    private class DownloadFileFromURL extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection serverURL = url.openConnection();
                serverURL.connect();

                int lenghtOfFile = serverURL.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                OutputStream output = new FileOutputStream("/sdcard/link.mp4");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

                File inputFile = new File("/sdcard/link.mp4");

                String size = "size" + inputFile.length();
                byte[] sendSize = size.getBytes();
                mService.write(sendSize);
                mOutStringBuffer = new StringBuffer("");
                mOutStringBuffer.setLength(0);

                byte[] buffer = new byte[1024];
                sendFile(buffer, "/sdcard/link.mp4");

                String end = "end!";
                byte[] sendEnd = size.getBytes();
                mService.write(sendEnd);
                mOutStringBuffer = new StringBuffer("");
                mOutStringBuffer.setLength(0);

                Toast.makeText(getApplicationContext(), "File sended", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            pbDownload.setProgress(Integer.parseInt(progress[0]));
        }
        @Override
        protected void onPostExecute(String file_url) {
            Log.i(TAG, "dl finish !");
        }
    }
}