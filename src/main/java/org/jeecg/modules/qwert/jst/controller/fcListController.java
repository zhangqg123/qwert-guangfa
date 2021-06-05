package org.jeecg.modules.qwert.jst.controller;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.qwert.jst.entity.GuangfaBranch;
import org.jeecg.modules.qwert.jst.entity.JstZcTarget;
import org.jeecg.modules.qwert.jst.service.IGuangfaFormService;
import org.jeecg.modules.qwert.jst.service.IJstZcTargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
* @Description: jst_zc_target
* @Author: jeecg-boot
* @Date:   2020-07-24
* @Version: V1.0
*/

@RestController
@RequestMapping("/work/guangfa")
@Slf4j
public class fcListController extends JeecgController<JstZcTarget, IJstZcTargetService> {
//   @Autowired
//   private IGuangfaFormService guangfaFormService;
    @Autowired
    private IJstZcTargetService jstZcTargetService;

   /**
    * 分页列表查询
    *
    * @return
    */
   @GetMapping(value = "/gfTarget")
   public Result<?> queryJmacList2(String from, String dev_no) {
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
       List<GuangfaBranch> pvList = jstZcTargetService.queryGfTarget(from,dev_no);
       return Result.ok(pvList);
   }
}
