package com.lotustechnologicalsolution.chiri.HelpingClasses;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lotustechnologicalsolution.chiri.Interfaces.Callback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ApiRequest {
    public static RequestQueue requestQueue;

    public static void CallApi(final Context context, String url, JSONObject jsonObject,
                               final Callback callback) {

        Log.d(Variables.tag, url);
        Log.d(Variables.tag, jsonObject.toString());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                response -> {
                    callback.Responce(response.toString());
                    Log.d(Variables.tag, response.toString());
                }, error -> {
            //  callback.Responce(error.toString());
            if (error instanceof NetworkError) {
            } else if (error instanceof ServerError) {
            } else if (error instanceof AuthFailureError) {
            } else if (error instanceof ParseError) {
            } else if (error instanceof TimeoutError) {
                Toast.makeText(context,
                        "Oops. Timeout error!",
                        Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Api-Key", ApiUrl.ApiKey);
                headers.put("User-Id", "" + new Preferences(context).getKeyUserId());
                headers.put("Auth-Token", "" + new Preferences(context).getKeyUserAuthToken());
                Log.d(Variables.tag, headers.toString());
                return headers;

            }
        };

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjReq);

    }


    public static void CallApi(final Context context, String url, JSONObject jsonObject,
                               final Callback callback, int type) {

        Log.d(Variables.tag, url);
        Log.d(Variables.tag, jsonObject.toString());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(type,
                url, jsonObject,
                response -> {
                    callback.Responce(response.toString());
                    Log.d(Variables.tag, response.toString());
                }, error -> callback.Responce(error.toString()));

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjReq);

    }
}
