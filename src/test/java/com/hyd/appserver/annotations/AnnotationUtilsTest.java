package com.hyd.appserver.annotations;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * (description)
 *
 * @author yiding.he
 */
public class AnnotationUtilsTest {

    @Test
    public void testGetFunction() throws Exception {
        Function function = AnnotationUtils.getFunction(SubclassOfGetTime.class);
        assertNotNull(function);
    }
}
