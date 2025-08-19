
# Getting Started with Reveal SDK using Node.js (TypeScript)

This guide will help you set up and implement a Reveal SDK server using Node.js with TypeScript. It covers dependencies, integration steps, configuration, and links to documentation and support resources.

## Dependencies

The essential dependency for a Node.js TypeScript application is the `reveal-sdk-node` package. Install it using npm:

```sh
npm install reveal-sdk-node express cors dotenv
npm install --save-dev typescript @types/node @types/express
```

## Environment Configuration

Database and server configuration is managed via the `.env` file. Update the following properties to connect to your Amazon Redshift instance:

```ini
REDSHIFT_HOST=your-redshift-host
REDSHIFT_DATABASE=your-database
REDSHIFT_USERNAME=your-username
REDSHIFT_PASSWORD=your-password
REDSHIFT_SCHEMA=your-schema
PORT=5111
```

## Integrating Reveal

Reveal is integrated into your Node.js app in `reveal.ts`:

```ts
import reveal from 'reveal-sdk-node';
import express from 'express';
import cors from 'cors';
import dotenv from 'dotenv';
dotenv.config();

const app = express();
app.use(cors());

const revealOptions = {
  userContextProvider: ...,
  authenticationProvider: ...,
  dataSourceProvider: ...,
  dataSourceItemProvider: ...,
  dataSourceItemFilter: ...,
  dashboardProvider: ...,
  dashboardStorageProvider: ...
};

app.use('/reveal-api/', reveal(revealOptions));
```

### Core Server Functions

#### User Context Provider

Extracts user and request-specific properties from headers and environment variables:

```ts
const userContextProvider = (request) => {
  // Parse headers and set user context properties
  // ...see reveal.ts for details...
};
```

#### Authentication Provider

Provides credentials for Redshift connections:

```ts
const authenticationProvider = async (userContext, dataSource) => {
  // Return credentials from userContext or .env
};
```

#### Data Source Provider

Configures Redshift connection details:

```ts
const dataSourceProvider = async (userContext, dataSource) => {
  // Set host, database, schema from userContext or .env
};
```

#### Data Source Item Provider

Customizes queries and data source items based on user context and request:

```ts
const dataSourceItemProvider = async (userContext, dataSourceItem) => {
  // Set custom queries based on dashboard item and user context
};
```

#### Data Source Item Filter

Controls which tables/data a user can access based on their role:

```ts
const dataSourceItemFilter = async (userContext, item) => {
  // Filter tables for admin/user roles
};
```

#### Dashboard Provider & Storage

Handles loading and saving dashboard files from the server's `dashboards` directory.

## Setting up the Client

### HTML Client Setup

The [HTML client](../../client/index-dsi.html) requires:
- jQuery
- Reveal JavaScript library

Example usage:

```html
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/reveal-sdk@latest/dist/reveal.js"></script>
```

### Loading Dashboards

Dashboards are loaded using the `loadDashboard` function:

```js
$.ig.RVDashboard.loadDashboard("Marketing").then(dashboard => {
    var revealView = new $.ig.RevealView("#revealView");
    revealView.dashboard = dashboard;
});
```

### Additional Headers Provider

Pass custom headers (e.g., user ID, order ID) to the server:

```js
$.ig.RevealSdkSettings.setAdditionalHeadersProvider(function (url) {
    return headers;
});
```

## Resources

- [Documentation](https://help.revealbi.io/web/)
- [GitHub](https://github.com/RevealBi/sdk-samples-javascript)
- [Support via Discord](https://discord.gg/reveal)
- [Support via GitHub Discussions](https://github.com/RevealBi/Reveal.Sdk/discussions)
- [YouTube Channel](https://www.youtube.com/@RevealBI/videos)
- [JavaScript API](https://help.revealbi.io/api/javascript/latest/)
- [Developer Playground](https://help.revealbi.io/playground/)
- [Feature Requests & Issues](https://github.com/RevealBi/Reveal.Sdk/issues)

## Licensing

A trial license key is valid for 30 days. Set the license key in the `revealOptions.settings` or as an environment variable.

---

For more details, see the `reveal.ts` file in this project for implementation specifics.
