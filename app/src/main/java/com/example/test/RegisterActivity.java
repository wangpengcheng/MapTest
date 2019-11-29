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
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;

import static com.example.test.R.id.password_show1;
import static com.example.test.R.id.password_show2;

public class RegisterActivity extends AppCompatActivity {
    //预定义togglebutton
    private ToggleButton r_password_show1,r_password_show2;
    //定义文本框
    private EditText user_name_edit,password_edit,password_edit2,phone_edit,check_edit;
    //email_edit;
    //定义操作按钮
    private Button reset_button,register_button,check_button;
    //定义获取字符
    private String user_name,pass_word,pass_word2,phone_number,check_number;
    //user_email;
    //获取主线程上下文
    private Context context;
    //5表示初始状态
    private int status_flag=10;
    private  String check_temp="";
    //设置验证码出错信息
    private String temp_error;
    //设置验证码间隔
    private TimeCount check_time_wait;
    //预定义错误提示
    private String erorr_output[]={"网络连接出错","用户名已存在","输入密码不一致","该电话号码已被注册","短信发送出错","验证码错误","注册成功","注册失败"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //设置上下文
        context = this;
        //获取用户名文本框
        user_name_edit= (EditText)findViewById(R.id.user_name_edit);
        //获取用户密码文本框
        password_edit= (EditText)findViewById(R.id.password_edit1);
        //获取用户确认密码
        password_edit2= (EditText)findViewById(R.id.password_edit2);
        //获取用户手机号
        phone_edit= (EditText)findViewById(R.id.phone_edit);
        //获取用户验证码
        check_edit= (EditText)findViewById(R.id.check_edit);
        //获取用户邮箱
        //email_edit= (EditText)findViewById(R.id.email_edit);
        //获取验证码按钮
        check_button=(Button)findViewById(R.id.check_button);
        //获取注册按钮
        register_button=(Button)findViewById(R.id.register_button);
        //获取重置按钮
        reset_button=(Button)findViewById(R.id.reset_button);
        //获取togglebutton1
        r_password_show1=(ToggleButton)findViewById(password_show1) ;
        //获取togglebutton2
        r_password_show2=(ToggleButton)findViewById(password_show2) ;
        //设置获取验证码等待按钮
        check_time_wait = new TimeCount(60000, 1000);
        //设置用户名点击事件
        user_name_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    // 新建一个可以添加属性的文本对象
                    //SpannableString ss = new SpannableString("数字");
                    // 新建一个属性对象,设置文字的大小
                   // AbsoluteSizeSpan ass = new AbsoluteSizeSpan(15,true);
                    // 附加属性到文本
                   // ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    // 设置hint
                    //user_name_edit.setHint("");// 一定要进行转换,否则属性会消失
                }else {
                    user_name=user_name_edit.getText().toString();
                    if(user_name.equals("")){
                        // 新建一个可以添加属性的文本对象
                        //设置添加文字
                        String temp="用户名不能为空";
                       // SpannableString ss = new SpannableString(temp);
                        // 新建一个属性对象,设置文字的大小
                       // AbsoluteSizeSpan ass = new AbsoluteSizeSpan(18,true);
                        //设置hint颜色
                       // user_name_edit.setHintTextColor(getResources().getColor(R.color.input_error));
                        // 附加属性到文本
                        //ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        // 设置hint
                        //Toast.makeText(RegisterActivity.this,temp, Toast.LENGTH_SHORT).show();
                        user_name_edit.setHint(temp);// 一定要进行转换,否则属性会消失
                    }else{
                        //开辟子线程
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    //创建okhttp
                                    OkHttpClient client=new OkHttpClient();
                                    //创建post请求
                                    RequestBody requestBody=new FormEncodingBuilder()
                                            .add("user_name",user_name)
                                            .build();
                                    //创建请求对象
                                    Request request=new Request.Builder()
                                            .url("http://118.24.49.94/check_user_name.php")
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
                                        //提取json对象result参数
                                        String result_state=jsonObject.getString("result");
                                        if(result_state.equals("notValidate!")){
                                            status_flag=1;//该用户名已经存在
                                            Log.i("提示信息","用户名已经存在");
                                        }else {
                                            //不存在则表示正常，可注册
                                            status_flag=6;
                                        }
                                    }else{
                                        status_flag=0;//网络连接出错
                                        throw new IOException("Unexpected code " + response);
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        if(status_flag==0){
                            String temp="请检查网络连接";
                            Toast.makeText(RegisterActivity.this,temp, Toast.LENGTH_SHORT).show();
                        }else if(status_flag==1){
                            //设置添加文字
                            String temp="该用户名已存在";
                            user_name_edit.setHint(temp);
                            Toast.makeText(RegisterActivity.this,temp, Toast.LENGTH_SHORT).show();
                            user_name_edit.setText("");
                        }
                    }
                }
            }
        });
        //为密码框设置事件监听
        password_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                }else {
                    pass_word=password_edit.getText().toString();
                    if(pass_word.equals("")){
                        // 新建一个可以添加属性的文本对象
                        //设置添加文字
                        String temp="密码不能为空";
                        password_edit.setHint(temp);
                       // password_edit.setText("");

                    }
                }
            }
        });
        //设置密码框2焦点事件
        password_edit2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    //设置点击事件
                }else {
                    pass_word=password_edit.getText().toString();
                    pass_word2=password_edit2.getText().toString();
                    if(pass_word2.equals("")){
                        // 新建一个可以添加属性的文本对象
                        //设置添加文字
                        String temp="确认密码不能为空";
                        //设置sethint文字
                        password_edit2.setHint(temp);
                    }else if (pass_word.equals(pass_word2)){
                        //密码一致，状态良好
                        Log.i("提示信息","密码一致");
                        status_flag=6;
                    }else {

                        status_flag=2;//设置flag为2表示密码不一致
                        // 新建一个可以添加属性的文本对象
                        //设置添加文字
                        String temp="密码不一致";
                        password_edit2.setHint(temp);// 一定要进行转换,否则属性会消失
                        password_edit2.setText("");
                    }
                }
            }
        });
        //设置电话号码焦点事件
        phone_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    // 新建一个可以添加属性的文本对象
                    phone_edit.setHint("找回密码");// 一定要进行转换,否则属性会消失
                }else {
                    phone_number=phone_edit.getText().toString();
                    if(phone_number.equals("")){
                        // 新建一个可以添加属性的文本对象
                        //设置添加文字
                        String temp="电话号码不能为空";
                        phone_edit.setHint(temp);
                    }else{
                        //开辟子线程
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    //创建okhttp
                                    OkHttpClient client=new OkHttpClient();
                                    //创建post请求
                                    RequestBody requestBody=new FormEncodingBuilder()
                                            .add("user_phone",phone_number)
                                            .build();
                                    //创建请求对象
                                    Request request=new Request.Builder()
                                            .url("http://118.24.49.94/check_user_phone.php")
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
                                        //提取json对象result参数
                                        String result_state=jsonObject.getString("result");
                                        if(result_state.equals("notValidate!")){
                                            status_flag=3;//电话号码已注册
                                            String temp="电话号码已注册";
                                            Log.i("错误提示",temp);
                                            phone_edit.setHint(temp);
                                            phone_edit.setText("");
                                        }else{
                                            //表示无错误
                                            status_flag=6;
                                        }
                                    }else{
                                        status_flag=0;//网络连接出错
                                        throw new IOException("Unexpected code " + response);
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
            }
        });
        //设置验证码按钮
        check_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //获取电话号码
                phone_number=phone_edit.getText().toString();

                if(phone_number.equals("")){
                    String temp="请先输入电话号码";
                    Toast.makeText(RegisterActivity.this,temp, Toast.LENGTH_SHORT).show();
                    Log.i("错误提示",temp);
                }else{


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                //先检查电话号码是否被注册
                                //创建okhttp
                                OkHttpClient client=new OkHttpClient();
                                //创建post请求
                                RequestBody requestBody=new FormEncodingBuilder()
                                        .add("user_phone",phone_number)
                                        .build();
                                //创建请求对象
                                Request request=new Request.Builder()
                                        .url("http://118.24.49.94/check_user_phone.php")
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
                                    //提取json对象result参数
                                    String result_state=jsonObject.getString("result");
                                    if(result_state.equals("notValidate!")){
                                        status_flag=3;//电话号码已注册
                                        String temp="电话号码已注册";
                                        Log.i("错误提示",temp);
                                    }else{
                                        //表示无错误
                                        status_flag=6;
                                    }
                                }else{
                                    status_flag=0;//网络连接出错
                                    throw new IOException("Unexpected code " + response);
                                }
                                //如果已经被注册设置提醒
                                if( status_flag==3){
                                    String temp="电话号码已注册";
                                    Log.i("错误","手机号码已经被注册");
                                    Looper.prepare();
                                    Toast.makeText(RegisterActivity.this,temp,Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                    phone_edit.setHint(temp);
                                    phone_edit.setText("");
                                }else {
                                    //创建okhttp
                                    OkHttpClient client1 = new OkHttpClient();
                                    //创建post请求
                                    RequestBody requestBody1 = new FormEncodingBuilder()
                                            .add("phone", phone_number)
                                            .build();
                                    //创建请求对象
                                    Request request1 = new Request.Builder()
                                            .url("http://118.24.49.94/app.php")
                                            .post(requestBody1)
                                            .build();
                                    //创建回执对象
                                    Response response1 = client1.newCall(request1).execute();
                                    //判断返回成功并输出
                                    if (response.isSuccessful()) {
                                        //创建回执对象字符串
                                        String responseData = response1.body().string();
                                        //将字符串转换为json对象
                                        JSONObject jsonObject = new JSONObject(responseData);
                                        //提取json对象result参数
                                        String result_state = jsonObject.getString("result");
                                        if (result_state.equals("0")) {
                                            check_temp = jsonObject.getString("check_num");
                                            //验证码已经成功发送
                                            String temp = "验证码已发送注意接收";
                                            Looper.prepare();
                                            Toast.makeText(RegisterActivity.this,temp,Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                            Log.i("错误提示", temp);
                                            Log.i("获取的验证码", check_temp);
                                            status_flag = 6;
                                            //设置按钮不可用
                                            check_time_wait.start();
                                        } else {
                                            status_flag = 4;//发送短信验证出错
                                            temp_error = jsonObject.getString("errmsg");
                                            Log.i("错误提示", temp_error);
                                            //user_name_edit.setText("");
                                            check_time_wait.start();
                                        }
                                    } else {
                                        status_flag = 0;
                                        throw new IOException("Unexpected code " + response);
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }

            }

        });


        //设置验证码框焦点事件
        check_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){

                }else {
                    check_number=check_edit.getText().toString();
                    //pass_word2=password_edit2.getText().toString();
                    if(check_temp.equals("")||check_number.equals("")){
                        check_edit.setHint("输入验证码");
                    }else if (check_number.equals(check_temp)){
                        String temp="验证码正确";
                        Log.i("错误提示",temp);
                        status_flag=6;

                    }else {
                        status_flag=5;//验证码错误
                        Toast.makeText(RegisterActivity.this,"验证码错误", Toast.LENGTH_SHORT).show();
                        check_edit.setHint("验证码错误");
                        check_edit.setText("");
                    }
                }
            }
        });
        //注册按钮
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取用户名
                user_name=user_name_edit.getText().toString();
                //获取电话号码
                phone_number=phone_edit.getText().toString();
                //获取密码1
                pass_word=password_edit.getText().toString();
                //获取密码2
                pass_word2=password_edit2.getText().toString();
                //获取邮箱

                //获取验证码
                //检查有无错误
                if(user_name.equals("")||phone_number.equals("")||pass_word.equals("")||pass_word2.equals("")){
                    Toast.makeText(RegisterActivity.this,"填写信息错误", Toast.LENGTH_SHORT).show();
                }else if(status_flag==6){//之前状态正确发送信息
                    //开辟子线程
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //创建okhttp
                                OkHttpClient client = new OkHttpClient();
                                //创建post请求
                                RequestBody requestBody = new FormEncodingBuilder()
                                        .add("login_name", user_name)
                                        .add("password", pass_word)
                                        .add("password_confirm", pass_word2)
                                        .add("phone", phone_number)
                                        .build();
                                //创建请求对象
                                Request request = new Request.Builder()
                                        .url("http://118.24.49.94/register.php")
                                        .post(requestBody)
                                        .build();
                                //创建回执对象
                                Response response = client.newCall(request).execute();
                                //判断返回成功并输出
                                if (response.isSuccessful()) {
                                    //创建回执对象字符串
                                    String responseData = response.body().string();
                                    //将字符串转换为json对象
                                    JSONObject jsonObject = new JSONObject(responseData);
                                    //提取json对象result参数
                                    String result_state = jsonObject.getString("result");
                                    Log.i("注册提示:", result_state);
                                    if (result_state.equals("注册成功")) {
                                        status_flag=8;//注册成功
                                        //活动跳转
                                        Intent intent = new Intent(context, MainActivity.class);
                                        startActivity(intent);
                                        String temp ="注册成功请登录";
                                        Looper.prepare();
                                        Toast.makeText(RegisterActivity.this,temp,Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    } else {
                                        status_flag = 7;//注册失败
                                    }
                                } else {
                                    status_flag = 0;//网络出错
                                    throw new IOException("Unexpected code " + response);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    if(status_flag==8) {
                    }
                }else {
                    //输出错误提示
                    Toast.makeText(RegisterActivity.this,erorr_output[status_flag],Toast.LENGTH_SHORT).show();

                }
            }
        });
        //重置按钮事件
        reset_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                user_name_edit.setText("");
                password_edit.setText("");
                password_edit2.setText("");
                phone_edit.setText("");
                check_edit.setText("");
            }
        });
        //设置密码显示和隐藏按钮
        r_password_show1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    password_edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    r_password_show1.setBackgroundResource(R.drawable.password_show);
                }else {
                    password_edit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    r_password_show1.setBackgroundResource(R.drawable.password_hide);
                }
            }
        });
        r_password_show2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    password_edit2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    r_password_show2.setBackgroundResource(R.drawable.password_show);
                }else {
                    password_edit2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    r_password_show2.setBackgroundResource(R.drawable.password_hide);
                }
            }
        });

    }
    //设置倒计时按钮
    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

                check_button.setClickable(false);
            //check_button.setTextSize(11);
            check_button.setText("已发送("+millisUntilFinished / 1000 +")");
            //check_button.setBackgroundColor(Color.parseColor("#B6B6D8"));
        }

        @Override
        public void onFinish() {
            check_button.setText("获取验证码");
            check_button.setClickable(true);
           // check_button.setBackgroundColor(Color.parseColor("#4EB84A"));

        }
    }
}
