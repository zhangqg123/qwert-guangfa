package org.jeecg.modules.qwert.jst.controller;

import java.util.ArrayList;
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
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.qwert.jst.entity.*;
import org.jeecg.modules.qwert.jst.service.IJstZcDevService;
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
	 @Autowired
	 private IJstZcDevService jstZcDevService;
	 @Autowired
	 public RedisUtil redisUtil;

	 @GetMapping(value = "/devRedis")
	 public Result<?> queryDevRedis(HttpServletRequest req) {
		 String devNo=req.getParameter("code");
		 //{7770=195};{5083=223, 5084=535};{5085=0, 5086=0, 7783=0, 7059=0, 7785=0, 7784=0, 7787=0, 7786=0, 7072=0, 6953=0, 6946=0, 6945=0, 5969=0};{5096=300, 5097=100, 5098=800, 7782=450, 5099=100, 6955=20, 6954=210, 5095=50};{7772=0, 7771=0, 7774=0, 7762=21, 7773=0, 6956=0, 6967=0};{7761=0, 7760=0, 5100=0, 7763=0, 5101=0, 5102=0, 7765=0, 7764=0, 7756=0, 7755=0, 7758=0, 7757=0, 7759=0, 7730=0, 7776=0, 7732=0, 7775=0, 7731=0, 5707=0, 5708=0, 7767=0, 6952=0, 7766=0, 7769=0, 7768=0, 7729=0, 7781=0, 7780=0, 5087=0, 5088=0, 7741=0, 7740=0, 7743=0, 7742=0, 7778=0, 7734=0, 7777=0, 7733=0, 5116=0, 7736=0, 7779=0, 7735=0, 7738=0, 7737=0, 7739=0, 7750=0, 7752=0, 7751=0, 7754=0, 7753=0, 5090=0, 5091=0, 5092=0, 5093=0, 5094=0, 7745=0, 7744=0, 7747=0, 7746=0, 7749=0, 7748=0}
		 //{6410=285, 5783=0, 5860=200, 6412=0, 5861=20, 6390=552, 5717=0, 6425=665, 5753=3, 5710=3, 6424=3108, 5711=213, 6427=6, 5799=0, 5712=499, 6426=3108, 5713=0, 5856=0, 5714=0, 6409=1453, 5715=0, 6408=894, 5716=32767, 6384=0, 6386=2250, 6388=2262, 5850=0, 6389=2262, 5730=63098, 5865=450, 5723=0, 5866=50, 5823=0, 5769=0}
		 String ret = (String) redisUtil.get(devNo);
		 //String ret = "{7770=195};{5085=0, 5086=0, 7783=0, 7059=0, 7785=0, 7784=0, 7787=0, 7786=0, 7072=0, 6953=0, 6946=0, 6945=0, 5969=0};{5096=300, 5097=100, 5098=800, 7782=450, 5099=100, 6955=20, 6954=210, 5095=50};{7772=0, 7771=0, 7774=0, 7762=21, 7773=0, 6956=0, 6967=0};{7761=0, 7760=0, 5100=0, 7763=0, 5101=0, 5102=0, 7765=0, 7764=0, 7756=0, 7755=0, 7758=0, 7757=0, 7759=0, 7730=0, 7776=0, 7732=0, 7775=0, 7731=0, 5707=0, 5708=0, 7767=0, 6952=0, 7766=0, 7769=0, 7768=0, 7729=0, 7781=0, 7780=0, 5087=0, 5088=0, 7741=0, 7740=0, 7743=0, 7742=0, 7778=0, 7734=0, 7777=0, 7733=0, 5116=0, 7736=0, 7779=0, 7735=0, 7738=0, 7737=0, 7739=0, 7750=0, 7752=0, 7751=0, 7754=0, 7753=0, 5090=0, 5091=0, 5092=0, 5093=0, 5094=0, 7745=0, 7744=0, 7747=0, 7746=0, 7749=0, 7748=0}";
		 ret=ret.replaceAll(" ","");
		 QueryWrapper<JstZcDev> queryWrapper = new QueryWrapper<JstZcDev>();
		 queryWrapper.eq("dev_no", devNo);
		 JstZcDev jzd = jstZcDevService.getOne(queryWrapper);
		 String orgUser = jzd.getOrgUser();
		 String catNo = jzd.getDevCat();
		 String devName = jzd.getDevName();
		 List<JstZcTarget> jztCollect = null;
		 if(orgUser.equals("guangfa")){
			 jztCollect = jstZcTargetService.queryJztList5(devNo);
		 }
		 if(orgUser.equals("jinshitan")){
			 jztCollect = jstZcTargetService.queryJztList4(catNo);
		 }
		 List<JstZcTarget2> jztList = new ArrayList<JstZcTarget2>();

		 String[] r1 = ret.split(";");

		 for(int h=0;h<jztCollect.size();h++){
			 boolean findflag = false;
			 JstZcTarget tc = jztCollect.get(h);
			 for(int i=0;i<r1.length;i++){
			 	if(findflag){
			 		break;
				}
				 String r2 = r1[i];
				 if(r2!=null&&r2.indexOf("{")!=-1){
					r2=r2.substring(1,r2.length()-1);
					 String[] r3 = r2.split(",");
					for(int j=0;j<r3.length;j++){
						String[] r4 = r3[j].split("=");
						if(r4[0].equals(tc.getId())){
							JstZcTarget2 jzt2 = new JstZcTarget2();
							jzt2.setId(r4[0]);
							jzt2.setTargetNo(tc.getTargetNo());
							jzt2.setTargetName(tc.getTargetName());
							jzt2.setValue(r4[1]);
							jztList.add(jzt2);
							findflag=true;
							break;
						}
					}
				 }
			 }
		 }
//		 List<JstZcTarget> jztList = (List<JstZcTarget>) jstZcTargetService.listByIds(jztIdList);
		 String str = JSON.toJSONString(jztList);
		 Result<Page<GuangfaBranch>> result = new Result<Page<GuangfaBranch>>();
		 return Result.OK(devName,str);
	 }

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
		if(from.equals("dl")&&(dev_no.toUpperCase().indexOf("TH")!=-1||dev_no.toUpperCase().indexOf("WDB")!=-1||dev_no.toUpperCase().indexOf("DLS")!=-1)){
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
		if(code.indexOf("zhk")!=-1){
			from="gfzhksd";
			dev_no=code.substring(3);
		}
		if(code.indexOf("zht")!=-1){
			from="gfzhtd";
			dev_no=code.substring(3);
		}
		if(code.indexOf("ykq")!=-1){
			from="ykfh";
			dev_no=code.substring(3);
		}
		if(code.indexOf("ddfh")!=-1){
			from="ddfh";
			dev_no=code.substring(4);
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
