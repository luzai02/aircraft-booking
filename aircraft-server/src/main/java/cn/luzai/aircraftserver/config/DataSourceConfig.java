package cn.luzai.aircraftserver.config;



import cn.luzai.aircraftserver.ddc.DynamicDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;


import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 多数据源配置
 */
@Configuration
// 扫描 Mapper 接口所在的包，并指定 SqlSessionFactory，以便 MyBatis 能够正确地找到和使用这些接口
@MapperScan(basePackages = "cn.luzai.aircraftserver.mapper", sqlSessionFactoryRef = "sqlSessionFactory")
public class DataSourceConfig {

    @Bean(name = "metaDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.meta")
    @Primary
    public DataSource metaDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }


    @Bean(name = "airlineADataSource")
    @ConfigurationProperties(prefix = "spring.datasource.airline-a")
    public DataSource airlineADataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }


    @Bean(name = "airlineBDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.airline-b")
    public DataSource airlineBDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }


    @Bean(name = "airlineCDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.airline-c")
    public DataSource airlineCDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    // 动态数据源（整合所有数据源）
    @Bean(name = "dynamicDataSource")
    public DataSource dynamicDataSource(
            @Qualifier("metaDataSource") DataSource metaDataSource,
            @Qualifier("airlineADataSource") DataSource airlineADataSource,
            @Qualifier("airlineBDataSource") DataSource airlineBDataSource,
            @Qualifier("airlineCDataSource") DataSource airlineCDataSource) {

        DynamicDataSource dynamicDataSource = new DynamicDataSource();

        // 设置默认数据源（元数据库）
        dynamicDataSource.setDefaultTargetDataSource(metaDataSource);

        // 使用map来存放多个数据源
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("meta", metaDataSource);
        targetDataSources.put("airline-a", airlineADataSource);
        targetDataSources.put("airline-b", airlineBDataSource);
        targetDataSources.put("airline-c", airlineCDataSource);

        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.afterPropertiesSet();

        return dynamicDataSource;
    }


    // todo 没理解？？？
    // 整合动态数据源，并为 MyBatis 提供一个配置完善的 SqlSessionFactory
    @Bean(name = "sqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dynamicDataSource") DataSource dynamicDataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dynamicDataSource);

        // 设置 Mapper XML 文件位置
        sessionFactory.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath:mapper/**/*.xml")
        );

        // 设置实体类别名包
        sessionFactory.setTypeAliasesPackage("cn.luzai.aircraftpojo.entity");

        // MyBatis 配置
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCacheEnabled(true);
        configuration.setLazyLoadingEnabled(true);
        sessionFactory.setConfiguration(configuration);

        return sessionFactory.getObject();
    }


    // 事务管理器
    @Bean(name = "transactionManager")
    @Primary
    public DataSourceTransactionManager transactionManager(@Qualifier("dynamicDataSource") DataSource dynamicDataSource) {
        return new DataSourceTransactionManager(dynamicDataSource);
    }
}
