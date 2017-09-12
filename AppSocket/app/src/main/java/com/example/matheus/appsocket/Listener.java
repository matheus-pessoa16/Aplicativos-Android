package com.example.matheus.appsocket;

import android.os.Handler;

import messageProtocol.*;
import java.io.IOException;
import java.io.ObjectInputStream;


/**
 * Created by matheus on 01/12/15.
 */
public class Listener implements Runnable{
    private ObjectInputStream in;
    private Handler handler;
    private Message msg;
    private android.os.Message msgControl;


    public Listener(ObjectInputStream in, Handler handler) {
        this.in = in;
        this.handler = handler;
    }

    @Override
    public void run() {
        while(true){
            msgControl = new android.os.Message();

            try {
                msg = (Message)in.readObject();


                if(msg instanceof Text){
                    msgControl.arg1 = SocketTask.TEXT_MESSAGE;
                    msgControl.obj = ((Text) msg).play();
                }else{
                    if(msg instanceof Audio){
                        msgControl.arg1 = SocketTask.AUDIO_MESSAGE;
                    }
                }


                handler.sendMessage(msgControl);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}
