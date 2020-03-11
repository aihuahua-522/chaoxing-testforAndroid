# 针对学习通抓包分析

## 请求地址

```http
http://passport2.chaoxing.com/cloudscanlogin?mobiletip=%e7%94%b5%e8%84%91%e7%ab%af%e7%99%bb%e5%bd%95%e7%a1%ae%e8%ae%a4&pcrefer=http://i.chaoxing.com
  在这里我们需要通过元素选择器获取uuid和enc
  看java示例
   String uuid = document.select("input[ id =uuid]").attr("value");
   String enc = document.select("input[ id =enc]").attr("value");

```

## 返回信息


```http
JSESSIONID=8ED0FD5C4998FC01D5B3A60ABDACB35A;
Path=/; HttpOnly
route=48dac4a1afc32ddf7230daab78ce59ff;Path=/
以上信息需要setCookie
```
## 加载二维码

```java
scanningUrl = "http://passport2.chaoxing.com/createqr?uuid=" + uuid + "&xxtrefer=&type=1&mobiletip=%E7%94%B5%E8%84%91%E7%AB%AF%E7%99%BB%E5%BD%95%E7%A1%AE%E8%AE%A4";
直接返回图片流
这里使用管理的加载
 requireActivity().runOnUiThread(() -> Glide.with(requireActivity())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(root.qrImg));

```



## 扫描地址

```java
dataMap = new HashMap<>();
dataMap.put("uuid", uuid);
dataMap.put("enc", enc);
String url = "http://passport2.chaoxing.com/getauthstatus";
Jsoup.connect(url).cookies(cookies).data(dataMap).method(Connection.Method.POST).execute();
cookies是第一次请求地址的返回cookie
```

```json
二维码未扫描返回:{"mes":"未登录","type":"3","status":false}
二维码扫描未登录返回:{"uid":"82659775","nickname":"爱花花","mes":"已扫 描","type":"4","status":false}
二维码扫描且登录返回: {"mes":"验证通过","status":true}
这个时候需要setCookies(有一堆)
二维码过期: {"mes":"二维码已失效","type":"2","status":false}
二维码扫描取消登录： {"mes":"用户手机端取消登录","type":"6","status":false}
```

## 获取头像

```http

头像api：http://photo.chaoxing.com/p/82659775_160
82659775是返回的uid
160是分辨率
```

## 获取课程列表(以及姓名)

```java
String getClassUrl = "http://mooc1-2.chaoxing.com/visit/courses";
String getName = "http://i.chaoxing.com/base";
Document classDocument = null;
try {
    Map<String, String> cookies = viewModel.getCookies();
    String text = Jsoup.connect(getName).cookies(cookies).get().select(".user-name").text();
    classDocument = Jsoup.connect(getClassUrl).cookies(cookies).get();
    if (classDocument.title().contains("用户登录")) {
        Toast.makeText(requireActivity(), "cookies失效,请重新登录", Toast.LENGTH_SHORT).show();
        return;
    }
} catch (IOException e) {
    e.printStackTrace();
}
Elements classElements = classDocument.select(".ulDiv > ul > li[style]");
for (Element classElement : classElements) {
    ClassBean classBean = new ClassBean();
    String courseId = classElement.select("[name = courseId]").attr("value");
    String classId = classElement.select("[name = classId]").attr("value");
    //课程名
    String className = classElement.select(".clearfix > a ").attr("title");
    //班级名
    String classmate = classElement.select(".Mconright > p ").get(2).attr("title");
    //教师
    String teacher = classElement.select(".Mconright > p ").get(0).attr("title");
    //任务地址
    String url = "https://mobilelearn.chaoxing.com/widget/pcpick/stu/index?courseId=" + courseId + "&jclassId=" + classId;
    classBean.setCourseId(courseId);
    classBean.setClassId(classId);
    classBean.setClassName(className);
    classBean.setTeacher(teacher);
    classBean.setUrl(url);
    classBean.setClassmate(classmate);
    classBeans.add(classBean);
}
```

## 获取签到列表

```java
任务列表：url = "https://mobilelearn.chaoxing.com/widget/pcpick/stu/index?courseId=206811209&jclassId=13721305"

Connection.Response response = Jsoup.connect(url).cookies(mViewModel.getCookies()).method(Connection.Method.GET).execute();
Document document = response.parse();
elements = document.select("#startList div .Mct");
elements是签到列表元素
```



## 开始签到

```java
for (Element ele : elements) {
    String onclick = ele.attr("onclick");
    System.out.println(onclick);
    if (onclick != null && onclick.length() > 0) {
        String split = onclick.split("\\(")[1];
        String activeId = split.split(",")[0];
        System.out.println(split);
        System.out.println(activeId);
        System.out.println("保存的数据" + mViewModel.getTemp().get(activeId));
        if (mViewModel.getTemp().get(activeId) != null) {
            SignBean signBean = new SignBean();
            signBean.setSignClass(classBeans.get(i).getClassName());
            signBean.setSignName(classBeans.get(i).getClassmate());
            signBean.setSignState("签到成功");
            signBean.setSignTime(ele.select(".Color_Orang").text());
            signBeans.add(signBean);
            continue;
        }
        String signUrl = "https://mobilelearn.chaoxing.com/pptSign/stuSignajax?name="
            + URLDecoder.decode(mViewModel.getTemp().get("name").toString())
            + "&address="
            + mViewModel.getTemp().get("signPlace")
            + "&activeId="
            + activeId
            + "&uid="
            + mViewModel.getCookies().get("_uid")
            + "&clientip=&latitude=-1&longitude=-1&fid="
            + mViewModel.getCookies().get("fid")
            + "&appType=15&ifTiJiao=1";
        System.out.println(signUrl);
        System.out.println("==============" + activeId + "签到中=================");
        Connection.Response signResponse = Jsoup.connect(signUrl).cookies(mViewModel.getCookies()).method(Connection.Method.GET).execute();
        Element element = signResponse.parse().body();
        System.out.println("签到状态" + element.getElementsByTag("body").text());
        SignBean signBean = new SignBean();
        signBean.setSignClass(classBeans.get(i).getClassName());
        signBean.setSignName(classBeans.get(i).getClassmate());
        signBean.setSignState(element.getElementsByTag("body").text());
        signBean.setSignTime(ele.select(".Color_Orang").text());
        if (signBean.getSignState().equals("您已签到过了")) {
            mViewModel.setTemp(activeId, "签到成功");
        }
        signBeans.add(signBean);
        Thread.sleep(1000);
    }
}
```

