package com.jiaruiblog.util;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

/**
 * @Author Jarrett Luo
 * @Date 2022/11/29 17:16
 * @Version 1.0
 */
public class JavaSerializable
{
    /**
     * 持久化为XML对象
     *
     * @param obj Object
     * @param out OutputStream
     */
    public void storeXml(Object obj, OutputStream out)
    {
        XMLEncoder encoder = new XMLEncoder(out);
        encoder.writeObject(obj);
        encoder.flush();
        encoder.close();
    }

    /**
     * 从XML中加载对象
     *
     * @param in InputStream
     * @return Object
     */
    public Object loadXml(InputStream in)
    {
        XMLDecoder decoder = new XMLDecoder(in);
        Object obj = decoder.readObject();
        decoder.close();
        return obj;
    }

    /**
     * 持久化对象
     *
     * @param obj Object Object
     * @param out OutputStream OutputStream
     * @throws IOException -> IOException
     */
    public void store(Object obj, OutputStream out) throws IOException {
        ObjectOutputStream outputStream = new ObjectOutputStream(out);
        outputStream.writeObject(obj);
        outputStream.flush();
        outputStream.close();
    }

    /**
     * 加载对象
     *
     * @param in InputStream
     * @return Object
     * @throws IOException IOException
     * @throws ClassNotFoundException ClassNotFoundException
     */
    public Object load(InputStream in) throws IOException, ClassNotFoundException
    {
        ObjectInputStream inputStream = new ObjectInputStream(in);
        Object obj = inputStream.readObject();
        inputStream.close();
        return obj;
    }

    public static void main(String[] args) throws Exception
    {
        String storeName = "java object";
        File xmlFile = new File("xmlFile.dat");
        JavaSerializable serializable = new JavaSerializable();
        serializable.storeXml(storeName, new FileOutputStream(xmlFile));

        File file = new File("file.dat");
        serializable.store(storeName, new FileOutputStream(file));

    }
}