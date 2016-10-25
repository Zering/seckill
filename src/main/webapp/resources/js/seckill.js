var seckill = {
	URL : {
		now : function(){
			return '/seckill/time/now';
		},

		exposer : function(seckillId){
			return '/seckill/'+seckillId+'/exposer';
		},
		
		execution : function(seckillId, md5) {
			return '/seckill/' + seckillId + '/' + md5 + '/execution';
		}
	},
	
	handleSeckillkill : function(seckillId, node) {
		// 获取秒杀地址，控制显示逻辑，执行秒杀
		node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
		console.log('exposerUrl=' + seckill.URL.exposer(seckillId));//TODO
		$.post(seckill.URL.exposer(seckillId), {}, function(result) {
			// 在回调函数中，执行交互流程
			if (result && result['success']) {
				var exposer = result['data'];
				if (exposer['exposed']) {
					// 开启秒杀
					var md5 = exposer['md5'];
					var killUrl = seckill.URL.execution(seckillId, md5);
					console.log('killUrl=' + killUrl);//TODO
					$('#killBtn').one('click', function() {
						// 执行秒杀请求
						// 1.先禁用按钮
						$(this).addClass('disabled');
						// 2.发送秒杀请求
						$.post(killUrl, {}, function(result) {
							if (result && result['success']) {
								var killResult = result['data'];
								var state = killResult['state'];
								var stateInfo = killResult['stateInfo'];
								// 3.显示秒杀结果
								node.html('<span class="label label-success">' + stateInfo + '</span>');
							}
						});
					});
					node.show();
				} else {
					// 未开启秒杀
					var now = exposer['now'];
					var start = exposer['start'];
					var end = exposer['end'];
					// 重新计算计时逻辑
					seckill.countdown(seckillId, now, start, end);
				}
			} else {
				console.log('result=' + result);
			}
		});
	},
	
	countdown : function(seckillId,nowTime,startTime,endTime){
		var seckillBox = $('#seckillBox');
		if(nowTime > endTime){
			seckillBox.html('秒杀结束');
		} else if (nowTime < startTime){
			var killTime = new Date(startTime + 1000);//加一秒防止时间偏移
			seckillBox.countdown(killTime, function(event) {
				var format = event.strftime('秒杀倒计时：%D天 %H时 %M分 %S秒');
				seckillBox.html(format);
			}).on('finish.countdown',function(){
				seckill.handleSeckillkill(seckillId,seckillBox);
			});
		} else{
			seckill.handleSeckillkill(seckillId,seckillBox);
		}
	},
	
	validatePhone : function(phone){
		if(phone && phone.length == 11 && !isNaN(phone)){
			return true;
		} else{
			return false;
		}
			
	},
	
	detail : {
		init : function(params) {
			var killPhone = $.cookie('killPhone');
			var startTime = params['startTime'];
			var endTime = params['endTime'];
			var seckillId = params['seckillId'];
			if (!seckill.validatePhone(killPhone)) {
				var killPhoneModal = $('#killPhoneModal');
				killPhoneModal.modal({
					show : true,// 显示弹出层
					backdrop : 'static',// 禁止位置关闭
					keyboard : false // 关闭键盘事件
				})
				$('#killPhoneBtn').click(function() {
					var inputPhone = $('#killphoneKey').val();
					if (seckill.validatePhone(inputPhone)) {
						// 电话写入cookie
						$.cookie('killPhone', inputPhone, {
							expires : 7,
							path : '/seckill'
						});
						window.location.reload();
					} else {
						$('#killphoneMessage').hide().html('<label class="label label-danger">手机号错误!</label>').show(300);
					}
				});
			}
			// 计时交互
			var startTime = params['startTime'];
			var endTime = params['endTime'];
			var seckillId = params['seckillId'];
			$.get(seckill.URL.now(), {}, function(result) {
				if (result && result['success']) {
					var nowTime = result['data'];
					// 时间判断，计时交互
					seckill.countdown(seckillId, nowTime, startTime, endTime);
				} else {
					console.log(result['reult:'] + result);
				}
			});
		}
	}
}