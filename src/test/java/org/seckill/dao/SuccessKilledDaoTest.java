package org.seckill.dao;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

	@Resource
	private SuccessKilledDao successKilledDao;
	
	@Test
	public void testInsertSuccessKilled() {
		int result = successKilledDao.insertSuccessKilled(1000L, 15051648239L);
		System.out.println(result);
	}
	
	@Test
	public void testQueryByIdWithSeckill() {
		
	}
}
