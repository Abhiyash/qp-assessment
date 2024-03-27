package app.abhiyash.configurations;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class DatabaseConfigs {

    @Bean
    public DataSource getMSSQLDataSource(){
        DataSourceBuilder MSSQLDataSourceBuilder = DataSourceBuilder.create();
        MSSQLDataSourceBuilder.url("jdbc:sqlserver://localhost:1433;encrypt=true;trustServerCertificate=true;databaseName=GroceryMarketplace");
        MSSQLDataSourceBuilder.username("bookappuser");
        MSSQLDataSourceBuilder.password("bookApp");
        MSSQLDataSourceBuilder.driverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return MSSQLDataSourceBuilder.build();
    }
}
