package org.jeecg.modules.qwert.jst.work;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.qwert.jst.entity.*;
import org.jeecg.modules.qwert.jst.service.IGuangfaFormService;
import org.jeecg.modules.qwert.jst.service.IJstZcDevService;
import org.jeecg.modules.qwert.jst.service.IJstZcJgService;
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
public class GuangfaListController extends JeecgController<JstZcTarget, IJstZcTargetService> {
   @Autowired
   private IGuangfaFormService guangfaFormService;
    @Autowired
    private IJstZcTargetService jstZcTargetService;

   /**
    * 分页列表查询
    *
    * @return
    */
   @GetMapping(value = "/gfbranch")
   public String queryJmacList2() {
//       List<GuangfaBranch> pvList = guangfaFormService.queryGfBranch();
       List<GuangfaBranch> pvList = jstZcTargetService.queryGfBranch();
       StringBuilder sb=new StringBuilder();
       for(int i=0;i<pvList.size();i++){
           GuangfaBranch pv = pvList.get(i);
           int tn = pv.getTagName().lastIndexOf('\\');
           String tmpTag = pv.getTagName().substring(tn+1);
//           String tmpTag = pv.getTagName().replace("\\", "-");
           sb.append("\""+tmpTag+"\":\""+pv.getPv()+"\",");
       }
       String str = sb.substring(0, sb.length() - 1);
       String ret = "{\"data\":[{"+str+"}]}";
       return ret;
   }
}
