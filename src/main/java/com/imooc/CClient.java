package com.imooc;

import java.io.IOException;

/**
 * @author ZEXIN HUANG
 * @version 1.0
 * @date 2020-05-02 23:43
 */
public class CClient {

    public static void main(String[] args) throws IOException {
        new NioClient().start("CClient");
    }
}
