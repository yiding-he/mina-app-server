package com.hyd.appserver.test;

import com.hyd.appserver.Request;
import com.hyd.appserver.Response;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * (description)
 *
 * @author yiding.he
 */
public class ActionRunnerDemo {

    @Test
    public void testCreate() throws Exception {
        ActionRunner runner = ActionRunner.create("demo.actions");

        Request request = new Request("GetTime");
        Response response = runner.run(request);

        assertFalse(response.isSuccess());

        request.setParameter("time", "201212121212");
        response = runner.run(request);
        assertTrue(response.isSuccess());
    }

    @Test
    public void testCreateWithSpring() throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");
        ActionRunner runner = ActionRunner.createFromSpring(context, "demo.actions");

        Request request = new Request("GetTime");
        Response response = runner.run(request);
        assertFalse(response.isSuccess());

        request.setParameter("time", "201212121212");
        response = runner.run(request);
        assertTrue(response.isSuccess());
    }
}
