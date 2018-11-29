package com.tencent.qq.test;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pangli on 2018/11/27 16:03
 * 备注：  获取手机通讯录
 */
public class PhoneUtils {
    public static List<String> getAllPhone(Context context) {
        List<String> phones = new ArrayList<>();
        //得到ContentResolver对象
        ContentResolver cr = context.getContentResolver();
        //取得电话本中开始一项的光标
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts
                .SORT_KEY_PRIMARY);
        if (cursor != null) {
            try {
                //向下移动光标
                while (cursor.moveToNext()) {
                    //取得联系人名字
                    int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                    String contact = cursor.getString(nameFieldColumnIndex);
                    //取得电话号码
                    String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract
                            .CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
                    if (phone != null) {
                        while (phone.moveToNext()) {
                            String PhoneNumber = phone.getString(phone.getColumnIndex(ContactsContract
                                    .CommonDataKinds.Phone.NUMBER));
                            //格式化手机号
                            PhoneNumber = PhoneNumber.replace("-", "");
                            PhoneNumber = PhoneNumber.replace(" ", "");
                            Log.e("Zorro", contact + "---" + PhoneNumber);
                            phones.add(PhoneNumber);
                        }
                        phone.close();
                    }
                }
                Toast.makeText(context, "通讯录数据获取成功", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "通讯录数据获取失败", Toast.LENGTH_SHORT).show();
            } finally {
                cursor.close();
            }
        } else {
            Toast.makeText(context, "通讯录数据获取失败", Toast.LENGTH_SHORT).show();
        }
        return phones;
    }

    public static List<String> getAllPhone(Context context, Handler handler) {
        List<String> phones = new ArrayList<>();
        //得到ContentResolver对象
        ContentResolver cr = context.getContentResolver();
        //取得电话本中开始一项的光标
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts
                .SORT_KEY_PRIMARY);
        if (cursor != null) {
            try {
                //向下移动光标
                while (cursor.moveToNext()) {
                    //取得联系人名字
                    int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                    String contact = cursor.getString(nameFieldColumnIndex);
                    //取得电话号码
                    String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract
                            .CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
                    if (phone != null) {
                        while (phone.moveToNext()) {
                            String PhoneNumber = phone.getString(phone.getColumnIndex(ContactsContract
                                    .CommonDataKinds.Phone.NUMBER));
                            //格式化手机号
                            PhoneNumber = PhoneNumber.replace("-", "");
                            PhoneNumber = PhoneNumber.replace(" ", "");
                            Log.e("Zorro", contact + "---" + PhoneNumber);
                            phones.add(PhoneNumber);
                        }
                        phone.close();
                    }
                }
                showToast(context, handler, "通讯录数据获取成功");
            } catch (Exception e) {
                e.printStackTrace();
                showToast(context, handler, "通讯录数据获取失败");
            } finally {
                cursor.close();
            }
        } else {
            showToast(context, handler, "通讯录数据获取失败");
        }
        return phones;
    }

    private static void showToast(final Context context, Handler handler, final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
