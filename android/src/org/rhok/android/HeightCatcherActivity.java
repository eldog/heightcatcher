package org.rhok.android;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class HeightCatcherActivity extends Activity
{
    private static final String TAG = "HeightCatcher";
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int DRAW_HEIGHT_REQUEST_CODE = 1;

    private String imagePath;
    private Uri imageURI;

    private Location location;

    private Button cameraLaunchButton;
    private Button editPointsButton;
    private Spinner referenceSpinner;
    private TextView heightTextView;
    private TextView locationTextView;
    private Button getLocationButton;
    private Button calcBMIButton;
    private EditText nameEditText;
    private EditText weightEditText;
    private DatePicker datePicker;

    LocationManager lm;

    private double height = -1;

    // The coords of the ref points and the person points
    private float[] coords;

    private HeightCatcherDB mDb;

    private OnClickListener editPointsListener = new OnClickListener()
    {

        public void onClick(View v)
        {

            launchDrawHeightActivity();

        }
    };

    private OnClickListener getLocationListener = new OnClickListener()
    {

        public void onClick(View v)
        {
            lm = ((LocationManager) getSystemService(Context.LOCATION_SERVICE));

            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                    new GeoListener());

        }
    };

    private class GeoListener implements LocationListener
    {

        public void onLocationChanged(Location newLocation)
        {
            Log.d(TAG, "Location changed " + newLocation.getLatitude() + " "
                    + newLocation.getLongitude());
            location = newLocation;
            locationTextView.setText(String.format("Lat: %.2f Long: %.2f",
                    location.getLatitude(), location.getLongitude()));
            lm.removeUpdates(this);
        }

        public void onProviderDisabled(String provider)
        {
            // TODO Auto-generated method stub

        }

        public void onProviderEnabled(String provider)
        {
            // TODO Auto-generated method stub

        }

        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            // TODO Auto-generated method stub

        }

    }

    private OnClickListener cameraOnClickListener = new OnClickListener()
    {
        public void onClick(View v)
        {
            String fileName = "temp.jpg";
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            imageURI = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
            try
            {
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
            } catch (ActivityNotFoundException e)
            {
                errorToToastAndLog("No application available to take a photo");
            }
        }
    };

    private void launchDrawHeightActivity()
    {
        if (!TextUtils.isEmpty(imagePath))
        {
            Intent intent = new Intent(getApplicationContext(),
                    DrawHeightActivity.class);
            intent.putExtra(HeightCatcher.IMAGE_LOCATION, imagePath);
            startActivityForResult(intent, DRAW_HEIGHT_REQUEST_CODE);
        } else
        {
            Log.wtf(TAG,
                    "How did we press the launch if imagepath wasn't set?");
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == IMAGE_REQUEST_CODE)
        {
            switch (resultCode)
            {
            case RESULT_OK:
                String[] projection =
                    { MediaStore.Images.Media.DATA };
                Cursor cursor = managedQuery(imageURI, projection, null, null,
                        null);
                int column_index_data = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String capturedImageFilePath = cursor
                        .getString(column_index_data);
                imagePath = capturedImageFilePath;
                launchDrawHeightActivity();
                editPointsButton.setEnabled(true);

            }
        } else if (requestCode == DRAW_HEIGHT_REQUEST_CODE)
        {
            switch (resultCode)
            {
            case RESULT_OK:
                coords = data.getFloatArrayExtra(HeightCatcher.POINTS);
                RefObj refObj = (RefObj) referenceSpinner.getSelectedItem();
                height = Person.height(refObj, coords[0], coords[1], coords[2],
                        coords[3], coords[4], coords[5], coords[6], coords[7]);
                heightTextView.setText(String.format("%.2f cm", height));
            }
        }
    };

    @SuppressWarnings("unused")
    private void debugToToastAndLog(String logMessage)
    {
        Log.d(TAG, logMessage);
        Toast.makeText(HeightCatcherActivity.this, logMessage,
                Toast.LENGTH_LONG).show();
    }

    private void errorToToastAndLog(String errorMessage)
    {
        Log.e(TAG, errorMessage);
        Toast.makeText(HeightCatcherActivity.this, "ERROR: " + errorMessage,
                Toast.LENGTH_LONG).show();
    }

    private OnClickListener calcBMIOnClickListener = new OnClickListener()
    {

        public void onClick(View v)
        {
            double weightKilos;
            double heightMetres;
            try
            {
                weightKilos = Double.parseDouble(weightEditText.getText()
                        .toString());
            } catch (NumberFormatException e)
            {
                errorToToastAndLog("You need to enter a number into weight");
                return;
            }
            if (height < 0)
            {
                errorToToastAndLog("You need to measure the height");
                return;
            }
            if (TextUtils.isEmpty(nameEditText.getText().toString()))
            {
                errorToToastAndLog("You need to enter a name");
                return;
            }

            Calendar c = Calendar.getInstance();

            c.set(datePicker.getYear(), datePicker.getMonth(), datePicker
                    .getDayOfMonth());

            long age = c.getTimeInMillis();

            double longitude = 0.0;
            double latitude = 0.0;

            if (location != null)
            {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }

            // Save it to the db
            mDb.addPerson(nameEditText.getText().toString(), age, weightKilos,
                    imagePath, "Scott Jamie", latitude, longitude,
                    ((RefObj) referenceSpinner.getSelectedItem()).id,
                    coords[0], coords[1], coords[2], coords[3], coords[4],
                    coords[5], coords[6], coords[7]);

            heightMetres = height / 100;

            double bMI = weightKilos / (heightMetres * heightMetres);

            String message;
            if (bMI >= 30.0)
            {
                message = "This person is obese";
            } else if (bMI >= 25.0)
            {
                message = "This person is overweight";
            } else if (bMI >= 18.5)
            {
                message = "This person is normal";
            } else
            {
                message = "This person is underweight";
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(
                    HeightCatcherActivity.this);
            builder.setMessage(String.format("BMI is %.2f\n%s", bMI, message))
                    .setCancelable(false).setPositiveButton("OK",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog,
                                        int id)
                                {
                                    dialog.cancel();
                                    HeightCatcherActivity.this.finish();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.height_catcher_activity);

        cameraLaunchButton = (Button) findViewById(R.id.camera_launch_button);
        referenceSpinner = (Spinner) findViewById(R.id.reference_spinner);
        heightTextView = (TextView) findViewById(R.id.height_text_view);
        nameEditText = (EditText) findViewById(R.id.name_edit_text);
        weightEditText = (EditText) findViewById(R.id.weight_edit_text);
        datePicker = (DatePicker) findViewById(R.id.age_date_picker);
        calcBMIButton = (Button) findViewById(R.id.calculate_BMI_button);
        locationTextView = (TextView) findViewById(R.id.location_text_view);
        getLocationButton = (Button) findViewById(R.id.get_location_button);

        editPointsButton = (Button) findViewById(R.id.edit_points_button);

        mDb = new HeightCatcherDB(getApplicationContext());

        ArrayList<RefObj> r = mDb.getRefObjs();

        ArrayAdapter<RefObj> adapter = new ArrayAdapter<RefObj>(
                getApplicationContext(), android.R.layout.simple_spinner_item,
                r);
        adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        referenceSpinner.setAdapter(adapter);

        cameraLaunchButton.setOnClickListener(cameraOnClickListener);
        editPointsButton.setOnClickListener(editPointsListener);
        getLocationButton.setOnClickListener(getLocationListener);
        calcBMIButton.setOnClickListener(calcBMIOnClickListener);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(nameEditText.getWindowToken(),
                InputMethodManager.HIDE_IMPLICIT_ONLY);

    }
}