package sqlite.com.example.matheus.sistemaaquisicaodados;

import android.app.Notification;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by matheus on 09/09/17.
 */

public class ConnectionThread extends Thread{

    BluetoothSocket btSock = null;
    BluetoothServerSocket btServerSocket = null;
    InputStream input = null;
    OutputStream output;
    String myUUID = "00001101-0000-1000-8000-00805F9B34FB";
    boolean server, running = false, isConnected = false;
    String btDevAddress = null;


    public ConnectionThread(){
        this.server = true;
    }

    public ConnectionThread(String btDevAddress) {

        this.server = false;
        this.btDevAddress = btDevAddress;
    }

    public void run(){
        this.running = true;
        BluetoothAdapter btAdapt = BluetoothAdapter.getDefaultAdapter();

        if(this.server){
            try{

                btServerSocket = btAdapt.listenUsingRfcommWithServiceRecord("Super Counter", UUID.fromString(myUUID));
                btSock = btServerSocket.accept();

                if(btSock != null){
                    btServerSocket.close();
                }

            }catch (IOException e){
                e.printStackTrace();
                toMainActivity("---N".getBytes());
            }
        }else {

            /*  Cliente.
             */
            try {

                /*  Obtem uma representação do dispositivo Bluetooth com
                endereço btDevAddress.
                    Cria um socket Bluetooth.
                 */
                BluetoothDevice btDevice = btAdapt.getRemoteDevice(btDevAddress);
                btSock = btDevice.createRfcommSocketToServiceRecord(UUID.fromString(myUUID));

                /*  Envia ao sistema um comando para cancelar qualquer processo
                de descoberta em execução.
                 */
                btAdapt.cancelDiscovery();

                /*  Solicita uma conexão ao dispositivo cujo endereço é
                btDevAddress.
                    Permanece em estado de espera até que a conexão seja
                estabelecida.
                 */
                if (btSock != null) {
                    btSock.connect();
                }

            } catch (IOException e) {

                /*  Caso ocorra alguma exceção, exibe o stack trace para debug.
                    Envia um código para a Activity principal, informando que
                a conexão falhou.
                 */
                e.printStackTrace();
                toMainActivity("---N".getBytes());
            }
        }

        if(btSock != null){
            this.isConnected = true;
            toMainActivity("---S".getBytes());

            try{
                input = btSock.getInputStream();
                output = btSock.getOutputStream();

                while(running){

                    byte [] buffer = new byte[128];
                    int bytes;
                    int bytesRead = -1;

                    do{

                        bytes = input.read(buffer, bytesRead+1, 1);
                        bytesRead += bytes;
                    }while(buffer[bytesRead] != '\n');

                    toMainActivity(Arrays.copyOfRange(buffer, 0, bytesRead-1));
                }
            }catch(IOException e){
                e.printStackTrace();
                toMainActivity("---N".getBytes());
                this.isConnected = false;
            }
        }

    }

    private void toMainActivity(byte[] data){
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putByteArray("data", data);
        message.setData(bundle);
        MainActivity.handler.sendMessage(message);
    }


    public void write(byte [] data){
        if(output != null){
            try{
                output.write(data);
            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
            toMainActivity("---N".getBytes());
        }
    }


    public void cancel(){
        try{
            running = false;
            this.isConnected = false;
            btServerSocket.close();
            btSock.close();

        }catch (IOException e){
            e.printStackTrace();
        }

        running = false;
        this.isConnected = false;
    }

    public boolean isConnected(){
        return this.isConnected;
    }

}
