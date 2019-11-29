package com.example.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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

import static com.example.test.R.id.c_change_button;
import static com.example.test.R.id.c_check_button;
import static com.example.test.R.id.c_check_edit;
import static com.example.test.R.id.c_password_edit;
import static com.example.test.R.id.c_password_edit2;
import static com.example.test.R.id.c_reset_button;
import static com.example.test.R.id.c_user_phone_edit;
import static com.example.test.R.id.change_password_show1;
import static com.example.test.R.id.change_password_show2;

public class FindPassWordActivity extends AppCompatActivity {
    //定义显示图片
    private ToggleButton c_password_show1,c_password_show2;
    //定义文本框
    private TextView change_phone_edit,change_password_edit,change_password_edit2,change_check_edit;
    //定义按钮
    private Button change_check_button,change_button,change_reset_button;
    //定义文本框内容
    private String change_userphone,change_password1,change_password2,change_check;
    //定义临时验证码变量
    private String change_check_temp="";
    //获取主线程上下文
    private Context c_context;
    //设置错误状态码
    private int error_status;
    //设置按钮等待
    private TimeCount check_time_wait2;
    //是否注册成功

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pass_word);
        c_context = this;
        //获取手机号文本框
        change_phone_edit= (EditText)findViewById(c_user_phone_edit);
        //获取用户密码文本框
        change_password_edit= (EditText)findViewById(c_password_edit);
        //获取用户确认密码
        change_password_edit2= (EditText)findViewById(c_password_edit2);
        //获取用户验证码
        change_check_edit= (EditText)findViewById(c_check_edit);
        //获取用户邮箱
        //email_edit= (EditText)findViewById(R.id.email_edit);
        //获取验证码按钮
        change_check_button=(Button)findViewById(c_check_button);
        //获取更改按钮
        change_button=(Button)findViewById(c_change_button);
        //获取重置按钮
        change_reset_button=(Button)findViewById(c_reset_button);
        //设置获取验证码等待按钮
        check_time_wait2 = new TimeCount(60000, 1000);
        //获取显示密码togglebutton1
        c_password_show1=(ToggleButton)findViewById(change_password_show1) ;
        //获取显示密码togglebutton2
        c_password_show2=(ToggleButton)findViewById(change_password_show2) ;
        //设置手机号码框
        change_phone_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    String temp="输入绑定手机号";
                    change_phone_edit.setHint(temp);
                }else {
                    change_userphone=change_phone_edit.getText().toString();
                    if(change_userphone.equals("")){
                        String temp="手机号码不能为空";
                        change_phone_edit.setHint(temp);
                    }
                }
            }
        });
        //设置密码框事件
        change_password_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                }else {
                    change_password1=change_password_edit.getText().toString();
                    if(change_password1.equals("")){
                        // 新建一个可以添加属性的文本对象
                        //设置添加文字
                        String temp="密码不能为空";
                        change_password_edit.setHint(temp);
                        // password_edit.setText("");
                        error_status=1;//密码为空
                    }
                }
            }
        });
        //设置确认密码框焦点事件
        change_password_edit2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    //设置点击事件
                }else {
                    change_password1=change_password_edit.getText().toString();
                    change_password2=change_password_edit2.getText().toString();
                    if(change_password2.equals("")){
                        // 新建一个可以添加属性的文本对象
                        //设置添加文字
                        String temp="确认密码不能为空";
                        //设置sethint文字
                        change_password_edit2.setHint(temp);
                    }else if (change_password1.equals(change_password2)){
                        //密码一致，状态良好
                        Log.i("提示信息","密码一致");
                    }else {

                        error_status=2;//2表示密码不一致
                        // 新建一个可以添加属性的文本对象
                        //设置添加文字
                        String temp="密码不一致";
                        change_password_edit2.setHint(temp);// 一定要进行转换,否则属性会消失
                        change_password_edit2.setText("");
                    }
                }
            }
        });
        //设置获取验证码按钮点击事件
        change_check_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取手机号码
                change_userphone=change_phone_edit.getText().toString();
                //开启线程查询手机号码是否存在并发送验证码
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //创建http check_client
                            OkHttpClient check_client=new OkHttpClient();
                            //创建post请求
                            RequestBody check_request_body= new FormEncodingBuilder().add("user_phone",change_userphone).build();
                            //创建请求对象
                            Request check_request=new Request.Builder()
                                    .url("http://118.24.49.94/send_verification_code.php")
                                    .post(check_request_body)
                                    .build();
                            //创建回执对象
                            Response check_response=check_client.newCall(check_request).execute();
                            //判断返回成功并输出
                            if(check_response.isSuccessful()){
                                //创建回执对象字符串
                                String check_resphone_data=check_response.body().string();
                                //将字符串转换为json对象
                                JSONObject check_resphone_json = new JSONObject(check_resphone_data);
                                //提取json对象result参数
                                //result=not_exist 电话号码不存在 result=success；存在且验证码发送成功并且存在check_num验证码;
                                String check_result=check_resphone_json.getString("result");
                                if(check_result.equals("not_exist")){
                                    error_status=3;//电话号码不存在，请注册
                                    String temp="该电话号码不存在";
                                    Log.i("错误提示",temp);
                                    Looper.prepare();
                                    Toast.makeText(FindPassWordActivity.this,temp, Toast.LENGTH_SHORT).show();
                                    Looper.loop();

                                }else if(check_result.equals("success")){
                                    //如果成功获取验证码，将验证码取出
                                    change_check_temp=check_resphone_json.getString("check_num");
                                    //更改验证码按钮样式
                                    check_time_wait2.start();
                                }
                            }else{
                                error_status=4;//网络连接出错
                                String temp="网络连接出错";
                                Looper.prepare();
                                Toast.makeText(FindPassWordActivity.this,temp, Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                throw new IOException("Unexpected code " + check_response);
                            }
                        }catch (Exception e){
                            e.printStackTrace();//输出线程错误事件
                        }
                    }
                }).start();

            }
        });
        //设置更改按钮事件,点击按钮进行更改
        change_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //收集各个文本框内容
                //获取用户手机
                change_userphone = change_phone_edit.getText().toString();
                //获取用户密码
                change_password1 = change_password_edit.getText().toString();
                //获取用户手机确认密码
                change_password2 = change_password_edit2.getText().toString();
                //获取用户输入的验证码
                change_check = change_check_edit.getText().toString();
                //判断是否有信息填写不完全
                if (change_userphone.equals("") || change_password1.equals("") || change_password2.equals("") || change_check.equals("")) {
                    Toast.makeText(FindPassWordActivity.this, "请将信息填写完整", Toast.LENGTH_SHORT).show();
                }else if(!change_password1.equals(change_password2)){
                    Toast.makeText(FindPassWordActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                    change_password_edit2.setText("");
                }else if(!change_check.equals(change_check_temp)){
                    Toast.makeText(FindPassWordActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                    change_check_edit.setText("");
                }else {//开始注册
                    //开启线程查询手机号码是否存在并发送验证码
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //创建http check_client
                                OkHttpClient change_password_client=new OkHttpClient();
                                //创建post请求
                                RequestBody change_password_request_body= new FormEncodingBuilder()
                                        .add("user_phone",change_userphone)
                                        .add("password",change_password1)
                                        .add("password_confirm",change_password2)
                                        .build();
                                //创建请求对象
                                Request change_password_request=new Request.Builder()
                                        .url("http://118.24.49.94/change_password.php")
                                        .post(change_password_request_body)
                                        .build();
                                //创建回执对象
                                Response change_password_response=change_password_client.newCall(change_password_request).execute();
                                //判断返回成功并输出
                                if(change_password_response.isSuccessful()){
                                    //创建回执对象字符串
                                    String change_response_data=change_password_response.body().string();
                                    //将字符串转换为json对象
                                    JSONObject change_respone_json = new JSONObject(change_response_data);
                                    //提取json对象result参数
                                    //result=not_exist 电话号码不存在 result=success；存在且验证码发送成功并且存在check_num验证码;
                                    String check_result=change_respone_json.getString("result");
                                    if(check_result.equals("no_user")){
                                        error_status=3;//电话号码不存在，请注册
                                        String temp="该电话号码不存在";
                                        Log.i("错误提示",temp);
                                        Looper.prepare();
                                        Toast.makeText(FindPassWordActivity.this,temp, Toast.LENGTH_SHORT).show();
                                        Looper.loop();

                                    }else if(check_result.equals("change_successs")){
                                        String temp="更改成功!请登录";
                                        Log.i("错误提示",temp);
                                       error_status=5;//登录成功
                                        //线程跳转
                                        Intent intent=new Intent(c_context,MainActivity.class);
                                        startActivity(intent);
                                        Looper.prepare();
                                        Toast.makeText(FindPassWordActivity.this,temp, Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                }else{
                                    error_status=4;//网络连接出错
                                    String temp="网络连接出错";
                                    Looper.prepare();
                                    Toast.makeText(FindPassWordActivity.this,temp, Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                    throw new IOException("Unexpected code " + change_password_response);
                                }
                            }catch (Exception e){
                                e.printStackTrace();//输出线程错误事件
                            }

                        }

                    }).start();

                }
            }
        });


        //设置重置按钮事件
        change_reset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_phone_edit.setText("");
                change_password_edit.setText("");
                change_password_edit2.setText("");
                change_check_edit.setText("");
            }
        });
        //设置密码显隐性
        c_password_show1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    change_password_edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    c_password_show1.setBackgroundResource(R.drawable.password_show);
                } else {
                    change_password_edit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    c_password_show1.setBackgroundResource(R.drawable.password_hide);
                }
            }
        });
        //设置确认密码显隐性
        c_password_show2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    change_password_edit2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    c_password_show2.setBackgroundResource(R.drawable.password_show);
                } else {
                    change_password_edit2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    c_password_show2.setBackgroundResource(R.drawable.password_hide);
                }
            }
        });

    }
    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            change_check_button.setClickable(false);
            //check_button.setTextSize(11);
            change_check_button.setText("已发送("+millisUntilFinished / 1000 +")");
            //check_button.setBackgroundColor(Color.parseColor("#B6B6D8"));
        }

        @Override
        public void onFinish() {
            change_check_button.setText("获取验证码");
            change_check_button.setClickable(true);
            // check_button.setBackgroundColor(Color.parseColor("#4EB84A"));

        }


    }

}
