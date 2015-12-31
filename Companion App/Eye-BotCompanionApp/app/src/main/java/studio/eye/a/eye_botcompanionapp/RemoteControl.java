package studio.eye.a.eye_botcompanionapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;

public class RemoteControl extends Activity
{
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);

    Button left_button;
    Button right_button;
    Button up_button;
    Button down_button;
    Button a_button;
    Button b_button;
    Button settings_button;

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote);
        init ();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
        {
            findViewById(android.R.id.content).setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void init ()
    {
        left_button = (Button) findViewById(R.id.leftBT);
        right_button = (Button) findViewById(R.id.rightBT);
        up_button = (Button) findViewById(R.id.upBT);
        down_button = (Button) findViewById(R.id.downBT);
        a_button = (Button) findViewById(R.id.aBT);
        b_button = (Button) findViewById(R.id.bBT);
        settings_button = (Button) findViewById(R.id.remoteSettingsBT);

        settings_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onClickAnimation(settings_button);
                Intent intent = new Intent(RemoteControl.this, RemoteSettings.class);
                startActivity(intent);
            }
        });

        left_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onClickAnimation(left_button);
                BluetoothService.SendOverBluetooth("d1");
            }
        });

        right_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onClickAnimation(right_button);
                BluetoothService.SendOverBluetooth("d2");
            }
        });

        up_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onClickAnimation(up_button);
                BluetoothService.SendOverBluetooth("d3");
            }
        });

        down_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onClickAnimation(down_button);
                BluetoothService.SendOverBluetooth("d4");
            }
        });

        a_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onClickAnimation(a_button);
                BluetoothService.SendOverBluetooth("a1");
            }
        });

        b_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onClickAnimation(b_button);
                BluetoothService.SendOverBluetooth("a2");
            }
        });
    }

    public void onClickAnimation(View v)
    {
        v.startAnimation(buttonClick);
    }

}
