package com.bijlipay.Utill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@Service
public class Aes256CryptUtil {

    private final Logger logger = LoggerFactory.getLogger(Aes256CryptUtil.class);
    Cipher ecipher;
    Cipher dcipher;

    /**
     * Input a string that will be md5 hashed to create the key.
     *
     * @return void, cipher initialized
     */

    public Aes256CryptUtil() {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(256);
            this.setupCrypto(kgen.generateKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Aes256CryptUtil(String key, String algo) {
        SecretKeySpec skey = new SecretKeySpec(getMD5(key), algo);
        this.setupCrypto(skey);
    }

    private void setupCrypto(SecretKey key) {
        // Create an 8-byte initialization vector
        byte[] iv = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f};

        AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
        try {
            ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            // CBC requires an initialization vector
            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Buffer used to transport the bytes from one stream to another
    byte[] buf = new byte[1024];

    /**
     * Input is a string to encrypt.
     *
     * @return a Hex string of the byte array
     */
    public String encrypt(String plaintext) {
        try {
            byte[] ciphertext = ecipher.doFinal(plaintext.getBytes("UTF-8"));
            return this.byteToHex(ciphertext);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Input encrypted String represented in HEX
     *
     * @return a string decrypted in plain text
     */
    public String decrypt(String hexCipherText) {
        try {
            String plaintext = new String(dcipher.doFinal(this.hexToByte(hexCipherText)), "UTF-8");
            return plaintext;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private static byte[] getMD5(String input) {
        try {
            byte[] bytesOfMessage = input.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(bytesOfMessage);
        } catch (Exception e) {
            return null;
        }
    }

    static final String HEXES = "0123456789ABCDEF";

    public static String byteToHex(byte[] raw) {
        if (raw == null) {
            return null;
        }
        String result = "";
        for (int i = 0; i < raw.length; i++) {
            result += Integer.toString((raw[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public static byte[] hexToByte(String hexString) {
        int len = hexString.length();
        byte[] ba = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            ba[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return ba;
    }

//    public static void main(String args[]) {
//
//
//        try {
//            //pass key and algorithm
//            Aes256CryptUtil aescryptutil = new Aes256CryptUtil("X4bO6rMoXJSgAjhA", "AES");
//
//            String plainRequest = "test";
//
//            String encryptedString = aescryptutil.encrypt(plainRequest);
//
//            System.out.println("Encrypted data String: "+encryptedString);
//
//            String decryptedString = aescryptutil.decrypt("6377634de72f29a030c5b7612fb49589fce2698d0ff51cf4d38f47bcc15f6bcb6dba53124399ec20b49505d447eaab161db66f61e5233bb1083f2d24d47bb266a4702e5f1eb3ab041dbaf217803df6f880c7c39133bc6f61dc2b2a0de4f087fa7291257a07b11f259313fb95d0d075413d0e8893e46db659f3024e2f7b9408e2372c5ced7be3fc7056eb5343fa03c535950d91f5a31b4689751066f642c3a16f2e5b0f4cefe938d310ee246699924d96c067bdf0e6e6e2f25c6afc2492b661be2b95b19f7d313d6d39e9af04f4086af1f816b16dc6e495d3ceed25a1be3124c6587e1f08b4bafc184d01c858625566bb7a27f08d6307693a4ac6d88e670b8cd37418a10deede570822fb522fabe93ebfc5fc7dd7509abef54a3091250318777be0fcbed7742e70035bc48bfb82749318790b899398b23714d0070f4eace8b74d8b838197f84522e3d1b9e62472d3ebff1b655e3e5a8941492743b5763734dfa19783bdc1a5a8b4ec61772b3d1a56e70b022e4cf77008fd620ff8acaf3acea19d655103b6f48f111ed90d6b530733b16eaa6b4cc989194733685f6e6559b9b3dac42f8afdbd55f9d872de4612598fe51f220ca0d130a0287e20488ba2af269e56ed7dc28fbb6c4421c6819db93e8da68dbccd539faa01caeec47c7d311e57800d34738f82d2b2cfcbafac025fe24c2506090fa9ad6311825461ec80650ceff4e48762ea89466596aec80ebc2ceb7040984e2c6f03683778cbb17b1c577104e34a5e08994fc446c1324e9417e79551151b71d749af34f695f1b6ea50d3b9c9106e72608b53c819d8ab694719b859cba470e2f59fd5bf20c018c9406bed817520a4038a7c95df1772dac8c66ce9bc85bc8db8be4948589c5028552f88c88367b56338dcc03fad272574985212fa160b5f6c1c647a3802938c4c8a579bba537dcd32b70ea2eacacf02d4f8fa40396b95465b45aba73e644c52c03ce00a4b895f2181627fbe0766dd77bbe501f259926738c9a594fd89640f7bdd438f1b7a0aa799fb0362d7870f9e6fabd185a2486e98eb441b5eab9296e5c85ddc3c5d413ce9bd23c25c7ec4c4973bd83ecdd0b65c2ee43f8662cbe08c20e82bb97822963bd20ffd3eda30ded4029a20231581e6b2d61a85a3b99af8b06100551978e66b5489375a0dddf20439351146a8d5de182d4b1d7e");
//
//            System.out.println("Decrypted String: "+decryptedString);
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//        }
//    }
}