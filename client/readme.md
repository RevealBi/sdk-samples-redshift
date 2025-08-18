# Reveal SDK - Getting Started with HTML Clients

This repository provides a simple example of how to get started with the [Reveal SDK](https://www.revealbi.io/) using plain HTML and JavaScript.  

The sample demonstrates how to embed Reveal dashboards into your web application and connect to **Amazon Redshift**.

---

## 🚀 Getting Started

1. Clone or download the [sdk-samples-redshift](https://github.com/RevealBi/sdk-samples-redshift) repository and run one of the servers: [ASP.NET](https://github.com/RevealBi/sdk-samples-redshift/tree/main/server/aspnet), [Java](https://github.com/RevealBi/sdk-samples-redshift/tree/main/server/java), [NodeJS](https://github.com/RevealBi/sdk-samples-redshift/tree/main/server/node-js), or [NodeTS](https://github.com/RevealBi/sdk-samples-redshift/tree/main/server/node-ts).
2. Open `index.html` in your browser (or run with a local web server).
3. Update the base URL in `index.html` to point to your Reveal server:

   ```javascript
   $.ig.RevealSdkSettings.setBaseUrl("http://localhost:5111/");
   ```

You should now see the Reveal view embedded in your page, and if you updated your data source / authentication details in the server, you can create / save dashboards.

<img width="1899" height="877" alt="reveal-load-dashboard" src="https://github.com/user-attachments/assets/6d4a4e94-5fde-4ba2-b96e-ad04c4c4541e" />

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

---

## 🛠️ Requirements

* A running instance of the **Reveal Server** (see Reveal docs for setup)
* Browser that supports ES6 and modern JavaScript
* Local server (optional, but recommended for testing)

---

## 🧑‍💻 Usage Notes

* Update the `setBaseUrl` parameter with the correct URL of your Reveal server.
* Clone this repo and open `index.html` directly, or serve it via a local development server.
* Use the provided links above to explore connecting Reveal with Amazon Redshift and loading dashboards.

---

## 📌 Next Steps

* Experiment with connecting to different data sources.
* Try creating and saving your own dashboards.
* Explore the [Reveal SDK Samples](https://github.com/RevealBi) for more advanced scenarios.
