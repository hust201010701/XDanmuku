package com.orzangleli.douyu.message;


import com.orzangleli.douyu.utils.Format;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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
public class DyMsg {


    /**
     * 转换成斗鱼规定的格式,固定请求头为小端
     */
    public static byte[] getBytes(String data) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);

        try {
            bout.reset();
            //写入斗鱼协议报文头，前两个32位字都是数据报的长度
            dout.write(Format.toLH(data.length() + 8), 0, 4);
            dout.write(Format.toLH(data.length() + 8), 0, 4);
            //写入固定请求字段类型码为689
            int a = 689;
            dout.write(Format.toLH(a), 0, 2);
            //加密字段
            dout.writeByte(0);
            //保留字段
            dout.writeByte(0);
            dout.writeBytes(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bout.toByteArray();
    }

    /**
     * 解析是否成功
     * @param response
     */
    public static boolean isLoginSuccess(byte[] response){
        boolean isSuccess = false;

        if (response.length <= 12){
            return isSuccess;
        }

        String responseStr = new String(response, 0, response.length);

        if (responseStr.contains("type@=loginres")){
            isSuccess = true;
        }
        return isSuccess;
    }
}
