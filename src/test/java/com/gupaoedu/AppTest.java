package com.gupaoedu;

import static org.junit.Assert.assertTrue;

import com.gupaoedu.demo.dao.SysLogDao;
import com.gupaoedu.demo.entity.SysLog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Unit test for simple App.
 */
@ContextConfiguration(locations = "classpath:application.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class AppTest {
    @Autowired
    private SysLogDao sysLogDao;

    /**
     * Rigorous Test :-)
     */
    @Test
    public void test() {
        String type = "2";
        List<SysLog> logs = sysLogDao.selectList(type);
        System.out.println(Arrays.toString(logs.toArray()));
    }

    @Test
    public void insert() {
        SysLog sysLog = new SysLog();
        sysLog.setId(UUID.randomUUID().toString());
        sysLog.setTitle("测试");
        sysLog.setType("1");
        int result = sysLogDao.insert(sysLog);
        System.out.println(result);
    }

    @Test
    public void update() {
        SysLog sysLog = new SysLog();
        String id = "24a4377c-c8a5-40d0-9ed1-8bee9dc7840a";
        sysLog.setId(id);
        sysLog.setTitle("测试update111");
        sysLog.setType("2");
        int result = sysLogDao.update(sysLog);
        System.out.println(result);
    }

    @Test
    public void delete() {
        SysLog sysLog = new SysLog();
        String id = "24a4377c-c8a5-40d0-9ed1-8bee9dc7840a";
        sysLog.setId(id);

        int result = sysLogDao.delete(sysLog);
        System.out.println(result);
    }


}
