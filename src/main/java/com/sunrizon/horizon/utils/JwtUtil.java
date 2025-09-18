package com.sunrizon.horizon.utils;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import cn.hutool.core.date.DateUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;

/**
 * Utility class for creating and managing JSON Web Tokens (JWT) for
 * authentication.
 * <p>
 * This component encapsulates token creation logic using Hutool's JWT library
 * and a shared secret key.
 */
@Component
public class JwtUtil {

  /** Claim key for user email */
  public static final String CLAIM_EMAIL = "email";

  /** Secret key used for signing the JWT tokens (HS256 algorithm). */
  private static final String SECRET_KEY = "daf66e01593f61a15b857cf433aae03a005812b31234e149036bcc8dee755dbb";

  /**
   * Builds a JWT signer instance using the configured secret key.
   *
   * @return {@link JWTSigner} instance configured with HS256 and the secret key
   */
  private JWTSigner getJwtSigner() {
    return JWTSignerUtil.hs256(SECRET_KEY.getBytes());
  }

  /**
   * Creates a signed JWT token (authorization token) for the given
   * authenticated user.
   * <p>
   * The token contains:
   * <ul>
   * <li>Email as the subject</li>
   * <li>Expiration time (current time + 2 hours)</li>
   * </ul>
   *
   * @param authentication Spring Security {@link Authentication} object
   *                       containing the authenticated user details
   * @return A signed JWT string that can be used as an authorization token
   */
  public String createAuthorization(Authentication authentication) {
    return JWT.create()
        .setPayload(CLAIM_EMAIL, authentication.getName())
        .setExpiresAt(DateUtil.offsetHour(DateUtil.date(), 2))
        .sign(getJwtSigner())
        .toString();
  }

  /**
   * Validates both the signature and the expiration date of a JWT token.
   *
   * @param token JWT token
   * @return true if valid; false otherwise
   */
  public boolean validateAuthorization(String authorization) {
    return JWTUtil.verify(authorization, getJwtSigner()) && isNotExpired(authorization);
  }

  /** Validates only the expiration date */
  private boolean isNotExpired(String authorization) {
    try {
      JWTValidator.of(authorization).validateDate(DateUtil.date());
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Extracts the email claim from the token.
   *
   * @param token JWT token
   * @return Email string from token payload
   */
  public String getEmail(String token) {
    return JWT.of(token).getPayload(CLAIM_EMAIL).toString();
  }

}
