package org.eurekaclinical.common.config;

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

import org.eurekaclinical.common.filter.AutoAuthorizationFilter;
import org.eurekaclinical.standardapis.props.CasJerseyEurekaClinicalProperties;

/**
 * The same as the {@link ServiceServletModule} except it activates the
 * auto authorization filter, which will automatically authorize a user to 
 * access a service if there is a user template with auto authorization 
 * enabled.
 * 
 * @author Andrew Post
 */
public class ServiceServletModuleWithAutoAuthorization extends ServiceServletModule {
    
    public ServiceServletModuleWithAutoAuthorization(CasJerseyEurekaClinicalProperties inProperties, String inPackageNames) {
        super(inProperties, inPackageNames);
    }

    @Override
    protected void setupFilters() {
        super.setupFilters();
        filter("/*").through(AutoAuthorizationFilter.class);
    }
    
    
    
}
