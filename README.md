# bjond-fitbit-adapter
This adapter will allow the bjönd rule engine to respond to fitbit events.

## Environment Variabes

The following environment variables will need to be set in your development environment.

```bash
# Fitbit adapter db info.
export FITBIT_POSTGRESQL_SERVER=localhost
export FITBIT_ADAPTER_POSTGRESQL_DB_USERNAME=bjondhealth
export FITBIT_ADAPTER_POSTGRESQL_DB_PASSWORD=bjondhealth
export FITBIT_ADAPTER_APP_NAME=fitbit-adapter
export FITBIT_ADAPTER_POSTGRESQL_DB_HOST=localhost
export FITBIT_ADAPTER_POSTGRESQL_DB_PORT=5432
export FITBIT_ADAPTER_PUBLIC_URL=http://localhost:8080
export FITBIT_ADAPTER_PORT=8080

# JWT for Fitbit
export FITBIT_BJOND_ADAPTER_SUBJECT=[Configured in Bjönd UI]
export FITBIT_BJOND_ADAPTER_AUDIENCE=[Configured in Bjönd UI]
export FITBIT_BJOND_SERVER_ENCRYPTION_KEY=[Assigned Automatically in Bjönd UI]
```

Note- if you are using OS X and are running the server in eclipse, you need to also set these using launctl; this makes the variables available to processes spawned from the desktop, nit the bash shell.

That would be:

```bash
# Fitbit adapter db info.
launchctl setenv FITBIT_POSTGRESQL_SERVER localhost
launchctl setenv FITBIT_ADAPTER_POSTGRESQL_DB_USERNAME bjondhealth
launchctl setenv FITBIT_ADAPTER_POSTGRESQL_DB_PASSWORD bjondhealth
launchctl setenv FITBIT_ADAPTER_APP_NAME fitbit-adapter
launchctl setenv FITBIT_ADAPTER_POSTGRESQL_DB_HOST localhost
launchctl setenv FITBIT_ADAPTER_POSTGRESQL_DB_PORT 5432
launchctl setenv FITBIT_ADAPTER_PUBLIC_URL http://localhost:8080
launchctl setenv FITBIT_ADAPTER_PORT 8080

# JWT for Fitbit
launchctl setenv FITBIT_BJOND_ADAPTER_SUBJECT [Configured in Bjönd UI]
launchctl setenv FITBIT_BJOND_ADAPTER_AUDIENCE [Configured in Bjönd UI]
launchctl setenv FITBIT_BJOND_SERVER_ENCRYPTION_KEY [Assigned Automatically in Bjönd UI]
```

You can set these in the terminal, but you will need to restart Ecipse for them to be available after changing any of these.

## Database

You must set up a PostgreSQL database named 'fitbit-adapter'. 

```bash
createdb -U bjondhealth fitbit-adapter
```

Once you have the variables set and ready, you will need to make sure you add the connection into to your standalone.xml. Add the following to the 'datasources' section under 

```xml
<subsystem xmlns="urn:jboss:domain:datasources:3.0">
```

```xml
<datasource jta="true" jndi-name="java:jboss/datasources/FitBitAdapterDS" pool-name="FitBitAdapterDS" enabled="true" use-java-context="true" use-ccm="true">
    <connection-url>jdbc:postgresql://${env.FITBIT_ADAPTER_POSTGRESQL_DB_HOST}:${env.FITBIT_ADAPTER_POSTGRESQL_DB_PORT}/${env.FITBIT_ADAPTER_APP_NAME}</connection-url>
    <driver>postgresql</driver>
    <new-connection-sql>select 1</new-connection-sql>
    <pool>
        <min-pool-size>5</min-pool-size>
        <max-pool-size>10</max-pool-size>
        <prefill>true</prefill>
        <flush-strategy>IdleConnections</flush-strategy>
    </pool>
    <security>
        <user-name>${env.FITBIT_ADAPTER_POSTGRESQL_DB_USERNAME}</user-name>
        <password>${env.FITBIT_ADAPTER_POSTGRESQL_DB_PASSWORD}</password>
    </security>
    <validation>
        <check-valid-connection-sql>SELECT 1</check-valid-connection-sql>
        <background-validation>true</background-validation>
    </validation>
    <statement>
        <prepared-statement-cache-size>100</prepared-statement-cache-size>
        <share-prepared-statements>true</share-prepared-statements>
    </statement>
</datasource>
```

At the root of the project, issue the following command to update your database schema:

```bash
gradle flywayMigrate
```

## Build

Run the following command to build the adapter. A war file will be generated inside the builds/lib directory- you can deploy it to your WildFly server just as you would core.

```bash
gradle
```

You can run unit tests by running the following. Make sure you stop your local WildFly server beforehand- this tests runs WildFly inside an Arquillian container.

```bash
gradle test
```

## Register the Adapter

When you have the adapter war started up, and you have your local Bjönd server started, run the following URL in your browser to register it with Bjönd.

```html
http://localhost:8080/bjond-fitbit-adapter/services/adapter/registerwithbjond?server=http://localhost:8080
```

Now you are connected with the server and should be able to use and debug the adapter.
