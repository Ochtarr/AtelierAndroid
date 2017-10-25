package fac.appandroid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Client extends AppCompatActivity {

    public final UUID MY_UUID = UUID.randomUUID();

    //for log
    private static final String TAG = "ClientActivity";

    //for save user preferences
    private SharedPreferences userPrefOnClient;
    public static final String prefName = "ClientPreferences";

    //for bluetooth
    public BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Set<BluetoothDevice> devices;
    private List<String> devicesTxt = new ArrayList<String>();

    private ListView listDevices;
    private Button btStart;
    private Button btStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        listDevices = (ListView) findViewById(R.id.listDevices);
        btStart = (Button) findViewById(R.id.btStart);
        btStop = (Button) findViewById(R.id.btStop);

        if (mBluetoothAdapter == null)
        {
            btStart.setEnabled(false);
            btStop.setEnabled(false);
        }
        else
        {
            btStart.setEnabled(true);
            btStop.setEnabled(false);

            setBluetooth(true);

            onClickBtStart();
            onClickBtStop();

            //setVisible();

            //quand on a choisis dans la liste des device .. on fait ça ? :
            /*BluetoothDevice device = null;
            Thread thread = new ConnectThread(device);
            thread.start();*/
        }

        userPrefOnClient = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        //Set<String> listDevicesSaved = userPrefOnClient.getStringSet("listBluetoohDevices", null);

    }

    @Override
    protected void onResume()
    {
        Log.d(TAG, "onResume");
        super.onResume();
        if (mBluetoothAdapter == null)
        {
            btStart.setEnabled(false);
            btStop.setEnabled(false);
        }
        else
        {
            btStart.setEnabled(true);
            btStop.setEnabled(false);
            setBluetooth(true);
            onClickBtStart();
            onClickBtStop();
        }
        userPrefOnClient = getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }

    @Override
    protected void onRestart()
    {
        Log.d(TAG, "onRestart");
        super.onRestart();
        if (mBluetoothAdapter == null)
        {
            btStart.setEnabled(false);
            btStop.setEnabled(false);
        }
        else
        {
            btStart.setEnabled(true);
            btStop.setEnabled(false);
            setBluetooth(true);
            onClickBtStart();
            onClickBtStop();
        }
        userPrefOnClient = getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }

    @Override
    protected void onPause(){
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();

        if (mBluetoothAdapter != null)
        {
            setBluetooth(false);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();

        try {
            btStart.setEnabled(true);
            btStop.setEnabled(false);

            unregisterReceiver(mReceiver);
        } catch (Exception e){

        }
    }

    private void onClickBtStart() {
        btStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                listAllDevices(); //list All devices

                IntentFilter filter = new IntentFilter();

                filter.addAction(BluetoothDevice.ACTION_FOUND);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

                registerReceiver(mReceiver, filter);
                mBluetoothAdapter.startDiscovery();
            }
        });
    }

    private void onClickBtStop() {
        btStop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
            }
        });
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                btStart.setEnabled(false);
                btStop.setEnabled(true); }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                btStart.setEnabled(true);
                btStop.setEnabled(false); }
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String txtDevice = device.getName() + " - " + device.getAddress();
                Toast.makeText(getApplicationContext(), txtDevice, Toast.LENGTH_SHORT).show();

                devices.add(device);
                devicesTxt.add(txtDevice);

                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(Client.this, android.R.layout.simple_list_item_1, devicesTxt);
                listDevices.setAdapter(adapter);
            }

            //Toast.makeText(getApplicationContext(), BluetoothDevice., Toast.LENGTH_SHORT).show();
        }
    };

    public void listAllDevices() {
        devicesTxt.clear();
        devices = mBluetoothAdapter.getBondedDevices();

        for (BluetoothDevice blueDevice : devices) {
            String txtDevice = blueDevice.getName() + " - " + blueDevice.getAddress();
            devicesTxt.add(txtDevice);
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(Client.this, android.R.layout.simple_list_item_1, devicesTxt);
        listDevices.setAdapter(adapter);
    }

    public void setBluetooth(boolean enable) {
        boolean isEnabled = mBluetoothAdapter.isEnabled();

        if (enable && !isEnabled) {
            mBluetoothAdapter.enable();
        }
        else if(!enable && isEnabled) {
            mBluetoothAdapter.disable();
        }
        else if(enable && isEnabled) {
            Log.i("DEBUG", "Bluetooth déja actif");
        }
    }

//    public void setVisible() {
//        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//        startActivity(discoverableIntent);
//    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            mBluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }
            //manageConnectedSocket(mmSocket);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}