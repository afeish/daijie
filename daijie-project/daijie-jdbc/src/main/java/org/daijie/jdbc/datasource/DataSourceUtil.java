package org.daijie.jdbc.datasource;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Sets;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * 数据源工具类
 * @author daijie
 * @since 2019/5/23
 */
public class DataSourceUtil {

    private static final String SET_METHOD_PREFIX = "set";

    private static Collection<Class<?>> generalClassType;

    static {
        generalClassType = Sets.<Class<?>>newHashSet(boolean.class, Boolean.class, int.class, Integer.class, long.class, Long.class, String.class);
    }

    /**
     * 根据配置创建数据源
     * @param dataSourceClassName 数据源类名称
     * @param dataSourceProperties 配置
     * @return DataSource
     * @throws ReflectiveOperationException 抛出异常
     */
    public static DataSource getDataSource(final String dataSourceClassName, final Map<String, Object> dataSourceProperties) throws ReflectiveOperationException {
        return getDataSource(Class.forName(dataSourceClassName), dataSourceProperties);
    }

    /**
     * 根据配置创建数据源
     * @param dataSourceClass 数据源类
     * @param dataSourceProperties 配置
     * @return DataSource
     * @throws ReflectiveOperationException 抛出异常
     */
    public static DataSource getDataSource(final Class dataSourceClass, final Map<String, Object> dataSourceProperties) throws ReflectiveOperationException {
        DataSource result = (DataSource) dataSourceClass.newInstance();
        for (Map.Entry<String, Object> entry : dataSourceProperties.entrySet()) {
            callSetterMethod(result, getSetterMethodName(entry.getKey()), null == entry.getValue() ? null : entry.getValue().toString());
        }
        return result;
    }

    private static String getSetterMethodName(final String propertyName) {
        if (propertyName.contains("-")) {
            return CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, SET_METHOD_PREFIX + "-" + propertyName);
        }
        return SET_METHOD_PREFIX + String.valueOf(propertyName.charAt(0)).toUpperCase() + propertyName.substring(1, propertyName.length());
    }

    private static void callSetterMethod(final DataSource dataSource, final String methodName, final String setterValue) {
        for (Class<?> each : generalClassType) {
            try {
                Method method = dataSource.getClass().getMethod(methodName, each);
                if (boolean.class == each || Boolean.class == each) {
                    method.invoke(dataSource, Boolean.valueOf(setterValue));
                } else if (int.class == each || Integer.class == each) {
                    method.invoke(dataSource, Integer.parseInt(setterValue));
                } else if (long.class == each || Long.class == each) {
                    method.invoke(dataSource, Long.parseLong(setterValue));
                } else {
                    method.invoke(dataSource, setterValue);
                }
                return;
            } catch (final ReflectiveOperationException ex) {
            }
        }
    }
}
