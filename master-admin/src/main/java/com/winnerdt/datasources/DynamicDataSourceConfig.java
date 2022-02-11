package com.winnerdt.datasources;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.config.GlobalConfig.DbConfig;
import com.baomidou.mybatisplus.extension.injector.LogicSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置多数据源
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017/8/19 0:41
 */
@Configuration
public class DynamicDataSourceConfig {

    @Value("${spring.profiles.active:#{null}}")
    private String operatingEnvironment;

    @Bean(name = "first")
    @ConfigurationProperties("spring.datasource.druid.first")
    public DataSource firstDataSource(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "second")
    @ConfigurationProperties("spring.datasource.druid.second")
    public DataSource secondDataSource(){
        return DruidDataSourceBuilder.create().build();
    }


    @Bean
    @Primary
    public DynamicDataSource dataSource(DataSource firstDataSource, DataSource secondDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceNames.FIRST, firstDataSource);
        targetDataSources.put(DataSourceNames.SECOND, secondDataSource);
        return new DynamicDataSource(firstDataSource, targetDataSources);
    }

    /*
    * -------------------------下面是mybatis plus相关的东西-------------------------------------
    * */
    /**
     * 分页拦截器
     *
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    // 创建全局配置
    @Bean
    public GlobalConfig GlobalConfig() {
        // 全局配置文件
        GlobalConfig globalConfig = new GlobalConfig();
        DbConfig dbConfig = new DbConfig();
        // 默认为自增
        dbConfig.setIdType(IdType.AUTO);
        // 手动指定db 的类型, 这里是mysql
        dbConfig.setDbType(DbType.SQL_SERVER);
        globalConfig.setDbConfig(dbConfig);

        // 逻辑删除注入器
        LogicSqlInjector injector = new LogicSqlInjector();
        globalConfig.setSqlInjector(injector);
        return globalConfig;
    }
    @Bean("sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        //***导入MybatisSqlSession配置***
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        //指明数据源
        sqlSessionFactory.setDataSource(dataSource(firstDataSource(), secondDataSource()));
        //指明mapper.xml位置(配置文件中指明的xml位置会失效用此方式代替，具体原因未知)
        sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/**/*.xml"));
        //指明实体扫描(多个package用逗号或者分号分隔)
        sqlSessionFactory.setTypeAliasesPackage("com.winnerdt.modules.*.entity");

        //***导入Mybatis配置***
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCacheEnabled(false);
        sqlSessionFactory.setConfiguration(configuration);
        sqlSessionFactory.setPlugins(new Interceptor[]{paginationInterceptor()});

        //***导入全局配置***
        sqlSessionFactory.setGlobalConfig(GlobalConfig());
        return sqlSessionFactory.getObject();
    }
}
