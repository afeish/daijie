package org.daijie.activiti.cloud.service;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

@SuppressWarnings("serial")
public class ApplyLeaveTaskService implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		System.out.println(this.getClass().getName() + "申请请假！");
	}
}
