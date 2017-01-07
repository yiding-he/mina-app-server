package demo.components;

import com.hyd.appserver.Action;
import com.hyd.appserver.ActionContext;
import com.hyd.appserver.InvocationListener;
import org.springframework.stereotype.Component;

/**
 * (描述)
 *
 * @author 贺一丁
 */
@Component
public class EmptyInvocationListener implements InvocationListener {

    @Override
    public void invocationFinished(ActionContext context) throws Exception {
        Action action = context.getAction();
        if (action != null) {
            System.out.println("接口 " + action.getClass() + " 被执行了。");
            System.out.println("请求：" + context.getRequest());
            System.out.println("结果：" + context.getResponse());
            System.out.println("执行开始时间：" + context.getExecutionStartMillis());
            System.out.println("执行结束时间：" + context.getExecutionEndMillis());
            System.out.println("协议：" + context.getProtocol());
        }
    }
}
