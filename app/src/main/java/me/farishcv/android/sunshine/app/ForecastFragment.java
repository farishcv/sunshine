package me.farishcv.android.sunshine.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            FetchWeatherTak weatherTak = new FetchWeatherTak();
            weatherTak.execute("560025");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootview.findViewById(R.id.listview_forecast);
        ArrayAdapter<String> ad = new ArrayAdapter<String>(getContext(), R.layout.list_item_forecast, R.id.list_item_forecast_textview,
                Arrays.asList("Today - Sunny - 88/63", "Tomorrow - Foggy - 70/40", "Weds - Cloudy - 72/63", "Thurs - Asteroids - 75/65", "Fri - Heavy Rain - 65/56", "Sat - HELP TRAPPED IN WEATHER STATION - 60/51", "Sun - Sunny - 80/68"));
        listView.setAdapter(ad);

        return rootview;
    }

    public class FetchWeatherTak extends AsyncTask<String, Void, Void> {
        private final String LOG_TAG = FetchWeatherTak.class.getSimpleName();

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection urlconnection = null;
            BufferedReader reader = null;

            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 7;

            try {

                final String FORECAST_BASE_URL =
                        "http://api.openweathermap.org/data/2.5/weather?q=560025&mode=json&units=metric&cnt=7&appid=44db6a862fba0b067b1930da0d769e98";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URI" + builtUri.toString());

                urlconnection = (HttpURLConnection) url.openConnection();
                urlconnection.setRequestMethod("GET");
                urlconnection.connect();

                InputStream inputStream = urlconnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;


                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlconnection != null) {
                    urlconnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }

            }

            return null;
        }

    }

}