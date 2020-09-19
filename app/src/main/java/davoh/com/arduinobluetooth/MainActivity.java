package davoh.com.arduinobluetooth;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements FragmentDispositivosBT.FragmentDispositivosBTListener
,FragmentMenuNavigation.FragmentMenuNavigationListener{




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.contenedor, new FragmentDispositivosBT())
                .commit();

    }




    @Override
    public void navigationTo(int position) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Intent intent;
        switch (position){
            case Constants.NAV_MENU:
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.contenedor, new FragmentMenuNavigation())
                        .commit();
                break;

            case Constants.EXTRA_INPUT_TEXT:
                intent= new Intent(this, RoboticArmActivity.class);
                intent.putExtra(Constants.EXTRA_NAVIGATION, Constants.EXTRA_INPUT_TEXT);
                startActivity(intent);
                break;

            case Constants.EXTRA_SEEKBACK:
                intent = new Intent(this, RoboticArmActivity.class);
                intent.putExtra(Constants.EXTRA_NAVIGATION, Constants.EXTRA_SEEKBACK);
                startActivity(intent);
                break;
        }



    }


}




