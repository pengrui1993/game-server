package test.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

public class TestJwt {
    public static void main(String[] args) throws InterruptedException {
//        System.out.println(algorithm);
        Class<JWTCreator> jwtCreatorClass = JWTCreator.class;
        byte[] key = "hello".getBytes(StandardCharsets.UTF_8);
        String jwtToken = JWT.create()
                .withIssuer("Baeldung")
                .withSubject("Baeldung Details")
                .withClaim("userId", "1234")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 5000L))
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis() + 1000L))
//                .sign(Algorithm.HMAC256(key))
                .sign(Algorithm.none())
                ;
        System.out.println(jwtToken);
        Thread.sleep(2000);
        DecodedJWT verify = JWT.require(Algorithm.none()).build().verify(jwtToken);
        System.out.println(verify.getClaims());

//        DecodedJWT decode = JWT.decode(jwtToken);
//        System.out.println(decode.getHeader());
//        System.out.println(decode.getPayload());
//        System.out.println(decode.getSignature());
//        System.out.println(JWT.decode(jwtToken).getClaims());
    }
}
