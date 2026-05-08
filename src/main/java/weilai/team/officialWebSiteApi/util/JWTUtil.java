package weilai.team.officialWebSiteApi.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

public class JWTUtil {
    private static final String key = Values.KEY;

    private static final String JWTKey = Values.JWT_KEY;

    /**
     * 此函数使用提供的声明映射生成 JWT 令牌，
     * 使用 AES 算法加密该令牌，并返回加密后的令牌。
     *
     *
     * @return 表示加密的 JWT 令牌的字符串。
     *
     */
    public static String createToken(String value){
        JwtBuilder jwtBuilder = Jwts.builder();
        Long outTime = Values.OUT_TIME;
        String jwtString = jwtBuilder
                    .setSubject(value)
                    .setExpiration(new Date(System.currentTimeMillis() + outTime))
                    .signWith(SignatureAlgorithm.HS256,JWTKey)
                    .compact();
            return encrypt(jwtString);
    }




    /**
     * 此函数从给定的 JWT 令牌中检索和解密声明。
     * 首先使用 {@link #decrypt(String)} 方法解密令牌。
     * 然后，使用解密的令牌解析和检索 JWT 中的声明。
     *
     * @param token 用于检索和解密声明的 JWT 令牌。
     *             它应该是 Base64 编码的字符串。
     *
     * @return 从解密的 JWT 令牌中提取的声明。
     *         声明作为 {@link Claims} 对象返回。
     */
    public static String getInformation(String token){
        String JwtToken = decrypt(token);
        Claims body = Jwts.parser()
                .setSigningKey(JWTKey)
                .parseClaimsJws(JwtToken)
                .getBody();
        return body.getSubject();
    }



    /**
     * 该函数使用 AES 算法对提供的数据进行加密。
     * 加密密钥是从 JWT 类的 'key' 属性派生的。
     *
     * @param data 要加密的数据。它应该是字符串。
     *
     * @return 已加密的数据，作为 Base64 编码的字符串返回。
     *
     * @throws RuntimeException 如果在加密过程中发生错误。
     */
    private static String encrypt(String data){
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(),"AES");
        try {
            Cipher aes = Cipher.getInstance("AES");
            aes.init(Cipher.ENCRYPT_MODE,secretKeySpec);
            byte[] bytes = aes.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            LogUtil.Error("token安全令牌错误！",e);
        }
        return null;
    }

    /**
     * 该函数使用 AES 算法对提供的数据进行解密。
     *
     * @param data 要解密的数据。它应该是 Base64 编码的字符串。
     * @return 已解密的数据，作为字符串返回。
     * @throws RuntimeException 如果在解密过程中发生错误。
     */
    private static String decrypt(String data){
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(data));
            return new String(decryptedBytes);
        } catch (Exception e) {
            LogUtil.Error("token安全令牌错误！",e);
        }
        return null;
    }
}
