/*
 * Copyright (c) 2012. SoftExcel Technologies Inc. (SoftExcel),
 * Virginia. All Rights Reserved. Permission to use, copy, modify, and
 * distribute this software and its documentation for educational,
 * research, and not-for-profit purposes, without fee and without a
 * signed licensing agreement, is hereby granted, provided that the above
 * copyright notice, this paragraph, and the next two paragraphs appear in
 * all copies, modifications, and distributions. Contact SoftExcel
 * TechnologiesInc. (info@softexcel.com), for commercial licensing
 * opportunities.
 *
 * IN NO EVENT SHALL SOFTEXCEL BE LIABLE TO ANY PARTY FOR DIRECT,
 * INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING
 * LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS
 * DOCUMENTATION, EVEN IF SOFTEXCEL HAS BEEN ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 * SOFTEXCEL SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE. THE SOFTWARE AND ACCOMPANYING DOCUMENTATION, IF
 * ANY, PROVIDED HEREUNDER IS PROVIDED "AS IS". SOFTEXCEL HAS NO
 * OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR
 * MODIFICATIONS.
 */

package com.scoovyfoo;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * User: Anupam Chandra
 * Date: 1/9/12
 * Time: 11:23 AM
 */
public class QueueServerSocketConnection extends IntentService {
    // Logging
    private static final String TAG = "QueueServerSocketConnection";
    private static final int SOCKET_TIMEOUT = 500000;

    // Binder given to clients
    private final IBinder mBinder = new QueueServerBinder();

    // TokenListener
    private Handler mHandler = null;

    private Socket mSocket = null;
    private DataInputStream mDataInputStream = null;

    public QueueServerSocketConnection() {
        super("QueueServerSocketConnection");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Inside onHandleIntent");
        do {
            try {
                Integer tokenNum = mDataInputStream.readInt();
                Log.d(TAG, "Got a new token " + tokenNum);
                if (mHandler != null) {
                    Log.d(TAG, "Sending token to handler " + tokenNum);
                    Message msg = mHandler.obtainMessage();
                    msg.obj = tokenNum;
                    mHandler.sendMessage(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                throw new RuntimeException(e);
            }
        } while (true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mDataInputStream.close();
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class QueueServerBinder extends Binder {
        public void setHandler(Handler aHandler) {
            Log.d(TAG, "Binding handler");
            QueueServerSocketConnection.this.mHandler = aHandler;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "inside onCreate - Creating Socket");
        super.onCreate();
        if (mSocket == null || mSocket.isClosed()) {
            try {
                //mSocket = new Socket(InetAddress.getByName("localhost"), 7201);
                mSocket = new Socket();
                Log.d(TAG, "Opening client socket - ");
                mSocket.bind(null);

                mSocket.connect((new InetSocketAddress(getString(R.string.queue_server_dns),
                        getResources().getInteger(R.integer.next_token_port))), SOCKET_TIMEOUT);

                Log.d(TAG, "Client socket - " + mSocket.isConnected());
                assert mSocket != null;
                mDataInputStream = new DataInputStream(mSocket.getInputStream());
                Log.i(TAG, "Socket Created, Stream Opened");
            } catch (UnknownHostException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
