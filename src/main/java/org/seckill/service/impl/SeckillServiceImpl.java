package org.seckill.service.impl;

import java.util.Date;
import java.util.List;

import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExcution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

@Service
public class SeckillServiceImpl implements SeckillService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	// MD5加密盐值
	private final String slat = "fczabc1234hrau734bjhad!@#$33jfd";

	@Autowired
	private SeckillDao seckillDao;

	@Autowired
	private SuccessKilledDao successKilledDao;

	@Override
	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 4);
	}

	@Override
	public Seckill getById(long seckillId) {
		return seckillDao.queryById(seckillId);
	}

	@Override
	public Exposer exportSeckillUrl(long seckillId) {
		Seckill seckill = seckillDao.queryById(seckillId);
		if (seckill == null) {
			return new Exposer(false, seckillId);
		}
		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		Date now = new Date();
		if (now.getTime() < startTime.getTime() || now.getTime() > endTime.getTime()) {
			return new Exposer(false, seckillId, now.getTime(), startTime.getTime(), endTime.getTime());
		}

		String md5 = getMD5(seckillId);

		return new Exposer(true, md5, seckillId);
	}

	private String getMD5(long seckillId) {
		String base = seckillId + "/" + slat;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}

	@Override
	@Transactional
	public SeckillExcution excuteSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, SeckillCloseException, RepeatKillException {
		if (md5 == null || !md5.equals(getMD5(seckillId))) {
			throw new SeckillException("Seckill data rewrite");
		}

		Date now = new Date();
		int updateCount = seckillDao.reduceNumber(seckillId, now);
		try {
			if (updateCount <= 0) {
				throw new SeckillCloseException("Seckill is closed");
			} else {
				int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
				if (insertCount <= 0) {
					throw new RepeatKillException("Seckill repeat");
				} else {
					SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
					return new SeckillExcution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
				}

			}
		} catch (SeckillCloseException e1) {
			throw e1;
		} catch (RepeatKillException e2) {
			throw e2;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			// 将编译器异常装换成运行期异常（事务能回滚运行期异常）;
			throw new SeckillException("Seckill inner error" + e.getMessage());
		}

	}

}
