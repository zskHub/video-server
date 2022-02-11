package com.winnerdt.common.config;


/**
 * mybatis-plus配置
 *
 * @author Mark sunlightcs@gmail.com
 * @since 3.1.0 2018-02-05
 */
//@Configuration
public class MybatisPlusConfig {
    /*
    * 由于mybatis plus使用多数据源的时候会倒是yml配置文件里面的配置失效，
    * 所以使用javaconfig的方式配置，又因为牵涉到动态数据的问题，
    * 所以将有关mybatis plus的相关配置放到了DynamicDataSourceConfig.java中。
    *
    *
    * */

    /**
     * 分页插件
     */
//    @Bean
//    public PaginationInterceptor paginationInterceptor() {
//        return new PaginationInterceptor();
//    }
}
