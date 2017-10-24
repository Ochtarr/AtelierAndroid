package fac.appandroid;

import android.content.Intent;
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

    private Button btServer;
    private Button btClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btClient = (Button) findViewById(R.id.btClient);
        btServer = (Button) findViewById(R.id.btServer);

        onClickBtClient();
        onClickBtServer();
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
}