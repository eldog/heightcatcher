package org.rhok.android;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class UploadActivity extends Activity
{
    private static final String TAG = "HeightCatcher";

    private HeightCatcherDB mDb;
    

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_activity);
        mDb = new HeightCatcherDB(getApplicationContext());
        ArrayList<Person> people = mDb.getPeople();
        if (people != null && people.size() > 0)
        {
            new UploadHeightTask().execute(mDb.getPeople());
        }
        else
        {
            debugToLogAndToast("Nothing to upload");
            finish();
        }
    }

    private void errorToLogAndToast(String message)
    {
        Log.e(TAG, message);
        Toast.makeText(getApplicationContext(), "ERROR: " + message,
                Toast.LENGTH_LONG).show();
    }

    private void debugToLogAndToast(String message)
    {
        Log.d(TAG, message);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
                .show();
    }

    private class UploadHeightTask extends
            AsyncTask<ArrayList<Person>, String, HttpResponse>
    {
        HttpPost mPost;
        HttpClient mClient;
        HttpResponse mResponse;
        private final ProgressDialog mProgressDialog = new ProgressDialog(
                UploadActivity.this);

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            mProgressDialog.setMessage("Uploading heights");
            mProgressDialog.show();
        }

        @Override
        protected HttpResponse doInBackground(ArrayList<Person>... persons)
        {
            int total = persons[0].size();
            int numberSoFar = 0;
            for (Person person : persons[0])
            {
                publishProgress("Uploading " + ++numberSoFar + "/" + total
                        + " records");
                mClient = new DefaultHttpClient();
                mPost = new HttpPost("http://heightcatcher.appspot.com/person");
                try
                {
                    mPost.setEntity(new UrlEncodedFormEntity(person
                            .toNameValuePairList()));
                } catch (UnsupportedEncodingException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                }
                try
                {
                    mResponse = mClient.execute(mPost);

                } catch (ClientProtocolException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return null;

                } catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return null;

                }
            }
            return mResponse;
        }

        @Override
        protected void onProgressUpdate(String... values)
        {
            super.onProgressUpdate(values);
            mProgressDialog.setMessage(values[0]);
        }

        @Override
        protected void onPostExecute(HttpResponse result)
        {

            super.onPostExecute(result);

            mProgressDialog.dismiss();

            if (result != null)
            {
                debugToLogAndToast(result.getStatusLine().getStatusCode()
                        + ": " + result.getStatusLine().getReasonPhrase());
                mDb.flush();
            } else
            {
                debugToLogAndToast("result was null");
            }
            finish();

        }

    }
}
