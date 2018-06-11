package com.hyd.appserver.utils.dynamicobj;

import org.junit.Test;

import java.util.Arrays;

/**
 * (描述)
 *
 * @author 贺一丁
 */
public class DynamicObjectTest {

    @Test
    public void test() throws Exception {
        DynamicObject user = new DynamicObject();
        user.put("name", "zhangsan");
        user.put("alive", "true");
        user.put("birthday", "2012-08-24 23:03");
        user.put("likeBooks", Arrays.asList(
                new DynamicObject().append("name", "book1").append("author", "lisi"),
                new DynamicObject().append("name", "book2").append("author", "lisi").append("references", Arrays.asList(
                        new DynamicObject().append("name", "book3").append("author", "lisi")
                ))
        ));

        User u = user.convert(User.class);
        System.out.println(u);
    }
}
