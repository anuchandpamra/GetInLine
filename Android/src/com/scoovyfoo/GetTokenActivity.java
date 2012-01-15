package com.scoovyfoo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.*;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class GetTokenActivity extends Activity implements TokenListener {
    // Tag to be used for Logging messages
    private static final String TAG = "GetTokenActivity";

    // Handle to the GUI components that we are going to manipulate in this
    // class
    private TextView mNextAvailableToken; // Label to display the next available token number
    private TextView mMyTokenNumber; // Label to display the number of the current token I am holding

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Entered OnCreate");
        setContentView(R.layout.main);

        // Obtain handles to GUI objects
        mNextAvailableToken = (TextView) findViewById(R.id.nextAvailableToken);
        mMyTokenNumber = (TextView) findViewById(R.id.myTokenNumber);
        Button lGetNextToken = (Button) findViewById(R.id.getNextToken);

        // Set the callback for the button
        lGetNextToken.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getNextToken();
            }
        });

        // Bind to the service that will listen on a socket for the next available token number
        Intent intent = new Intent(this, QueueServerSocketConnection.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        // Now start the service
        startService(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        unbindService(mConnection);
        Intent intent = new Intent(this, QueueServerSocketConnection.class);
        stopService(intent);
    }

    /**
     * Gets the next token from the server.
     */
    protected void getNextToken() {
        new GetMeAToken().execute("http://"
                + getString(R.string.queue_server_dns)
                + getString(R.string.queue_server_port)
                + getString(R.string.get_token_url));
    }
    
    private void displayToken(String token){
        mMyTokenNumber.setText(token);
    }

    public void connectionAttemptFailed() {
        mNextAvailableToken.setText("Socket Connection Failed");
    }

    public void connectionTerminated() {
        mNextAvailableToken.setText("Socket Connection Terminated");
    }

    public void receiveNewToken(Integer token) {
        mNextAvailableToken.setText(token.toString());
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.i(TAG, "Socket Service Connected");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            QueueServerSocketConnection.QueueServerBinder binder =
                    (QueueServerSocketConnection.QueueServerBinder) service;
            binder.setHandler(mHandler);
        }

        public void onServiceDisconnected(ComponentName arg0) {
        }
    };


    // Define the Handler that receives messages from the thread and update the nextAvailableToken
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Integer token = (Integer) msg.obj;
            receiveNewToken(token);
        }
    };


    private class GetMeAToken extends AsyncTask<String,Void,String> {
        private final HttpClient client = new DefaultHttpClient();
        private String token;
        private boolean error = false;

        protected void onPreExecute() {
        }

        protected String doInBackground(String... urls) {
            try {
                HttpGet httpget = new HttpGet(urls[0]);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                JSONObject jsonResponse = new JSONObject(client.execute(httpget, responseHandler));
                token = jsonResponse.getString("number");
            }  catch (IOException e) {
                Log.d(TAG,"IOException while getting json response",e);
                error = true;
                cancel(true);
            } catch (JSONException e) {
                Log.d(TAG,"JSONException while getting json response",e);
                error = true;
                cancel(true);
            }
            return token;
        }

        protected void onPostExecute(String token) {
            if (error) {
                Toast toast = Toast.makeText(GetTokenActivity.this, getString(R.string.get_token_error), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 75);
                toast.show();
            } else {
                GetTokenActivity.this.displayToken(token);
            }
        }
    }


}
