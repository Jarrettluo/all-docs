package com.jiaruiblog.entity;

/**
 * 公用数据返回模型
 * @author jiarui.luo
 */
public class ResponseModel {

    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";

    private String code = "fail";
    private String message = "";
    private String data;

    /**
     * 私有构造函数，此类不允许手动实例化，需要调用getInstance()获取实例
     */
    private ResponseModel() {
    }

    /**
     * 返回默认的实例
     *
     * @return
     */
    public static ResponseModel getInstance() {
        ResponseModel model = new ResponseModel();
        model.setCode(ResponseModel.FAIL);
        return model;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
