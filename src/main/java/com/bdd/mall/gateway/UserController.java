package com.bdd.mall.gateway;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 用户登录页.
     *
     * @return
     */
    @GetMapping("/notLogin")
    public String notLogin() {
        return "用户登录页";
    }

    /**
     * 无权限页面.
     *
     * @return
     */
    @GetMapping("/403")
    public String authDenied() {
        return "403";
    }

    @GetMapping("/logout")
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "用户登录页";
    }

    /**
     * 用户登录.
     *
     * @return
     */
    @GetMapping("/login")
    public String login(HttpServletRequest request) {

        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();

        if (!subject.isAuthenticated()) {

            UsernamePasswordToken token = new UsernamePasswordToken("test", "123456");

            try {

                subject.login(token);

            } catch (UnknownAccountException uae) {

                System.out.println("没有用户名为" + token.getPrincipal() + "的用户");

            } catch (IncorrectCredentialsException ice) {

                System.out.println("用户名为：" + token.getPrincipal() + "的用户密码不正确");

            } catch (LockedAccountException lae) {

                System.out.println("用户名为：" + token.getPrincipal() + "的用户已被冻结");

            } catch (AuthenticationException e) {

                System.out.println("未知错误！");

            }
        }

        logger.debug("sessionId:[{}] role test:{}", subject.getSession().getId(), subject.hasRole("admin"));

        subject.getSession().setAttribute("abc", "中文");

        return "OK";
    }

    @GetMapping("/test")
    public String test() {

        Subject subject = SecurityUtils.getSubject();
        logger.debug("sessionId:[{}] role test:{} session[abc]:{}",
                subject.getSession().getId(), subject.hasRole("admin"), subject.getSession().getAttribute("abc"));

        return "test";
    }

}