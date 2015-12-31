package studio.eye.a.eye_botcompanionapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.Toast;

public class MainMenu extends Activity
{
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);

    Button remoteButton;
    Button mappingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        init ();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    void init ()
    {
        remoteButton = (Button) findViewById(R.id.remoteBT);
        mappingButton = (Button) findViewById(R.id.mappingBT);

        remoteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onClickAnimation(remoteButton);
                Intent intent = new Intent(MainMenu.this, RemoteControl.class);
                startActivity(intent);
            }
        });

        mappingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
            {
                onClickAnimation(mappingButton);
                Toast.makeText(MainMenu.this, "This feature is not available yet", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void onClickAnimation(View v)
    {
        v.startAnimation(buttonClick);
    }
}
