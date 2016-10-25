package org.seckill.web;

import java.util.Date;
import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExcution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
//@RequestMapping("/seckill")
public class SeckillController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SeckillService seckillService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Model model) {
		List<Seckill> list = seckillService.getSeckillList();
		model.addAttribute("list", list);
		return "list";
	}

	@RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
	public String detail(@PathVariable("seckillId") Long seckillId, Model model) {

		if (seckillId == null) {
			return "redirect:/seckill/list";
		}

		Seckill seckill = seckillService.getById(seckillId);
		if (seckill == null) {
			// 对比重定向和转发
			return "forward:/seckill/list";
		}

		model.addAttribute("seckill", seckill);
		return "detail";
	}

	@RequestMapping(value = "/{seckillId}/exposer", 
			method = RequestMethod.POST, 
			produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId) {
		try {
			Exposer exposer = seckillService.exportSeckillUrl(seckillId);
			return new SeckillResult<Exposer>(true, exposer);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return new SeckillResult<Exposer>(false, e.getMessage());
		}
	}
	
	@RequestMapping(value = "/{seckillId}/{md5}/execution", 
			method = RequestMethod.POST, 
			produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public SeckillResult<SeckillExcution> excute(@PathVariable("seckillId") Long seckillId,
												 @PathVariable("md5") String md5,
												 @CookieValue(value = "killPhone", required =false) Long phone){
		if (phone == null) {
			return new SeckillResult<SeckillExcution>(false, "未注册");
		}
		try {
			SeckillExcution seckillExcution = seckillService.excuteSeckill(seckillId, phone, md5);
			return new SeckillResult<SeckillExcution>(true, seckillExcution);
		} catch(RepeatKillException e1){
			SeckillExcution seckillExcution = new SeckillExcution(seckillId, SeckillStateEnum.REPEAT_KILL);
			return new SeckillResult<SeckillExcution>(true, seckillExcution);
		} catch (SeckillCloseException e2) {
			SeckillExcution seckillExcution = new SeckillExcution(seckillId, SeckillStateEnum.END);
			return new SeckillResult<SeckillExcution>(true, seckillExcution);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			SeckillExcution seckillExcution = new SeckillExcution(seckillId, SeckillStateEnum.INNER_ERROR);
			return new SeckillResult<SeckillExcution>(true, seckillExcution);
		}
	}
	
	@RequestMapping(value = "/time/now", method = RequestMethod.GET)
	@ResponseBody
	public SeckillResult<Long> time() {
		Date now = new Date();
		return new SeckillResult<Long>(true, now.getTime());
	}
}
