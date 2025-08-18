# Getting Started with Reveal SDK using Java

You'll find everything you need to learn and implement a Reveal SDK server in this repository. This documents includes the general over of Reveal, links to source code for getting started samples, links to product documentation, support and video training.

## Dependencies

The essential dependencies for a Java application is the Reveal SDK depenency (be sure to use the latest [version](https://help.revealbi.io/web/release-notes/):

```xml
<groupId>com.server</groupId>
<artifactId>reveal</artifactId>
<version>0.0.1-SNAPSHOT</version>
<packaging>war</packaging>
<name>reveal</name>
...
<dependency>
  <groupId>com.infragistics.reveal.sdk</groupId>
  <artifactId>reveal-sdk</artifactId>
  <version>1.7.6</version>
</dependency>
```

## Integrating Reveal

Reveal is integrated into your Java app in the `RevealJerseyConfig.java`:

```java
public void configureReveal(AuthenticationProvider authProvider, 
                           DataSourceProvider dsProvider,
                           DashboardProvider dashboardProvider,
                           UserContextProvider userContextProvider,
                           ObjectFilter objectFilter) {
    RevealEngineInitializer.initialize(new InitializeParameterBuilder()
        .setAuthProvider(authProvider)
        .setDataSourceProvider(dsProvider)
        .setDashboardProvider(dashboardProvider)
        //.setDashboardProvider(new RVDashboardProvider("C:\\Dev-Reveal\\Features\\DOM - Java\\java\\Dashboards"))
        .setUserContextProvider(userContextProvider)
        .setObjectFilter(objectFilter)
        .build());

    for (Class<?> clazz : RevealEngineInitializer.getClassesToRegister()) {
        register(clazz);
    }

    register(CorsFilter.class);
    register(DomController.class);
}
```

In this setup:
- **AddReveal Configuration**: Registers essential services like `AuthenticationProvider` and `DataSourceProvider`, while including optional configurations such as `UserContextProvider`, `ObjectFilterProvider`, and `DashboardProvider` as needed.
- **Data Sources**: Registers the Amazon Redshift connector, which is necessary for Amazon Redshift integrations in Java.  

### Core Server Functions

#### Authentication

Authentication is handled by implementing the `IRVAuthenticationProvider`. A username and password credential are created, and the connection details are stored in the data source provider. The example utilizes an Amazon Redshift instance.

- **[Authentication](https://help.revealbi.io/web/authentication/)**: Detailed documentation on setting up authentication.

```java
@Override
public IRVDataSourceCredential resolveCredentials(IRVUserContext userContext, RVDashboardDataSource dataSource) {
    if (dataSource instanceof RVRedshiftDataSource) {
        return new RVUsernamePasswordDataSourceCredential(redshiftUsername, redshiftPassword);
    }
    return null;
}
```

#### Data Source Provider

The `DataSourceProvider` specifies the location of the database, including host, database name, schema, and port. This information can be retrieved from various sources, such as app settings, Azure Key Vault, or configuration files. The example uses app settings to store these details.

Look for the application.settings to update these properties to connect to your database:

```ini
# Redshift Configuration
REDSHIFT_HOST=
REDSHIFT_DATABASE=
REDSHIFT_USERNAME=
REDSHIFT_PASSWORD=
REDSHIFT_SCHEMA=
```

Once you've updated the config, you can connect to your database.

```java
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
```

#### Data Source Items

Custom data source items can be created, such as parameterized queries and stored procedures. These items are defined in the `DataSourceProvider` and are made accessible to users through a dialog.

- **[Custom Queries](https://help.revealbi.io/web/custom-queries/)**: Steps for adding custom queries to data sources.

```java
public RVDataSourceItem changeDataSourceItem(IRVUserContext userContext, String dashboardsID, RVDataSourceItem dataSourceItem) {
    
    // ****
    // Every request for data passes thru changeDataSourceItem
    // You can set query properties based on the incoming requests
    // for example, you can check:
    // - dsi.getId()
    // - dsi.getTable()
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
            String safeCustomerId = customerId != null ? customerId.replace("'", "''") : "";
            redshiftDsi.setCustomQuery("SELECT OrderID, OrderDate, RequiredDate, ShippedDate FROM Orders WHERE CustomerID = '" + safeCustomerId + "' ORDER BY OrderID");
            break;

        // Example of an ad-hoc-query
        case "CustomerOrders":
            String safeOrderId = orderId != null ? orderId.replace("'", "''") : "";
            String customQuery = "SELECT * FROM orders WHERE orderId = '" + safeOrderId + "'";
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
```

### Optional, but Important Server Functions

#### Object Filter

The `ObjectFilter` controls the data access permissions for users. It has a `Filter` function that can be customized to restrict data visibility based on user roles or other criteria. The example demonstrates a scenario where users with the "user" role can only access "All Orders" and "Invoices" data.

- **[Object Filter](https://github.com/RevealBi/sdk-samples-redshift/blob/main/server/java/src/main/java/com/server/reveal/ObjectFilter.java)**: Examples of filtering data objects.

#### User Context

The `UserContext` provides information about the logged-in user. It can be used to store default properties like `UserID` or other custom properties defined in the `UserContextProvider`. The `GetUserContext` method is used to retrieve the user context.

- **[User Context](https://github.com/RevealBi/sdk-samples-redshift/blob/main/server/java/src/main/java/com/server/reveal/UserContextProvider.java)**: Explanation of how to utilize the user context.

#### Dashboard Provider

The `DashboardProvider` enables customization of dashboard saving behavior. It can be used to determine the save location based on the user's context, like saving to different folders or databases.

## Setting up the Client

### HTML Client Setup

The HTML client requires three dependencies: jQuery, JS, and the Reveal JavaScript library. These can be accessed locally or through a CDN. The client code specifies the server URL and a callback function that handles user interaction.

### Loading Dashboards

Dashboards are loaded using the `LoadDashboard` function, which takes the name of the dashboard file as a parameter. In HTML clients, a selector is used to specify where the dashboard should be rendered.

### Additional Headers Provider

The `SetAdditionalHeadersProvider` API allows passing custom headers to the server. These headers can contain information like customer ID or other relevant details.

- **[SetAdditionalHeadersProvider](https://help.revealbi.io/web/user-context/)**: Documentation on using this API for custom headers.

### Adding Custom Menu Items to Visualizations

In Reveal, you can customize the menu that appears on specific visualizations using the `onMenuOpening` event. This can be especially useful for adding custom actions directly accessible to users from visualizations.

- **[Custom Menu Items](https://help.revealbi.io/web/custom-menu-items/)**: Instructions for adding custom menu items to visualizations.

### Using the Reveal SDK DOM

The `Reveal.SDK.DOM` library, currently in beta, provides a typed view of dashboards. It allows easy access to dashboard properties, such as file name and title.

- **[Reveal SDK DOM](https://github.com/RevealBi/Reveal.Sdk.Dom)**: Library for accessing dashboard properties.

#### Dashboard Titles vs. File Names

The dashboard title displayed to the user can differ from the underlying file name. The `DashboardsThumbnail` and `DashboardsNames` APIs are used to retrieve both the title and file name, ensuring consistency in user experience.

## Video Training

Explore these video resources to help you set up and configure Reveal BI Server.  This step-by-step is using Databricks, the difference will be data source type, for example, `RVRedshiftDataSource` vs. `RVDatabricksDataSource`.

- [Overview of a Java Spring Boot Server](https://youtu.be/lSKaXMfqCXI)

## Licensing

A trial license key is valid for 30 days and can be extended upon request. When a license is purchased, the key is valid for the duration of the contract. It's important to keep track of the license expiry date to avoid disruptions. The license key can be set in code, configuration files, or the home directory.

## Resources

The following resources are available to help with the PoC:

- **[Documentation](https://help.revealbi.io/web/)**: Comprehensive documentation covering installation, licensing, and various features.
- **[GitHub](https://github.com/RevealBi/sdk-samples-javascript)**: The Reveal BI GitHub repository contains SDK samples, issue tracking for bug reports and feature requests, and discussions for community support.
- **[Support via Discord Channel](https://discord.gg/reveal)**: A Discord channel dedicated to Reveal provides direct interaction with the product team.
- **[Support via GitHub Discussions](https://github.com/RevealBi/Reveal.Sdk/discussions)**: A GitHub channel dedicated to Reveal provides direct interaction with the product team. Usually, you'd use this if you can't access Discord due to corporate policy.
- **[YouTube Channel](https://www.youtube.com/@RevealBI/videos)**: Webinars and videos covering various aspects of Reveal are available on the YouTube channel.
- **[JavaScript API](https://help.revealbi.io/api/javascript/latest/)**: Reveal offers a comprehensive JavaScript API that allows customization of almost every aspect of the dashboard, including visualization chooser, editing modes, and adding custom elements.
- **[Developer Playground](https://help.revealbi.io/playground/)**: An interactive playground to experiment with Reveal BI's features.
- **[Add Feature Requests, Bug Reports, or Review Open Issues](https://github.com/RevealBi/Reveal.Sdk/issues)**: Reveal's GitHub repository where you can review, add, or comment on new or existing issues.
