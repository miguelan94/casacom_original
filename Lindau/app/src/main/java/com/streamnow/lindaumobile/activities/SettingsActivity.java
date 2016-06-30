package com.streamnow.lindaumobile.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.streamnow.lindaumobile.R;
import com.streamnow.lindaumobile.datamodel.LDSessionUser;
import com.streamnow.lindaumobile.lib.LDConnection;
import com.streamnow.lindaumobile.utils.Lindau;
import com.streamnow.lindaumobile.utils.SettingsAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class SettingsActivity extends Activity {

    protected final LDSessionUser sessionUser = Lindau.getInstance().getCurrentSessionUser();
    protected ArrayList<String> items;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        RelativeLayout settings_menu = (RelativeLayout)findViewById(R.id.settings_menu_background);
        settings_menu.setBackgroundColor(sessionUser.userInfo.partner.backgroundColorSmartphone);

        if(getIntent().getBooleanExtra("main_menu",true)){
            this.items = new ArrayList<>();
            String [] list = {getResources().getString(R.string.profile),getResources().getString(R.string.contacts),getResources().getString(R.string.logout),getResources().getString(R.string.shopping)};
            items.addAll(Arrays.asList(list));
        }

        ListView listView = (ListView)findViewById(R.id.settings_menu_list_view);
        listView.setAdapter(new SettingsAdapter(this,items));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                menuItemClicked(position);
            }
        });



    }

    private void menuItemClicked(int position){
        if(position==0){ //profile clicked
            progressDialog = ProgressDialog.show(this, getString(R.string.app_name), getString(R.string.please_wait), true);
            RequestParams requestParams = new RequestParams();
            requestParams.add("uid", sessionUser.deviceSessionId);
            //requestParams.add("password", "");
            requestParams.add("source", "Mobile");

            LDConnection.post("auth/login", requestParams, new JsonHttpResponseHandler()
            {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                {
                     Log.d("JSON", "JSONObject OK: " + response.toString());

                    LDSessionUser sessionUser;

                    try
                    {
                        sessionUser = new LDSessionUser(response);
                    }
                    catch(Exception e)
                    {
                        sessionUser = null;
                        e.printStackTrace();
                    }

                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                    System.out.println("login onFailure throwable: " + throwable.toString() + " status code = " + statusCode);
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    System.out.println("login onFailure json");
                    progressDialog.dismiss();
                }
            });

        }else if(position==1){//contacts

            //Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            //startActivityForResult(intent, 5);
        }else if(position==2){//logout

            RequestParams requestParams = new RequestParams();
           // requestParams.add("access_token", sessionUser.accessToken);
            LDConnection.get("logout", requestParams, new JsonHttpResponseHandler()
            {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                {
                    //Log.d("JSON", "JSONObject OK: " + response.toString());
                    Intent i = new Intent(SettingsActivity.this,LoginActivity.class);
                    startActivity(i);

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                    System.out.println("login onFailure throwable: " + throwable.toString() + " status code = " + statusCode);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    System.out.println("login onFailure json");
                }
            });

        }else if(position==3){//shopping

        }
    }
}
