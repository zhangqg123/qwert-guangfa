package org.jeecg.modules.qwert.jst.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.qwert.jst.entity.JstZcDev;

import javax.jms.JMSException;
import java.util.List;

/**
 * @Description: jst_zc_dev
 * @Author: jeecg-boot
 * @Date:   2020-07-24
 * @Version: V1.0
 */
public interface IJstZcJobService extends IService<JstZcDev> {
	public void readCat(String catOrigin);
	public void readDev(String devId) ;

	List<JstZcDev> queryJzdList2(String catNo);
}
