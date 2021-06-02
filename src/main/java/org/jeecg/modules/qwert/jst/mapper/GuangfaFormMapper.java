package org.jeecg.modules.qwert.jst.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.qwert.jst.entity.GuangfaBranch;
import org.jeecg.modules.qwert.jst.entity.JstZcDev;

import java.util.List;

/**
 * @Description: jst_zc_dev
 * @Author: jeecg-boot
 * @Date:   2020-07-24
 * @Version: V1.0
 */
public interface GuangfaFormMapper extends BaseMapper<GuangfaBranch> {
	public List<GuangfaBranch> queryGfBranch();
}
