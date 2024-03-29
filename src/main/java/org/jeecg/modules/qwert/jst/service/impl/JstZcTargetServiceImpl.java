package org.jeecg.modules.qwert.jst.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.jeecg.modules.qwert.jst.entity.GuangfaBranch;
import org.jeecg.modules.qwert.jst.entity.JstZcTarget;
import org.jeecg.modules.qwert.jst.entity.JstZcTarget2;
import org.jeecg.modules.qwert.jst.mapper.JstZcTargetMapper;
import org.jeecg.modules.qwert.jst.service.IJstZcTargetService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: jst_zc_target
 * @Author: jeecg-boot
 * @Date:   2020-07-24
 * @Version: V1.0
 */
@Service
public class JstZcTargetServiceImpl extends ServiceImpl<JstZcTargetMapper, JstZcTarget> implements IJstZcTargetService {
	@Resource
	private JstZcTargetMapper jstZcTargetMapper;

//	@Cacheable(value = CacheConstant.JST_TARGET_CACHE)
	@Override
	public List<JstZcTarget> queryJztList() {
//		QueryWrapper<JstZcTarget> zqw = QueryGenerator.initQueryWrapper(new JstZcTarget(), null);
//		zqw.orderByAsc("instruct");
//		zqw.orderByAsc("tmp");
		List<JstZcTarget> jztList = this.jstZcTargetMapper.queryJztList();			
		return jztList;
	}
	@Override
	public List<JstZcTarget2> queryJztList2() {
		List<JstZcTarget2> jztList = this.jstZcTargetMapper.queryJztList2();			
		return jztList;
	}
	@Override
	public List<JstZcTarget> queryJztList3(String dev_type) {
		List<JstZcTarget> jztList = this.jstZcTargetMapper.queryJztList3(dev_type);			
		return jztList;
	}
	
//	@CacheEvict(value = CacheConstant.JST_TARGET_CACHE,allEntries=true)
	@Override
	public boolean edit(JstZcTarget jstZcTarget) {
		return this.updateById(jstZcTarget);
	}
	@Override
	public List<JstZcTarget> queryJztList4(String dev_type) {
		List<JstZcTarget> jztList = this.jstZcTargetMapper.queryJztList4(dev_type);			
		return jztList;
	}
	public List<JstZcTarget> queryJztList5(String dev_no) {
		List<JstZcTarget> jztList = this.jstZcTargetMapper.queryJztList5(dev_no);
		return jztList;
	}
	@Override
	public Page<JstZcTarget> queryJztPageByOrgUser(Page<JstZcTarget> page, String orgUser) {
		 return page.setRecords(jstZcTargetMapper.queryJztPageByOrgUser(page, orgUser));
	}

	@Override
	public Page<GuangfaBranch> queryGfPageByFromDevNo(Page<GuangfaBranch> page, String from,String dev_no) {
		List<GuangfaBranch> tmp = jstZcTargetMapper.queryGfPageByFromDevNo(page, from, dev_no);
		return page.setRecords(jstZcTargetMapper.queryGfPageByFromDevNo(page, from,dev_no));
	}

	@Override
	public List<GuangfaBranch> queryGfBranch() {
		List<GuangfaBranch> pvList = this.jstZcTargetMapper.queryGfBranch();
		return pvList;
	}

	@Override
	public List<GuangfaBranch> queryGfTarget(String from, String dev_no) {
		List<GuangfaBranch> pvList = this.jstZcTargetMapper.queryGfTarget(from,dev_no);
		return pvList;
	}

	@Override
	public List<JstZcTarget2> queryJztListFromPos(String devPos) {
		List<JstZcTarget2> jztList = this.jstZcTargetMapper.queryJztListFromPos(devPos);
		return jztList;
	}

}
