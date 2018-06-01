package com.orzangleli.douyu.client;


import java.io.IOException;

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
public class GetDmThread extends Thread {

    @Override
    public void run(){
        Client client = Client.getInstance();
        while (client.getClientStatus() && !isInterrupted()){
            try {
                client.getServMsg();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

