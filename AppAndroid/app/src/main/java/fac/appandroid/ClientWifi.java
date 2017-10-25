package fac.appandroid;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.UUID;

public class ClientWifi extends AppCompatActivity {

    public final UUID MY_UUID = UUID.randomUUID();
    //for log
    static final String TAG = "ServerWifiActivity";

    //for save user preferences
    private SharedPreferences userPrefOnServerWifi;
    public static final String prefName = "ServerPreferences";

    private final IntentFilter intentFilter = new IntentFilter();

    private static WifiP2pManager mManager;
    private static WifiP2pManager.Channel mChannel;
    private static Wifi_BroadcastReceiver receiver;
    private boolean isWifiP2pEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_wifi);
        initialize();
        receiver = new Wifi_BroadcastReceiver(this, mChannel, mManager);
    }

    private void initialize() {
        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
    }

    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
        receiver = new Wifi_BroadcastReceiver(this, mChannel, mManager);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public void setIsWifiP2pEnabled(boolean b){
        this.isWifiP2pEnabled = b;
    }


}
