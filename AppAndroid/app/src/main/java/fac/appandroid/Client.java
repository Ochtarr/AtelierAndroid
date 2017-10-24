package fac.appandroid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v4.app.SupportActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Client extends AppCompatActivity {

    public final UUID MY_UUID = UUID.randomUUID();
    public BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Set<BluetoothDevice> devices;
    private ListView listDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        listDevices = (ListView) findViewById(R.id.listDevices);

        setBluetooth(true);
        //setVisible();
        listAllDevices();

        //quand on a choisis dans la liste des device .. on fait ça ? :
        /*BluetoothDevice device = null;
        Thread thread = new ConnectThread(device);
        thread.start();*/
    }

    public void listAllDevices() {
        List<String> devicesTxt = new ArrayList<String>();

        devices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice blueDevice : devices) {
            devicesTxt.add(blueDevice.getName());
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

    public void setVisible() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

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
