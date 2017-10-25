package fac.appandroid;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class Settings extends AppCompatActivity {

    //for log
    private static final String TAG = "SettingsActivity";

    private ConnectivityManager connMgr;
    private NetworkInfo infosConnection;
    private NetworkInfo infoBluetooth;
    private boolean isWifiConn;
    private boolean isMobileConn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Log.d(TAG, "connMgr OK");
        infosConnection = connMgr.getActiveNetworkInfo();
        Log.d(TAG, "infoWifi OK");

        Log.d(TAG, "infoConnections:" + infosConnection.toString());
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
    protected void onRestart()
    {
        Log.d(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onStop()
    {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}
