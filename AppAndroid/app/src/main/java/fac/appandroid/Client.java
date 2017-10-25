package fac.appandroid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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

    // Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;


    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    //for bluetooth
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Set<BluetoothDevice> devices;
    private List<String> devicesTxt = new ArrayList<String>();
    private StringBuffer mOutStringBuffer;

    private BluetoothService mService = null;

    private ListView listDevices;
    private Button btStart;
    private Button btStop;
    private Button btSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        listDevices = (ListView) findViewById(R.id.listDevices);
        btStart = (Button) findViewById(R.id.btStart);
        btStop = (Button) findViewById(R.id.btStop);
        btSend = (Button) findViewById(R.id.btSend);

        initBluetooth();

        onClickBtStart();
        onClickBtStop();
        onClickBtSend();
        onClickListDevice();

        userPrefOnClient = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        //Set<String> listDevicesSaved = userPrefOnClient.getStringSet("listBluetoohDevices", null);

    }

    private void onClickListDevice() {
        listDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<BluetoothDevice> myDevice = new ArrayList<>(devices);
                Log.d(TAG, "onClickListDevice - " + myDevice.get(i).getName());

                BluetoothDevice device = myDevice.get(i);
                mService.connect(device);
            }
        });
    }

    private void initBluetooth() {
        if (mBluetoothAdapter == null)
        {
            btStart.setEnabled(false);
            btStop.setEnabled(false);
            btSend.setEnabled(false);
        }
        else
        {
            btStart.setEnabled(true);
            btStop.setEnabled(false);
            btSend.setEnabled(false);

            setBluetooth(true);
            Log.d(TAG, "initBluetooth");
            mService = new BluetoothService(this, mHandler);
            mOutStringBuffer = new StringBuffer("");
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
                    btSend.setEnabled(true);
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }

            Log.d(TAG, "Handler - " + msg.what);
        }
    };

    @Override
    protected void onResume()
    {
        Log.d(TAG, "onResume");
        super.onResume();
        initBluetooth();
        userPrefOnClient = getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }

    @Override
    protected void onRestart()
    {
        Log.d(TAG, "onRestart");
        super.onRestart();
        initBluetooth();
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
            btSend.setEnabled(false);

            unregisterReceiver(mReceiver);
        } catch (Exception e){

        }
    }

    private void onClickBtSend() {
        btSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClickBtSend");
                // Check that we're actually connected before trying anything
                if (mService.getState() != BluetoothService.STATE_CONNECTED) {
                    Toast.makeText(Client.this, "not connected", Toast.LENGTH_SHORT).show();
                    return;
                }

                String message = "bonjour";

                if (message.length() > 0) {
                    // Get the message bytes and tell the BluetoothChatService to write
                    byte[] send = message.getBytes();
                    mService.write(send);
                    mOutStringBuffer.setLength(0);
                }
            }
        });
    }

    private void onClickBtStart() {
        btStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClickBtStart");
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
                Log.d(TAG, "onClickBtStop");
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
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                btStart.setEnabled(true);
                btStop.setEnabled(false); }
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String txtDevice = device.getName() + " - " + device.getAddress();
                Toast.makeText(getApplicationContext(), txtDevice, Toast.LENGTH_SHORT).show();

                //devices.add(device);
                devicesTxt.add(txtDevice);

                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(Client.this, android.R.layout.simple_list_item_1, devicesTxt);
                listDevices.setAdapter(adapter);

                Log.d(TAG, "BroadcastReceiver - Detect " + txtDevice);
            }
        }
    };

    private void listAllDevices() {
        devicesTxt.clear();
        devices = mBluetoothAdapter.getBondedDevices();

        for (BluetoothDevice blueDevice : devices) {
            String txtDevice = blueDevice.getName() + " - " + blueDevice.getAddress();
            devicesTxt.add(txtDevice);
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(Client.this, android.R.layout.simple_list_item_1, devicesTxt);
        listDevices.setAdapter(adapter);
    }

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

}