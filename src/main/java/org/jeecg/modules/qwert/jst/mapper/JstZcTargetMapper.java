package org.jeecg.modules.qwert.jst.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.qwert.jst.entity.GuangfaBranch;
import org.jeecg.modules.qwert.jst.entity.JstZcAlarm;
import org.jeecg.modules.qwert.jst.entity.JstZcTarget;
import org.jeecg.modules.qwert.jst.entity.JstZcTarget2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @Description: jst_zc_target
 * @Author: jeecg-boot
 * @Date:   2020-07-24
 * @Version: V1.0
 */
public interface JstZcTargetMapper extends BaseMapper<JstZcTarget> {

	public List<JstZcTarget> queryJztList();
	public List<JstZcTarget2> queryJztList2();
	public List<JstZcTarget> queryJztList3(@Param("dev_type") String dev_type);
	public List<JstZcTarget> queryJztList4(@Param("dev_type") String dev_type);
	public List<JstZcTarget> queryJztList5(@Param("dev_no") String dev_no);
	public List<JstZcTarget> queryJztPageByOrgUser(Page<JstZcTarget> page,@Param("orgUser") String orgUser);

	List<GuangfaBranch> queryGfBranch();
	List<GuangfaBranch> queryGfTarget(@Param("from") String from,@Param("dev_no") String dev_no);

	List<GuangfaBranch> queryGfPageByFromDevNo(Page<GuangfaBranch> page,@Param("from") String from,@Param("dev_no") String dev_no);

    List<JstZcTarget2> queryJztListFromPos(@Param("dev_pos") String devPos);
}
