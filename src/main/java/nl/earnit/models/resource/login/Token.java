package nl.earnit.models.resource.login;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Token {
    private String token;
    private Long expires;

    public Token() {}

    public Token(String token, Long expires) {
        this.token = token;
        this.expires = expires;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getExpires() {
        return expires;
    }

    public void setExpires(Long expires) {
        this.expires = expires;
    }
}
