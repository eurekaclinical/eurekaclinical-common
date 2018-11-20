package org.eurekaclinical.common.resource;

/*-
 * #%L
 * Eureka! Clinical User Agreement Service
 * %%
 * Copyright (C) 2016 Emory University
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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.eurekaclinical.standardapis.dao.UserDao;
import org.eurekaclinical.common.comm.User;
import org.eurekaclinical.standardapis.entity.RoleEntity;
import org.eurekaclinical.standardapis.entity.UserEntity;

/**
 *
 * @author Andrew Post
 * @param <U> a user type.
 * @param <E> a user entity type.
 * @param <R> a role entity type.
 */
public abstract class AbstractUserResource<U extends User, E extends UserEntity<R>, R extends RoleEntity> extends AbstractNamedReadWriteResource<E, U> {

    public AbstractUserResource(UserDao<R, E> inUserDao) {
        super(inUserDao);
    }

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    public U getCurrent(@Context HttpServletRequest req) {
        return getByName(req.getRemoteUser(), req);
    }

    @Override
    protected boolean isAuthorizedComm(U commObj, HttpServletRequest req) {
        return req.getRemoteUser().equals(commObj.getUsername());
    }

    @Override
    protected boolean isAuthorizedEntity(E entity, HttpServletRequest req) {
        return req.getRemoteUser().equals(entity.getUsername());
    }

}
