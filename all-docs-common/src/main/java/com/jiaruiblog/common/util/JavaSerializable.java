package com.jiaruiblog.common.util;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

/**
 * @author Jarrett Luo
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
        try (XMLEncoder encoder = new XMLEncoder(out)) {
            encoder.writeObject(obj);
            encoder.flush();
        }
    }

    /**
     * 从XML中加载对象
     *
     * @param in InputStream
     * @return Object
     */
    public Object loadXml(InputStream in)
    {
        try (XMLDecoder decoder = new XMLDecoder(in)) {
            return decoder.readObject();
        }
    }

    /**
     * 持久化对象
     *
     * @param obj Object Object
     * @param out OutputStream OutputStream
     * @throws IOException -> IOException
     */
    public void store(Object obj, OutputStream out) throws IOException {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(out)) {
            outputStream.writeObject(obj);
            outputStream.flush();
        }
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
        try (ObjectInputStream inputStream = new ObjectInputStream(in)) {
            return inputStream.readObject();
        }
    }

    public static void main(String[] args) throws Exception
    {
        String storeName = "java object";
        File xmlFile = new File("xmlFile.dat");
        JavaSerializable serializable = new JavaSerializable();
        try (FileOutputStream xmlOut = new FileOutputStream(xmlFile)) {
            serializable.storeXml(storeName, xmlOut);
        }

        File file = new File("file.dat");
        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            serializable.store(storeName, fileOut);
        }

    }
}