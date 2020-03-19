package com.huahua.chaoxing;

import com.huahua.chaoxing.util.SSLSocketClient;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class picTest {


    private static HashMap<String, String> stringStringHashMap;

    public static void main(String[] args) throws IOException {


        stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("route", "b4d4d919a086adf195f8995091f2db3d");
        stringStringHashMap.put("_industry", "5");
        stringStringHashMap.put("fidsCount", "1");
        stringStringHashMap.put("lv", "4");
        stringStringHashMap.put("fid", "10567");
        stringStringHashMap.put("_uid", "82674419");
        stringStringHashMap.put("uf", "b2d2c93beefa90dc0e22d16d4c94f2c477f521284e68967da266dbc4d63edd7611dd7ddbb57b0a04cad620ec8842526f913b662843f1f4ad6d92e371d7fdf644d490c7da6f959ca6f3dba97bc9a5444941f23d7060bd78ccc4d86d773b6beb7424656e81bc3a59c9aa2ebad65cd196bb");
        stringStringHashMap.put("_d", "1584427256629");
        stringStringHashMap.put("UID", "82674419");
        stringStringHashMap.put("vc", "E5FB851D7A53505DBE80B21DA949D116");
        stringStringHashMap.put("vc2", "AD57CF34ECE8862FB5B0AF6174F01269");
        stringStringHashMap.put("vc3", "YZylttC%2F5PZCC83mhzsXtRv4lpm0eWV4ucIl6%2Bdnh3eoCqKqHq8za7XUp00p%2F7xUJjaJmubBe071YMpE%2Brl3ohcfUePfX%2BjGPdhR8nPvGWsQGHSKpk%2FVyYBvSQKN3jPMR3%2B6Cew0so0CTLFdFUX%2FbA5gzqsV9jFRSXfEk37IkCE%3D08ba75ebf5b93ff2bdd1ba10fb7d37e9");
        stringStringHashMap.put("xxtenc", "9859de4514cc55ab32cbee1ce9eda30b");
        stringStringHashMap.put("DSSTASH_LOG", "C_38-UN_10009-US_82674419-T_1584427256631");
        stringStringHashMap.put("sso_puid", "82674419");
        stringStringHashMap.put("KI4SO_SERVER_EC", "RERFSWdRQWdsckQ0aGRFcytqcUZFajBGZG1XM2hMSFFPbGExaitDNHpIOG1zWS85VUw3OVdSUk1K%0AYmdUcjhSM0NPdGdxMXhwZmNPcApyTHAzald4UVJVaUxyaFg4Zlk5SGQrU1ZLS0E2TitMclJ1a0ZW%0AMy9nZ01FRXdUUWt5Q3dmVlQ3aTM5NTEvSC");
        stringStringHashMap.put("_tid", "58785311");


//        FileInputStream inputStream = new FileInputStream("20200317144116342.jpg");


        // 到这里一定登录成功（bug除外）
        String getTokenUrl = "https://pan-yz.chaoxing.com/api/token/uservalid";

        String tokenResult = Jsoup.connect(getTokenUrl).method(Connection.Method.GET).cookies(stringStringHashMap).execute().body();
        System.out.println("tokenResult ----> " + "\n" + tokenResult);


        String sendUidUrl = "https://pan-yz.chaoxing.com/api/crcstatus?puid=" + stringStringHashMap.get("UID") + "&crc=bfb2e7968005665f8ac0d0465099a9d7&_token=6e580ee9878115521398035f530c4155";
        String sendUidResult = Jsoup.connect(sendUidUrl).method(Connection.Method.GET).cookies(stringStringHashMap).execute().body();
        System.out.println("sendUidResult ---->" + "\n" + sendUidResult);


        String uploadUrl = "https://pan-yz.chaoxing.com/upload?_token=6e580ee9878115521398035f530c4155";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("puid", "82674419");
        postFile(uploadUrl, hashMap, new File("a.json"), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println(response.body().string());
            }
        });
    }

    public static void postFile(final String url, final Map<String, String> map, File file, Callback callback) {
        // form 表单形式上传
        //创建OkHttpClient对象(前提是导入了okhttp.jar和okio.jar)
        OkHttpClient client = new OkHttpClient().newBuilder()
                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())//配置    //忽略验证证书
                .build();

        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (file != null) {
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
            String filename = file.getName();
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            requestBody.addFormDataPart("file", filename, body);
        }
        if (map != null) {
            // map 里面是请求中所需要的 key 和 value
            Set<Map.Entry<String, String>> entries = map.entrySet();
            for (Map.Entry entry : entries) {
                String key = String.valueOf(entry.getKey());
                String value = String.valueOf(entry.getValue());
//                Log.d("HttpUtils", "key=="+key+"value=="+value);
                requestBody.addFormDataPart(key, value);
            }
        }
        Request request = new Request.Builder().url(url).header("Cookie", stringStringHashMap.toString()).post(requestBody.build()).build();
        // readTimeout("请求超时时间" , 时间单位);
        client.newBuilder().readTimeout(5000, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(callback);

    }
}