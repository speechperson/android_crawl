package com.example.scores;


import android.accounts.NetworkErrorException;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;

import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class NetUtils {

    //The page where we post the user's information
    public static  String LOGIN_URL="http://jxgl.cqu.edu.cn/_data/index_login.aspx";
    //The page where we get the grade data
    public static  String GRADES_URL="http://jxgl.cqu.edu.cn/xscj/Stu_MyScore_rpt.aspx";

    private static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }
        return result.toString();
    }
    //MD5 encryption functions that use 32-bit encryption
    public static String md5Decode32(String content) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(content.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException",e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        }
        //Completes the zero operation on the generated 16-byte array
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10){
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    //Because of the "HttpOnly",we use this method to get the cookie from the HeaderField
    public static String getCookie(HttpURLConnection connection){
        String sessionId = "";
        String cookieVal = "";
        String key = null;
        sessionId= connection.getHeaderField("Set-Cookie");
        for(int i = 1; (key = connection.getHeaderFieldKey(i)) != null; i++){
            if(key.equalsIgnoreCase("set-cookie")){
                cookieVal = connection.getHeaderField(i);
                cookieVal = cookieVal.substring(0, cookieVal.indexOf(";"));
                sessionId = sessionId + cookieVal + ";\n";
            }
        }
        String[] array = sessionId.split(";");
        String asp=array[2];
        String id=array[3];
        asp=asp.substring(8,asp.length());
        id=id.substring(1,id.length());
        asp=asp+";";
        return asp.concat(id);
    }

    //Log in to the page with a username and password and save the cookie
    public static String[] LoginByPost(String number, String passwd)
    {
        String[] data={"Wrong","Wrong"};
        try{

            HttpURLConnection conn = (HttpURLConnection) new URL(LOGIN_URL).openConnection();
            //Set the request mode, request timeout information
            conn.setRequestMethod("POST");
            conn.setReadTimeout(30000);
            conn.setConnectTimeout(30000);
            //Set the running input, output:
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setUseCaches(false);
            conn.setFollowRedirects(true);

            conn.setRequestProperty("Content-Type" ,"application/x-www-form-urlencoded");
            conn.setRequestProperty("Connection", "keep-alive");
            //
            conn.setRequestProperty("Keep-Alive", "timeout=5, max=1000");

            //Imitate encryption function which is used by the teaching administration network in JavaScript
            String check=md5Decode32(number+md5Decode32(passwd).substring(0,30).toUpperCase()+"10611").substring(0,30).toUpperCase();

            //Simulate the WebForms including the student's information submitted by the educational administration network
            List<NameValuePair> paramsgra1 = new ArrayList<NameValuePair>();
            paramsgra1.add(new BasicNameValuePair("__VIEWSTATE","dDw1OTgzNjYzMjM7dDw7bDxpPDE+O2k8Mz47aTw1Pjs+O2w8dDxwPGw8VGV4dDs+O2w86YeN5bqG5aSn5a2mOz4+Ozs+O3Q8cDxsPFRleHQ7PjtsPFw8c2NyaXB0IHR5cGU9InRleHQvamF2YXNjcmlwdCJcPgpcPCEtLQpmdW5jdGlvbiBvcGVuV2luTG9nKHRoZVVSTCx3LGgpewp2YXIgVGZvcm0scmV0U3RyXDsKZXZhbCgiVGZvcm09J3dpZHRoPSIrdysiLGhlaWdodD0iK2grIixzY3JvbGxiYXJzPW5vLHJlc2l6YWJsZT1ubyciKVw7CnBvcD13aW5kb3cub3Blbih0aGVVUkwsJ3dpbktQVCcsVGZvcm0pXDsgLy9wb3AubW92ZVRvKDAsNzUpXDsKZXZhbCgiVGZvcm09J2RpYWxvZ1dpZHRoOiIrdysicHhcO2RpYWxvZ0hlaWdodDoiK2grInB4XDtzdGF0dXM6bm9cO3Njcm9sbGJhcnM9bm9cO2hlbHA6bm8nIilcOwppZih0eXBlb2YocmV0U3RyKSE9J3VuZGVmaW5lZCcpIGFsZXJ0KHJldFN0cilcOwp9CmZ1bmN0aW9uIHNob3dMYXkoZGl2SWQpewp2YXIgb2JqRGl2ID0gZXZhbChkaXZJZClcOwppZiAob2JqRGl2LnN0eWxlLmRpc3BsYXk9PSJub25lIikKe29iakRpdi5zdHlsZS5kaXNwbGF5PSIiXDt9CmVsc2V7b2JqRGl2LnN0eWxlLmRpc3BsYXk9Im5vbmUiXDt9Cn0KZnVuY3Rpb24gc2VsVHllTmFtZSgpewogIGRvY3VtZW50LmFsbC50eXBlTmFtZS52YWx1ZT1kb2N1bWVudC5hbGwuU2VsX1R5cGUub3B0aW9uc1tkb2N1bWVudC5hbGwuU2VsX1R5cGUuc2VsZWN0ZWRJbmRleF0udGV4dFw7Cn0KZnVuY3Rpb24gd2luZG93Lm9ubG9hZCgpewoJdmFyIHNQQz13aW5kb3cubmF2aWdhdG9yLnVzZXJBZ2VudCt3aW5kb3cubmF2aWdhdG9yLmNwdUNsYXNzK3dpbmRvdy5uYXZpZ2F0b3IuYXBwTWlub3JWZXJzaW9uKycgU046TlVMTCdcOwp0cnl7ZG9jdW1lbnQuYWxsLnBjSW5mby52YWx1ZT1zUENcO31jYXRjaChlcnIpe30KdHJ5e2RvY3VtZW50LmFsbC50eHRfZHNkc2RzZGpramtqYy5mb2N1cygpXDt9Y2F0Y2goZXJyKXt9CnRyeXtkb2N1bWVudC5hbGwudHlwZU5hbWUudmFsdWU9ZG9jdW1lbnQuYWxsLlNlbF9UeXBlLm9wdGlvbnNbZG9jdW1lbnQuYWxsLlNlbF9UeXBlLnNlbGVjdGVkSW5kZXhdLnRleHRcO31jYXRjaChlcnIpe30KfQpmdW5jdGlvbiBvcGVuV2luRGlhbG9nKHVybCxzY3IsdyxoKQp7CnZhciBUZm9ybVw7CmV2YWwoIlRmb3JtPSdkaWFsb2dXaWR0aDoiK3crInB4XDtkaWFsb2dIZWlnaHQ6IitoKyJweFw7c3RhdHVzOiIrc2NyKyJcO3Njcm9sbGJhcnM9bm9cO2hlbHA6bm8nIilcOwp3aW5kb3cuc2hvd01vZGFsRGlhbG9nKHVybCwxLFRmb3JtKVw7Cn0KZnVuY3Rpb24gb3Blbldpbih0aGVVUkwpewp2YXIgVGZvcm0sdyxoXDsKdHJ5ewoJdz13aW5kb3cuc2NyZWVuLndpZHRoLTEwXDsKfWNhdGNoKGUpe30KdHJ5ewpoPXdpbmRvdy5zY3JlZW4uaGVpZ2h0LTMwXDsKfWNhdGNoKGUpe30KdHJ5e2V2YWwoIlRmb3JtPSd3aWR0aD0iK3crIixoZWlnaHQ9IitoKyIsc2Nyb2xsYmFycz1ubyxzdGF0dXM9bm8scmVzaXphYmxlPXllcyciKVw7CnBvcD1wYXJlbnQud2luZG93Lm9wZW4odGhlVVJMLCcnLFRmb3JtKVw7CnBvcC5tb3ZlVG8oMCwwKVw7CnBhcmVudC5vcGVuZXI9bnVsbFw7CnBhcmVudC5jbG9zZSgpXDt9Y2F0Y2goZSl7fQp9CmZ1bmN0aW9uIGNoYW5nZVZhbGlkYXRlQ29kZShPYmopewp2YXIgZHQgPSBuZXcgRGF0ZSgpXDsKT2JqLnNyYz0iLi4vc3lzL1ZhbGlkYXRlQ29kZS5hc3B4P3Q9IitkdC5nZXRNaWxsaXNlY29uZHMoKVw7Cn0KXFwtLVw+Clw8L3NjcmlwdFw+Oz4+Ozs+O3Q8O2w8aTwxPjs+O2w8dDw7bDxpPDA+Oz47bDx0PHA8bDxUZXh0Oz47bDxcPG9wdGlvbiB2YWx1ZT0nU1RVJyB1c3JJRD0n5a2m5Y+3J1w+5a2m55SfXDwvb3B0aW9uXD4KXDxvcHRpb24gdmFsdWU9J1RFQScgdXNySUQ9J+W4kOWPtydcPuaVmeW4iFw8L29wdGlvblw+Clw8b3B0aW9uIHZhbHVlPSdTWVMnIHVzcklEPSfluJDlj7cnXD7nrqHnkIbkurrlkZhcPC9vcHRpb25cPgpcPG9wdGlvbiB2YWx1ZT0nQURNJyB1c3JJRD0n5biQ5Y+3J1w+6Zeo5oi357u05oqk5ZGYXDwvb3B0aW9uXD4KOz4+Ozs+Oz4+Oz4+Oz4+Oz62hRbfKCEh4NgqUfD+QNlfnJS/3A=="));
            paramsgra1.add(new BasicNameValuePair("__VIEWSTATEGENERATOR","CAA0A5A7"));
            paramsgra1.add(new BasicNameValuePair("Sel_Type","STU"));
            paramsgra1.add(new BasicNameValuePair("txt_dsdsdsdjkjkjc",number));
            paramsgra1.add(new BasicNameValuePair("txt_dsdfdfgfouyy",passwd));
            paramsgra1.add(new BasicNameValuePair("txt_ysdsdsdskgf",""));
            paramsgra1.add(new BasicNameValuePair("pcInfo",""));
            paramsgra1.add(new BasicNameValuePair("typeName",""));
            paramsgra1.add(new BasicNameValuePair("aerererdsdxcxdfgfg",""));
            paramsgra1.add(new BasicNameValuePair("efdfdfuuyyuuckjg",check));

            OutputStream out=conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "gb2312"));

            String a=getQuery(paramsgra1);
            writer.write(a);
            writer.flush();
            writer.close();
            
            //Get the cookie
            int responseCode=conn.getResponseCode();
            if(responseCode==200){
                String cookie=getCookie(conn);
                //Get the source code to return to the web page and save it, easy to determine whether the login was successful
                StringBuilder buffer = new StringBuilder();
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "gb2312"));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                is.close();
                //Combine the page source and cookie and return
                data= new String[]{buffer.toString(), cookie};
                return data;
            }else{
                throw new NetworkErrorException("response status is "+responseCode);
            }
        }catch (Exception e){e.printStackTrace();}

        return data;
    }

    public static String Search(String year,String term,String cookie){
        try{
            HttpURLConnection conn = (HttpURLConnection) new URL(GRADES_URL).openConnection();

            conn.setRequestMethod("POST");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setFollowRedirects(true);
            conn.setRequestProperty("Content-Type" ,"application/x-www-form-urlencoded");
            conn.setRequestProperty("Connection", "keep-alive");
            //conn.setRequestProperty("Keep-Alive", "timeout=5, max=1000");
            conn.setRequestProperty("Cookie",cookie);

            //Simulate the WebForms including the information about grade submitted by the educational administration network
            List<NameValuePair> paramsgra1 = new ArrayList<NameValuePair>();
            paramsgra1.add(new BasicNameValuePair("sel_xn",year));
            paramsgra1.add(new BasicNameValuePair("sel_xq",term));
            paramsgra1.add(new BasicNameValuePair("SJ","1"));
            paramsgra1.add(new BasicNameValuePair("btn_search","检索"));
            paramsgra1.add(new BasicNameValuePair("SelXNXQ","2"));
            paramsgra1.add(new BasicNameValuePair("zfx_flag","0"));
            paramsgra1.add(new BasicNameValuePair("zxf","0"));


            OutputStream out=conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(out, "gb2312"));

            String a=getQuery(paramsgra1);
            writer.write(a);
            writer.flush();
            writer.close();
            int responseCode=conn.getResponseCode();
            if(responseCode==200){

               StringBuilder buffer = new StringBuilder();
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "gb2312"));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                is.close();
                return buffer.toString();

            }else{
                throw new NetworkErrorException("response status is "+responseCode);
            }
        }catch (Exception e){e.printStackTrace();}
        return null;
    }
}

