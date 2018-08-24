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
import org.eurekaclinical.standardapis.entity.UserEntity;
import org.eurekaclinical.standardapis.entity.UserTemplateEntity;
import org.jasig.cas.client.authentication.AttributePrincipal;

@Deprecated
public abstract class AbstractAutoAuthorizationFilter<R extends RoleEntity, U extends UserEntity<R>, T extends UserTemplateEntity<R>> implements Filter {

    private final UserTemplateDao<R, T> userTemplateDao;
    private final UserDao<U> userDao;
    private final AutoAuthCriteriaParser AUTO_AUTH_CRITERIA_PARSER = new AutoAuthCriteriaParser();

    protected AbstractAutoAuthorizationFilter(UserTemplateDao<R, T> inUserTemplateDao,
            UserDao<U> inUserDao) {
        this.userTemplateDao = inUserTemplateDao;
        this.userDao = inUserDao;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        AttributePrincipal userPrincipal = (AttributePrincipal) servletRequest.getUserPrincipal();
        HttpSession session = servletRequest.getSession(false);
        if (userPrincipal != null && session != null) {
            Map<String, Object> attributes = userPrincipal.getAttributes();
            String[] roleNames;
            synchronized (session) {
                roleNames = (String[]) session.getAttribute("roles");
                if (roleNames == null) {
                    //User Not Found
                    String remoteUser = servletRequest.getRemoteUser();
                    T autoAuthorizationTemplate = this.userTemplateDao.getAutoAuthorizationTemplate();
                    try {
                        if (remoteUser != null && autoAuthorizationTemplate != null && AUTO_AUTH_CRITERIA_PARSER.parse(autoAuthorizationTemplate.getCriteria(), attributes)) {
                            //User Creation
                            U user = toUserEntity(autoAuthorizationTemplate, remoteUser);
                            this.userDao.create(user);
                        } else {
                            // throw new Exception(Remote User or Template error);
                        }
                    } catch (Exception ex) {
                        // throw new Exception(User Creation error);
                    }
                    chain.doFilter(request, response);

                }
            }
            chain.doFilter(request, response);
        } else {
            //throw new Exception
        }
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    protected abstract U toUserEntity(T userTemplate, String username);

}
