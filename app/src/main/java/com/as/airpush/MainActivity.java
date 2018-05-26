package com.as.airpush;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.as.airpush.SecActivity.adapter;


public class MainActivity extends AppCompatActivity {

//    private StringBuilder content = new StringBuilder();
    public static List<MyMessage> messageList = new ArrayList<>();
    public static List<String>  notiList = new ArrayList<>();

    public static Map<String, String> moduleMap = new HashMap<>();

    private EditText textUser, textPass;
    private Button btn_login;
    private static boolean isLogin ;
    public static String url = "http://84r8ha.natappfree.cc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        textUser = (EditText) findViewById(R.id.textUser);
        textPass = (EditText) findViewById(R.id.textPass);
        btn_login = (Button)findViewById(R.id.btn_login);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user = textUser.getText().toString();
                String pass = textPass.getText().toString();
                if (!user.equals("root") && !pass.equals("root")) {
                    Intent intent = new Intent(MainActivity.this, SecActivity.class);
                    startActivity(intent);

                    new MyAsync().execute(url + "/info");

                }else if(user.isEmpty() || pass.isEmpty()){
                    Toast.makeText(getApplicationContext(), "请输入账号或密码！", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "请输入正确的账号或密码！", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    static class MyAsync extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.e("First", "doInBackground: " );
            if (!isLogin) {
                try {
                    InputStream inputStream = new URL( url+"/login").openStream();
                    inputStream.close();

                    String strMenu = getJson(url + "/module");
                    Log.e("Moudule----------", "doInBackground: ======================="+strMenu );

                    JSONObject jb = new JSONObject(strMenu);
                    int len = jb.getInt("argc");
                    JSONArray jsonArray = jb.getJSONArray("args");
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONArray ja = (JSONArray) jsonArray.get(i);
                        moduleMap.put(ja.get(0).toString(), ja.get(1).toString());
                        Log.e("Moudule----------", "doInBackground: ======================="+ja.get(0).toString() + ja.get(1).toString() );
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                isLogin = true;
            }

            return getJson(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.e("aaa", "onPostExecute: " + s);

            try {
                JSONArray jsonArray = new JSONObject(s).getJSONArray("args");
                messageList.clear();
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jb = jsonArray.getJSONObject(i);
                    Log.e("jbaaa", "onPostExecute: "+ jb.getString("name"));
                    Log.e("jbaaa", "onPostExecute: "+ jb.getString("cond"));
                    Log.e("jbaaa", "onPostExecute: "+ jb.getString("note"));


                    for (int j = 0; j < 1; j++) {
                        messageList.add(new MyMessage(jb.getString("name"), jb.getString("cond"), jb.getString("note") ));
                    }
                }
//                adapter.notifyDataSetChanged();

                for(MyMessage message : messageList){
                    Log.d("as", "--------->>>>>>: " + message.getContent());
                }

            } catch (Exception e) {
                e.printStackTrace();

                for(int i = 0; i < 10; i++){
                    messageList.add(new MyMessage("Name"+i, "Time"+i, "Content"+i));
                }
            }

        }

    }

    public static String getJson(String url){
        StringBuilder stringBuilder = null;
        try {
            InputStream inputStream = new URL(url).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            stringBuilder = new StringBuilder();
            while( (line = reader.readLine()) != null){
                stringBuilder.append(line);
            }
            reader.close();
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        String temp = null;
        try {
            temp = stringBuilder.toString();
        }catch(Exception e){
        }

        return temp;
    }

}
