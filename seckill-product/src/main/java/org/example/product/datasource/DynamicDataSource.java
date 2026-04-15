package org.example.product.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        String ds = DataSourceContextHolder.get();
        System.out.println("=====> 当前数据源: " + (ds == null ? "master(默认)" : ds));
        return ds;
    }
}