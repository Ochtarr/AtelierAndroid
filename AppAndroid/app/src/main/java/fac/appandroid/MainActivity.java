package fac.appandroid;

import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Button btServer;
    private Button btClient;
    private Button btSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btClient = (Button) findViewById(R.id.btClient);
        btServer = (Button) findViewById(R.id.btServer);
        btSettings = (Button) findViewById(R.id.btSettings);

        onClickBtClient();
        onClickBtServer();
        onClickBtSettings();
    }

    @Override
    protected void onStart(){
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onRestart(){
        Log.d(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onResume()
    {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        Log.d(TAG, "onPause");
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

    private void onClickBtClient() {
        btClient.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, Client.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
    }

    private void onClickBtServer() {
        btServer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, Server.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
    }

    private void onClickBtSettings() {
        btSettings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, Settings.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
    }
}