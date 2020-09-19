package davoh.com.arduinobluetooth;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class FragmentMenuNavigation extends Fragment {


    private FragmentMenuNavigationListener listener;

    Button btnInput, btnSeekBar;


    //AQUI SOLO SE MUESTRA EL FRAGMENT
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_navigation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        btnInput = view.findViewById(R.id.btnInput);
        btnSeekBar = view.findViewById(R.id.btnSeekBar);

        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.navigationTo(Constants.EXTRA_INPUT_TEXT);
            }
        });

        btnSeekBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.navigationTo(Constants.EXTRA_SEEKBACK);
            }
        });

    }



    public interface FragmentMenuNavigationListener {
        //aqui van las funciones que se comunican con el main activity
        void navigationTo(int position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof FragmentMenuNavigationListener){
            listener = (FragmentMenuNavigationListener) context;
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