package org.eurekaclinical.common.config;

import org.eurekaclinical.common.filter.AutoAuthorizationFilter;
import org.eurekaclinical.standardapis.props.CasJerseyEurekaClinicalProperties;

public class ProxyingServiceServletModuleWithAutoAuthorization extends ProxyingServiceServletModule{

	public ProxyingServiceServletModuleWithAutoAuthorization(CasJerseyEurekaClinicalProperties inProperties,
			String inPackageNames) {
		super(inProperties, inPackageNames);
	}
	
	 @Override
	    protected void setupFilters() {
	        super.setupFilters();
	        filter("/*").through(AutoAuthorizationFilter.class);
	    }

}
