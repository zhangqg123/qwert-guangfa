package org.jeecg.modules.qwert.jst.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.qwert.jst.entity.GuangfaBranch;
import org.jeecg.modules.qwert.jst.entity.JstZcDev;

import java.util.List;

/**
 * @Description: jst_zc_dev
 * @Author: jeecg-boot
 * @Date:   2020-07-24
 * @Version: V1.0
 */
public interface IGuangfaFormService extends IService<GuangfaBranch> {
	public List<GuangfaBranch> queryGfBranch();
}
