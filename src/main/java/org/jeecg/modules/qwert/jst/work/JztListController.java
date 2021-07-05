package org.jeecg.modules.qwert.jst.work;

import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.qwert.jst.entity.*;
import org.jeecg.modules.qwert.jst.service.IJstZcDevService;
import org.jeecg.modules.qwert.jst.service.IJstZcJgService;
import org.jeecg.modules.qwert.jst.service.IJstZcTargetService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: jst_zc_target
 * @Author: jeecg-boot
 * @Date:   2020-07-24
 * @Version: V1.0
 */

@RestController
@RequestMapping("/work/jst/jzt")
@Slf4j
public class JztListController extends JeecgController<JstZcTarget, IJstZcTargetService> {
	@Autowired
	private IJstZcTargetService jstZcTargetService;
	@Autowired
	private IJstZcDevService jstZcDevService;
	@Autowired
	private IJstZcJgService jstZcJgService;
	 @Autowired
	 public RedisTemplate<String, Object> redisTemplate;
	 @Autowired
	 public RedisUtil redisUtil;

	/**
	 * 分页列表查询
	 *
	 * @param jstZcTarget
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<?> queryPageList() {
		List<JstZcTarget2> jztList = jstZcTargetService.queryJztList2();
		return Result.ok(jztList);			
	}
	@GetMapping(value = "/jlwList")
	public Result<?> queryJlwList() {
		List<JstZcJg> jgList = jstZcJgService.list();
		return Result.ok(jgList);			
	}
	
	@GetMapping(value = "/jmacList")
	public Result<?> queryJmacList() {
		List<JstZcDev> jmacList = jstZcDevService.queryJmacList();
		return Result.ok(jmacList);			
	}
	@GetMapping(value = "/jmacList2")
	public String queryJmacList2() {
		List<JstZcDev> jmacList = jstZcDevService.queryJmacList();
		String str = JSON.toJSONString(jmacList);
		String ret = "{\"data\":"+str+"}";
		return ret;
	}
	@GetMapping(value = "/devRedis")
	public String queryDevRedis(HttpServletRequest req) {
		String code=req.getParameter("code");

		//{6410=285, 5783=0, 5860=200, 6412=0, 5861=20, 6390=552, 5717=0, 6425=665, 5753=3, 5710=3, 6424=3108, 5711=213, 6427=6, 5799=0, 5712=499, 6426=3108, 5713=0, 5856=0, 5714=0, 6409=1453, 5715=0, 6408=894, 5716=32767, 6384=0, 6386=2250, 6388=2262, 5850=0, 6389=2262, 5730=63098, 5865=450, 5723=0, 5866=50, 5823=0, 5769=0}
		//{7770=195};{5083=223, 5084=535};{5085=0, 5086=0, 7783=0, 7059=0, 7785=0, 7784=0, 7787=0, 7786=0, 7072=0, 6953=0, 6946=0, 6945=0, 5969=0};{5096=300, 5097=100, 5098=800, 7782=450, 5099=100, 6955=20, 6954=210, 5095=50};{7772=0, 7771=0, 7774=0, 7762=21, 7773=0, 6956=0, 6967=0};{7761=0, 7760=0, 5100=0, 7763=0, 5101=0, 5102=0, 7765=0, 7764=0, 7756=0, 7755=0, 7758=0, 7757=0, 7759=0, 7730=0, 7776=0, 7732=0, 7775=0, 7731=0, 5707=0, 5708=0, 7767=0, 6952=0, 7766=0, 7769=0, 7768=0, 7729=0, 7781=0, 7780=0, 5087=0, 5088=0, 7741=0, 7740=0, 7743=0, 7742=0, 7778=0, 7734=0, 7777=0, 7733=0, 5116=0, 7736=0, 7779=0, 7735=0, 7738=0, 7737=0, 7739=0, 7750=0, 7752=0, 7751=0, 7754=0, 7753=0, 5090=0, 5091=0, 5092=0, 5093=0, 5094=0, 7745=0, 7744=0, 7747=0, 7746=0, 7749=0, 7748=0}
		Object aaa = redisUtil.get("RES_242");
		System.out.println(aaa);
		return String.valueOf(aaa);
	}
	 @GetMapping(value = "/formData")
	 public String queryFormData(HttpServletRequest req) {
		 //	String position=req.getParameter("position");
		 String devPos = "gfp001";
		 List<JstZcTarget2> jztCollect = jstZcTargetService.queryJztListFromPos(devPos);
		 StringBuilder sb = new StringBuilder();
		 sb.append("{\"data\"");
		 sb.append(":[{");
		 String tmpDevNo = null;
		 String ret=null;
		 for (int h = 0; h < jztCollect.size(); h++) {
			 JstZcTarget2 jzt = jztCollect.get(h);

			 if (tmpDevNo==null || !tmpDevNo.equals(jzt.getDevNo())) {
				 ret = (String) redisUtil.get(jzt.getDevNo());
			 }
			 if (ret != null) {
				 String[] r1 = ret.split(";");
				 boolean findflag = false;
				 for (int i = 0; i < r1.length; i++) {
					 if (findflag) {
						 break;
					 }
					 String r2 = r1[i];
					 if (r2 != null && r2.indexOf("{") != -1) {
						 r2 = r2.substring(1, r2.length() - 1);
						 String[] r3 = r2.split(",");
						 for (int j = 0; j < r3.length; j++) {
							 String[] r4 = r3[j].split("=");
							 if (r4[0].equals(jzt.getId())) {
							 	// targetNo 是否需要引号？
								 sb.append(jzt.getTargetNo()+":"+r4[1]+",");
								 findflag = true;
								 break;
							 }
						 }
					 }
				 }
			 }
			 tmpDevNo = jzt.getDevNo();
		 }
		 sb.substring(0,sb.length()-1);
		 sb.append("}]}");
		 Object aaa = redisUtil.get("RES_242");
		 System.out.println(aaa);
		 return String.valueOf(aaa);
	 }

 }

 
 
 