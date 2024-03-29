package org.jeecg.modules.qwert.jst.service;

import java.util.List;

import org.jeecg.modules.qwert.jst.entity.GuangfaBranch;
import org.jeecg.modules.qwert.jst.entity.JstZcTarget;
import org.jeecg.modules.qwert.jst.entity.JstZcTarget2;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: jst_zc_target
 * @Author: jeecg-boot
 * @Date:   2020-07-24
 * @Version: V1.0
 */
public interface IJstZcTargetService extends IService<JstZcTarget> {
	public List<JstZcTarget> queryJztList();
	public List<JstZcTarget2> queryJztList2();
	public List<JstZcTarget> queryJztList3(String dev_type);
	public List<JstZcTarget> queryJztList4(String dev_type);
	public List<JstZcTarget> queryJztList5(String dev_no);
	public boolean edit(JstZcTarget jstZcTarget);
	Page<JstZcTarget> queryJztPageByOrgUser(Page<JstZcTarget> page, String orgUser);
	Page<GuangfaBranch> queryGfPageByFromDevNo(Page<GuangfaBranch> page, String from,String dev_no);

    List<GuangfaBranch> queryGfBranch();

    List<GuangfaBranch> queryGfTarget(String from, String dev_no);

    List<JstZcTarget2> queryJztListFromPos(String devPos);
}
