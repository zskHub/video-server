package com.winnerdt.common.utils.base64ToMultipartFile;

import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.Base64.Decoder;

/**
 * @author:zsk
 * @CreateTime:2019-02-13 16:42
 */
public class Base64MultipartFileUtil {

    public static MultipartFile base64ToMultipart(String base64) {
        try {
            String[] baseStrs = base64.split(",");

            Decoder decoder = Base64.getDecoder();
            byte[] b = new byte[0];
            b = decoder.decode(baseStrs[1]);

            for(int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }

            return new BASE64DecodedMultipartFile(b, baseStrs[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
