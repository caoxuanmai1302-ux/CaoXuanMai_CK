package com.example.caoxuanmai_ck;
import java.text.NumberFormat; import java.util.Locale;
public class MoneyJava {
    public static String vnd(double a){ return NumberFormat.getCurrencyInstance(new Locale("vi","VN")).format(a); }
}