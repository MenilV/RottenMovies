package com.menil.rottenmovies;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
//TODO: Make this work
/**
 * Created by menil on 14.11.2014.
 */
    public class ReadWriteJson {
    private Context c;

    ReadWriteJson(Context c)
    {
        this.c=c;
    }

    private void writeToFile(String data, String fileToWrite) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(c.openFileOutput(fileToWrite, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    private String readFromFile(String fileToRead) {

        String ret = "";

        try {
            InputStream inputStream = c.openFileInput(fileToRead);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
}
