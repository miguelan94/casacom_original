package com.streamnow.lindaumobile.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.streamnow.lindaumobile.R;
import com.streamnow.lindaumobile.datamodel.LDSessionUser;
import com.streamnow.lindaumobile.utils.Lindau;
import com.streamnow.lindaumobile.lib.LDConnection;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.*;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.impl.client.SystemDefaultCredentialsProvider;

public class LoginActivity extends BaseActivity implements View.OnClickListener
{
    private final int LOGIN_BUTTON_TAG = 21;
    private final int RESET_BUTTON_TAG = 22;
    private ProgressDialog progressDialog;

    private Button loginButton;
    private Button resetButton;
    private EditText userEditText;
    private EditText passwdEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) this.findViewById(R.id.loginButton);
        resetButton = (Button)findViewById(R.id.resetButton);
        userEditText = (EditText) this.findViewById(R.id.userEditText);
        passwdEditText = (EditText) this.findViewById(R.id.passwdEditText);

        loginButton.setOnClickListener(this);
        loginButton.setTag(LOGIN_BUTTON_TAG);
        resetButton.setOnClickListener(this);
        resetButton.setTag(RESET_BUTTON_TAG);

        TextWatcher textWatcher = new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s)
            {
                if( userEditText.getText().toString().isEmpty()  && passwdEditText.getText().toString().isEmpty() )
                {
                    loginButton.setText(R.string.login_button_title1);
                }
                else
                {
                    loginButton.setText(R.string.login_button_title2);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        };

        userEditText.addTextChangedListener(textWatcher);
        passwdEditText.addTextChangedListener(textWatcher);

        userEditText.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    passwdEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });

        passwdEditText.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if( (event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER) )
                {
                    loginButtonClicked(null);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v)
    {
        if( (int)v.getTag() == LOGIN_BUTTON_TAG )
        {
            this.loginButtonClicked(v);
        }
        if((int)v.getTag() == RESET_BUTTON_TAG){
            resetButtonClicked(v);
        }
    }

    public void loginButtonClicked(View sender)
    {
        progressDialog = ProgressDialog.show(this, getString(R.string.app_name), getString(R.string.please_wait), true);

        if( LDConnection.isSetCurrentUrl() )
        {
            continueLogin();
        }
        else
        {
            RequestParams requestParams = new RequestParams("app", Lindau.getInstance().appId);
            LDConnection.get("getURL", requestParams, new JsonHttpResponseHandler()
            {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                {
                    // System.out.println("JSONObject: " + response.toString());
                    try
                    {
                        String url = response.getString("url");
                        LDConnection.setCurrentUrlString(url);

                        System.out.println("Response.url = '" + url + "'");
                        continueLogin();
                    }
                    catch (JSONException e)
                    {
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
                    System.out.println("getURL KO: " + throwable.toString() + " status code = " + statusCode + " responseString = " + response);
                }
            });
        }
    }

    public void resetButtonClicked(View v){
        if(LDConnection.isSetCurrentUrl()){
            showAlertDialogReset("Enter your email or username");
        }
        else{
            RequestParams requestParams = new RequestParams("app", Lindau.getInstance().appId);
            LDConnection.get("getURL", requestParams, new JsonHttpResponseHandler()
            {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                {
                    try
                    {
                        String url = response.getString("url");
                        LDConnection.setCurrentUrlString(url);
                        showAlertDialogReset("Enter your email or username");
                    }
                    catch (JSONException e)
                    {
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
    }



    private void continueLogin()
    {
        String username, password;

        if( userEditText.getText().toString().isEmpty()  && passwdEditText.getText().toString().isEmpty() )
        {
            username = Lindau.getInstance().appDemoAccount;
            password = Lindau.getInstance().appDemoAccount;
        }
        else
        {
            username = userEditText.getText().toString();
            password = passwdEditText.getText().toString();
        }

        RequestParams requestParams = new RequestParams();
        requestParams.add("email", username);
        requestParams.add("password", password);
        requestParams.add("source", "Mobile");

        LDConnection.post("auth/login", requestParams, new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response)
            {
                // Log.d("JSON", "JSONObject OK: " + response.toString());

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

                if( sessionUser != null && sessionUser.accessToken != null )
                {
                    Lindau.getInstance().setCurrentSessionUser(sessionUser);

                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    showAlertDialog(getString(R.string.login_error));
                }
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
    }

    private void showAlertDialog(String msg){
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(msg)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .show();
    }

    private void showAlertDialogReset(String msg)
    {
        final EditText inputEmail = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(msg)
                .setView(inputEmail)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which) {


                        if(inputEmail!=null && !inputEmail.getText().toString().equals("")){
                            String email = inputEmail.getText().toString();
                            RequestParams requestParams = new RequestParams("email", email);
                            LDConnection.post("auth/reset", requestParams, new JsonHttpResponseHandler()
                            {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                                {
                                    System.out.println("Response: " + response.toString());
                                    try{
                                        if(response.get("msg").equals("Ok")){
                                            Toast.makeText(LoginActivity.this,"Password account reseted",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    catch (JSONException e){

                                    }

                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                                    System.out.println("onFailure throwable: " + throwable.toString() + " status code = " + statusCode);

                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    System.out.println(" onFailure json" + errorResponse.toString());

                                }
                            });
                        }
                    }
                })
                .setIcon(R.mipmap.ic_launcher)
                .show();
    }
}

/*
Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
setSupportActionBar(toolbar);

FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
fab.setOnClickListener(new View.OnClickListener()
{
    @Override
    public void onClick(View view)
    {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
});
*/