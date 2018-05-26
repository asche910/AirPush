package com.as.airpush;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;
import static com.as.airpush.MainActivity.messageList;
import static com.as.airpush.MainActivity.moduleMap;
import static com.as.airpush.MainActivity.url;
import static com.as.airpush.SecActivity.adapter;

public class FragmentAdd extends DialogFragment {

    EditText editName, editDesc, editWhen, editNoti, editArgs;
    Button btnAdd;
    public static int curFlag;
    private String curModule;
    private Context mContext;
    private int resCode;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(mContext == null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mContext = getContext();
            }
        }

        View view = inflater.inflate(R.layout.fragment_add, container);
        editName = (EditText) view.findViewById(R.id.add_name);
        editDesc = (EditText) view.findViewById(R.id.add_des);
        editWhen = (EditText) view.findViewById(R.id.add_when);
        editNoti = (EditText) view.findViewById(R.id.add_not);
        editArgs = (EditText) view.findViewById(R.id.add_args);

        int j = 0, m = 0;
        for(String val: moduleMap.values()){
            if(j == curFlag){
                editArgs.setHint("参数（形如：" + val + ")");
                break;
            }
            j++;
        }

        for(String str: moduleMap.keySet()){
            if(m == j){
                curModule = str;
            }
            m++;
        }

        btnAdd = (Button)view.findViewById(R.id.add_submit);

        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String textName = editName.getText().toString();
                final String textDesc = editDesc.getText().toString();
                final String textWhen = editWhen.getText().toString();
                final String textNoti = editNoti.getText().toString();
                final String textArgs = editArgs.getText().toString();

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("Modu", curModule);
                            jsonObject.put("Name", textName);
                            jsonObject.put("Desc", textDesc);
                            jsonObject.put("When", textWhen);
                            jsonObject.put("Noti",textNoti);
                            jsonObject.put("Args", textArgs);

                            String content = String.valueOf(jsonObject);

                            HttpURLConnection con = (HttpURLConnection) new URL(url + "/product").openConnection();
                            con.setConnectTimeout(5000);
                            con.setDoOutput(true);
                            con.setRequestMethod("POST");
                            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                            OutputStream out = con.getOutputStream();
                            out.write(content.getBytes());
                            out.close();
                            resCode = con.getResponseCode();


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                if(resCode == 200){
                    Log.e(TAG, "run: =======================Succeed!" );
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Toast.makeText(mContext, "发送成功！", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Log.e(TAG, "run: =======================Failed!" );
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Toast.makeText(getContext(), "发送成功！", Toast.LENGTH_SHORT).show();
                    }
                }

                getDialog().cancel();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

}
