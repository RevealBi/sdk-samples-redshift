using Reveal.Sdk;
using Reveal.Sdk.Data;
using Reveal.Sdk.Data.Amazon.Redshift;

namespace RevealSdk.Server.Reveal
{
    internal class DataSourceProvider : IRVDataSourceProvider
    {
        public Task<RVDashboardDataSource> ChangeDataSourceAsync(IRVUserContext userContext, RVDashboardDataSource dataSource)
        {
            if (dataSource is RVRedshiftDataSource SqlDs)
            {
                Console.WriteLine("host: " + (string)userContext.Properties["Host"]);
                SqlDs.Host = (string)userContext.Properties["Host"];
                SqlDs.Database = (string)userContext.Properties["Database"];
            }
            return Task.FromResult(dataSource);
        }

        public Task<RVDataSourceItem>? ChangeDataSourceItemAsync(IRVUserContext userContext, string dashboardId, RVDataSourceItem dataSourceItem)
        {
            // ****
            // Every request for data passes thru changeDataSourceItem
            // You can set query properties based on the incoming requests
            // for example, you can check:
            // - dsi.id
            // - dsi.table
            // - dsi.procedure
            // - dsi.title
            // and take a specific action on the dsi as this request is processed
            // ****

            if (dataSourceItem is not RVRedshiftDataSourceItem sqlDsi) return Task.FromResult(dataSourceItem);

            // Ensure data source is updated
            ChangeDataSourceAsync(userContext, sqlDsi.DataSource);

            // Get the UserContext properties
            string customerId = userContext.UserId;
            string? orderId = userContext.Properties["OrderId"]?.ToString();
            bool isAdmin = userContext.Properties["Role"]?.ToString() == "Admin";

            // Get filterTables from userContext properties
            var filterTables = userContext.Properties["FilterTables"] as string[] ?? Array.Empty<string>();

            // Execute query based on the incoming client request
            switch (sqlDsi.Id)
            {
                // Example of how to use a view in Redshift
                case "TenMostExpensiveProducts":
                    sqlDsi.CustomQuery = "Select * from ten_most_expensive_products";
                    break;

                // Example of how to use a view with parameter in Redshift
                case "CustOrderHist":
                    sqlDsi.CustomQuery = "select * from cust_order_hist";
                    break;

                // Example of how to use a standard SQL statement with parameter
                case "CustOrdersOrders":
                    sqlDsi.CustomQuery = $@"SELECT OrderID, OrderDate, RequiredDate, ShippedDate FROM Orders
                                WHERE CustomerID = '{customerId}' ORDER BY OrderID";
                    break;

                // Example of an ad-hoc-query
                case "CustomerOrders":
                    sqlDsi.CustomQuery = $"SELECT * FROM orders WHERE orderId = '{orderId}'";
                    break;

                default:
                    // Check for general table access logic
                    if (filterTables.Contains(sqlDsi.Table))
                    {
                        if (isAdmin)
                        {
                            sqlDsi.CustomQuery = $"SELECT * FROM [{sqlDsi.Table}]";
                        }
                        else
                        {
                            sqlDsi.CustomQuery = $"SELECT * FROM [{sqlDsi.Table}] WHERE customerid = '{customerId}'";
                        }
                    }
                break; 
            }
            return Task.FromResult(dataSourceItem);
        }
    }
}