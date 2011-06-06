package org.rhok.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainMenuActivity extends Activity
{
    private Button captureHeightButton;
    private Button uploadButton;

    private OnClickListener captureOnClickListener = new OnClickListener()
    {

        public void onClick(View v)
        {
            if (externalStorageIsAvailable())
            {
                startActivity(new Intent(getApplicationContext(),
                        HeightCatcherActivity.class));
            } else
            {
                errorToLogAndToast("You need to mount some external storage to add a record");
            }
        }
    };

    private boolean externalStorageIsAvailable()
    {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    private void errorToLogAndToast(String errorMessage)
    {
        Toast
                .makeText(getApplicationContext(), errorMessage,
                        Toast.LENGTH_LONG).show();
    }

    private OnClickListener uploadOnClickListener = new OnClickListener()
    {

        public void onClick(View v)
        {
            startActivity(new Intent(getApplicationContext(),
                    UploadActivity.class));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_activity);

        captureHeightButton = (Button) findViewById(R.id.capture_height_button);
        uploadButton = (Button) findViewById(R.id.upload_height_button);

        captureHeightButton.setOnClickListener(captureOnClickListener);
        uploadButton.setOnClickListener(uploadOnClickListener);
    }
}
