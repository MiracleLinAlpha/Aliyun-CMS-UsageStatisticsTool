package util;


import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    /*
     * 时间戳转换为yyyy-MM-dd格式/
     */
    public String StampToTime(long stamp) {
        Date date = new Date(stamp);//新建一个时间对象
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//你要转换成的时间格式,大小写不要变
        String time = sdf.format(date);//转换你的时间
        return time;
    }


    /*
     *yyyy-MM-dd格式转换为时间戳 /
     */
    public  long TimeToStamp(String date) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date datetime = sdf.parse(date);//将你的日期转换为时间戳
        long time = datetime.getTime();
        return time;
    }
}
