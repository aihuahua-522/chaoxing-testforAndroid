package com.huahua.chaoxing.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author by Administrator
 * Email 1986754601@qq.com
 * Date on 2020/3/11.
 * 作用：
 * PS: Not easy to write code, please indicate.
 */

public class DateUtil {
    public static String getThisTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }
}
