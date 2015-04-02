package com.hyd.utilities;

import java.util.HashMap;
import java.util.Map;

/**
 * 当需要创建 Map 对象做参数时，用 Parameter 更方便。下面是一个例子：
 * <pre>
 *     Map&lt;String, Object&gt; loginParam = new Parameter()
 *             .setParameter("username", user)
 *             .setParameter("password", pass)
 *             .setParameter("captcha", captcha);
 *
 *     loginService.login(loginParam);
 * </pre>
 *
 * @author 贺一丁
 */
public class Parameter extends HashMap<String, Object> {

    public Parameter(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public Parameter(int initialCapacity) {
        super(initialCapacity);
    }

    public Parameter() {
    }

    public Parameter(Map<String, ?> m) {
        super(m);
    }

    public Parameter setParameter(String key, Object value) {
        put(key, value);
        return this;
    }
}
