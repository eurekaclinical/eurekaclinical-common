package org.eurekaclinical.common.filter;

/*-
 * #%L
 * Eureka! Clinical Common
 * %%
 * Copyright (C) 2016 - 2018 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import java.io.IOException;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.Filter;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.eurekaclinical.standardapis.dao.UserDao;

import org.eurekaclinical.standardapis.dao.UserTemplateDao;
import org.eurekaclinical.standardapis.entity.RoleEntity;
import org.eurekaclinical.standardapis.entity.UserTemplateEntity;
import org.jasig.cas.client.authentication.AttributePrincipal;

/**
 * Implements auto-authorization. This filter checks if a user record exists for
 * the current user. If there is not, if there is an auto-authorization 
 * user template, and if the user's attributes math any attribute constraints
 * that are specified in the template, it will create a user record with
 * the roles and other settings that are specified in the template.
 * 
 * The following types are injected into the constructor and require bindings:
 * * UserTemplateDao&lt;? extends RoleEntity, ?&gt;
 * * UserDao&lt;? extends UserEntity&lt;? extends RoleEntity&gt;&gt;
 * 
 * @author Andrew Post
 */
@Singleton
public class AutoAuthorizationFilter implements Filter {

    private final UserTemplateDao<?, ?> userTemplateDao;
    private final AutoAuthCriteriaParser AUTO_AUTH_CRITERIA_PARSER = new AutoAuthCriteriaParser();
    private final UserDao<?, ?> userDao;

    @Inject
    public AutoAuthorizationFilter(UserTemplateDao<?, ?> inUserTemplateDao, UserDao<?, ?> inUserDao) {
        this.userTemplateDao = inUserTemplateDao;
        this.userDao = inUserDao;
    }

    /**
     * Initializes the filter.
     * 
     * @param filterConfig filter configuration.
     * @throws javax.servlet.ServletException if an error occurred during 
     * initialization.
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        AttributePrincipal userPrincipal = (AttributePrincipal) servletRequest.getUserPrincipal();
        HttpSession session = servletRequest.getSession(false);
        if (userPrincipal != null && session != null) {
            String[] roleNames;
            synchronized (session) {
                roleNames = (String[]) session.getAttribute("roles");
                if (roleNames == null) {
                    //User Not Found
                    createUser(servletRequest.getRemoteUser(), userPrincipal.getAttributes());
                    chain.doFilter(request, response);
                }
            }
            chain.doFilter(request, response);
        } else {
            //throw new Exception
        }
    }

    /**
     * Cleans up resources that were created by the filter.
     */
    @Override
    public void destroy() {

    }
    
    private void createUser(String username, Map<String, Object> attributes) {
        UserTemplateEntity autoAuthorizationTemplate = this.userTemplateDao.getAutoAuthorizationTemplate();
        try {
            if (username != null && autoAuthorizationTemplate != null && AUTO_AUTH_CRITERIA_PARSER.parse(autoAuthorizationTemplate.getCriteria(), attributes)) {
                this.userDao.createUser(username, autoAuthorizationTemplate.getRoles());
            }
        } catch (CriteriaParseException ex) {
            // throw new Exception(User Creation error);
        }
    }

}
