package com.gupaoedu.demo.dao;

import com.gupaoedu.demo.entity.SysLog;
import com.gupaoedu.orm.BaseDaoSupport;
import com.gupaoedu.orm.QueryRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class SysLogDao extends BaseDaoSupport<SysLog, String> {

    @Autowired
    private void setDataSource(DataSource dataSource) {
        super.setReadonlyDataSource(dataSource);
        super.setWriteDataSource(dataSource);
    }

    protected SysLogDao() throws Exception {
    }

    public List<SysLog> selectList(String type) {
        QueryRule queryRule = new QueryRule();
        queryRule.andEqual("type", type);
        return super.select(queryRule);
    }
}
