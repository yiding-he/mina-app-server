package demo;

import com.hyd.appserver.MinaAppServer;

/**
 * (description)
 *
 * @author yiding.he
 */
public class MostSimpleServer {

    public static void main(String[] args) {
        System.out.println(MostSimpleServer.class.getClassLoader().getClass().getName());
        
        new MinaAppServer(8090).start();
    }
}
