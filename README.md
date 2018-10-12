# Simple Sales Management API with Salesmen sales reports

### Setup database

1. Go to `src/main/resources`

```bash
cd src/main/resources
```

2. Copy the configuration file

```bash
cp application.properties.example application.properties
```

3. Edit your `application.properties` file

```properties
# DataSource settings: set here your own configurations for the database 
spring.datasource.url = jdbc:mysql://localhost/[database-name]
spring.datasource.username = [db-username]
spring.datasource.password = [db-password]

# ...
```

Replace `[database-name]`, `[db-username]` and `[db-password]`, respectivelly, by your **database name**, **database user name** and **database password**.

In this case, it's for a MySQL database configuration, with `localhost` host and on the default MySQL port. You can change these configurations on this `spring.datasource.url` URL configuration property value.