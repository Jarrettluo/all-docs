package com.jiaruiblog.util.poi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @ClassName Converter
 * @Description 转换的抽象类
 * @author luojiarui
 * @Date 2023/2/22 22:58
 * @Version 1.0
 **/
public abstract class Converter {


    protected InputStream inStream;
    protected OutputStream outStream;

    protected boolean showOutputMessages = false;
    protected boolean closeStreamsWhenComplete = true;

    protected Converter(InputStream inStream, OutputStream outStream,
                     boolean showMessages, boolean closeStreamsWhenComplete){
        this.inStream = inStream;
        this.outStream = outStream;
        this.showOutputMessages = showMessages;
        this.closeStreamsWhenComplete = closeStreamsWhenComplete;
    }

    public abstract void convert() throws Exception;

}
