package test.sigature;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class FileSignatures {
    public static void test0() throws IOException {
//        DigestUtils.class
        String jar = "mina-core-2.2.3.jar";
        String dir = "/Users/pengrui/.m2/repository/org/apache/mina/mina-core/2.2.3/";
        String sha1 = dir+jar+".sha1";
        String file = dir+jar;
//        DigestUtils digest = new DigestUtils(MessageDigestAlgorithms.SHA_512);
        DigestUtils digest = new DigestUtils(MessageDigestAlgorithms.SHA_1);
        String s = digest.digestAsHex(new File(file));
        byte[] bytes = Files.readAllBytes(new File(sha1).toPath());
        System.out.println(Objects.equals(new String(bytes),s));//2a978175e8775dd2e5bb2c66ce1a0ccef9f49385
        System.out.println(new DigestUtils(MessageDigestAlgorithms.SHA_512).digestAsHex(file));
    }

    static void test1() throws NoSuchAlgorithmException {
        MessageDigest instance = MessageDigest.getInstance("SHA-512");
        System.out.println(instance);
    }

    public static void main(String[] args) throws Exception {
        test1();
    }
}
