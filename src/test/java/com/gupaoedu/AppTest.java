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
        String type = "1";
        List<SysLog> logs = sysLogDao.selectList(type);
        System.out.println(Arrays.toString(logs.toArray()));
    }
}
