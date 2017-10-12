package com.cookie_apps.myseniorapp1.MyClass;

/**
 * Created by acount on 16/03/16.
 */
public class MyParseDateConverter
{
    static String[] day_of_week_en = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    static String[] day_of_week_th = {"วันจันทร์", "วันอังคาร", "วันพุธ", "วันพฤหัสบดี", "วันศุกร์", "วันเสาร์", "วันอาทิตย์"};
    static String[] month_en = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    static String[] month_th = {"มกราคม", "กุมภาพันธ์", "มีนาคม", "เมษายน", "พฤษภาคม", "มิถุนายน", "กรกฎาคม", "สิงหาคม", "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"};

    public static String convert(String date, int style){
        String[] Date = date.split(" ");

        String day = Date[2];
        String day_of_week = Date[0];
        String month = Date[1];
        String year = Date[5];
        String time = Date[3];

        int index = -1;
        for (int i=0;i<day_of_week_en.length;i++) {
            if (day_of_week_en[i].equals(day_of_week)) {
                index = i;
                day_of_week = day_of_week_th[i];
                break;
            }
        }
        for (int i=0;i<month_en.length;i++) {
            if (month_en[i].equals(month)) {
                index = i;
                month = month_th[i];
                break;
            }
        }

        if (style==0) return day_of_week + " " + day + " " + month + " " + year + " เวลา " + time; else return day + " " + month + " " + year;
    }
}
