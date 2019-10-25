package com.yb.cheung.modules.sys.oauth2;

import com.yb.cheung.common.utils.Sha256Hash;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class MyPasswordEncoder implements PasswordEncoder {

    @Override
    public boolean upgradeEncoding(String encodedPassword) {
        return false;
    }

    @Override
    public String encode(CharSequence charSequence) {
        return charSequence.toString();
    }

    @Override
    public boolean matches(CharSequence formpsw, String pswSalt) {
        String s[] = pswSalt.split(",");
        String psw = s[0];
        String salt = s[1];
        formpsw = new Sha256Hash(formpsw.toString(),salt).toHex();
        return psw.equals(formpsw.toString());
    }
}
