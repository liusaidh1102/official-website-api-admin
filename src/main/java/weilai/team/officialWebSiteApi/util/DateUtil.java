package weilai.team.officialWebSiteApi.util;

import java.text.SimpleDateFormat;

/**
 * @author lzw
 * @date 2024/12/13 17:18
 * @description 作用：
 */
public class DateUtil {

    static  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm-HH:mm");



    //抛出异常，外部自己处理
    public static Long getDateLong(String dateStr) throws Exception{
        return dateFormat.parse(dateStr).getTime();
    }


    public static String getDateStr(Long dateLong){
        return dateFormat.format(dateLong);
    }


}
