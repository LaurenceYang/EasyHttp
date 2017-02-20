package com.yang.easyhttp.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by yuan on 07/12/2016.
 */

public class EasyIOUtils {

    /**
     * 关闭流
     * @param closeables io
     */
    public static void close(Closeable... closeables) {
        for (Closeable io : closeables) {
            if (io != null) {
                try {
                    io.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
