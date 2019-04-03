package org.daijie.core.controller;

import org.daijie.core.controller.enums.ResultCode;
import org.daijie.core.controller.exception.ApiException;
import org.daijie.core.result.ApiResult;
import org.daijie.core.result.factory.ModelResultInitialFactory.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 此类定义为返回数据RestController的父类，对restapi接口进行统一管理
 * @author daijie_jay
 * @since 2018年1月1日
 */
@RequestMapping(produces = "application/json;charset=UTF-8")
public abstract class ApiController implements ExceptionController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 异常时，根据异常级别响应默认返回数据
	 */
	@Override
	public Object exceptionHandler(Exception e) {
		logger.error(e.getMessage(), e);
		if(e instanceof ApiException){
			return Result.build(null, e.getMessage(), ApiResult.ERROR, ((ApiException) e).getCode());
		}else{
			return Result.build(null, e.getMessage(), ApiResult.ERROR, ResultCode.CODE_500);
		}
	}
}
