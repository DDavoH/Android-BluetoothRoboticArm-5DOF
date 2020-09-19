package davoh.com.arduinobluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.UUID;


public class FragmentSeekBar extends Fragment {



    //VARIABLES PARA LA CONEXION BLUETOOTH
    private Handler bluetooothIn;
    private int handlerState;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private ConnectedThread MyConexionBT;


    private String ValorCadena1="015";
    private String ValorCadena2="015";
    private String ValorCadena3="015";
    private String ValorCadena4="015";
    private String ValorCadena5="015";

    SeekBar seekBar1, seekBar2, seekBar3, seekBar4, seekBar5;
    TextView tv1, tv2, tv3, tv4, tv5;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //private static final UUID BTMODULEUUID = UUID.fromString("FDA50693A4E24FB1AFCFC6EB07647825");



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_seekbar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        seekBar1=view.findViewById(R.id.seekBar1);
        seekBar2=view.findViewById(R.id.seekBar2);
        seekBar3=view.findViewById(R.id.seekBar3);
        seekBar4=view.findViewById(R.id.seekBar4);
        seekBar5=view.findViewById(R.id.seekBar5);
        tv1 = view.findViewById(R.id.tv1);
        tv2 = view.findViewById(R.id.tv2);
        tv3 = view.findViewById(R.id.tv3);
        tv4 = view.findViewById(R.id.tv4);
        tv5 = view.findViewById(R.id.tv5);



        VerifivarEstadoBT();


        SingletonAddress singletonAddress = SingletonAddress.getInstance();
        String address =   singletonAddress.getAddress();

        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);

        } catch (IOException e) {
            Toast.makeText(requireContext(), "La creacion del socket fallo", Toast.LENGTH_SHORT).show();

        }

        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException ignored) {
            }
        }
        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();

        //read saved data



        //read data
        String defaultSharedPreferencesName = this.requireActivity().getPackageName() + "_preferences";
        SharedPreferences spr = this.requireActivity().getSharedPreferences(defaultSharedPreferencesName, Context.MODE_PRIVATE);

        ValorCadena1 = spr.getString(Constants.SP_SERVO1, "015");
        ValorCadena2 = spr.getString(Constants.SP_SERVO2, "015");
        ValorCadena3 = spr.getString(Constants.SP_SERVO3, "015");
        ValorCadena4 = spr.getString(Constants.SP_SERVO4, "015");
        ValorCadena5 = spr.getString(Constants.SP_SERVO5, "015");


        SeekBarController(seekBar1,seekBar2,seekBar3,seekBar4,seekBar5);
        updateText();

    }

    public void EnviarPosiciones() {
        //Un ejemplo la cadena enviada seria: 180180180180180
        MyConexionBT.write("*" + ValorCadena1 + ValorCadena2 + ValorCadena3 + ValorCadena4+ ValorCadena5 +"#");
    }


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    private void VerifivarEstadoBT() {
        if (btAdapter == null) {
            Toast.makeText(requireContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_SHORT).show();

        } else {
            if (btAdapter.isEnabled()) {
                Toast.makeText(requireContext(), "Dispositivo conectado", Toast.LENGTH_LONG).show();

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
                Toast.makeText(requireContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                requireActivity().finish();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
          /*  try {
            btSocket.close();

        } catch (IOException e2) {
        }*/
    }



    @Override
    public void onDetach() {
        super.onDetach();
        //to close conection
        try {
            btSocket.close();

        } catch (IOException e2) {
        }
    }

    private void SeekBarController(SeekBar seekBar1, SeekBar seekBar2,  SeekBar seekBar3,
                                        SeekBar seekBar4,  SeekBar seekBar5){
        //Initialize values
        seekBar1.setProgress(Integer.parseInt(ValorCadena1));
        seekBar2.setProgress(Integer.parseInt(ValorCadena2));
        seekBar3.setProgress(Integer.parseInt(ValorCadena3));
        seekBar4.setProgress(Integer.parseInt(ValorCadena4));
        seekBar5.setProgress(Integer.parseInt(ValorCadena5));

        //listeners
        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                DecimalFormat mformat = new DecimalFormat("000");
                ValorCadena1= mformat.format(i);
                EnviarPosiciones();
                updateText();
                saveData();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                DecimalFormat mformat = new DecimalFormat("000");
                ValorCadena2= mformat.format(i);
                EnviarPosiciones();
                updateText();
                saveData();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                DecimalFormat mformat = new DecimalFormat("000");
                ValorCadena3= mformat.format(i);
                EnviarPosiciones();
                updateText();
                saveData();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                DecimalFormat mformat = new DecimalFormat("000");
                ValorCadena4= mformat.format(i);
                EnviarPosiciones();
                updateText();
                saveData();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar5.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                DecimalFormat mformat = new DecimalFormat("000");
                ValorCadena5= mformat.format(i);
                EnviarPosiciones();
                updateText();
                saveData();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    public void updateText(){
        tv1.setText(ValorCadena1);
        tv2.setText(ValorCadena2);
        tv3.setText(ValorCadena3);
        tv4.setText(ValorCadena4);
        tv5.setText(ValorCadena5);
    }

    public void saveData(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        sp.edit().putString(Constants.SP_SERVO1, ValorCadena1).apply();
        sp.edit().putString(Constants.SP_SERVO2, ValorCadena2).apply();
        sp.edit().putString(Constants.SP_SERVO3, ValorCadena3).apply();
        sp.edit().putString(Constants.SP_SERVO4, ValorCadena4).apply();
        sp.edit().putString(Constants.SP_SERVO5, ValorCadena5).apply();
    }





}