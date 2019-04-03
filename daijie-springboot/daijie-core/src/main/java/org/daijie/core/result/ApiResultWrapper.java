package org.daijie.core.result;

import org.daijie.core.controller.enums.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Conventions;

import java.io.Serializable;
import java.util.Map;

/**
 * ApiResult返回数据的工具包
 * 对它进行增删改查的封装
 * @author daijie
 * @since 2017年6月5日
 */
public class ApiResultWrapper implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Logger logger = LoggerFactory.getLogger(ApiResultWrapper.class);

	private ApiResult apiResult = new ApiResult();

	public ApiResultWrapper() {

	}

	public ApiResultWrapper(boolean success) {
		apiResult.setSuccess(success);
	}
	
	public ApiResultWrapper(boolean success, ResultCode code) {
		apiResult.setSuccess(success);
		apiResult.setCode(code.getValue());
		apiResult.setMsg(code.getDescription());
	}
	
	public ApiResultWrapper(boolean success, ResultCode code, String msg) {
		apiResult.setSuccess(success);
		apiResult.setCode(code.getValue());
		apiResult.setMsg(msg);
	}

	public ApiResultWrapper setSuccess(boolean success) {
		apiResult.setSuccess(success);
		return this;
	}

	public ApiResultWrapper setMsg(String msg) {
		apiResult.setMsg(msg);
		return this;
	}
	
	public ApiResultWrapper setCode(ResultCode code) {
		apiResult.setCode(code.getValue());
		return this;
	}

	public ApiResultWrapper addDatas(Map<String, Object> dataMap) {
		if (dataMap != null) {
			apiResult.getData().putAll(dataMap);
		}
		return this;
	}

	public ApiResultWrapper addData(Object modelData) {
		addData(Conventions.getVariableName(modelData), modelData);
		return this;
	}

	public ApiResultWrapper addData(String modelName, Object modelData) {
		apiResult.getData().put(modelName, modelData);
		return this;
	}

	public ApiResultWrapper removeData(String modelName) {
		if (apiResult.getData().containsKey(modelName)) {
			apiResult.getData().remove(modelName);
		}
		return this;
	}

	public ApiResultWrapper clearDatas() {
		apiResult.getData().clear();
		return this;
	}

	public ApiResult build() {
		return apiResult;
	}
}
