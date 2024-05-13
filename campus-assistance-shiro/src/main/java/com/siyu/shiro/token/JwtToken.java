package com.siyu.shiro.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.shiro.authc.HostAuthenticationToken;

@AllArgsConstructor
public class JwtToken implements HostAuthenticationToken {

    @Getter
    private String token;

    private String host;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
