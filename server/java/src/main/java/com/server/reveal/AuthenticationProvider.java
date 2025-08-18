package com.server.reveal;

import com.infragistics.reveal.sdk.api.IRVAuthenticationProvider;
import com.infragistics.reveal.sdk.api.IRVDataSourceCredential;
import com.infragistics.reveal.sdk.api.IRVUserContext;
import com.infragistics.reveal.sdk.api.RVUsernamePasswordDataSourceCredential;
import com.infragistics.reveal.sdk.api.model.RVDashboardDataSource;
import com.infragistics.reveal.sdk.api.model.RVRedshiftDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationProvider implements IRVAuthenticationProvider {
    
    @Value("${REDSHIFT_USERNAME}")
    private String redshiftUsername;
    
    @Value("${REDSHIFT_PASSWORD}")
    private String redshiftPassword;
    
    @Override
    public IRVDataSourceCredential resolveCredentials(IRVUserContext userContext, RVDashboardDataSource dataSource) {
        if (dataSource instanceof RVRedshiftDataSource) {
            return new RVUsernamePasswordDataSourceCredential(redshiftUsername, redshiftPassword);
        }
        return null;
    }
}
