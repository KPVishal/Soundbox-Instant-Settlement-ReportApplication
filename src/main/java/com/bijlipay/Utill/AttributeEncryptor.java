package com.bijlipay.Utill;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HexFormat;




@Service
public class AttributeEncryptor {

//    @Value("${axis_publickey}")
//    public String publickey;
    private final Logger logger = LoggerFactory.getLogger(AttributeEncryptor.class);

    //Axis Nodal Account Encryption and Decryption
    public String decryptedCallBackNodal(String encryptData) throws Exception {
        String reqData = null;
        String keyStr = "X4bO6rMoXJSgAjhA"; // nodal decrypt key
//        String keyStr = "cEWu9yYcr9LyrBMa"; // PRD
        reqData = decrypt(encryptData, keyStr);
        logger.info("Callback DMO: " + reqData);
        return reqData;
    }

    public String decrypt(String encryptedData, String keyStr) throws Exception {
        Key key = generateKey1(keyStr);
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(2, key);
        byte[] decordedValue = java.util.Base64.getDecoder().decode(encryptedData);

        byte[] decValue = c.doFinal(decordedValue);
        return new String(decValue);
    }

    private Key generateKey1(String key) throws Exception {
        byte[] keyValue = key.getBytes();
        return new SecretKeySpec(keyValue, "AES");
    }

//    public static PublicKey getPublicKey(String base64PublicKey) {
//        PublicKey publicKey = null;
//        try {
//            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
//            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//            publicKey = keyFactory.generatePublic(keySpec);
//            return publicKey;
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (InvalidKeySpecException e) {
//            e.printStackTrace();
//        }
//        return publicKey;
//    }

    // QR UPI
    // TODO Encrypt using RSA public key
//    public String encryptMessage(String plainText) throws Exception {
////        System.out.println("PlainText " + plainText);
//        final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publickey));
//        byte[] bytes = cipher.doFinal(plainText.getBytes());
//        String checksum = HexFormat.of().formatHex(bytes);
//        return checksum;
//    }

//    public static String decryptedCallBackResponse(String encryptData) throws Exception {
//        String reqData = null;
////        String keyStr = "LuxgAAKMxzonzOzA"; //UAT
////                String keyStr = "9zQVluiBdJVFnB8C"; //UAT-4096 new
//        String keyStr = "cEWu9yYcr9LyrBMa"; // PRD
//        reqData = .decrypt(encryptData, keyStr);
////        logger.info("Callback : " + reqData);
//        return reqData;
}
