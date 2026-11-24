package com.bijlipay.Dto;


import java.io.*;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class AccountSettlementEncryption {
    private static RSAPrivateKey getRSAPrivateKey(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String strKeyPEM = "";
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            strKeyPEM += line + "\n";
        }
        br.close();
        strKeyPEM = strKeyPEM.
                replaceAll("\\n", "").
                replace("-----BEGIN PRIVATE KEY-----", "").
                replace("-----END PRIVATE KEY-----", "");
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(strKeyPEM));
        RSAPrivateKey privKey = (RSAPrivateKey)kf.generatePrivate(keySpecPKCS8);
        return privKey;
    }

    private static RSAPublicKey getRSAPublicKey(String filename) throws CertificateException, FileNotFoundException {
        CertificateFactory fac = CertificateFactory.getInstance("X509");
        FileInputStream is = new FileInputStream(filename);
        X509Certificate cert = (X509Certificate) fac.generateCertificate(is);
        RSAPublicKey certPub = (RSAPublicKey) cert.getPublicKey();
        return certPub;
    }
}
