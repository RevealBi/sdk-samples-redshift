package com.server.reveal;

import com.infragistics.reveal.sdk.api.IRVDataSourceProvider;
import com.infragistics.reveal.sdk.api.IRVUserContext;
import com.infragistics.reveal.sdk.api.model.*;
import org.springframework.stereotype.Component;

@Component
public class DataSourceProvider implements IRVDataSourceProvider {
    
    
    public RVDataSourceItem changeDataSourceItem(IRVUserContext userContext, String dashboardsID, RVDataSourceItem dataSourceItem) {
        
        // ****
        // Every request for data passes thru changeDataSourceItem
        // You can set query properties based on the incoming requests
        // for example, you can check:
        // - dsi.getId()
        // - dsi.getTable()
        // - dsi.getProcedure()
        // - dsi.getTitle()
        // and take a specific action on the dsi as this request is processed
        // ****
        
        if (!(dataSourceItem instanceof RVRedshiftDataSourceItem)) {
            return dataSourceItem;
        }

        RVRedshiftDataSourceItem redshiftDsi = (RVRedshiftDataSourceItem) dataSourceItem;

        // Ensure data source is updated
        changeDataSource(userContext, dataSourceItem.getDataSource());

        // Get the UserContext properties
        String customerId = userContext.getUserId();
        String orderId = userContext.getProperties().get("OrderId") != null ? 
            userContext.getProperties().get("OrderId").toString() : null;
        boolean isAdmin = "Admin".equals(userContext.getProperties().get("Role"));

        // Get filterTables from userContext properties
        String[] filterTables = userContext.getProperties().get("FilterTables") instanceof String[] ? 
            (String[]) userContext.getProperties().get("FilterTables") : new String[0];

        // Execute query based on the incoming client request
        switch (redshiftDsi.getId()) {
            // Example of how to use a view in Redshift
            case "TenMostExpensiveProducts":
                redshiftDsi.setCustomQuery("Select * from ten_most_expensive_products");
                break;

            // Example of how to use a view with parameter in Redshift
            case "CustOrderHist":
                redshiftDsi.setCustomQuery("select * from cust_order_hist");
                break;

            // Example of how to use a standard SQL statement with parameter
            case "CustOrdersOrders":
                redshiftDsi.setCustomQuery("SELECT OrderID, OrderDate, RequiredDate, ShippedDate FROM Orders WHERE CustomerID = '" + customerId + "' ORDER BY OrderID");
                break;

            // Example of an ad-hoc-query
            case "CustomerOrders":
                String customQuery = "SELECT * FROM orders WHERE orderId = '" + orderId + "'";
                redshiftDsi.setCustomQuery(customQuery);
                break;

            default:
                // Check for general table access logic
                if (java.util.Arrays.asList(filterTables).contains(redshiftDsi.getTable())) {
                    if (isAdmin) {
                        redshiftDsi.setCustomQuery("SELECT * FROM [" + redshiftDsi.getTable() + "]");
                    } else {
                        redshiftDsi.setCustomQuery("SELECT * FROM [" + redshiftDsi.getTable() + "] WHERE customerid = '" + customerId + "'");
                    }
                }
                break;
        }
        
        return dataSourceItem;
    }

    public RVDashboardDataSource changeDataSource(IRVUserContext userContext, RVDashboardDataSource dataSource) {
        if (dataSource instanceof RVRedshiftDataSource) {
            RVRedshiftDataSource redshiftDataSource = (RVRedshiftDataSource) dataSource;

            String host = (String) userContext.getProperties().get("Host");
            String database = (String) userContext.getProperties().get("Database");
            String schema = (String) userContext.getProperties().get("Schema");

            System.out.println("Redshift Host: " + host);

            redshiftDataSource.setHost(host);
            redshiftDataSource.setDatabase(database);
            redshiftDataSource.setSchema(schema);
        }
        return dataSource;
    }
}