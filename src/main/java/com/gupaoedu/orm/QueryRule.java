package com.gupaoedu.orm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class QueryRule {

    public static final String AND = "and";

    public static final String OR = "or";

    public static final String EQUAL = "=";

    public static final String NOT_EQUAL = "!=";


    private List<Rule> rules = new LinkedList<>();

    public void andEqual(String paramName, Object paramValue) {
        rules.add(new Rule(AND, paramName, paramValue, EQUAL));
    }

    public List<Rule> getRules() {
        return this.rules;
    }

    protected class Rule {
        private String andOr;
        private String paramName;
        private Object paramValue;
        private String type;

        public Rule(String andOr, String paramName, Object paramValue, String type) {
            this.andOr = andOr;
            this.paramName = paramName;
            this.paramValue = paramValue;
            this.type = type;
        }

        public String getAndOr() {
            return andOr;
        }

        public String getParamName() {
            return paramName;
        }

        public Object getParamValue() {
            return paramValue;
        }

        public String getType() {
            return type;
        }
    }
}
