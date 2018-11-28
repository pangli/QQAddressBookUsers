package com.tencent.qq.test;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

/**
 * 通讯录好友发消息
 * 1.使用此服务需要获取手机特殊权限：部分手机点击本demo页面中“打开辅助服务”按钮进入辅助功能页即可找到名称为“添加qq通讯录好友”的服务，然后打开即可，
 * 其他手机需要在辅助功能中找到“无障碍”项，然后在“无障碍”中找到“添加qq通讯录好友”打开即可
 * note:APP获取到辅助功能权限后，一旦APP进程被强杀就会清除该权限，再次进入APP又需要重新申请，正常退出则不会
 * Created by zorro
 * WeChatPeopleNearbyGreet
 */
public class AutoService extends AccessibilityService {
    public static final String TAG = "Zorro";
    /**
     * 向qq通讯录好友自动打招呼的内容
     */
    public static List<String> phoneList;
    public static String sendContent = "测试";
    public static boolean enableFunc;          //是否开启自动添加qq通讯录好友为好友的功能;
    private int i = 0;//记录已打招呼的人数
    private int prepos = -1;//记录页面跳转来源，0--从主页跳转到qq通讯录好友搜索页面，1--从通讯录搜索跳转到用户信息页面
    private TextToSpeech mTts;
    private String currentActivity = "";

    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
        if (event != null) {
            int eventType = event.getEventType();
            currentActivity = String.valueOf(event.getClassName());
            if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && MainActivity.canShowWindow(this)) {
                TasksWindow.show(this, event.getPackageName() + "\n" + currentActivity);
            }
            String str_eventType;
            switch (eventType) {
                case AccessibilityEvent.TYPE_VIEW_CLICKED:
                    Log.e(TAG, "==============Start====================");
                    str_eventType = "TYPE_VIEW_CLICKED";
                    Log.e(TAG, "=============END=====================");
                    break;
                case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                    str_eventType = "TYPE_VIEW_FOCUSED";
                    break;
                case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                    str_eventType = "TYPE_VIEW_LONG_CLICKED";
                    break;
                case AccessibilityEvent.TYPE_VIEW_SELECTED:
                    str_eventType = "TYPE_VIEW_SELECTED";
                    break;
                case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                    str_eventType = "TYPE_VIEW_TEXT_CHANGED";
                    break;
                case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                    str_eventType = "TYPE_WINDOW_STATE_CHANGED";
                    break;
                case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                    str_eventType = "TYPE_NOTIFICATION_STATE_CHANGED";
                    break;
                case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
                    str_eventType = "TYPE_TOUCH_EXPLORATION_GESTURE_END";
                    break;
                case AccessibilityEvent.TYPE_ANNOUNCEMENT:
                    str_eventType = "TYPE_ANNOUNCEMENT";
                    break;
                case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
                    str_eventType = "TYPE_TOUCH_EXPLORATION_GESTURE_START";
                    break;
                case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                    str_eventType = "TYPE_VIEW_HOVER_ENTER";
                    break;
                case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
                    str_eventType = "TYPE_VIEW_HOVER_EXIT";
                    break;
                case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                    str_eventType = "TYPE_VIEW_SCROLLED";
                    break;
                case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                    str_eventType = "TYPE_VIEW_TEXT_SELECTION_CHANGED";
                    break;
                case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                    str_eventType = "TYPE_WINDOW_CONTENT_CHANGED";
                    break;
                default:
                    str_eventType = String.valueOf(eventType);
            }
            if (currentActivity != null && currentActivity.contains("com.tencent.mobileqq")) {
                Log.e(TAG, "eventType---" + str_eventType + "----currentActivity---" + currentActivity);
            }
            //自动加人
            if (enableFunc) {
                if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && ("com.tencent.mobileqq.activity" +
                        ".SplashActivity").equals(currentActivity)) {//qq主页
                    //然后跳转到qq通讯录好友
                    Log.e(TAG, "iiiiiiiiiiiii---" + i + "---------prepos====" + prepos);
                    prepos = -1;
                    if (i < phoneList.size()) {
                        sleep(1000);
                        openNext("搜索");
                    } else {
                        i = 0;
                        enableFunc = false;
                    }
                    Log.e(TAG, "iiiiiiiiiiiii---" + i + "---------prepos====" + prepos);
                } else if ("com.tencent.mobileqq.search.activity.UniteSearchActivity".equals(currentActivity) &&
                        eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {//qq搜索页
                    if (prepos == 1) {
                        sleep(500);
                        openNext("取消");
                    } else {
                        prepos = 0;
                        //inputSearch(phoneList.get(i));
                        //搜索框id==com.tencent.mobileqq:id/et_search_keyword
                        inputContent(phoneList.get(i), "et_search_keyword");
                        sleep(1000);
                        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                        if (nodeInfo != null) {
                            List<AccessibilityNodeInfo> list = null;
                            List<AccessibilityNodeInfo> list1 = nodeInfo.findAccessibilityNodeInfosByText
                                    ("来自:手机通讯录");
                            List<AccessibilityNodeInfo> list2 = nodeInfo.findAccessibilityNodeInfosByText
                                    ("来自:通讯录临时会话");
                            if (list1 != null && !list1.isEmpty()) {
                                list = list1;
                            } else if (list2 != null && !list2.isEmpty()) {
                                list = list2;
                            }
                            //通讯录临时会话
                            if (list != null && !list.isEmpty()) {
                                list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                list.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            } else {
                                i++;
                                sleep(500);
                                openNext("取消");
                            }
                        } else {
                            i++;
                            sleep(500);
                            openNext("取消");
                        }
                    }
                } else if ("com.tencent.mobileqq.activity.FriendProfileCardActivity".equals(currentActivity) &&
                        eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {//通讯录好友保存到通讯录界面
                    prepos = 1;
                    i++;
                    sleep(500);
                    openNext("返回");
                } else if ("com.tencent.mobileqq.activity.ChatActivity".equals(currentActivity) && eventType ==
                        AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {//消息发送页
                    //输入消息内容并发送
                    sleep(500);
                    //发送消息输入框id---com.tencent.mobileqq:id/input
                    inputContent(sendContent, "input");
                    openNext("发送");
                    i++;
                    sleep(1000);
                    performChatBackClick();
                }
            }
        }
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击匹配的nodeInfo
     *
     * @param str text关键字
     */
    private void openNext(String str) {
        Log.e(TAG, "str----" + str);
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            Toast.makeText(this, "rootWindow为空", Toast.LENGTH_SHORT).show();
            return;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(str);
        if (list != null && list.size() > 0) {
            list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            list.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            Toast.makeText(this, "找不到有效的节点", Toast.LENGTH_SHORT).show();
        }
    }


    //延迟打开界面
    private void openDelay(int delaytime, final String text) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openNext(text);
            }
        }, delaytime);
    }

    //自动输入搜索的用户
    private void inputSearch(String hello) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        //找到当前获取焦点的view
        if (nodeInfo != null) {
            //com.tencent.mobileqq:id/et_search_keyword
            AccessibilityNodeInfo target = nodeInfo.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
            if (target == null) {
                Log.d(TAG, "inputHello: null");
                return;
            }
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", hello);
            clipboard.setPrimaryClip(clip);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                target.performAction(AccessibilityNodeInfo.ACTION_PASTE);

            }
        }
    }

    //自动输入搜索的用户
    private void inputContent(String hello, String id) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mobileqq:id/" + id);
        if (list != null && !list.isEmpty()) {
            AccessibilityNodeInfo target = list.get(0);
            if (target == null) {
                Log.d(TAG, "inputHello: null");
                return;
            }
            target.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", hello);
            clipboard.setPrimaryClip(clip);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                target.performAction(AccessibilityNodeInfo.ACTION_PASTE);
            }
        }
    }

    /*****
     * qq聊天界面返回点击
     */
    public void performChatBackClick() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        AccessibilityNodeInfo targetNode = null;
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent" +
                    ".mobileqq:id/rlCommenTitle");
            if (list.size() > 0) {
                targetNode = list.get(0);
            }
        }
        if (targetNode != null) {
            final AccessibilityNodeInfo n = targetNode.getChild(0);
            performClick(n);
        }
    }

    /**
     * 执行具体的点击
     *
     * @param nodeInfo
     */
    public void performClick(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return;
        }
        if (nodeInfo.isClickable()) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            performClick(nodeInfo.getParent());
        }
    }

    @Override
    public void onInterrupt() {
        Toast.makeText(this, "服务已中断", Toast.LENGTH_SHORT).show();
        mTts.shutdown();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Toast.makeText(this, "服务已开启", Toast.LENGTH_SHORT).show();
        mTts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    mTts.setLanguage(Locale.CHINESE);
                }
            }
        });
    }

    @Override
    public boolean onUnbind(Intent intent) {
        TasksWindow.dismiss();
        return super.onUnbind(intent);
    }

}
