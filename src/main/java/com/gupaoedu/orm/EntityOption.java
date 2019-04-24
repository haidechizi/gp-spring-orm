package com.gupaoedu.orm;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityOption<T> {

    private Class<?> clazz = null;

    private String allColumns;

    private String tableName;

    private String id;

    private final RowMapper<T> rowMapper;

    private final Map<String, Field> paramNameMapping;

    private final Map<String, String> paramSqlNameMapping;


    public EntityOption(Class<?> clazz) throws Exception {
        this.clazz = clazz;
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new Exception(clazz + "必须使用Entity注解标注");
        }

        if (!clazz.isAnnotationPresent(Table.class)) {
            throw new Exception(clazz + "必须使用Table注解标注");
        }

        Table table = clazz.getAnnotation(Table.class);

        this.tableName = table.name();
        if ("".equals(tableName.trim())) {
            throw new Exception("table name 为设置");
        }

        paramNameMapping = new HashMap<>(32);

        paramSqlNameMapping = new HashMap<>(32);

        createAllColumns(clazz);


        this.rowMapper = createRowMapper();
    }

    private void createAllColumns(Class<?> clazz) {
        StringBuilder sb = new StringBuilder();
        Field[] fields = clazz.getDeclaredFields();
        int index = 0;
        for (Field field : fields) {
            String paramName = field.getName();
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                if (!"".equals(column.name().trim())) {
                    paramName = column.name();

                }
                paramNameMapping.put(paramName, field);


            } else {
                paramNameMapping.put(field.getName(), field);

            }
            paramSqlNameMapping.put(field.getName(), paramName);
            if (index != 0) {
                sb.append(",");
            }
            if (field.isAnnotationPresent(Id.class)) {
                this.id = field.getName();
            }
            sb.append(paramName).append(" AS \"").append(field.getName()).append("\"");
            index++;
        }
        this.allColumns = sb.toString();


    }

    private RowMapper<T> createRowMapper() {
        return new RowMapper<T>() {
            @Override
            public T mapRow(ResultSet resultSet, int i) throws SQLException {
                try {
                    T t = (T) clazz.newInstance();
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int count = metaData.getColumnCount();
                    for (int j = 1; j <= count; j++) {
                        Object value = resultSet.getObject(j);
                        String columnName = metaData.getColumnName(j);

                        setValue(t, columnName, value);
                    }
                    return t;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    public String getAllColumns() {
        return allColumns;
    }

    public String getTableName() {
        return tableName;
    }

    public String getId() {
        return id;
    }

    private void setValue(T t, String columnName, Object value) throws Exception {
        Field field = this.paramNameMapping.get(columnName);
        if (field == null) {
            throw new Exception("columeName ：" + columnName + " is not exists");
        }
        field.setAccessible(true);
        field.set(t, value);
    }

    public RowMapper<T> getRowMapper() {
        return this.rowMapper;
    }

    public UpdateObject createInsertObject(T t) {
        if (t == null) {
            throw new RuntimeException("object is null");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ").append(this.tableName);
        List<Object> paramValues = new ArrayList<>();

        StringBuilder paramSb = new StringBuilder();

        StringBuilder valueSb = new StringBuilder();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object obj = field.get(t);
                if (obj == null) {
                    continue;

                }
                if (String.class == field.getType()) {
                    if ("".equals(obj)) {
                        continue;
                    }
                }
                paramValues.add(obj);
                paramSb.append(paramSqlNameMapping.get(field.getName())).append(",");
                valueSb.append("?,");

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

        if (paramValues.size() == 0) {
            throw new RuntimeException("对象未设置属性");
        }
        String fieldStr = paramSb.toString();
        fieldStr = fieldStr.substring(0, fieldStr.length() - 1);

        String valueStr = valueSb.toString();
        valueStr = valueStr.substring(0, valueStr.length() - 1);

        sb.append("(").append(fieldStr).append(")").append("values").append("(").append(valueStr).append(")");

        UpdateObject updateObject = new UpdateObject(sb.toString(), paramValues.toArray());

        return updateObject;
    }


    public UpdateObject createUpdateObject(T t) {
        if (t == null) {
            throw new RuntimeException("object is null");
        }

        if (this.id == null || "".equals(this.id.trim())) {
            throw new RuntimeException("id is null");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("update ").append(this.tableName).append(" set ");
        List<Object> paramValues = new ArrayList<>();

        StringBuilder paramSb = new StringBuilder();

        Object idValue = null;

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {

                Object obj = field.get(t);
                if (field.getName().equals(this.id)) {
                    if(obj == null || "".equals(obj)) {
                        throw new RuntimeException("id is null");
                    }
                    idValue = obj;
                    continue;
                }
                if (obj == null) {
                    continue;

                }
                if (String.class == field.getType()) {
                    if ("".equals(obj)) {
                        continue;
                    }
                }
                paramValues.add(obj);
                paramSb.append(paramSqlNameMapping.get(field.getName())).append("=").append("?,");

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

        if (paramValues.size() == 0) {
            throw new RuntimeException("对象未设置属性");
        }
        String fieldStr = paramSb.toString();
        fieldStr = fieldStr.substring(0, fieldStr.length() - 1);


        sb.append(fieldStr).append(" where ").append(this.id).append("=").append("?");
        paramValues.add(idValue);

        UpdateObject updateObject = new UpdateObject(sb.toString(), paramValues.toArray());

        return updateObject;
    }

    public UpdateObject createDeleteObject(T t) {
        if (t == null) {
            throw new RuntimeException("object is null");
        }

        if (this.id == null || "".equals(this.id.trim())) {
            throw new RuntimeException("id is null");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("delete from ").append(this.tableName).append(" where ");
        List<Object> paramValues = new ArrayList<>();


        Object idValue = null;

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {

                Object obj = field.get(t);
                if (field.getName().equals(this.id)) {
                    if(obj == null || "".equals(obj)) {
                        throw new RuntimeException("id is null");
                    }
                    idValue = obj;
                    break;
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

        sb.append(this.id).append(" = ").append("?");
        paramValues.add(idValue);

        UpdateObject updateObject = new UpdateObject(sb.toString(), paramValues.toArray());

        return updateObject;
    }

    public static class UpdateObject {
        private String sql;

        private Object[] objectValue;

        public UpdateObject(String sql, Object[] objectValue) {
            this.sql = sql;
            this.objectValue = objectValue;
        }

        public String getSql() {
            return sql;
        }

        public Object[] getObjectValue() {
            return objectValue;
        }
    }
}
