package demo;

import com.hyd.appserver.MinaAppServer;
import com.hyd.appserver.authentication.BasicAuthenticator;
import com.hyd.appserver.core.ActionStatistics;
import com.hyd.appserver.core.ServerStatistics;

import java.util.List;

/**
 * Mina App Server 服务器使用示例
 *
 * @author yiding.he
 */
public class Server {

    public static void main(String[] args) throws Exception {
        // startServerManually();
        // startServerAutomatically();
        overrideListenIp();
    }

    private static void overrideListenIp() throws Exception {
        DefaultServerMainMain.main(new String[]{"-ip", "127.0.0.1"});
    }

    private static void startServerAutomatically() throws Exception {
        DefaultServerMainMain.main(new String[]{});
    }

    private static void startServerManually() {
        // 创建一个基于用户名/密码的 Authenticator 鉴权器
        BasicAuthenticator authenticator = new BasicAuthenticator();
        authenticator.putKeyMapping("username", "password");

        // 创建一个 server 示例
        MinaAppServer server = new MinaAppServer(8090);
        server.setActionPackages("demo.actions");

        // 设置服务器鉴权
        server.setAuthenticator(authenticator);

        server.start();
    }

    /////////////////////////////////////////
    
    public static class ShowStaticThread extends Thread {

        private MinaAppServer server;

        public ShowStaticThread(MinaAppServer server) {
            this.server = server;
            setDaemon(true);
        }

        @Override
        public void run() {
            while (true) {

                ServerStatistics stat = this.server.getCore().getServerStatistics();
                List<ActionStatistics> actionStat = stat.getAllActionStatistics();

                for (ActionStatistics actionStatistics : actionStat) {
                    System.out.println(actionStatistics.getActionName() + ":" + actionStatistics.getCounters());
                }

                System.out.println("\n/////////////////////////////////////////\n");

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();  // todo: implement this
                }
            }
        }
    }
}


