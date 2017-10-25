package fac.appandroid;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Connection;
import android.util.Log;

public class Settings extends AppCompatActivity {

    //for log
    private static final String TAG = "SettingsActivity";

    private ConnectivityManager connMgr;
    private Network[] infosConnection;
    //private NetworkInfo infoBluetooth;
    private boolean isWifiConn;
    private boolean isMobileConn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        infosConnection = connMgr.getAllNetworks();

        Log.d(TAG, "infoConnections:" + infosConnection.toString());

        //retrieve Wifi & Bluetooth connection
        int cmp = 1;
        for(Network n : infosConnection)
        {
            NetworkInfo ni = connMgr.getNetworkInfo(n);
            Log.d(TAG, "infoNetwork " + cmp + ":" + ni.toString());
            if(ni.getType() == connMgr.TYPE_BLUETOOTH){
                Log.d(TAG, "Connection Bluetooth");
                Log.d(TAG, "Bluetooth state : "+ ni.getState());
            }
            else if(ni.getType() == connMgr.TYPE_WIFI){
                Log.d(TAG, "Connection Wifi");
                Log.d(TAG, "Wifi state : "+ ni.getState());
            }
            else{
                Log.d(TAG, "Error : infoNetwork " + cmp + " : type=" + ni.getType() + " is not handled by this app" );
            }
            cmp++;
        }
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
