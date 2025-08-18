package com.server.reveal;

import com.infragistics.reveal.sdk.api.IRVUserContext;
import com.infragistics.reveal.sdk.base.RVUserContext;
import com.infragistics.reveal.sdk.rest.RVContainerRequestAwareUserContextProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import jakarta.ws.rs.container.ContainerRequestContext;

@Component
public class UserContextProvider extends RVContainerRequestAwareUserContextProvider {
    
    @Value("${REDSHIFT_HOST}")
    private String redshiftHost;
    
    @Value("${REDSHIFT_DATABASE}")
    private String redshiftDatabase;
    
    @Value("${REDSHIFT_USERNAME}")
    private String redshiftUsername;
    
    @Value("${REDSHIFT_PASSWORD}")
    private String redshiftPassword;
    
    @Value("${REDSHIFT_SCHEMA}")
    private String redshiftSchema;

    @Override
    protected IRVUserContext getUserContext(ContainerRequestContext requestContext) {
        String headerValue = requestContext.getHeaderString("x-header-one");
        String userId = null;
        String orderId = null;

        if (headerValue != null && !headerValue.isEmpty()) {
            String[] pairs = headerValue.split(",");
            for (String pair : pairs) {
                String[] kv = pair.split(":", 2);
                if (kv.length == 2) {
                    String key = kv[0].trim();
                    String value = kv[1].trim();
                    if (key.equalsIgnoreCase("userId")) {
                        userId = value;
                    } else if (key.equalsIgnoreCase("orderId")) {
                        orderId = value;
                    }
                }
            }
        }

        // default to User role
        String role = "User";

        // null is used here just for demo 
        if ("BLONP".equals(userId) || userId == null) {
            role = "Admin";
        }

        String[] filterTables = role.equals("Admin") 
            ? new String[0] 
            : new String[]{"customers", "orders", "order_details"};

        var props = new HashMap<String, Object>();
        props.put("OrderId", orderId != null ? orderId : "");
        props.put("Role", role);
        props.put("Host", redshiftHost);
        props.put("Database", redshiftDatabase);
        props.put("Username", redshiftUsername);
        props.put("Password", redshiftPassword);
        props.put("Schema", redshiftSchema);
        props.put("FilterTables", filterTables);

        return new RVUserContext(userId, props); 
    }
}