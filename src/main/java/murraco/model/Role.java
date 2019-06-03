package murraco.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    /**
     * 管理员
     */
    ROLE_ADMIN,
    /**
     * 游客
     */
    ROLE_GUEST,
    /**
     * 普通用户
     */
    ROLE_CLIENT;

    @Override
    public String getAuthority() {
        return name();
    }

}
