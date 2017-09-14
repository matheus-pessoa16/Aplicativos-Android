package sqlite.com.example.matheus.sistemaaquisicaodados;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity implements View.OnClickListener{

    public enum Sensor {
        LDR(0), NTC(1), MOTOR(2);

        private final int indice;

        Sensor(int i) {
            indice = i;
        }

        public int getIndice() {
            return indice;
        }
    }


    static Sensor SENSOR = Sensor.LDR;

    static TextView statusMessage;
   // static TextView counterMessage;

    private ImageView ldrBtn;
    private ImageView ntcBtn;
    private ImageView motorBtn;


    ConnectionThread connect;

    static String dados;
    GraphView graph;
    LineGraphSeries<DataPoint> series;
    private int lastX = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusMessage = (TextView) findViewById(R.id.statusMessage);
        //counterMessage = (TextView) findViewById(R.id.counterMessage);
        graph = (GraphView) findViewById(R.id.graph);


        ldrBtn = (ImageView) findViewById(R.id.ldrBtn);
        ntcBtn = (ImageView) findViewById(R.id.ntcBtn);
        motorBtn = (ImageView) findViewById(R.id.motorBtn);

        ldrBtn.setOnClickListener(this);
        ntcBtn.setOnClickListener(this);
        motorBtn.setOnClickListener(this);

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            statusMessage.setText("Bluetooth não conectado");
        } else {
            statusMessage.setText("Bluetooth Conectado");
        }

        btAdapter.enable();

        connect = new ConnectionThread("00:15:83:35:5A:23");
        connect.start();


        try {
            Thread.sleep(1000);
        } catch (Exception E) {
            E.printStackTrace();
        }

        series = new LineGraphSeries<>();
        graph.addSeries(series);

        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(1023);
        //viewport.setScrollable(true);

        viewport.setMinX(0);
        viewport.setMaxX(lastX);
        viewport.scrollToEnd();
        viewport.setScalable(true);
        viewport.setScalableY(true);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.motorBtn:
                SENSOR = Sensor.MOTOR;
                graph.setTitle("MOTOR");

                break;

            case R.id.ntcBtn:
                SENSOR = Sensor.NTC;
                graph.setTitle("NTC");
                break;

            default:
                SENSOR = Sensor.LDR;
                graph.setTitle("LDR");

                break;
        }
    }


    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString = new String(data);


            if (dataString.equals("---N"))
                statusMessage.setText("Ocorreu um erro durante a conexão D:");
            else if (dataString.equals("---S"))
                statusMessage.setText("Conectado :D");
            else {

                /* Se a mensagem não for um código de status,
                    então ela deve ser tratada pelo aplicativo
                    como uma mensagem vinda diretamente do outro
                    lado da conexão. Nesse caso, simplesmente
                    atualizamos o valor contido no TextView do
                    contador.
                 */
                //counterMessage.setText(dataString);
                dados = dataString;
                Log.d("myTag", dataString);
            }


        }
    };

    // capturar o botão que foi pressionado
    // enviar para o arduino um comando para mandar dados de outra variável (mudar sensor)
    // atualizar a string global de dados
    // resetar o gráfico para exibir a nova string

    public void restartCounter(View view) {
        connect.write("Trocar\n".getBytes());
    }

    // add random data to graph
    private void addEntry() {
        // here, we choose to display max 10 points on the viewport and we scroll to end
        DataPoint[] values = new DataPoint[30];
        String valor;
        if (dados != null)

        {
//            for (int i = 0; i < 10; i++) {
//                DataPoint v = new DataPoint(lastX++, Double.parseDouble(dados));
//                values[i] = v;
//            }

            valor = dados.split(";")[SENSOR.getIndice()];
            series.appendData(new DataPoint(lastX++, Double.parseDouble(valor)), true, 40);



        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {

            @Override
            public void run() {

                // we add 100 new entries
                for (int i = 0; i < 1000; i++) {
                    runOnUiThread(new Runnable() {


                        @Override
                        public void run() {
                            addEntry();
                        }
                    });


                    // sleep to slow down the add of entries
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }

            }
        }).start();
    }


}



