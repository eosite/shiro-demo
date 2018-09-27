package com.bdd.mall.gateway.shiro;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 权限配置
 *
 * @author xing
 * @date 2018-09-25
 */
@ComponentScan(basePackages = {"org.apache.shiro"})
@Configuration
public class ShiroConfig {

    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public ShiroRealm shiroRealm() {
        ShiroRealm shiroRealm = new ShiroRealm();
        return shiroRealm;
    }

    @Bean("shiroRedisTemplate")
    public RedisTemplate<Object, Object> shiroRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new RedisObjectSerializer());
        return template;
    }

    @Bean
    public ShiroRedisCacheManager shiroRedisCacheManager() {
        return new ShiroRedisCacheManager();
    }

    @Bean
    public RedisSessionDao redisSessionDao() {
        return new RedisSessionDao();
    }

    @Bean
    public SessionManager sessionManager(RedisSessionDao redisSessionDao, ShiroRedisCacheManager shiroRedisCacheManager) {
        ShiroWebSessionManager sessionManager = new ShiroWebSessionManager();
        sessionManager.setSessionDAO(redisSessionDao);
        sessionManager.setGlobalSessionTimeout(1800000);
        sessionManager.setCacheManager(shiroRedisCacheManager);

        return sessionManager;
    }

    @Bean
    public SecurityManager securityManager(ShiroRealm shiroRealm, ShiroRedisCacheManager shiroRedisCacheManager, SessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(shiroRealm);
        securityManager.setCacheManager(shiroRedisCacheManager);
        securityManager.setSessionManager(sessionManager);

//        // 关闭shiro自带的session
//        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
//        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
//        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
//        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
//        securityManager.setSubjectDAO(subjectDAO);

        return securityManager;
    }

    /**
     * 设置@RequiresRole注解
     *
     * @return
     */
    @Bean
    public static DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    /**
     * 统一鉴权
     *
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setLoginUrl("/api/v1/user/notLogin");
        shiroFilterFactoryBean.setUnauthorizedUrl("/api/v1/user/403");

        Map<String, String> chain = new LinkedHashMap<>();

        chain.put("/api/v1/user/login", "anon");
        chain.put("/api/v1/user/logout", "anon,logout");

        chain.put("/api/v1/goods/**", "roles[admin]");
        chain.put("/api/**", "authc");

        // chain.addPathDefinition("/api/v1/user", "authc, roles[admin]");


        shiroFilterFactoryBean.setFilterChainDefinitionMap(chain);

        return shiroFilterFactoryBean;
    }

}
