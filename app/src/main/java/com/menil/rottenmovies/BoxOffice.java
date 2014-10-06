package com.menil.rottenmovies;

import android.app.Activity;

/**
 * Created by menil on 06.10.2014.
 */

//public static final String apiURL="http://api.rottentomatoes.com/api/public/v1.0/lists/movies/box_office.json?limit=16&country=us&apikey=pj2z7eyve6mfdtcx4vynk26y";
public class BoxOffice extends Activity {
  /*  String input = readRottenT();
    public static final String apiURL="http://api.rottentomatoes.com/api/public/v1.0/lists/movies/box_office.json?limit=16&country=us&apikey=pj2z7eyve6mfdtcx4vynk26y";


    try
    {
        JSONObject json = new JSONObject(input);
        Log.i(Main.class.getName(), JSONObject.class.toString());
    }

    catch(
    Exception e
    )

    {
        e.printStackTrace();
    }
}
    private String readRottenT(){
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(apiURL);
        try{
            HttpResponse response = client.execute(httpget);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if(statusCode==200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            }
            else{
                Log.e(JSONObject.class.toString(), "Failed!");
            }
        } catch(ClientProtocolException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
        return builder.toString();
    }*/

  /*  //load from API
    String urlString = params[0];
    try{
        InputStream in = null;
        URL url= new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        in = new BufferedInputStream(urlConnection.getInputStream());
    }
    catch (Exception e){
        e.printStackTrace();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        return null;
    }*/
}
