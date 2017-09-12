package com.example.matheus.appsocket;

import android.os.AsyncTask;
import android.os.Handler;

import android.util.Log;

import messageProtocol.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


/**
 * Created by matheus on 24/11/15.
 */
public class SocketTask implements Runnable{
    private Socket socket;
    private ObjectInputStream in;
    protected ObjectOutputStream out;
    private String host;
    private int port;
    private Handler handler;
    private Sender sender;
    private Listener listen;
    private messageProtocol.Message msg;

    //STATUS MESSAGE
    public static final int TEXT_MESSAGE = 1;
    public static final int AUDIO_MESSAGE = 2;
    public static final int CONTROL_MESSAGE = 3;
    public static final int SENDING_MESSAGE = 4;

    /**
     * Construtor com host, porta e timeout
     *
     * @param host host para conexão
     * @param port porta para conexão
     */

    public SocketTask(String host, int port, Handler hd) {
        this.host = host;
        this.port = port;
        this.handler = hd;

    }


    public void startSender() {
        sender = new Sender(out, handler);
        new Thread(sender).start(); //thread de envio de mensagens
    }

    public void startListener() {
        listen = new Listener(in, handler);
        new Thread(listen).start();
    }


    public void sendMessage(Message m) {
        sender.setMessageContent(m);
    }


    @Override
    public void run() {
        try{
            socket = new Socket(this.host, this.port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        }catch (Exception e){
            e.getMessage();
        }
        startListener();
        startSender();
    }
}
