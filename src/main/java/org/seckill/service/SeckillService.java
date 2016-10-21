package org.seckill.service;

import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExcution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

public interface SeckillService {

	/**
	 * 查询所有的秒杀记录
	 * @return
	 */
	List<Seckill> getSeckillList();
	
	/**
	 * 查询单个秒杀记录
	 * @param seckillId
	 * @return
	 */
	Seckill getById(long seckillId);
	
	/**
	 * 秒杀开启时，输出秒杀接口地址
	 * 
	 * @param seckillId
	 */
	Exposer exportSeckillUrl(long seckillId);
	
	SeckillExcution excuteSeckill(long seckillId,long userPhone, String md5)
		throws SeckillException,SeckillCloseException,RepeatKillException;
}
