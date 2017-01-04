package com.hyd.appserver;

import com.hyd.appserver.utils.ClassUtils;
import junit.framework.TestCase;
import org.apache.mina.core.filterchain.IoFilter;

import java.util.List;

/**
 * (description)
 *
 * @author yiding.he
 */
public class ClassUtilsTest extends TestCase {

    public void testGetClasses() throws Exception {
        List<Class<IoFilter>> classes = ClassUtils.findClasses(IoFilter.class, "org.apache.mina.filter");

        for (Class aClass : classes) {
            System.out.println(aClass.getName());
        }
    }
}
