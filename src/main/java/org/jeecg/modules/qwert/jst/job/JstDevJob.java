package org.jeecg.modules.qwert.jst.job;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.qwert.jst.service.IJstZcJobService;
import org.jeecg.modules.qwert.jst.utils.JstConstant;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 示例带参定时任务
 * 
 * @Author Scott
 */
@Slf4j
public class JstDevJob implements Job {

	/**
	 * 若参数变量名修改 QuartzJobController中也需对应修改
	 */
	private String parameter;
	@Autowired
	private IJstZcJobService jstZcJobService;

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

		log.info(String.format("welcome %s! Jeecg-Boot 带参数定时任务 SampleParamJob !   时间:" + DateUtils.now(), this.parameter));
		JstConstant.debugflag=1;
		long start, end;
		start = System.currentTimeMillis();
		jstZcJobService.readDev(parameter);
	}
}
