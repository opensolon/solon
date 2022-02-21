package com.fujieid.jap.solon.http.controller;

import com.fujieid.jap.core.JapUser;
import com.fujieid.jap.core.context.JapAuthentication;
import com.fujieid.jap.http.adapter.jakarta.JakartaRequestAdapter;
import com.fujieid.jap.http.adapter.jakarta.JakartaResponseAdapter;
import com.fujieid.jap.solon.JapProps;
import com.fujieid.jap.sso.JapMfa;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.DownloadedFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author 颖
 * @author work
 */
public class MfaController extends JapController {

    @Inject
    JapProps japProperties;
    @Inject
    JapMfa japMfa;

    @Get
    @Mapping("/mfa/generate")
    public Object generate(String type, HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException, IllegalAccessException {
        // 校验用户是否登录
        JapUser japUser = JapAuthentication.getUser(
                new JakartaRequestAdapter(request),
                new JakartaResponseAdapter(response)
        );
        if(japUser == null) {
            throw new IllegalAccessException();
        }
        // 根据需求生成不同类型的 QRCode
        switch (type) {
            case "file": {
                File qrcode = this.japMfa.getOtpQrcodeFile(japUser.getUsername(), this.japProperties.getIssuer());
                return new DownloadedFile(
                        "image/png",
                        new FileInputStream(qrcode),
                        qrcode.getName()
                );
            }
            case "base64": {
                return this.japMfa.getOtpQrcodeFileBase64(
                        japUser.getUsername(),
                        this.japProperties.getIssuer(),
                        true
                );
            }
            case "url": {
                return this.japMfa.getOtpQrCodeUrl(
                        japUser.getUsername(),
                        this.japProperties.getIssuer()
                );
            }
            default: {
                this.japMfa.createOtpQrcode(
                        japUser.getUsername(),
                        this.japProperties.getIssuer(),
                        new JakartaResponseAdapter(response)
                );
                return null;
            }
        }
    }

    @Get
    @Mapping("/mfa/verify")
    public Object generate(String username, String secretKey, int otpCode) throws FileNotFoundException {
        if (username != null) {
            return this.japMfa.verifyByUsername(username, otpCode);
        } else if (secretKey != null) {
            return this.japMfa.verifyBySecret(secretKey, otpCode);
        } else {
            throw new IllegalArgumentException();
        }
    }

}
