package com.individual.serialization.api;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 底层序列化接口
 */
public interface Serialization {


    /**
     * 反序列化
     *
     * @param os serialize
     * @return
     */
    ObjectOutput serialize(OutputStream os) throws IOException;


    /**
     * 序列化
     *
     * @param is
     * @return
     */
    ObjectInput deserilize(InputStream is) throws IOException;
}
