package fac.appandroid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

public class Server extends AppCompatActivity {

    public final UUID MY_UUID = UUID.randomUUID();
    public BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private Button btDownload;
    private ProgressBar pbDownload;
    private TextView txtURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        btDownload = (Button) findViewById(R.id.btDownload);
        pbDownload = (ProgressBar) findViewById(R.id.pbDownload);
        txtURL = (TextView) findViewById(R.id.txtURL);

        onClickBtDownload();
    }

    private void onClickBtDownload() {
        btDownload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("DEBUG", "bt download");

                pbDownload.setProgress(0);

                String file_url = txtURL.getText().toString();
                new DownloadFileFromURL().execute(file_url);
            }
        });
    }

    private class AcceptThread extends Thread {
        private BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("name", MY_UUID);
            } catch (IOException e) { }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    //mmServerSocket.close();
                    break;
                }

                if (socket != null) {
                    //manageConnectedSocket(socket);

                    break;
                }
            }
        }

        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) { }
        }
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
                OutputStream output = new FileOutputStream("/sdcard/enculer.jpg");

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
            Log.i("DEBUG", "dl finish !");
        }
    }
}