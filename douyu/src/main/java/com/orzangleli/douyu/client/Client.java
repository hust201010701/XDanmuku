package com.orzangleli.douyu.client;

import android.support.annotation.Nullable;

import com.orzangleli.douyu.message.DMEntity;
import com.orzangleli.douyu.message.DyEncoder;
import com.orzangleli.douyu.message.DyMsg;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

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
public class Client {

    //斗鱼服务器
    public static final String host = "openbarrage.douyutv.com";
    public static final int port = 8601;

    //服务器返回的最大字节数
    public static final int MAX_BUFFER_LEANTH = 4096;

    //因为使用了多线程，所以使用一个变量来判断客户端是否准备好
    private boolean ready = false;

    //Socket
    private Socket sock;
    private BufferedInputStream bim;
    private BufferedOutputStream bom;

    private IReceiveDanmu iReceiveDanmu;

    //单例模式
    public static Client instance;

    private Client() {

    }

    public static Client getInstance() {
        if (null == instance) {
            instance = new Client();
        }
        return instance;
    }

    /**
     * 初始化
     */
    public void init(int roomID, int groupId, @Nullable IReceiveDanmu iReceiveDanmu) throws IOException {
        this.connectServer();
        this.loginRoom(roomID);
        this.joinGroup(roomID, groupId);
        this.ready = true;
        this.iReceiveDanmu = iReceiveDanmu;
    }

    /**
     * 获取客户端状态
     */
    public boolean getClientStatus() {
        return ready;
    }

    /**
     * 连接斗鱼服务器
     */
    public void connectServer() {
        try {
            sock = new Socket(host, port);
            bim = new BufferedInputStream(sock.getInputStream());
            bom = new BufferedOutputStream(sock.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("连接到服务器成功");
    }

    /**
     * 登陆斗鱼房间
     */
    public void loginRoom(int roomId) throws IOException {
        DyEncoder params = new DyEncoder();
        params.addItem("type", "loginreq");
        params.addItem("roomid", roomId);

        byte[] request = DyMsg.getBytes(params.getResult());
        System.out.println("loginroom:" + request.length);
        bom.write(request, 0, request.length);
        bom.flush();
        //读取返回数据判断是否登陆成功
        byte[] recvByte = new byte[MAX_BUFFER_LEANTH];
        bim.read(recvByte, 0, recvByte.length);

        if (DyMsg.isLoginSuccess(recvByte)) {
            System.out.println("登录房间成功");
        } else {
            System.out.println("登录房间失败");
        }


    }

    /**
     * 加入分组
     */
    public void joinGroup(int roomId, int groupId) {
        DyEncoder params = new DyEncoder();
        params.addItem("type", "joingroup");
        params.addItem("rid", roomId);
        params.addItem("gid", groupId);

        byte[] request = DyMsg.getBytes(params.getResult());

        System.out.println(new String(request));

        try {
            bom.write(request, 0, request.length);
            bom.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("加入组成功");
    }

    /**
     * 获得弹幕消息
     */
    public void getServMsg() throws IOException {
        byte[] b = new byte[MAX_BUFFER_LEANTH];

        String msg = null;

        int msgLenth = bim.read(b, 0, b.length);
        byte[] recvByte = new byte[msgLenth];
        //根据实际长度复制数组
        System.arraycopy(b, 0, recvByte, 0, msgLenth);
        if (recvByte.length > 12) {
            msg = new String(recvByte, 12, recvByte.length - 12);
        }

        if (msg != null && msg.contains("type@=")) {
            //弹幕有可能发生粘包情况，http://blog.csdn.net/pi9nc/article/details/17165171
            while (msg.lastIndexOf("type@=") > 5) {
                DMEntity entity = new DMEntity(msg.substring(msg.lastIndexOf("type@=")));
                parseDM(entity.getDMList());
                if (msg.lastIndexOf("type@=") > 12) {
                    msg = msg.substring(0, msg.lastIndexOf("type@=") - 12);
                }
            }
            DMEntity entity = null;
            if (msg.contains("type@=")) {
                entity = new DMEntity(msg.substring(msg.lastIndexOf("type@=")));
            } else {
                entity = new DMEntity(msg);
            }
            parseDM(entity.getDMList());
        }
    }

    /**
     * 处理弹幕消息
     */
    private void parseDM(Map<String, Object> msg) {
        if (msg.get("type") != null){
            //如果包含错误消息, 结束客户端的运行
            if (msg.get("type").equals("error")){
                this.ready = false;
                System.out.println(msg.toString());
            }

            if (msg.get("type").equals("chatmsg")){
                System.out.println(msg.get("nn")+": "+msg.get("txt"));
                if (iReceiveDanmu != null) {
                    iReceiveDanmu.receive((String)msg.get("nn"), (String)msg.get("txt"));
                }
            }
        }
    }

    public void keepAlive() throws IOException {
        //Unix时间戳
        int time = (int) (System.currentTimeMillis() / 1000);

        DyEncoder encoder = new DyEncoder();
        encoder.addItem("type", "keeplive");
        encoder.addItem("tick", time);

        byte[] keepAliveReq = DyMsg.getBytes(encoder.getResult());

        bom.write(keepAliveReq, 0, keepAliveReq.length);
        bom.flush();

        System.out.println("心跳包发送成功");
    }

}
