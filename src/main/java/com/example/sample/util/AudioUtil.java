package com.example.sample.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Arrays;

/**
 * 音频文件工具类
 *
 * @author zhangyonghong
 * @date 2019.6.14
 */
@Slf4j
public class AudioUtil {

    /**
     * wav 音频文件转 pcm 音频文件
     *
     * @param wavPath wav 音频文件路径
     * @param pcmPath pcm 音频文件路径
     */
    public static void wav2Pcm(String wavPath, String pcmPath) throws Exception {
        FileInputStream fis = new FileInputStream(wavPath);
        FileOutputStream fos = new FileOutputStream(pcmPath);
        byte[] wavBytes = inputStreamToBytes(fis);
        byte[] pcmBytes = Arrays.copyOfRange(wavBytes, 44, wavBytes.length);
        fos.write(pcmBytes);
        fis.close();
        fos.close();
    }

    /**
     * 输入流转字节数组
     *
     * @param fis 输入流
     * @return 字节数组
     */
    private static byte[] inputStreamToBytes(FileInputStream fis) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 8];
        int len;
        while ((len = fis.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        byte[] data = baos.toByteArray();
        baos.close();
        return data;
    }

    /**
     * pcm 音频文件转 wav 音频文件
     *
     * @param pcmPath pcm 音频文件路径
     * @param wavPath wav 音频文件路径
     */
    public static void pcm2Wav(String pcmPath, String wavPath) throws Exception {
        File pcmFile = new File(pcmPath);
        int dataLength = (int) pcmFile.length();

        int fileLength = dataLength + (44 - 8);
        short channels = 1;
        // short channels = 2;
        // int sampleRate = 8000;
        int sampleRate = 16000;
        // int sampleRate = 24000;
        // int sampleRate = 44100;
        short bitsPerSample = 16;
        short blockAlign = (short) (channels * bitsPerSample / 8);
        int avgBytesPerSec = blockAlign * sampleRate;
        WavHeader header = new WavHeader(fileLength, channels, sampleRate, avgBytesPerSec,
                blockAlign, bitsPerSample, dataLength);

        byte[] headerBytes = header.getBytes();
        FileOutputStream fos = new FileOutputStream(wavPath);
        fos.write(headerBytes, 0, headerBytes.length);
        FileInputStream fis = new FileInputStream(pcmFile);
        byte[] buf = new byte[1024 * 8];
        int length;
        while ((length = fis.read(buf)) != -1) {
            fos.write(buf, 0, length);
        }
        fis.close();
        fos.close();

        log.debug(">>>>> PCM_2_WAV_DONE");
    }

}
