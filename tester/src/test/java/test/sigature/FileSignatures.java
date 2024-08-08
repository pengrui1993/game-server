package test.sigature;


import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * use browser's api for calc same result
 * see file.html
 *
 * user api to calc the sha1 of file same as result of .sha1
 *
 */
public class FileSignatures {
    static String jar = "mina-core-2.2.3.jar";
    static String dir = "/Users/pengrui/.m2/repository/org/apache/mina/mina-core/2.2.3/";
    static String sha1 = dir+jar+".sha1";
    static String file = dir+jar;
    public static void calc() throws IOException {
//        DigestUtils.class
//        DigestUtils digest = new DigestUtils(MessageDigestAlgorithms.SHA_512);
        DigestUtils digest = new DigestUtils(MessageDigestAlgorithms.SHA_1);
        String s = digest.digestAsHex(new File(file));
        byte[] bytes = Files.readAllBytes(new File(sha1).toPath());
        String sha1FileInfo =new String(bytes);
        if(Objects.equals(sha1FileInfo,s)){
            System.out.println("correct about sha:");
            System.out.println("same result");
        }
        System.out.println(Objects.equals(new String(bytes),s));//2a978175e8775dd2e5bb2c66ce1a0ccef9f49385
    }

    static void test() throws NoSuchAlgorithmException {
        MessageDigest instance = MessageDigest.getInstance("SHA-512");
        System.out.println(instance);
        byte[] digest = instance.digest(file.getBytes(StandardCharsets.UTF_8));
        String s = Hex.encodeHexString(digest);
        String s1 = new DigestUtils(MessageDigestAlgorithms.SHA_512).digestAsHex(file);
        if(Objects.equals(s1,s)){
            System.out.println("same effect");
        }
        System.out.println(s);
    }

    public static void main(String[] args) throws Exception {
        test();
//        calc();
    }
}
