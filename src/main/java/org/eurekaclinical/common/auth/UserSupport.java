package org.eurekaclinical.common.auth;

/*
 * #%L
 * Eureka Common
 * %%
 * Copyright (C) 2012 - 2014 Emory University
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
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.eurekaclinical.common.comm.User;
import org.eurekaclinical.standardapis.entity.UserEntity;
import org.jasig.cas.client.authentication.AttributePrincipal;

/**
 *
 * @author Andrew Post
 */
public interface UserSupport<E extends UserEntity, U extends User> {

    AttributePrincipal getUserPrincipal(HttpServletRequest request);

    Map<String, Object> getUserPrincipalAttributes(HttpServletRequest request);

    boolean isSameUser(HttpServletRequest servletRequest, U user);

    boolean isSameUser(HttpServletRequest servletRequest, E user);

    boolean isSameUser(HttpServletRequest servletRequest, String username);

}
