package com.bijlipay.Config;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import org.apache.el.parser.ParseException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class JWEUtilsForPartners {

    public static String jweEncrypt(JWEAlgorithm alg, EncryptionMethod contentKeyEncMethod, RSAPublicKey publicKey,
                                    String payload) throws JOSEException, NoSuchAlgorithmException {

        // Generate the preset Content Encryption (CEK) key
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(contentKeyEncMethod.cekBitLength());
        SecretKey cek = keyGenerator.generateKey();

        JWEHeader jewHeader = new JWEHeader(alg, contentKeyEncMethod);

        // Encrypt the JWE with the RSA public key + specified AES CEK
        JWEObject jwe = new JWEObject(jewHeader, new Payload(payload));
        jwe.encrypt(new RSAEncrypter(publicKey, cek));
        String jweString = jwe.serialize();
        return jweString;
    }

    /**
     *
     * @return
     * @throws JOSEException
     * @throws ParseException
     */
    public static String jweDecrypt(RSAPrivateKey privateKey, String jweEncryptedPayload)
            throws JOSEException, java.text.ParseException {

        JWEObject jwe = JWEObject.parse(jweEncryptedPayload);
        jwe.decrypt(new RSADecrypter(privateKey));
        String decryptedValue = jwe.getPayload().toString();
        return decryptedValue;
    }

    private static String jwSign(RSAPrivateKey privateKey, String payloadToSign) throws JOSEException {
        // Create RSA-signer with the private key
        JWSSigner signer = new RSASSASigner(privateKey);
        // Prepare JWS object with simple string as payload
        JWSObject jwsObject = new JWSObject(
                new JWSHeader(JWSAlgorithm.RS256),
                new Payload(payloadToSign));

        // Compute the RSA signature
        jwsObject.sign(signer);
        return(jwsObject.serialize());
    }

    private static JWVerifyObject jwSignatureVerify(RSAPublicKey publicKey , String signedPayloadToVerify) throws JOSEException, ParseException, java.text.ParseException {
        JWSObject jwsObject = JWSObject.parse(signedPayloadToVerify);
        JWSVerifier verifier = new RSASSAVerifier(publicKey);
        boolean isSignatureValid =  jwsObject.verify(verifier);
        JWVerifyObject jwverifyObject = new JWVerifyObject();
        jwverifyObject.setSignatureValid(isSignatureValid);
        if(isSignatureValid) {
            jwverifyObject.setPayloadAfterVerification(jwsObject.getPayload().toString());
        }
        return jwverifyObject;
    }





    public static String jweEncryptAndSign(RSAPublicKey publicKeyToEncrypt, RSAPrivateKey privateKeyToSign, String payloadToEncryptAndSign)
            throws NoSuchAlgorithmException, JOSEException {
        JWEAlgorithm alg = JWEAlgorithm.RSA_OAEP_256;
        EncryptionMethod enc = EncryptionMethod.A256GCM;
        String encryptedResult = jweEncrypt(alg, enc, publicKeyToEncrypt, payloadToEncryptAndSign);
        String signedResult = jwSign(privateKeyToSign, encryptedResult);
        return signedResult;
    }

    public static String jweVerifyAndDecrypt(RSAPublicKey publicKeyToVerify, RSAPrivateKey privateKeyToDecrypt,  String payloadToVerifyAndDecrypt)
            throws JOSEException, ParseException, java.text.ParseException {

        JWVerifyObject jwVerifyObject = jwSignatureVerify(publicKeyToVerify, payloadToVerifyAndDecrypt);

        if (!jwVerifyObject.isSignatureValid()) {
            return null;

        } else {
            return jweDecrypt(privateKeyToDecrypt, jwVerifyObject.getPayloadAfterVerification());
        }

    }
}
