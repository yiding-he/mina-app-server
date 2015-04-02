package com.hyd.appserver;

import com.hyd.appserver.utils.JsonUtils;
import org.junit.Test;

import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * (description)
 *
 * @author yiding.he
 */
public class ResponseTest {

    @Test
    public void testResponse() throws Exception {
        Date now = new Date();

        Response r = new Response();
        r.put("name", "obama");
        r.put("size", 65536);
        r.put("pi", 3.14159265);
        r.put("now", now);
        r.put("values", new Integer[]{1, 2, 3, 4, 5});

        String json = JsonUtils.toJson(r);
        System.out.println("json: " + json);

        Response response = JsonUtils.parse(Response.class, json);

        System.out.println(response.getList("values"));
    }
}
