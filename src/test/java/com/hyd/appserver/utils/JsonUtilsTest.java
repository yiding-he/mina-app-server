package com.hyd.appserver.utils;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

/**
 * todo: description
 *
 * @author yiding.he
 */
public class JsonUtilsTest {

    @Test
    public void testParse() throws Exception {
        String json = "{\"name\":123}\u001a";
        System.out.println(JSON.parse(json));
    }

    @Test
    public void testParse2() throws Exception {
        String JSON = " {\"code\":0,\"data\":{\"result\":{\"awardAmount\":1,\"awardArea\":\"全国\",\"awardBatch\":100000,\"awardId\":16," +
                "\"awardName\":\"哈根达斯消费券\",\"awardStatus\":\"valid\",\"awardType\":2,\"batchName\":\"测试活动\",\"context\":{\"award" +
                "Ranges\":{100001:[0,-111485],100002:[-111484,-222969],100003:[-222968,435121],100004:[435122,746799],100005:[746800,106" +
                "1092],100006:[1061093,1860298],100007:[1860299,2659504],100008:[2659505,2971183]},\"pointer\":2880188,\"probabilities\"" +
                ":[{\"awardId\":5,\"awardName\":\"iphone5s\",\"probability\":-111484,\"ruleId\":100001},{\"awardId\":6,\"awardName\":\"苹" +
                "果（Apple） iPad mini MD540CH/A 7.9英寸平板电脑 （16G WiFi+Cellular版）黑色 \",\"probability\":-111484,\"ruleId\":100002},{" +
                "\"awardId\":8,\"awardName\":\"30元充值卡\",\"probability\":658090,\"ruleId\":100003},{\"awardId\":9,\"awardName\":\"50元" +
                "充值卡\",\"probability\":311678,\"ruleId\":100004},{\"awardId\":10,\"awardName\":\"100元充值卡\",\"probability\":314293," +
                "\"ruleId\":100005},{\"awardId\":11,\"awardName\":\"星巴克\",\"probability\":799206,\"ruleId\":100006},{\"awardId\":15," +
                "\"awardName\":\"长隆欢乐世界门票\",\"probability\":799206,\"ruleId\":100007},{\"awardId\":16,\"awardName\":\"哈根达斯消费券" +
                "\",\"probability\":311679,\"ruleId\":100008}],\"win100\":{\"awardId\":14,\"awardName\":\"100沃豆\",\"probability\":0,\"" +
                "ruleId\":100009}},\"expireDays\":15,\"messages\":[],\"resultCode\":0,\"ruleId\":100008,\"success\":true,\"userId\":6565317" +
                "}},\"empty\":false,\"propertyNames\":[\"result\"],\"success\":true}";

        System.out.println(JsonUtils.parseResponse(JSON));
    }
}
