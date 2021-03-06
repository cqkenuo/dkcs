package com.rt.zgloan.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rt.zgloan.R;
import com.rt.zgloan.base.BaseActivity;
import com.rt.zgloan.bean.BaseResponse;
import com.rt.zgloan.globe.Constant;
import com.rt.zgloan.http.HttpManager;
import com.rt.zgloan.http.HttpSubscriber;
import com.rt.zgloan.util.AbImageUtil;
import com.rt.zgloan.util.AbStringUtil;
import com.rt.zgloan.util.AppUtil;
import com.rt.zgloan.util.DialogUtil;
import com.rt.zgloan.util.StringUtil;
import com.rt.zgloan.util.ToastUtil;
import com.rt.zgloan.weight.EditTextWithDel;
import com.rt.zgloan.weight.LoadingFragment;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;

/**
 * 忘记密码
 * Created by hjy on 2017/8/25.
 */

public class ForgotPasswordActivity extends BaseActivity {

    @BindView(R.id.tv_get_code)
    TextView tv_get_code;
    @BindView(R.id.btn_sure)
    Button btn_sure;
    @BindView(R.id.edit_input_phone)
    EditTextWithDel editInputPhone;
    @BindView(R.id.edit_verification_code)
    EditText editVerificationCode;
    @BindView(R.id.edit_new_login_password)
    EditTextWithDel editNewLoginPassword;
    @BindView(R.id.layout_height_top)
    RelativeLayout mLayoutHeightTop;

    private String str_InputPhone, str_VerificationCode, str_NewLoginPassword;
    //    private MyCountDownTimer myCountDownTimer;
    private EditTextWithDel edit_graphic_verification_code;
    private String str_graphic_verification_code;

    private static final int INTERVAL = 1;
    private int curTime;

    @Override
    public Observable<BaseResponse> initObservable() {
        return null;
    }

    @Override
    public boolean isFirstLoad() {
        return false;
    }

    @OnClick({R.id.tv_get_code, R.id.btn_sure})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_get_code:
                getEditContent();
                if (StringUtil.isBlank(str_InputPhone)) {
                    ToastUtil.showToast("请输入手机号码");
                    return;
                }
                if (!AbStringUtil.isMobileNo(str_InputPhone)) {
                    ToastUtil.showToast("请输入正确格式的手机号码");
                    return;
                }
//                showGraphicalCodeDialog();
                setSendCode(true);
                getMobileCode(str_InputPhone);

                break;

            case R.id.btn_sure:
                getEditContent();
                if (StringUtil.isBlank(str_InputPhone)) {
                    ToastUtil.showToast("请输入手机号码");
                    return;
                }
                if (!AbStringUtil.isMobileNo(str_InputPhone)) {
                    ToastUtil.showToast("请输入正确格式的手机号码");
                    return;
                }
                if (StringUtil.isBlank(str_VerificationCode)) {
                    ToastUtil.showToast("请输入手机验证码");
                    return;
                }

                if (StringUtil.isBlank(str_NewLoginPassword)) {
                    ToastUtil.showToast("请输入新的密码");
                    return;
                }
                if (str_NewLoginPassword.length() < 6 || str_NewLoginPassword.length() > 16) {
                    ToastUtil.showToast("密码长度应在6-16位之间");
                    return;
                }
                forgotPasswordHttp(str_InputPhone, str_NewLoginPassword, str_VerificationCode);


