package com.huahua.chaoxing.util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * @author by Administrator
 * Email 1986754601@qq.com
 * Date on 2020/3/8.
 * 作用：
 * PS: Not easy to write code, please indicate.
 */

public class DataUtil {
    private Context context;

    public DataUtil(Context context) {
        this.context = context;
    }

    public void saveMap(HashMap<String, String> cookies) throws IOException {
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(context.getFilesDir().getAbsolutePath() + "cookies"));
        outputStream.writeObject(cookies);
    }

    public boolean clearMap() {
        File file = new File(context.getFilesDir().getAbsolutePath() + "cookies");
        boolean delete = file.delete();
        return delete;
    }

    public HashMap<String, String> getMap() throws IOException, ClassNotFoundException {
        File file = new File(context.getFilesDir().getAbsolutePath() + "cookies");
        if (file.exists()) {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
            return (HashMap<String, String>) inputStream.readObject();
        }
        return null;
    }
}
