package org.seckill.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExcution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/spring-dao.xml", "classpath:spring/spring-service.xml" })
public class SeckillServiceTest {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SeckillService seckillService;

	@Test
	public void testGetSeckillList() {
		List<Seckill> lists = seckillService.getSeckillList();
		logger.info("lists={}", lists);
	}

	@Test
	public void testGetById() {
		Seckill seckill = seckillService.getById(1000L);
		logger.info("seckill={}", seckill);
	}

	@Test
	public void testSeckillLogic() {
		long seckillId = 1002L;
		Exposer exposer = seckillService.exportSeckillUrl(seckillId);
		logger.info("exposer={}", exposer);
		if (exposer.isExposed()) {
			try {
				long userPhone = 15064826759L;
				SeckillExcution seckillExcution = seckillService.excuteSeckill(seckillId, userPhone, exposer.getMd5());
				logger.info("seckillExcution={}", seckillExcution);

			} catch (RepeatKillException e) {
				logger.error(e.getMessage());
			} catch (SeckillCloseException e) {
				logger.error(e.getMessage());
			}
		} else {
			// 秒杀未开启
			logger.warn("exposer={}", exposer);
		}

	}

}
