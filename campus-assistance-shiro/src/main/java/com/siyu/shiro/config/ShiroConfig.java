package com.siyu.shiro.config;

import com.siyu.shiro.filter.JwtAuthFilter;
import com.siyu.shiro.filter.RoleAuthFilter;
import com.siyu.shiro.realm.JwtRealm;
import com.siyu.shiro.realm.UsernamePasswordRealm;
import org.apache.shiro.authc.pam.FirstSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SessionStorageEvaluator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSessionStorageEvaluator;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.Map;

@Configuration
public class ShiroConfig {

    /**
     * 交由 Spring 来自动地管理 Shiro-Bean 的生命周期
     */
    @Bean
    public static LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }


    /**
     * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions)
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator和AuthorizationAttributeSourceAdvisor)即可实现此功能
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator app = new DefaultAdvisorAutoProxyCreator();
        app.setProxyTargetClass(true);
        return app;
    }

    /**
     * 开启Shiro的注解支持
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }


    /**
     * 结合ShiroFilterFactoryBean注册的filterMap有如下规则
     * noSessionCreation禁用session
     * authcToken使用JwtFilter验证 [permissive]允许token无效时访问
     * anyRole[role1, role2] 只允许被role1或role2访问
     */
    @Bean
    protected ShiroFilterChainDefinition shiroFilterChainDefinition() {

        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        //开放swagger
        chainDefinition.addPathDefinition("/swagger-ui/**", "anon");
        chainDefinition.addPathDefinition("/swagger-resources/**","anon");
        chainDefinition.addPathDefinition("/v3/**","anon");
        //开放knife
        chainDefinition.addPathDefinition("/doc.html","anon");
        chainDefinition.addPathDefinition("/webjars/**","anon");
        chainDefinition.addPathDefinition("/favicon.ico","anon");
        //开放login接口
        chainDefinition.addPathDefinition("/login","noSessionCreation, anon");
        chainDefinition.addPathDefinition("/admin/**", "noSessionCreation, authcToken, anyRole[admin]");
        chainDefinition.addPathDefinition("/**", "noSessionCreation, authcToken");
        return chainDefinition;
    }

    /**
     * 设置过滤器,将自定义的filter加入
     */
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);
        Map<String, Filter> filterMap = factoryBean.getFilters();
        filterMap.put("anyRole", createRolesFilter());
        filterMap.put("authcToken", createAuthFilter());
        factoryBean.setFilters(filterMap);
        factoryBean.setFilterChainDefinitionMap(shiroFilterChainDefinition().getFilterChainMap());
        return factoryBean;
    }

    /**
     * 注册shiro的filter,拦截请求
     */
    @Bean
    public FilterRegistrationBean<Filter> filterRegistrationBean(SecurityManager securityManager) throws Exception{
        FilterRegistrationBean<Filter> filterRegistration = new FilterRegistrationBean<Filter>();
        filterRegistration.setFilter((Filter)shiroFilter(securityManager).getObject());
//        filterRegistration.addInitParameter("targetFilterLifecycle", "true");
//        filterRegistration.setAsyncSupported(true);
//        filterRegistration.setEnabled(true);
//        filterRegistration.setDispatcherTypes(DispatcherType.REQUEST);
        return filterRegistration;
    }

    /**
     * 初始化Authenticator
     */
    @Bean
    public ModularRealmAuthenticator authenticator() {
        ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
        //设置两个Realm，一个用于用户登录验证；一个用于jwt token的认证
        authenticator.setRealms(Arrays.asList(jwtRealm(), usernamePasswordRealm()));
        //设置多个realm认证策略，一个成功即跳过其它的
        authenticator.setAuthenticationStrategy(new FirstSuccessfulStrategy());
        return authenticator;
    }

    /**
     * 禁用session, 不保存用户登录状态。保证每次请求都重新认证。
     * 需要注意的是，如果用户代码里调用Subject.getSession()还是可以用session，如果要完全禁用，要配合下面的noSessionCreation的Filter来实现
     */
    @Bean
    protected SessionStorageEvaluator sessionStorageEvaluator(){
        DefaultWebSessionStorageEvaluator sessionStorageEvaluator = new DefaultWebSessionStorageEvaluator();
        sessionStorageEvaluator.setSessionStorageEnabled(false);
        return sessionStorageEvaluator;
    }

    /**
     * 用于用户名密码登录时认证的realm
     */
    @Bean
    public UsernamePasswordRealm usernamePasswordRealm() {
        UsernamePasswordRealm usernamePasswordRealm = new UsernamePasswordRealm();
        return usernamePasswordRealm;
    }

    /**
     * 用于JWT认证的realm
     */
    @Bean
    public JwtRealm jwtRealm() {
        JwtRealm jwtRealm = new JwtRealm();
        return jwtRealm;
    }


    /**
     * 安全管理器
     * 配置 DefaultWebSecurityManager
     */
    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 1.Authenticator
        securityManager.setAuthenticator(authenticator());
        // 2.Realm
        securityManager.setRealms(Arrays.asList(usernamePasswordRealm(), jwtRealm()));
        // 3.关闭shiro自带的session
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        subjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator());
        securityManager.setSubjectDAO(subjectDAO);

        return securityManager;
    }

    @Bean
    public Authorizer authorizer(){
        return new ModularRealmAuthorizer();
    }

    protected JwtAuthFilter createAuthFilter(){
        return new JwtAuthFilter();
    }

    protected RoleAuthFilter createRolesFilter(){
        return new RoleAuthFilter();
    }
}
