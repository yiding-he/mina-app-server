package com.hyd.appserver;

import com.hyd.appserver.utils.ClassUtils;
import junit.framework.TestCase;
import org.apache.commons.lang.exception.Nestable;

import java.util.List;

/**
 * (description)
 *
 * @author yiding.he
 */
public class ClassUtilsTest extends TestCase {

    public void testGetClasses() throws Exception {
        List<Class<Nestable>> classes = ClassUtils.findClasses(Nestable.class, "org.apache.commons.lang");

        for (Class aClass : classes) {
            System.out.println(aClass.getName());
        }
    }
}
