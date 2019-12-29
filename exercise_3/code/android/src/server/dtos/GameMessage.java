package server.dtos;

import android.util.Log;

import java.util.Map;
import org.jose4j.json.JsonUtil;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

public class GameMessage {
    private String type, authentication, gameId;
    private Status status;
    private final String jsonJWTKeys = "{\"kty\":\"RSA\",\"kid\":\"49a889c0-5728-4a98-9d18-3fc2b2a961bc\",\"n\":\"p7WzX1dMQcjBw4PF1T6hcbQlj-rSCeX_Q41Jr8MRe-n48OEh419k0IaO6x7yFcA4y_6ZaZSm0wqw0Rv5mvG0GjrOBwKjpP_I6S5t1eoP-iU7TklUXwdeOhCTNfaR4VGkf7erfGk9bk3z76i1QjD9V_CBbpqX4zquuGKu0JBU9ywvDlvDewSyneCGP8OOeJ8hDv8BXI8NG0-fxYfFlDni3ToJsAE995TawKZvgg2DbiMiGS9A4nMXzEZbKUOvN7obBgz45EpCInFj7RmviU6i1PVZYeHUkB1Hn7RkuIbVmdXPfRxAfhqzN1G6eBOmw_dBMJg-T2Y2l8lkhD4U8sSXDQ\",\"e\":\"AQAB\"}";

    public GameMessage(String type, String authentication, Status status, String gameId) {
        this.type = type;
        this.authentication = authentication;
        this.status = status;
        this.gameId = gameId;
    }

    public String generateAuthenticationJWT() {
        try {
            Map<String, Object> jwtMap = JsonUtil.parseJson(jsonJWTKeys);
            RsaJsonWebKey rsaJsonWebKey = null;
            rsaJsonWebKey = new RsaJsonWebKey(jwtMap);
            JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                    .setRequireExpirationTime()
                    .setAllowedClockSkewInSeconds(30)
                    .setRequireSubject()
                    .setExpectedIssuer("SwLabEx3IdP")
                    .setExpectedAudience("GameImpl")
                    .setVerificationKey(rsaJsonWebKey.getKey())
                    .setJwsAlgorithmConstraints(
                            AlgorithmConstraints.ConstraintType.WHITELIST, AlgorithmIdentifiers.RSA_USING_SHA256)
                    .build();
        } catch (JoseException ex) {
            Log.e(this.getClass().getSimpleName(), "JoseException occured while generating a JWT: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAuthentication() {
        return authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public enum Status {
        OK, FAILED
    }
}