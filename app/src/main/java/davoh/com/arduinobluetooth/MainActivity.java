package davoh.com.arduinobluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    //VARIABLES PARA LA CONEXION BLUETOOTH
    public Handler bluetooothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIN = new StringBuilder();
    private ConnectedThread MyConexionBT;

    //BOTON PARA ACTUALIZAR DATOS
    Button botonActualizar;


    String ValorCadena1, ValorCadena2, ValorCadena3, ValorCadena4, ValorCadena5;
    EditText posicionServo1, posicionServo2, posicionServo3, posicionServo4, posicionServo5;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //private static final UUID BTMODULEUUID = UUID.fromString("FDA50693A4E24FB1AFCFC6EB07647825");
    private static String address = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btAdapter = BluetoothAdapter.getDefaultAdapter();
        botonActualizar=findViewById(R.id.button);
        posicionServo1=findViewById(R.id.editText);
        posicionServo2=findViewById(R.id.editText2);
        posicionServo3=findViewById(R.id.editText3);
        posicionServo4=findViewById(R.id.editText4);
        posicionServo5=findViewById(R.id.editText5);



        VerifivarEstadoBT();

        botonActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String val1 = posicionServo1.getText().toString();
                String val2 = posicionServo2.getText().toString();
                String val3 = posicionServo3.getText().toString();
                String val4 = posicionServo4.getText().toString();
                String val5 = posicionServo5.getText().toString();

                int angulo1 = Integer.parseInt(val1);
                int angulo2 = Integer.parseInt(val2);
                int angulo3 = Integer.parseInt(val3);
                int angulo4 = Integer.parseInt(val4);
                int angulo5 = Integer.parseInt(val5);



                if(angulo1<200 && angulo1>99){
                    ValorCadena1= Integer.toString(angulo1);
                }else if (angulo1<99 && angulo1>9){
                    ValorCadena1= "0"+ Integer.toString(angulo1);
                }else if (angulo1<10){
                    ValorCadena1= "0"+"0"+ Integer.toString(angulo1);
                }

                if(angulo2<200 && angulo2>99){
                    ValorCadena2= Integer.toString(angulo2);
                }else if (angulo2<99 && angulo2>9){
                    ValorCadena2= "0"+ Integer.toString(angulo2);
                }else if (angulo2<10){
                    ValorCadena2= "0"+"0"+ Integer.toString(angulo2);
                }

                if(angulo3<200 && angulo3>99){
                    ValorCadena3= Integer.toString(angulo3);
                }else if (angulo3<99 && angulo3>9){
                    ValorCadena3= "0"+ Integer.toString(angulo3);
                }else if (angulo3<10){
                    ValorCadena3= "0"+"0"+ Integer.toString(angulo3);
                }

                if(angulo4<200 && angulo4>99){
                    ValorCadena4= Integer.toString(angulo4);
                }else if (angulo4<99 && angulo4>9){
                    ValorCadena4= "0"+ Integer.toString(angulo4);
                }else if (angulo4<10){
                    ValorCadena4= "0"+"0"+ Integer.toString(angulo4);
                }

                if(angulo5<200 && angulo5>99){
                    ValorCadena5= Integer.toString(angulo5);
                }else if (angulo5<99 && angulo5>9){
                    ValorCadena5= "0"+ Integer.toString(angulo5);
                }else if (angulo5<10){
                    ValorCadena5= "0"+"0"+ Integer.toString(angulo5);
                }

                EnviarPosiciones();


            }
        });




    }


    public void EnviarPosiciones() {
        //Un ejemplo la cadena enviada seria: 180180180180180
        MyConexionBT.write("*" + ValorCadena1 + ValorCadena2 + ValorCadena3 + ValorCadena4+ ValorCadena5 +"#");
    }


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    public void onResume() {
        super.onResume();
        Intent intent = getIntent();

        address = intent.getStringExtra(DispositivosBT.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);

        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacion del socket fallo", Toast.LENGTH_SHORT).show();

        }

        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
            }
        }
        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            btSocket.close();

        } catch (IOException e2) {
        }
    }

    private void VerifivarEstadoBT() {
        if (btAdapter == null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_SHORT).show();

        } else {
            if (btAdapter.isEnabled()) {
                Toast.makeText(getBaseContext(), "Dispositivo conectado", Toast.LENGTH_LONG).show();

            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }


    //Crea la clase que permite crear el evento de conexion
    private class ConnectedThread extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run()
        {
            byte[] buffer = new byte[256];
            int bytes;

            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    // Envia los datos obtenidos hacia el evento via handler
                    bluetooothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //Envio de trama
        public void write(String input)
        {
            try {
                mmOutStream.write(input.getBytes());
            }
            catch (IOException e)
            {
                //si no es posible enviar datos se cierra la conexión
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}




