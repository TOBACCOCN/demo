package com.example.sample.trans.youdao;

import lombok.extern.slf4j.Slf4j;

import javax.websocket.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Slf4j
public class YoudaoSpeechTranslation {

    // private static Logger logger = LoggerFactory.getLogger(YoudaoSpeechTranslation.class);

    public static void main(String[] args) throws Exception {
        String appKey = "";
        String appSecret = "";
        // String filePath = "D:\\download\\中文英文语音数据\\中文30小时\\wav\\train\\S0002\\BAC009S0002W0123.wav";
        String filePath = "/home/hans/zyh/BAC009S0002W0123.wav";
        String from = "zh-CHS";
        String to = "en";

        recognizeAndTranslate(appKey, appSecret, filePath, from, to);

        // 阻塞线程，不要退出
        Thread.currentThread().join();
    }

    private static void recognizeAndTranslate(String appKey, String appSecret, String filePath, String from, String to) throws NoSuchAlgorithmException, IOException, DeploymentException, InterruptedException {
        String nonce = UUID.randomUUID().toString();
        String curtime = String.valueOf(System.currentTimeMillis() / 1000);
        String sign = encrypt(appKey + nonce + curtime + appSecret);
        String uri = "wss://openapi.youdao.com/stream_speech_trans?appKey=" + appKey + "&salt=" + nonce + "&curtime=" + curtime + "&sign=" + sign + "&version=v1&channel=1&format=wav&signType=v4&rate=16000&from=" + from + "&to=" + to + "&transPattern=stream";

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        Session session = container.connectToServer(YoudaoWebsocket.class, URI.create(uri));
        InputStream inputStream = new FileInputStream(filePath);
        int len;
        byte[] bytes = new byte[8192];
        while ((len = inputStream.read(bytes)) != -1) {
            session.getBasicRemote().sendBinary(ByteBuffer.wrap(bytes, 0, len));
            Thread.sleep(100);
        }
        byte[] closeBytes = "{\"end\": \"true\"}".getBytes();
        session.getBasicRemote().sendBinary(ByteBuffer.wrap(closeBytes));
    }

    /**
     * 获取 MessageDigest 的加密结果
     *
     * @param content 待加密内容
     * @return 加密后内容
     */
    private static String encrypt(String content) throws NoSuchAlgorithmException {
        byte[] bytes = content.getBytes();
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(bytes);
        // to HexString
        return bytes2Hex(md.digest());
    }

    private static String bytes2Hex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        String tmp;
        for (byte b : bytes) {
            tmp = (Integer.toHexString(b & 0xFF));
            if (tmp.length() == 1) {
                builder.append("0");
            }
            builder.append(tmp);
        }
        return builder.toString();
    }

}
