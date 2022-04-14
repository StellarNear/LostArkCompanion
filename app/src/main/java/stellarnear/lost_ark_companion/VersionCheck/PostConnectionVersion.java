package stellarnear.lost_ark_companion.VersionCheck;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import stellarnear.lost_ark_companion.BuildConfig;
import stellarnear.lost_ark_companion.Log.CustomLog;
import stellarnear.lost_ark_companion.R;

public class PostConnectionVersion {
    private final CustomLog log;
    private final Context mC;

    public PostConnectionVersion(Context mC) {
        this.mC = mC;
        this.log = new CustomLog(PostConnectionVersion.class);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        if (settings.getBoolean("switch_shadow_link", mC.getResources().getBoolean(R.bool.switch_shadow_link_def))) {
            SendRequestData send = new SendRequestData();
            send.execute();
        }
    }

    public String formatDataAsSingleString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();
        while (itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

    private String stringFromStream(InputStream inputStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer sb = new StringBuffer();
        String line = "";
        while ((line = in.readLine()) != null) {
            sb.append(line);
            break;
        }
        in.close();
        return sb.toString();
    }

    public class SendRequestData extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
        }

        protected String doInBackground(String... arg0) {
            try {
                URL url = new URL("https://script.google.com/macros/s/AKfycbzhemTimJo-zsRVLv7rbGMpleCBIdniecTByFyCYIwgwd5Bt9i9MP7GPWnESFvPZxNiUA/exec");
                JSONObject postDataParams = new JSONObject();
                String id = "1P7-upmAlpsYFnOsBn7bkRAYWLdgApargNv8PsZzO7y8";
                postDataParams.put("id", id);

                postDataParams.put("user", BuildConfig.APPLICATION_ID.replace("stellarnear.", ""));
                postDataParams.put("user_name", Settings.Secure.getString(mC.getContentResolver(), "bluetooth_name"));
                postDataParams.put("user_model", Build.MODEL);
                postDataParams.put("user_manufacturer", Build.MANUFACTURER);
                postDataParams.put("user_android_version", Build.VERSION.RELEASE);
                postDataParams.put("user_android_sdk", Build.VERSION.SDK_INT);

                SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
                postDataParams.put("date", formater.format(new Date()));

                postDataParams.put("version_name", BuildConfig.VERSION_NAME);
                postDataParams.put("version_code", String.valueOf(BuildConfig.VERSION_CODE));

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(formatDataAsSingleString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    return stringFromStream(conn.getInputStream());
                } else {
                    return "false : " + responseCode;
                }
            } catch (Exception e) {
                log.err("PostData error", e);
                return "Exception: " + e.getMessage();
            }
        }

    }

}