package com.example.test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private String getName,user_name1,user_pass1;
    private EditText editText_user,editText_passwd;
    private Button button_login;
    private TextView button_register,button_change_pwd;
    private SharedPreferences sharedPreferences;
    //设置登录状态记录变量
    private int statu_temp;//0表示初始状态
    private String user,passwd,result;
    //获取主线程上下文
    private Context context1;
    //设置状态变量
    private String result_state;
    //设置隐藏密码按钮
    private ToggleButton password_show;
    //设置错误提示
    private String erorr_out[]={"未知请联系开发人员","网络错误","用户名错误","密码错误","登录成功"};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置上下文
        context1 = this;
        //直接转换到地图
       //Intent intent=new Intent(MainActivity.this,SecondActivity.class);
      //  startActivity(intent);
        //获取用户名称框
        editText_user = (EditText)findViewById(R.id.username_edit);
        //获取用户密码框
        editText_passwd = (EditText)findViewById(R.id.password_edit);
        //获取用户登录按钮
        button_login = (Button)findViewById(R.id.login_button);
        //获取用户注册文本框
        button_register=(TextView)findViewById(R.id.register_txt);
        //获取忘记密码文本框
        button_change_pwd=(TextView)findViewById(R.id.change_pwd);
        //获取toggle按钮
         password_show = (ToggleButton)findViewById(R.id.password_show);
        //显示已保存的用户名和密码
        sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);
        getName = sharedPreferences.getString("username", null);
        button_login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //获取用户名称
                user = editText_user.getText().toString();
                //获取用户密码
                passwd = editText_passwd.getText().toString();
                String responseData;
                if (user.equals("") || passwd.equals("")) {
                    Toast.makeText(MainActivity.this,"用户名或密码不能为空，请重新输入!",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    //测试是否接收到数据
                    //Toast.makeText(MainActivity.this,passwd,Toast.LENGTH_SHORT).show();
                    //利用okhttp传输json数据
                    send_requst_mesage(user,passwd);
                }

            }
            private void send_requst_mesage(final String user_name, final String user_pwd)
            {
                //开辟子线程
                final JSONObject[] temp = new JSONObject[1];
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                            try{
                                //创建okhttp
                                OkHttpClient client=new OkHttpClient();
                                //创建post请求
                                RequestBody requestBody=new FormEncodingBuilder()
                                        .add("user_name",user_name)
                                        .add("user_pwd",user_pwd)
                                        .build();
                                //创建请求对象
                                Request request=new Request.Builder()
                                        .url("http://118.24.49.94/login.php")
                                        .post(requestBody)
                                        .build();
                                //创建回执对象
                                Response response=client.newCall(request).execute();
                                //判断返回成功并输出
                                if(response.isSuccessful()){
                                    //创建回执对象字符串
                                    String responseData=response.body().string();
                                    //将字符串转换为json对象
                                    JSONObject jsonObject = new JSONObject(responseData);
                                    temp[0] =jsonObject;
                                    //提取json对象result参数
                                     result_state=jsonObject.getString("result");
                                   if(result_state.equals("3")){
                                        Looper.prepare();
                                        Toast.makeText(MainActivity.this,"请输入正确用户名", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                       //表示用户名错误
                                       statu_temp=2;
                                    }else if(result_state.equals("0")){
                                       //表示密码错误
                                       statu_temp=3;
                                        Looper.prepare();
                                        Toast.makeText(MainActivity.this,"密码错误，请重新输入", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }else {
                                       //4表示登录成功
                                       statu_temp=4;
                                      // statu_temp=2;
                                       //显示登录成功
                                        //控制台输出用户名和密码
                                        //提取用户名
                                       //转入新活动
                                       Intent intent = new Intent(context1, SecondActivity.class);
                                       startActivity(intent);
                                       //提示登录成功
                                       Looper.prepare();
                                       Toast.makeText(MainActivity.this,erorr_out[statu_temp],Toast.LENGTH_SHORT).show();
                                       Looper.loop();
                                   }
                                }else{
                                    //1表示网络错误
                                    statu_temp=1;
                                    throw new IOException("Unexpected code " + response);
                                }
                            }catch (Exception e){
                                    e.printStackTrace();
                            }
                        }
                    }).start();
            }
        });
        //设置注册按钮事件
        button_register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //点击按钮转到注册事件
                Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);

            }
        });
        //设置忘记密码事件
        button_change_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击转到密码找回事件
                Intent intent=new Intent(MainActivity.this,FindPassWordActivity.class);
                startActivity(intent);
            }
        });
        //设置密码显示和隐藏按钮
        password_show.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editText_passwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    password_show.setBackgroundResource(R.drawable.password_show);
                }else {
                    editText_passwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    password_show.setBackgroundResource(R.drawable.password_hide);
                }
            }
        });

    }
}
