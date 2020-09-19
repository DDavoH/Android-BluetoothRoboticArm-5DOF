package davoh.com.arduinobluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;

public class FragmentDispositivosBT extends Fragment {

    private FragmentDispositivosBTListener listener;

    private static final String TAG= "DispositivosBT";
    public static final int REQUEST_BT= 3;
    ListView IdLista;


    private BluetoothAdapter mBtAdapter;


    //AQUI SOLO SE MUESTRA EL FRAGMENT
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dispositivos_bt, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        IdLista = view.findViewById(R.id.IdLista);

        VerificarEstadoBT();
    }

    private void VerificarEstadoBT(){
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBtAdapter==null){
            Toast.makeText(requireContext(), "Bluetooth no esta activado", Toast.LENGTH_SHORT).show();
        }else{
            if(mBtAdapter.isEnabled()){
                Log.d(TAG, "....Bluetooth Activado...");
                updateUI();
            }else{
                //SOLICITA AL USUARIO QUE ACTIVE EL BLUETOOTH
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_BT);
            }
        }
    }

    private void updateUI(){
        ArrayAdapter<String> mPairedDevicesArrayAdapter = new ArrayAdapter<>(requireContext(), R.layout.btdevices_row);
        IdLista.setAdapter(mPairedDevicesArrayAdapter);
        IdLista.setOnItemClickListener(mDeviceClickListener);
        mBtAdapter=BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        if(pairedDevices.size()>0){
            for(BluetoothDevice device : pairedDevices){
                mPairedDevicesArrayAdapter.add(device.getName()+ "\n"+device.getAddress());
            }
        }
    }


   private AdapterView.OnItemClickListener mDeviceClickListener =(new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() -17);

            //SAVE DATA IN SINGLETON
            SingletonAddress singletonAddress = SingletonAddress.getInstance();
            singletonAddress.setAddress(address);
            listener.navigationTo(Constants.NAV_MENU);
        }
    });


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode: "+requestCode);
        if(requestCode==REQUEST_BT){
            updateUI();
        }
    }

    public interface FragmentDispositivosBTListener {
        void navigationTo(int position);
        //aqui van las funciones que se comunican con el main activity
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof FragmentDispositivosBTListener){
            listener = (FragmentDispositivosBTListener) context;
        }else {
            throw new RuntimeException(context.toString() + "Must implement FragmentBListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener=null;
    }



}