                break;
        }
    }

    private void getMobileCode(String phoneNum) {
        mapParams.put("mobile", phoneNum);
        mPresenter.toSubscribe(HttpManager.getApi().getMobileCode(mapParams), new HttpSubscriber() {
            @Override
            protected void _onStart() {
                LoadingFragment.getInstance().show(((FragmentActivity) mContext).getSupportFragmentManager(), "");
            }

            @Override
            protected void _onNext(Object o) {
                ToastUtil.showToast("发送验证码成功");
            }

            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onCompleted() {
                LoadingFragment.getInstance().dismiss();
            }
        });

    }

    /**
     * 忘记密码
     */
    private void forgotPasswordHttp(String phoneNum, String psw, String verificationCode) {

        mapParams.put("mobile", phoneNum);
        mapParams.put("password", psw);
        mapParams.put("mobileCode", verificationCode);
        mPresenter.toSubscribe(
                HttpManager.getApi().resetPwd(mapParams), new HttpSubscriber() {
                    @Override
                    protected void _onStart() {
                        LoadingFragment.getInstance().show(((FragmentActivity) mContext).getSupportFragmentManager(), "正在修改...");
                    }

                    @Override
                    protected void _onNext(Object o) {
                        mActivity.startActivity(FrogotPasswordSuccessActivity.class);
                    }


                    @Override
                    protected void _onError(String message) {
//                        Log.e("tag", "_onError" + message);
                        ToastUtil.showToast(message);


                    }

                    @Override
                    protected void _onCompleted() {

                        LoadingFragment.getInstance().dismiss();
                    }
                }
        );

    }


    /**
     * 获取手机验证码
     */
    private void getCodeHttp() {
        mapParams.put("mobile", str_InputPhone);
        mapParams.put("code", str_graphic_verification_code);
        mPresenter.toSubscribe(
                HttpManager.getApi().getCodeForgotPassword(mapParams), new HttpSubscriber() {
                    @Override
                    protected void _onStart() {
                        LoadingFragment.getInstance().show(((FragmentActivity) mContext).getSupportFragmentManager(), "");
                    }

                    @Override
                    protected void _onNext(Object o) {
                        ToastUtil.showToast("发送验证码成功");
//                        myCountDownTimer.start();
                        DialogUtil.dismiss();

                    }


                    @Override
                    protected void _onError(String message) {
                        ToastUtil.showToast(message);


                    }

                    @Override
                    protected void _onCompleted() {
                        LoadingFragment.getInstance().dismiss();

                    }
                }
        );
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_forgot_password;
    }

    @Override
    public void initView() {
        if (AppUtil.isVersionKitkat()) {
            mLayoutHeightTop.setVisibility(View.VISIBLE);
        } else {
            mLayoutHeightTop.setVisibility(View.GONE);
        }
        mTitle.setTitle(true, "忘记密码");
        mTitle.setRightTitle("注册", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(RegisterActivity.class);
            }
        });
//        myCountDownTimer = new MyCountDownTimer(60000, 1000);


    }

    private void showGraphicalCodeDialog() {
        View view = View.inflate(this, R.layout.dialog_graphical_code, null);
        ImageView clear_dialog_imgs = (ImageView) view.findViewById(R.id.clear_dialog_imgs);
        edit_graphic_verification_code = (EditTextWithDel) view.findViewById(R.id.edit_graphic_verification_code);
        final ImageView graphic_verification_code_img = (ImageView) view.findViewById(R.id.graphic_verification_code_img);
        AbImageUtil.glideImage(Constant.Url + str_InputPhone, graphic_verification_code_img, R.mipmap.error, R.mipmap.preload);
        Button btn_sure = (Button) view.findViewById(R.id.btn_sure);

        graphic_verification_code_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AbImageUtil.glideImage(Constant.Url + str_InputPhone, graphic_verification_code_img, R.mipmap.error, R.mipmap.preload);
            }
        });

        clear_dialog_imgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.dismiss();
            }
        });
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_graphic_verification_code = edit_graphic_verification_code.getText().toString().trim();
                if (StringUtil.isBlank(str_graphic_verification_code)) {
                    ToastUtil.showToast("请输入图形验证码");
                    return;
                }
                getCodeHttp();
            }
        });

        DialogUtil.showAlertDialog(view);
    }

    private void getEditContent() {
        str_InputPhone = editInputPhone.getText().toString().trim();
        str_VerificationCode = editVerificationCode.getText().toString().trim();
        str_NewLoginPassword = editNewLoginPassword.getText().toString().trim();
    }

    @Override
    public void showLoading(String content) {

    }

    @Override
    public void showErrorMsg(String msg, String type) {

    }

    @Override
    public void recordSuccess(Object o) {

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INTERVAL:
                    if (curTime > 0) {
                        tv_get_code.setText("" + curTime + "秒");
                        mHandler.sendEmptyMessageDelayed(INTERVAL, 1000);
                        curTime--;
                    } else {
                        setSendCode(false);
                    }
                    break;

                default:
                    setSendCode(false);
                    break;
            }
        }

    };

    private void setSendCode(boolean send) {
        curTime = 60;
        if (send == true) {
            mHandler.sendEmptyMessage(INTERVAL);
            tv_get_code.setEnabled(false);
        } else {
            tv_get_code.setText("重新发送");
            tv_get_code.setEnabled(true);
        }
    }

//    //复写倒计时
//    private class MyCountDownTimer extends CountDownTimer {
//
//        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
//            super(millisInFuture, countDownInterval);
//        }
//
//        //计时过程
//        @Override
//        public void onTick(long l) {
//            //防止计时过程中重复点击
//            tv_get_code.setClickable(false);
//            tv_get_code.setText(l / 1000 + "s");
//
//        }
//
//        //计时完毕的方法
//        @Override
//        public void onFinish() {
//            //重新给Button设置文字
//            tv_get_code.setText("重新获取");
//            //设置可点击
//            tv_get_code.setClickable(true);
//        }
//    }

}
