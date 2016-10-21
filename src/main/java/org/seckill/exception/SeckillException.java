package org.seckill.exception;

/**
 * 秒杀异常
 * @author Zhanghj @ 2016年10月21日
 *
 */
public class SeckillException extends RuntimeException {
	
	public SeckillException(String message){
		super(message);
	}
	public SeckillException(String message,Throwable cause){
		super(message, cause);
	}
}
