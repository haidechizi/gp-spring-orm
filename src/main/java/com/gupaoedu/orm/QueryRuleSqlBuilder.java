package com.gupaoedu.orm;

import java.util.List;

public class QueryRuleSqlBuilder {

    private Object[] values;

    private String whereSql;

    public QueryRuleSqlBuilder(QueryRule queryRule) {
        buildWhereSql(queryRule);
    }

    private final void buildWhereSql(QueryRule queryRule) {
        List<QueryRule.Rule> rules = queryRule.getRules();

        if (rules == null || rules.size() == 0) {
            return ;
        }
        values = new Object[rules.size()];
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (QueryRule.Rule rule : rules) {
            sb.append(rule.getAndOr()).append(" ").append(rule.getParamName())
                    .append(" ").append(rule.getType()).append(" ?");
            this.values[i] = rule.getParamValue();
        }
        String condition = sb.toString();
        if (condition.startsWith(QueryRule.AND + " ")) {
            condition = condition.substring(4);
        }

        if (condition.startsWith(QueryRule.OR + " ")) {
            condition = condition.substring(3);
        }
        if (condition != "") {
            condition = "where " + condition;
        }
        this.whereSql = condition;
    }

    public String getWhereSql() {
        return whereSql;
    }

    public Object[] getValues() {
        return values;
    }
}
