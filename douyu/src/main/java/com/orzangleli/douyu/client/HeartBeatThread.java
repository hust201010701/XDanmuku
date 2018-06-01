package com.orzangleli.douyu.client;

/**
 * <p>ProjectInfo:   </p>
 * <p>Description: </p>
 * <p>Create Date: 2017/7/23</p>
 * <p>Copyright  : Genersoft Java Group(c)</p>
 *
 * @author 刘西安(liuxian@inspur.com)
 * @version 1
 */

/* ***********************************************************
 * Modifier  Date    Operate(AMD)   Description
 * 刘西安    2017/7/23     A              新增
 * **********************************************************/
public class HeartBeatThread extends Thread{

    @Override
    public void run() {
        Client client = Client.getInstance();

        while (client.getClientStatus() && !isInterrupted()){
            try {
                System.out.println("发送心跳包");
                client.keepAlive();
                Thread.sleep(45000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
