package com.bdd.mall.gateway.shiro;

import com.google.gson.Gson;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author xing
 * @date 2018-09-25
 */
@Component
public class ShiroRealm extends AuthorizingRealm {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    public ShiroRealm() {
    }

    /**
     * 获取用户信息
     *
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();

        if (username == null) {
            throw new AccountException("Null username are not allowed by this realm.");
        }

        //UserLogin userLogin = new Gson().fromJson(username, UserLogin.class);


//        User userDB = userService.findUserByName(username);
//
//
//        if (userDB == null) {
//            throw new UnknownAccountException("No account found for admin [" + username + "]");
//        }
//
//        //查询用户的角色和权限存到SimpleAuthenticationInfo中，这样在其它地方
//        //SecurityUtils.getSubject().getPrincipal()就能拿出用户的所有信息，包括角色和权限
//        Set<String> roles = roleService.getRolesByUserId(userDB.getUid());
//        Set<String> perms = permService.getPermsByUserId(userDB.getUid());
//        userDB.getRoles().addAll(roles);
//        userDB.getPerms().addAll(perms);
//
//        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(userDB, userDB.getPwd(), getName());
//        if (userDB.getSalt() != null) {
//            info.setCredentialsSalt(ByteSource.Util.bytes(userDB.getSalt()));
//        }

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(username, upToken.getPassword(), getName());

        logger.debug("info:{}", new Gson().toJson(info));


        return info;

    }


    /**
     * 获取用户的角色和权限
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }

        //User user = (User) getAvailablePrincipal(principals);

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addRole("admin");

        logger.debug("check permission. principals:{}", new Gson().toJson(principals));

        return info;
    }

}