package org.rhok.android;

import java.util.HashSet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

public class DrawHeightActivity extends Activity
{
    private String imagePath;

    private ToggleButton ref1Button;
    private ToggleButton ref2Button;
    private ToggleButton per1Button;
    private ToggleButton per2Button;
    private Button doneButton;
    
    public void setDoneButton(boolean b)
    {
        doneButton.setEnabled(b);
    }
    
    

    private OnClickListener colourOnClickListener = new OnClickListener()
    {
        
        

        public void onClick(View v)
        {
            HashSet<ToggleButton> tButtons = new HashSet<ToggleButton>();
            
            tButtons.add(ref1Button);
            tButtons.add(ref2Button);
            tButtons.add(per1Button);
            tButtons.add(per2Button);
            
            tButtons.remove((ToggleButton)v);
            ((ToggleButton) v).setChecked(true);
            
            for (ToggleButton tb : tButtons)
            {
                tb.setChecked(false);
            }
            
            if (v == ref1Button)
            {
                drawHeightView.setCoordIndex(0);
                
            } else if (v == ref2Button)
            {
                drawHeightView.setCoordIndex(1);
            } else if (v == per1Button)
            {
                drawHeightView.setCoordIndex(2);
            } else if (v == per2Button)
            {
                drawHeightView.setCoordIndex(3);
            }

        }
    };

    private OnClickListener doneOnClickListener = new OnClickListener()
    {

        public void onClick(View v)
        {
            Intent intent = new Intent();
            
            float[] coords = new float[8];
            
            // This is Durty code... Like mud.
            int j = 0;
            for (int i = 0; i < drawHeightView.coords.length; i++)
            {
                coords[j ++] = drawHeightView.coords[i].x; 
                coords[j ++] = drawHeightView.coords[i].y; 

            }
            
            intent.putExtra(HeightCatcher.POINTS, coords);
            setResult(RESULT_OK, intent);
            finish();

        }
    };

    private DrawHeightView drawHeightView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent.hasExtra(HeightCatcher.IMAGE_LOCATION))
        {
            // We better bloody have the image!
            imagePath = intent.getExtras().getString(
                    HeightCatcher.IMAGE_LOCATION);

            setContentView(R.layout.draw_height_activity);

            ref1Button = (ToggleButton) findViewById(R.id.ref_XY_1);
            ref2Button = (ToggleButton) findViewById(R.id.ref_XY_2);
            per1Button = (ToggleButton) findViewById(R.id.per_XY_1);
            per2Button = (ToggleButton) findViewById(R.id.per_XY_2);

            ref1Button.setOnClickListener(colourOnClickListener);
            ref2Button.setOnClickListener(colourOnClickListener);
            per1Button.setOnClickListener(colourOnClickListener);
            per2Button.setOnClickListener(colourOnClickListener);

            doneButton = (Button) findViewById(R.id.draw_done_button);
            doneButton.setOnClickListener(doneOnClickListener);

            drawHeightView = (DrawHeightView) findViewById(R.id.image);
            drawHeightView.setup(imagePath, this);

        }
    }
}
