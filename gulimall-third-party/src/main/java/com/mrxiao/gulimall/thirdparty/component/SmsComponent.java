package com.mrxiao.gulimall.thirdparty.component;

import com.mrxiao.gulimall.thirdparty.util.HttpUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @ClassName SmsComponent
 * @Description TODO
 * @Version 1.0
 * @date 2020/12/15 0015 20:19
 */
@ConfigurationProperties("spring.cloud.alicloud.sms")
@Data
@Component
@Slf4j
public class SmsComponent {

    private String host;
    private String path;
    private String appcode;
//    private String content;


    public void sendSms(String phone,String code){
//        String host = "https://cxkjsms.market.alicloudapi.com";
//        String path = "/chuangxinsms/dxjk";
        log.info("sendSms参数:{}"+phone);
        String method = "POST";
//        String appcode = "f164a5342b12454680349358de05226e";//开通服务后 买家中心-查看AppCode
        Map<String, String> headers = new HashMap<>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<>();
        // "【创信】你的验证码是：5873，3分钟内有效！"
//        querys.put("content", "【创信】你的验证码是："+code+"，3分钟内有效！");
        querys.put("content", "【创信】你的验证码是：5873，3分钟内有效！");
        querys.put("mobile", phone);
        Map<String, String> bodys = new HashMap<>();


        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            log.info(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
