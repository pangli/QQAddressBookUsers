package com.tencent.qq.test;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.tencent.qq.test.AutoService.TAG;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, CompoundButton
        .OnCheckedChangeListener {
    private EditText edit_send_message;
    private CheckBox cb_assist;
    private CheckBox cb_window;
    private CheckBox cb_people_nearby;
    private Button btn_phone;
    private Button btn_start_qq;
    private List<String> phoneList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edit_send_message = findViewById(R.id.edit_send_message);
        cb_assist = findViewById(R.id.cb_assist_permission);
        if (cb_assist != null) {
            cb_assist.setOnCheckedChangeListener(this);
        }
        cb_window = findViewById(R.id.cb_show_window);
        if (cb_window != null) {
            cb_window.setOnCheckedChangeListener(this);
        }
        cb_people_nearby = findViewById(R.id.cb_people_nearby);
        if (cb_people_nearby != null) {
            cb_people_nearby.setOnCheckedChangeListener(this);
        }
        btn_phone = findViewById(R.id.btn_phone);
        btn_start_qq = findViewById(R.id.btn_start_qq);
        if (btn_phone != null) {
            btn_phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    methodRequiresTwoPermission();
                }
            });
        }
        if (btn_start_qq != null) {
            btn_start_qq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String content = edit_send_message.getText().toString();
                    if (!TextUtils.isEmpty(content)) {
                        AutoService.sendContent = content;
                    } else {
                        Toast.makeText(MainActivity.this, "请先输入需要发送的消息内容", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (phoneList != null && !phoneList.isEmpty()) {
                        AutoService.phoneList = phoneList;
                        openApp();
                    } else {
                        Toast.makeText(MainActivity.this, "请先获取通讯录数据", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }

    }

    /**
     * 跳转微信主界面
     */
    public void openApp() {
        try {
            PackageManager packageManager = getPackageManager();
            Intent intent = new Intent();
            intent = packageManager.getLaunchIntentForPackage("com.tencent.mobileqq");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://im.qq.com/mobileqq/"));
            startActivity(viewIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCheckBox(cb_assist, isAccessibilitySettingsOn());
        updateCheckBox(cb_window, canShowWindow(this));
        if (canShowWindow(this)) {
            requestFloatWindowPermissionIfNeeded();
        }
    }

    /**
     * 申请辅助功能权限
     */
    private void requestAssistPermission() {
        try {
            //打开系统设置中辅助功能
            Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            Toast.makeText(MainActivity.this, "找到添加qq通讯录好友，然后开启服务即可", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 申请悬浮窗权限
     */
    private void requestFloatWindowPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.dialog_enable_overlay_window_msg)
                    .setPositiveButton(R.string.dialog_enable_overlay_window_positive_btn
                            , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            })
                    .setNegativeButton(android.R.string.cancel
                            , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setShowWindow(MainActivity.this, false);
                                    updateCheckBox(cb_window, false);
                                }
                            })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            setShowWindow(MainActivity.this, false);
                            updateCheckBox(cb_window, false);
                        }
                    })
                    .create()
                    .show();

        }
    }

/*    private MoveTextView floatBtn1;
    private MoveTextView floatBtn2;
    private WindowManager wm;

    //创建悬浮按钮
    private void createFloatView() {
        WindowManager.LayoutParams pl = new WindowManager.LayoutParams();
        wm = (WindowManager) getSystemService(getApplication().WINDOW_SERVICE);
        pl.type = WindowManager.LayoutParams.TYPE_TOAST;//修改为此TYPE_TOAST，可以不用申请悬浮窗权限就能创建悬浮窗,但在部分手机上会崩溃
        pl.format = PixelFormat.RGBA_8888;
        pl.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        pl.gravity = Gravity.END | Gravity.BOTTOM;
        pl.x = 0;
        pl.y = 0;

        pl.width = WindowManager.LayoutParams.WRAP_CONTENT;
        pl.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(this);
        floatBtn1 = (MoveTextView) inflater.inflate(R.layout.floatbtn, null);
        floatBtn1.setText("打招呼");
        floatBtn2 = (MoveTextView) inflater.inflate(R.layout.floatbtn, null);
        floatBtn2.setText("抢红包");
        wm.addView(floatBtn1, pl);
        pl.gravity = Gravity.BOTTOM | Gravity.START;
        wm.addView(floatBtn2, pl);

        floatBtn1.setOnClickListener(this);
        floatBtn2.setOnClickListener(this);
        floatBtn1.setWm(wm, pl);
        floatBtn2.setWm(wm, pl);
    }*/

    /**
     * 检测辅助功能是否开启
     */
    private boolean isAccessibilitySettingsOn() {
        int accessibilityEnabled = 0;
        String service = getPackageName() + "/" + AutoService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.d(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.d(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.d(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.d(TAG, "***ACCESSIBILITY IS DISABLED***");
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_assist_permission:
                if (isChecked && !isAccessibilitySettingsOn()) {
                    requestAssistPermission();
                }
                break;
            case R.id.cb_show_window:
                setShowWindow(this, isChecked);
                if (isChecked) {
                    requestFloatWindowPermissionIfNeeded();
                }
                if (!isChecked) {
                    TasksWindow.dismiss();
                } else {
                    TasksWindow.show(this, getPackageName() + "\n" + getClass().getName());
                }
                break;
            case R.id.cb_people_nearby:
                if (isChecked) {
                    if (isAccessibilitySettingsOn()) {
                        AutoService.enableFunc = true;
                    } else {
                        Toast.makeText(MainActivity.this, "辅助功能未开启", Toast.LENGTH_SHORT).show();
                        buttonView.setChecked(false);
                    }
                } else {
                    AutoService.enableFunc = false;
                }
                break;
        }
    }

    private void updateCheckBox(CheckBox box, boolean isChecked) {
        if (box != null) {
            box.setChecked(isChecked);
        }
    }

    public static boolean canShowWindow(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("show_window", true);
    }

    public static void setShowWindow(Context context, boolean isShow) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean("show_window", isShow).apply();
    }

    /**
     * 权限申请
     */
    @AfterPermissionGranted(101)
    private void methodRequiresTwoPermission() {
        String[] perms = {Manifest.permission.READ_CONTACTS};
        if (EasyPermissions.hasPermissions(this, perms)) {
            phoneList = PhoneUtils.getAllPhone(MainActivity.this);
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "联系人权限未开启,请先在设置中打开联系人", 101, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}
