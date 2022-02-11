package com.winnerdt.common.config;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * mybatis-plus  自定义Sql注入
 * @author zsk
 */
@Component
public class MySqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList() {
        List<AbstractMethod> methodList = super.getMethodList();
        //增加自定义方法
//        methodList.add(new DeleteAll());

        return methodList;
    }
}
