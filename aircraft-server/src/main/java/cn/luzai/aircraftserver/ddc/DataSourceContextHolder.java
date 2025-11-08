package cn.luzai.aircraftserver.ddc;

/**
 * 数据源上下文持有者（ThreadLocal）
 */
public class DataSourceContextHolder {

    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 设置数据源
     *
     * @param dataSourceName 数据源名称（meta/airline-a/airline-b/airline-c）
     */
    public static void setDataSource(String dataSourceName) {
        CONTEXT_HOLDER.set(dataSourceName);
    }

    /**
     * 获取当前数据源
     *
     * @return 数据源名称
     */
    public static String getDataSource() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 清除数据源
     */
    public static void clearDataSource() {
        CONTEXT_HOLDER.remove();
    }
}
