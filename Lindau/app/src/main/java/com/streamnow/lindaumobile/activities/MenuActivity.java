package com.streamnow.lindaumobile.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.streamnow.lindaumobile.datamodel.LDService;
import com.streamnow.lindaumobile.interfaces.IMenuPrintable;
import com.streamnow.lindaumobile.lib.LDConnection;
import com.streamnow.lindaumobile.utils.Lindau;
import com.streamnow.lindaumobile.utils.MenuAdapter;
import com.streamnow.lindaumobile.R;
import com.streamnow.lindaumobile.datamodel.LDSessionUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MenuActivity extends BaseActivity
{
    protected final LDSessionUser sessionUser = Lindau.getInstance().getCurrentSessionUser();
    String categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        categoryId = this.getIntent().getStringExtra("category_id");

        /*
        if(categoryId.equals("5")){
                //call API vodka
            RequestParams requestParams = new RequestParams();
            requestParams.add("appId","5033d287e70e42f0a5a9f44001cb2d");
            requestParams.add("userId",getIntent().getStringExtra("user_vodka"));
            requestParams.add("password",getIntent().getStringExtra("pass_vodka"));
            LDConnection.post("https://project-test.streamnow.ch/external/client/core/Login.do?appId=",requestParams,new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                {
                    ///obtenemos token y url

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    System.out.println("onFailure json");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    System.out.println("onFailure array");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                }
            });
        }
        */


        ArrayList<? extends IMenuPrintable> adapterArray;

        if( categoryId == null )
        {
            adapterArray = sessionUser.getAvailableServicesForSession();
        }
        else
        {
            adapterArray = sessionUser.getAvailableServicesForCategoryId(categoryId);
        }

        RelativeLayout mainBackground = (RelativeLayout) findViewById(R.id.main_menu_background);
        //mainBackground.setBackgroundColor(sessionUser.userInfo.partner.colorTop);
        mainBackground.setBackgroundColor(sessionUser.userInfo.partner.backgroundColorSmartphone);
        ImageView imageView = (ImageView)findViewById(R.id.settings_ico); //icono settings
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this,SettingsActivity.class);
                i.putExtra("main_menu",true);
                startActivity(i);
            }
        });

        final ListView listView = (ListView) findViewById(R.id.main_menu_list_view);
        listView.setAdapter(new MenuAdapter(this, adapterArray));



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                menuItemClicked(position);
            }
        });
    }

    private void menuItemClicked(int position)
    {
        ArrayList<? extends IMenuPrintable> services;

        if( getIntent().getBooleanExtra("sub_menu", false) )
        {
            services = sessionUser.getAvailableServicesForCategoryId(categoryId);
            LDService service = (LDService) services.get(position);

            if (service.type.equals("2"))
            {


                if(service.id.equals("29")){ //Tv

                }
                else if(service.id.equals("57")){ //music

                }
                else if(service.id.equals("59")){ //tv on demand

                }
                else if(service.id.equals("60")){ //my recordings

                }
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("api_url", service.apiUrl);
                intent.putExtra("service_id", service.id);
                startActivity(intent);
            }
            else if (service.type.equals("3"))
            {
                // TODO Open youtube video here
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("api_url", "https://m.youtube.com/watch?v=" + service.apiUrl);
                startActivity(intent);
            }
        }
        else
        {
            services = sessionUser.getAvailableServicesForCategoryId(sessionUser.categories.get(position).id);

            // System.out.println("clicked on item with title " + sessionUser.categories.get(position).name + " it has " + services.size() + " services available");

            if (services.size() == 1)
            {

                LDService service = (LDService) services.get(0);
                    //check service type
                if (service.type.equals("1"))
                {
                    if( service.id.equals("53") )
                    {
                        Intent intent = new Intent(this, ContactActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(this, DocmanMenuActivity.class);
                        intent.putExtra("root_menu", true);
                        intent.putExtras( new Bundle());
                        startActivity(intent);
                    }
                }
                else if (service.type.equals("2"))
                {
                    Intent intent = new Intent(this, WebViewActivity.class);
                    intent.putExtra("api_url", service.apiUrl);
                    startActivity(intent);
                }
                else if (service.type.equals("3"))
                {
                    // TODO Open youtube video here
                    Intent intent = new Intent(this, WebViewActivity.class);
                    intent.putExtra("api_url", "https://m.youtube.com/watch?v=" + service.apiUrl);
                    startActivity(intent);
                }
            }
            else if (services.size() > 1)
            {
                final Intent intent = new Intent(this, MenuActivity.class);
                if(sessionUser.categories.get(position).id.equals("5")){//entertainment

                    //System.out.println("Entertainment clicked, api_url--->" + service.apiUrl);
                    final RequestParams requestParams = new RequestParams("access_token",sessionUser.accessToken);
                    LDConnection.post("myentertaiment/getCredentials",requestParams,new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                        {
                            //Obtenemos username y password
                            Log.d("JSON","JSON OK: " + response.toString());
                            try{
                                intent.putExtra("user_vodka",response.getString(""));
                                intent.putExtra("pass_vodka",response.getString(""));
                            }
                            catch (JSONException e){
                               e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            System.out.println("onFailure json");
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            System.out.println("onFailure array");
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                        }
                    });
                }
                intent.putExtra("category_id", sessionUser.categories.get(position).id);
                intent.putExtra("sub_menu", true);
                startActivity(intent);
            }
        }
    }
}
