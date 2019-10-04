package com.example.classchat.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Util_ScreenShot {

    private static final String SAVE_PIC_PATH= Environment.getExternalStorageDirectory().getAbsolutePath();

    private static Bitmap takeScreenShot(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Log.i("TAG", "" + statusBarHeight);

        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay()
                .getHeight();

        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    private static void saveFile(Bitmap bm, String fileName, Context context) throws IOException {
        String subForder = SAVE_PIC_PATH;
        Log.e("SAVE_PIC_PATH", SAVE_PIC_PATH);
        File foder = new File(subForder);
        if (!foder.exists()) {
            foder.mkdirs();
        }

        File myCaptureFile = new File(subForder, fileName);
        if (!myCaptureFile.exists()) {
            myCaptureFile.createNewFile();
        }

        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();

        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    myCaptureFile.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        //通知图库更新
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(myCaptureFile);
        Log.e("Uri", String.valueOf(uri));
        intent.setData(uri);
        context.sendBroadcast(intent);

    }

    // 程序入口
    public static String shoot(Activity activity) throws IOException {
        String strFileName = "Laoke" + System.currentTimeMillis() + ".jpg";
        Util_ScreenShot.saveFile(Util_ScreenShot.takeScreenShot(activity), strFileName, activity);
        return strFileName;
    }
}
