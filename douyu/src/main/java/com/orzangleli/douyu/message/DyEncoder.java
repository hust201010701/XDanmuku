package com.orzangleli.douyu.message;

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
public class DyEncoder {

    private StringBuffer buffer = new StringBuffer();


    /**
     * 获得协议参数报头
     * @return
     */
    public String getResult(){
        buffer.append('\0');
        return buffer.toString();
    }

    /**
     * 添加协议参数项
     * @param key
     * @param value
     */
    public void addItem(String key, Object value){
        buffer.append(key.replaceAll("/", "@S").replaceAll("@", "@A"));
        buffer.append("@=");

        if (value instanceof String){
            buffer.append(((String) value).replaceAll("/", "@S").replaceAll("@", "@A"));
        }else if (value instanceof Integer){
            buffer.append(value);
        }

        buffer.append("/");
    }
}
