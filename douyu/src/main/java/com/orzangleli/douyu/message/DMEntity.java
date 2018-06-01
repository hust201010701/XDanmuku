package com.orzangleli.douyu.message;

import java.util.HashMap;
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
public class DMEntity {

    private Map<String, Object> DMList;

    public DMEntity(String data){
        this.DMList = parseResponse(data);
    }

    public Map<String, Object> getDMList(){
        return this.DMList;
    }

    /**
     * 解析服务器返回的弹幕数据
     */
    public Map<String, Object> parseResponse(String data){

        Map<String, Object> rtnMsg = new HashMap<>();

        //删除响应数组中最后一个'\0'
        if (data.contains("/")) {
            String temp = data.substring(0, data.lastIndexOf("/"));
            String[] buff = temp.split("/");

            for (String s : buff){
                int index = s.indexOf("@=");
                if (index != -1) {
                    String key = s.substring(0,index);
                    Object value = s.substring(index+2);
                    rtnMsg.put(key, value);
                }
            }
        }
        return rtnMsg;
    }
}
