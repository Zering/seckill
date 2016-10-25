package org.seckill.dao;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
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
		long seckillId = 1000L;
		long userPhone = 15725684546L;
		SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
		System.out.println(successKilled);
		System.out.println(successKilled.getSeckill());
	}
}
