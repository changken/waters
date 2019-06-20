package com.example.myapplication;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;




public class MainActivity extends AppCompatActivity {
    private MyAPIService MyAPI;
    private EditText account, password;
    private String mem_name;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ProgressDialogUtil.dismiss();
        account = (EditText) findViewById(R.id.editText6);
        password = (EditText) findViewById(R.id.editText7);


        Button login = (Button) findViewById(R.id.button1);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialogUtil.showProgressDialog(MainActivity.this);
                String mem_account = account.getText().toString().trim();//trim去除空白 getText獲取id裡的值
                String mem_password = password.getText().toString().trim();
                if(mem_account.equals("") || mem_password.equals(""))
                {
                    Toast.makeText(MainActivity.this,"請輸入帳號或密碼", Toast.LENGTH_SHORT).show();
                    ProgressDialogUtil.dismiss();
                }
                else {
                    getMember(mem_account , mem_password);
                }

                SharedPreferences bcde=getSharedPreferences("save",activity_register.MODE_PRIVATE);
                SharedPreferences abcd =getSharedPreferences("save",MODE_PRIVATE);

                SharedPreferences.Editor editor = abcd.edit();
                int x =bcde.getInt("restnum",1);
                editor.putInt("restnum",15);
                editor.commit();




            }
        });


        Button register = (Button) findViewById(R.id.button2);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialogUtil.showProgressDialog(MainActivity.this);
                Intent intent = new Intent(MainActivity.this, activity_register.class);
                startActivity(intent);
                ProgressDialogUtil.dismiss();
            }
        });
    }

    public void getMember(final String account, final String password) {
        MyAPI = RetrofitManager.getInstance().getAPI();
        Call<Member> call = MyAPI.getMem();
        call.enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {//如果請求連接資料庫並成功抓到值
                int len = response.body().getRecords().length;
                int j = 0;
                boolean Successlogin = false;
                while (j < len) {
                    if (response.body().getfields(j).getMem_account().equals(account) && response.body().getfields(j).getMem_password().equals(password)) {
                        Successlogin = true;SharedPreferences sharedPreferences = getSharedPreferences("User" , MODE_PRIVATE);
                        sharedPreferences.edit().putString("mem_acoount",response.body().getMem_account()).apply();
                        sharedPreferences.edit().putString("mem_id",response.body().getId(j));
                        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if (isFirstRun)
                        {
                            Intent intent = new Intent(MainActivity.this, home_page.class);//成功後切換至喜好頁面
                            startActivity(intent);
                            ProgressDialogUtil.dismiss();
                            editor.putBoolean("isFirstRun", false);//做個標記，改成false。
                            //提交到本地，存起来
                            editor.commit();
                            break;
                        }
                        else
                        {
                            Intent intent = new Intent(MainActivity.this, home_page.class);//成功後切換至首頁
                            startActivity(intent);
                            ProgressDialogUtil.dismiss();
                        }

                        mem_name = response.body().getfields(j).getMem_name();
                        SharedPreferences sharedPreferences1 = getSharedPreferences("User" , MODE_PRIVATE);
                        sharedPreferences1.edit().putString("mem_name",mem_name).apply();
                    }
                    j++;
                }
                if (Successlogin == false) {
                    Toast.makeText(MainActivity.this, "帳號或密碼輸入錯誤!", Toast.LENGTH_SHORT).show();
                    ProgressDialogUtil.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {
            }
        });

    }

    public void setCount(final int count){
        MyAPI = RetrofitManager.getInstance().getAPI();
        Call<Reqregist> call = MyAPI.changeInfor(new Reqregist(new fields(count)));
        call.enqueue(new Callback<Reqregist>(){

            @Override
            public void onResponse(Call<Reqregist> call, Response<Reqregist> response) {
                Toast.makeText(MainActivity.this,"ok",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<Reqregist> call, Throwable t) {
                Toast.makeText(MainActivity.this,"no",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
