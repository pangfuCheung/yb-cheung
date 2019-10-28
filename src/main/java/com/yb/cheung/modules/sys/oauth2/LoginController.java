package com.yb.cheung.modules.sys.oauth2;

import com.yb.cheung.common.utils.R;
import com.yb.cheung.modules.sys.service.SysCaptchaService;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;

@RestController
@Api(description = "登录接口")
public class LoginController {

    @Autowired
    private SysCaptchaService sysCaptchaService;

    @RequestMapping("/unlogin")
    public void  unlogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write(R.unLogin().toJSONString());
        out.flush();
        out.close();
        //没有登录
        response.setStatus(401);
    }

    /**
     * 验证码
     */
    @ApiOperation(value = "获取验证码",httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid",value = "用户账号" ,required = true , dataType = "String",paramType = "query")
    })
    @GetMapping(value = "/captcha.jpg",produces = MediaType.IMAGE_JPEG_VALUE)
    public void captcha(HttpServletRequest request, HttpServletResponse response,@RequestParam String uuid)throws IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        //获取图片验证码
        BufferedImage image = sysCaptchaService.getCaptcha(uuid);

        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
        IOUtils.closeQuietly(out);
    }

    /**
     * 以下两个方法只是声明实际登录不会进入到这里
     */
    @ApiOperation(value = "登录",httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username",value = "用户账号" ,required = true , dataType = "String" ,paramType = "form"),
            @ApiImplicitParam(name = "password",value = "用户密码" ,required = true , dataType = "String" ,paramType = "form"),
            @ApiImplicitParam(name = "uuid",value = "随机id" ,required = true , dataType = "String" ,paramType = "form"),
            @ApiImplicitParam(name = "captcha",value = "用户密码" ,required = true , dataType = "String" ,paramType = "form")
    })
    @RequestMapping("/login")
    public void login(){
    }

    @ApiOperation(value = "退出登录",httpMethod = "GET")
    @RequestMapping("/logout")
    public void logout(){
    }

}
