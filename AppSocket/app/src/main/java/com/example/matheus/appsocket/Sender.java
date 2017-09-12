package com.example.matheus.appsocket;

import android.os.Handler;
import android.os.Message;


import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by matheus on 01/12/15.
 */
public class Sender implements Runnable {


    private ObjectOutputStream out;
    private Handler handler;
    private Message msgControl;
    private messageProtocol.Message msgContent;


    public Sender(ObjectOutputStream out, Handler handler) {
        this.out = out;
        this.handler = handler;
    }

    @Override
    public void run() {

        while(true){
            if(msgContent != null) {
                msgControl = new Message();
                msgControl.arg1 = SocketTask.SENDING_MESSAGE;
                handler.sendMessage(msgControl);
                try {
                    out.writeObject(msgContent);
                    out.flush();
                    msgContent = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }




        }

    }


    public void setMessageContent(messageProtocol.Message msg){
        this.msgContent = msg;
    }


}
