package demo;

import com.hyd.appserver.MinaAppServer;

/**
 * Mina App Server 服务器使用示例
 *
 * @author yiding.he
 */
public class BatchServer {

    public static void main(String[] args) {
        MinaAppServer server = new MinaAppServer(8090, 1000);
        server.setActionPackages("demo.actions");
        server.start();
    }
}


