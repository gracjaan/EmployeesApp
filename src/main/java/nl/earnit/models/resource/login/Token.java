package nl.earnit.models.resource.login;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The type Token.
 */
@XmlRootElement
public class Token {
    private String token;

    private Long expires;

    /**
     * Instantiates a new Token.
     */
    public Token() {}

    /**
     * Instantiates a new Token.
     *
     * @param token   the token
     * @param expires the expires
     */
    public Token(String token, Long expires) {
        this.token = token;
        this.expires = expires;
    }

    /**
     * Gets token.
     *
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets token.
     *
     * @param token the token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Gets expires.
     *
     * @return the expires
     */
    public Long getExpires() {
        return expires;
    }

    /**
     * Sets expires.
     *
     * @param expires the expires
     */
    public void setExpires(Long expires) {
        this.expires = expires;
    }
}
