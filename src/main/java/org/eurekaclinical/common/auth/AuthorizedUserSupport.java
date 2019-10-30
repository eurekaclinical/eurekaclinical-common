package org.eurekaclinical.common.auth;

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


import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;
import org.eurekaclinical.common.comm.User;
import org.eurekaclinical.standardapis.dao.UserDao;
import org.eurekaclinical.standardapis.entity.RoleEntity;
import org.eurekaclinical.standardapis.entity.UserEntity;
import org.eurekaclinical.standardapis.exception.HttpStatusException;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Andrew Post
 */
public final class AuthorizedUserSupport<E extends UserEntity<? extends RoleEntity>, D extends UserDao<E>, U extends User> extends AbstractUserSupport<E, U> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizedUserSupport.class);

    private final D userDao;

    public AuthorizedUserSupport(D inUserDao) {
        this.userDao = inUserDao;
    }

    @Override
    public boolean isSameUser(HttpServletRequest servletRequest, E user) {
        return isSameUser(servletRequest, user.getUsername());
    }

    /**
     * Returns the user object, or if there isn't one, throws an exception.
     *
     * @param servletRequest the HTTP servlet request.
     * @return the user object.
     *
     * @throws HttpStatusException if the logged-in user isn't in the user
     * table, which means the user is not authorized to use eureka-protempa-etl.
     */
    public E getUser(HttpServletRequest servletRequest) {
        AttributePrincipal principal = getUserPrincipal(servletRequest);
        E result = this.userDao.getByPrincipal(principal);
        if (result == null) {
            throw new HttpStatusException(Status.FORBIDDEN, "User " + principal.getName() + " is not authorized to use this resource");
        }
        return result;
    }
}
