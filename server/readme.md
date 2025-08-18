# Reveal SDK - Getting Started with Reveal SDK Server Platforms

This repository provides getting started samples connected to an **Amazon Redshift** database using the [Reveal SDK](https://www.revealbi.io/).  

---

## 🚀 Getting Started

1. Clone or download the [sdk-samples-redshift](https://github.com/RevealBi/sdk-samples-redshift) repository.
2. Run one of the server samples:  
   - [ASP.NET](https://github.com/RevealBi/sdk-samples-redshift/tree/main/server/aspnet)  
   - [Java](https://github.com/RevealBi/sdk-samples-redshift/tree/main/server/java)  
   - [NodeJS](https://github.com/RevealBi/sdk-samples-redshift/tree/main/server/node-js)  
   - [NodeTS](https://github.com/RevealBi/sdk-samples-redshift/tree/main/server/node-ts)  
3. Open `index.html` in your browser (or run with a local web server).  
4. Update the base URL in `index.html` to point to your Reveal server:  
```javascript
   $.ig.RevealSdkSettings.setBaseUrl("http://localhost:5111/");
```

You should now see the Reveal view embedded in your page.
If you updated your data source / authentication details in the server, you can create and save dashboards.

<img width="1899" height="877" alt="reveal-load-dashboard" src="https://github.com/user-attachments/assets/6d4a4e94-5fde-4ba2-b96e-ad04c4c4541e" />

---

## 🗄️ Server Configuration

To connect Reveal to **Amazon Redshift**, update the datasource and authentication settings in the appropriate server configuration file:

* **Node.js / NodeTS** → `.env` file
* **Java** → `application.properties`
* **.NET Core** → `appSettings.json`

Example configuration:

```ini
# Redshift Configuration
REDSHIFT_HOST=
REDSHIFT_DATABASE=
REDSHIFT_USERNAME=
REDSHIFT_PASSWORD=
REDSHIFT_SCHEMA=

# Server Configuration
server.port=5111
```

### Run the server

* **Node.js**

  ```bash
  npm install
  npm start
  # or
  node main
  ```

* **Java**

  ```bash
  .\mvnw spring-boot:run
  ```

* **.NET Core**

  ```bash
  dotnet build && dotnet run
  ```

---

## 📂 Key Sample Scenarios

* **Loading Amazon Redshift Datasource to Create New Dashboards**
  [View Example →](https://github.com/RevealBi/sdk-samples-redshift/blob/main/client/index-ds.html)

* **Loading Amazon Redshift Datasource Items to Create New Dashboards**
  [View Example →](https://github.com/RevealBi/sdk-samples-redshift/blob/main/client/index-dsi.html)

* **Loading an Existing Reveal Dashboard**
  [View Example →](https://github.com/RevealBi/sdk-samples-redshift/blob/main/client/load-dashboard.html)

---

## 📖 Important Documentation

* [Getting Started with Reveal (JavaScript)](https://help.revealbi.io/web/getting-started-javascript/)
* [Creating New Dashboards](https://help.revealbi.io/web/creating-dashboards/)
* [Working with Data Sources](https://help.revealbi.io/web/datasources/)
* [Install Reveal Server SDK](https://help.revealbi.io/web/install-server-sdk/)
* [Authentication](https://help.revealbi.io/web/authentication/)
* [Custom Queries](https://help.revealbi.io/web/custom-queries/)
* [User Context](https://help.revealbi.io/web/user-context/)

---

## 🧑‍💻 Usage Notes

* Update the `setBaseUrl` parameter with the correct URL of your Reveal server.
* Clone this repo and open `index.html` directly, or serve it via a local development server.
* Make sure your server configuration matches your Redshift environment (host, schema, authentication).

---

## 📌 Next Steps

* Experiment with connecting to different data sources.
* Try creating and saving your own dashboards.
* Explore the [Reveal SDK Samples](https://github.com/RevealBi) for more advanced scenarios.

