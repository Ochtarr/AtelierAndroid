package fac.appandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.UUID;

public class ServerWifi extends AppCompatActivity {

    public final UUID MY_UUID = UUID.randomUUID();
    //for log
    private static final String TAG = "ServerWifiActivity";

    //for save user preferences
    private SharedPreferences userPrefOnServerWifi;
    public static final String prefName = "ServerPreferences";

    //UI
    private Button btDownload;
    private ProgressBar pbDownload;
    private TextView txtURL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_wifi);

        btDownload = (Button) findViewById(R.id.btDownload);
        pbDownload = (ProgressBar) findViewById(R.id.pbDownload);
        txtURL = (TextView) findViewById(R.id.txtURL);
        userPrefOnServerWifi = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        txtURL.setText(userPrefOnServerWifi.getString("url", ""));

        onClickBtDownload();
    }

    @Override
    protected void onResume(){
        Log.d(TAG, "onResume");
        super.onResume();
        userPrefOnServerWifi = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        txtURL.setText(userPrefOnServerWifi.getString("url", ""));
    }

    @Override
    protected void onRestart(){
        Log.d(TAG, "onRestart");
        super.onRestart();
        userPrefOnServerWifi = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        txtURL.setText(userPrefOnServerWifi.getString("url", ""));
    }

    @Override
    protected void onPause(){
        Log.d(TAG, "onPause");
        SharedPreferences.Editor ed = userPrefOnServerWifi.edit();
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
                //new Server.DownloadFileFromURL().execute(file_url);
            }
        });
    }
}
