package com.example.matheus.appsocket;

import android.app.Activity;
import android.app.ActivityManager;

import android.os.Handler;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity{

    private SocketTask connection;
    private ObjectInputStream is;
    protected ObjectOutputStream os;
    private String host;
    private int port = 2000;
    private messageProtocol.Message msgContent;


    private Button send;
    private Button connect;
    private TextView display;
    private TextView content;
    private EditText ip;


    private Handler hd = new Handler() {
        public void handleMessage(Message msg) {
            synchronized (msg){
                switch (msg.arg1){
                    case SocketTask.TEXT_MESSAGE:
                        display.setText(msg.obj.toString());
                        break;
                }
            }
        };

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send = (Button) findViewById(R.id.buttonSend);
        //connect = (Button) findViewById(R.id.buttonConnect);
        display = (TextView) findViewById(R.id.display);
        content = (EditText) findViewById(R.id.content);



        connect.setOnClickListener(connectListener);
        //send.setOnClickListener(sendMessage);

    }

    private View.OnClickListener connectListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            ip = (EditText) findViewById(R.id.ip);

            connection = new SocketTask(ip.getText().toString(), port, hd);
            //connection.startListener();

        }
    };

    private View.OnClickListener sendMessage= new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            msgContent = (messageProtocol.Message)content.getText();
            connection.sendMessage(msgContent);
        }
    };




}
