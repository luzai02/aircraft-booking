package cn.luzai.aircraftserver.ddc;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源（根据上下文切换数据源）
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        // 从 ThreadLocal 中获取当前线程的数据源标识
        return DataSourceContextHolder.getDataSource();
    }
}
