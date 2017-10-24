package fac.appandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import android.os.AsyncTask;

public class MainActivity extends AppCompatActivity {

    Button btServer;
    Button btClient;
    Button btDownload;
    ProgressBar pbDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btClient = (Button) findViewById(R.id.btClient);
        btServer = (Button) findViewById(R.id.btServer);
        btDownload = (Button) findViewById(R.id.btDownload);
        pbDownload = (ProgressBar) findViewById(R.id.pbDownload);

        onClickBtClient();
        onClickBtServer();
        onClickBtDownload();
    }

    void onClickBtClient() {
        btClient.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("DEBUG", "bouton client");
            }
        });
    }

    void onClickBtServer() {
        btServer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("DEBUG", "bouton server");
            }
        });
    }

    void onClickBtDownload() {
        btDownload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("DEBUG", "bouton download");

                pbDownload.setProgress(0);

                //J'ai mis une image au pif sur google mais on s'en bat les couilles frère
                String file_url = "http://media.rtl.fr/cache/rQwYMu3pakcZ6yEbvK84CA/880v587-0/online/image/2017/0630/7789172554_la-voie-lactee-au-dessus-de-siding-spring-en-australie.jpg";
                new DownloadFileFromURL().execute(file_url);
            }
        });
    }

    public class DownloadFileFromURL extends AsyncTask<String, String, String> {

        //ProgressBar pbDownload;

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
            Log.i("DEBUG", "dl fini !");
        }
    }
}