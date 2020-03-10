针对学习通抓包分析

	1. 	请求地址 http://passport2.chaoxing.com/cloudscanlogin?mobiletip=%e7%94%b5%e8%84%91%e7%ab%af%e7%99%bb%e5%bd%95%e7%a1%ae%e8%ae%a4&pcrefer=http://i.chaoxing.com
	
		JSESSIONID=8ED0FD5C4998FC01D5B3A60ABDACB35A;
		Path=/; HttpOnly
		route=48dac4a1afc32ddf7230daab78ce59ff;Path=/
		头像api：http://photo.chaoxing.com/p/82659775_160
		扫码获取cookies代码
		
		String uuid = "c38a1afb46be403a8369a9c0a0e1a644";
        String enc = "56b0f210c7a8f8b619e246c21e61b15d";
        String url = "http://passport2.chaoxing.com/getauthstatus";
        String JSESSIONID = "BD5C82018A501DB1293CADCF0594FF91";
        String route = "b9434b2aa11d2e38febba82dc6592cde";
        HashMap<String, String> map = new HashMap<>();
        map.put("uuid", uuid);
        map.put("enc", enc);

        HashMap<String, String> cookiesMap = new HashMap<>();
        cookiesMap.put("JSESSIONID", JSESSIONID);
        cookiesMap.put("route",route);
		
		
		
		二维码未扫描返回:{"mes":"未登录","type":"3","status":false}
		二维码扫描未登录返回:{"uid":"82659775","nickname":"郭梁","mes":"已扫描","type":"4","status":false}
		二维码扫描且登录返回: {"mes":"验证通过","status":true}
		二维码过期: {"mes":"二维码已失效","type":"2","status":false}
		二维码扫描取消登录： {"mes":"用户手机端取消登录","type":"6","status":false}
		
		任务列表：https://mobilelearn.chaoxing.com/widget/pcpick/stu/index?courseId=206811209&jclassId=13721305
		签到api：https://mobilelearn.chaoxing.com/widget/sign/pcStuSignController/preSign?activeId=84291057&classId=13721305&fid=10567&courseId=206811209
				 https://mobilelearn.chaoxing.com/widget/sign/pcStuSignController/preSign?activeId=86075275&classId=13721305&fid=10567&courseId=206811209
