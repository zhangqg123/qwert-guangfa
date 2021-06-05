package org.jeecg.modules.qwert.jst.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.qwert.jst.entity.GuangfaBranch;
import org.jeecg.modules.qwert.jst.entity.JstZcTarget;
import org.jeecg.modules.qwert.jst.entity.QwertFc;
import org.jeecg.modules.qwert.jst.service.IJstZcTargetService;
import org.jeecg.modules.qwert.jst.service.IQwertFcService;

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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: qwert_fc
 * @Author: jeecg-boot
 * @Date:   2021-06-05
 * @Version: V1.0
 */
@Api(tags="qwert_fc")
@RestController
@RequestMapping("/jst/qwertFc")
@Slf4j
public class QwertFcController extends JeecgController<QwertFc, IQwertFcService> {
	@Autowired
	private IQwertFcService qwertFcService;
	 @Autowired
	 private IJstZcTargetService jstZcTargetService;

	/**
	 * 分页列表查询
	 *
	 * @param qwertFc
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "qwert_fc-分页列表查询")
	@ApiOperation(value="qwert_fc-分页列表查询", notes="qwert_fc-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(QwertFc qwertFc,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		String code=req.getParameter("code");
		String from=code.substring(0,2);
		String dev_no = code.substring(2);
		if(from.equals("dl")&&dev_no.toUpperCase().indexOf("AC")!=-1){
			from="gfac";
		}
		if(from.equals("dl")&&dev_no.toUpperCase().indexOf("TH")!=-1){
			from="gfwsd";
		}
		if(from.equals("dl")&&dev_no.toUpperCase().indexOf("UPS")!=-1){
			from="gfups";
		}
		if(from.equals("dl")&&dev_no.toUpperCase().indexOf("DLY")!=-1){
			from="gfdly";
		}
		if(from.equals("dl")&&dev_no.toUpperCase().indexOf("KG")!=-1){
			from="gfkg";
		}

		Result<Page<GuangfaBranch>> result = new Result<Page<GuangfaBranch>>();
		Page<GuangfaBranch> pageList = new Page<GuangfaBranch>(pageNo,pageSize);
		pageList = jstZcTargetService.queryGfPageByFromDevNo(pageList, from,dev_no);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param qwertFc
	 * @return
	 */
	@AutoLog(value = "qwert_fc-添加")
	@ApiOperation(value="qwert_fc-添加", notes="qwert_fc-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody QwertFc qwertFc) {
		qwertFcService.save(qwertFc);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param qwertFc
	 * @return
	 */
	@AutoLog(value = "qwert_fc-编辑")
	@ApiOperation(value="qwert_fc-编辑", notes="qwert_fc-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody QwertFc qwertFc) {
		qwertFcService.updateById(qwertFc);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "qwert_fc-通过id删除")
	@ApiOperation(value="qwert_fc-通过id删除", notes="qwert_fc-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		qwertFcService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "qwert_fc-批量删除")
	@ApiOperation(value="qwert_fc-批量删除", notes="qwert_fc-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.qwertFcService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "qwert_fc-通过id查询")
	@ApiOperation(value="qwert_fc-通过id查询", notes="qwert_fc-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		QwertFc qwertFc = qwertFcService.getById(id);
		if(qwertFc==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(qwertFc);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param qwertFc
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, QwertFc qwertFc) {
        return super.exportXls(request, qwertFc, QwertFc.class, "qwert_fc");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, QwertFc.class);
    }

}
