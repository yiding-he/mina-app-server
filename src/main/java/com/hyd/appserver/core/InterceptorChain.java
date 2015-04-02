package com.hyd.appserver.core;

import com.hyd.appserver.Interceptor;

import java.util.ArrayList;

/**
 * 拦截器链，顺序为从外到内，也就是第一个获取的 Interceptor 是最外层
 * 的，最后一个是最内层的；而执行的时候的顺序则相反，先执行最内层（最后面）的。
 *
 * @author yiding.he
 */
public class InterceptorChain extends ArrayList<Interceptor> {

    private int pointer = -1;

    public InterceptorChain(InterceptorChain interceptors) {
        super(interceptors);
    }

    public InterceptorChain() {
    }

    public Interceptor next() {
        if (this.isEmpty()) {
            return null;
        }
        
        pointer++;
        if (pointer < this.size()) {
            return this.get(pointer);
        } else {
            pointer--;
            return null;
        }
    }

    public int getPointer() {
        return pointer;
    }

    public void reset() {
        pointer = -1;
    }
}
