package org.jeecg.modules.qwert.jst.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.math.BigInteger;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.qwert.conn.modbus4j.test.TestSerialPortWrapper;
import org.jeecg.modules.qwert.conn.snmp.SnmpData;
import org.jeecg.modules.qwert.conn.dbconn.mongo.common.model.Audit;
import org.jeecg.modules.qwert.conn.dbconn.mongo.repository.impl.DemoRepository;
import org.jeecg.modules.qwert.jst.entity.JstZcCat;
import org.jeecg.modules.qwert.jst.entity.JstZcConfig;
import org.jeecg.modules.qwert.jst.entity.JstZcDev;
import org.jeecg.modules.qwert.jst.entity.JstZcRev;
import org.jeecg.modules.qwert.jst.entity.JstZcTarget;
import org.jeecg.modules.qwert.jst.service.IJstZcAlarmService;
import org.jeecg.modules.qwert.jst.service.IJstZcCatService;
import org.jeecg.modules.qwert.jst.service.IJstZcConfigService;
import org.jeecg.modules.qwert.jst.service.IJstZcDevService;
import org.jeecg.modules.qwert.jst.service.IJstZcTargetService;
import org.jeecg.modules.qwert.jst.utils.JstConstant;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.qwert.conn.modbus4j.source.BatchRead;
import org.jeecg.modules.qwert.conn.modbus4j.source.BatchResults;
import org.jeecg.modules.qwert.conn.modbus4j.source.ModbusFactory;
import org.jeecg.modules.qwert.conn.modbus4j.source.ModbusMaster;
import org.jeecg.modules.qwert.conn.modbus4j.source.exception.ErrorResponseException;
import org.jeecg.modules.qwert.conn.modbus4j.source.exception.ModbusInitException;
import org.jeecg.modules.qwert.conn.modbus4j.source.exception.ModbusTransportException;
import org.jeecg.modules.qwert.conn.modbus4j.source.ip.IpParameters;
import org.jeecg.modules.qwert.conn.modbus4j.source.locator.BaseLocator;

import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

/**
 * @Description: jst_zc_dev
 * @Author: jeecg-boot
 * @Date: 2020-07-24
 * @Version: V1.0
 */
@Api(tags = "jst_zc_dev")
@RestController
@RequestMapping("/jst/jstZcDev")
@Slf4j
public class JstZcDevController extends JeecgController<JstZcDev, IJstZcDevService> {
	@Autowired
	private IJstZcCatService jstZcCatService;
	@Autowired
	private IJstZcDevService jstZcDevService;
	@Autowired
	private IJstZcTargetService jstZcTargetService;
	@Autowired
	private IJstZcAlarmService jstZcAlarmService;
	@Autowired
	private IJstZcConfigService jstZcConfigService;
    @Autowired
    DemoRepository repository;
    
    private List<JstZcCat> jzcList;
    private List<JstZcDev> jzdList;    
    private List<JstZcTarget> jztList;
//	private boolean runflag = true;

	/**
	 * 分页列表查询
	 *
	 * @param jstZcDev
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "jst_zc_dev-分页列表查询")
	@ApiOperation(value = "jst_zc_dev-分页列表查询", notes = "jst_zc_dev-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(JstZcDev jstZcDev, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
		Map<String, String[]> aaa = req.getParameterMap();
		QueryWrapper<JstZcDev> queryWrapper = QueryGenerator.initQueryWrapper(jstZcDev, req.getParameterMap());
		queryWrapper.orderByAsc("dev_cat");
		queryWrapper.orderByAsc("dev_no");
		Page<JstZcDev> page = new Page<JstZcDev>(pageNo, pageSize);
		IPage<JstZcDev> pageList = jstZcDevService.page(page, queryWrapper);
		return Result.ok(pageList,JstConstant.runflag);
	}

	/**
	 * 添加
	 *
	 * @param jstZcDev
	 * @return
	 */
	@AutoLog(value = "jst_zc_dev-添加")
	@ApiOperation(value = "jst_zc_dev-添加", notes = "jst_zc_dev-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody JstZcDev jstZcDev) {
		jstZcDevService.save(jstZcDev);
		return Result.ok("添加成功！");
	}

	/**
	 * 测试
	 *
	 * @param jstZcDev
	 * @return
	 * @throws InterruptedException 
	 */
//	@AutoLog(value = "jst_zc_dev-测试")
	@ApiOperation(value = "jst_zc_dev-测试", notes = "jst_zc_dev-测试")
	@PostMapping(value = "/conntest")
	public Result<?> conntest(@RequestBody JstZcRev jstZcRev) throws InterruptedException {
		List<JstZcConfig> jzConList = jstZcConfigService.list();
		
		for (int i=0;i<jzConList.size();i++) {
			JstZcConfig jc = jzConList.get(i);
			if(jc.getConfigNo().equals("debugflag")) {
				JstConstant.debugflag=Integer.parseInt(jc.getConfigValue());
			}
			if(jc.getConfigNo().equals("sleeptime")) {
				JstConstant.sleeptime=Integer.parseInt(jc.getConfigValue());
			}
		}
		long start, end;
		start = System.currentTimeMillis();

		String devNo=jstZcRev.getDevNo();
		String conInfo = jstZcRev.getConnInfo();
		String revList = jstZcRev.getRevList();
		List<JstZcTarget> jztList = null;
		List resList = new ArrayList();
		if (jstZcRev.getDevType() != null) {
			jztList = jstZcTargetService.queryJztList4(jstZcRev.getDevType());
		} else {
			jztList = JSONArray.parseArray(revList, JstZcTarget.class);
		}
		JSONObject jsonConInfo = JSON.parseObject(conInfo);
		String ipAddress = jsonConInfo.getString("ipAddress");
		String port = jsonConInfo.getString("port");
		String type = jsonConInfo.getString("type");
		String retry = jsonConInfo.getString("retry");

		String slave = null;
		String packageBit = null;

		String version = null;
		String timeOut = null;
		String community = null;
		BatchResults<String> results = null;
		if (type.equals("SOCKET")||type.equals("MODBUSRTU")||type.equals("MODBUSASCII")||type.equals("MODBUSTCP")) {
			handleModbus(type,devNo, jztList, resList, jsonConInfo);
		}
		if (type.equals("SNMP")) {
			handleSnmp(jztList, resList, jsonConInfo, ipAddress);
		}
		end = System.currentTimeMillis();
		System.out.println("开始时间:" + start + "; 结束时间:" + end + "; 用时:" + (end - start) + "(ms)");
		return Result.ok(resList);
	}

	private void handleSnmp(List<JstZcTarget> jztList, List resList, JSONObject jsonConInfo, String ipAddress) {
		String version;
		String timeOut;
		String community;
		version = jsonConInfo.getString("version");
		timeOut = jsonConInfo.getString("timeOut");
		community = jsonConInfo.getString("community");
		jztList.stream().sorted(Comparator.comparing(JstZcTarget::getInstruct));
		List<String> oidList = new ArrayList<String>();
		for (int i = 0; i < jztList.size(); i++) {
			JstZcTarget jzt = jztList.get(i);
			String oidval = jzt.getInstruct();
//			System.out.println(devNo+"::");
			List snmpList = SnmpData.snmpGet(ipAddress, community, oidval,null);
			if(snmpList.size()>0) {
				for(int j=0;j<snmpList.size();j++) {
					resList.add(snmpList.get(j));
				}
			}else {
				break;
			}
		}
	}

	public void handleModbus(String type,String devNo, List<JstZcTarget> jztList, List resList, JSONObject jsonConInfo)
			throws InterruptedException {
		String slave;
		String packageBit;
		String timeOut;
		BatchResults<String> results;
		slave = jsonConInfo.getString("slave");
		packageBit = jsonConInfo.getString("packageBit");
		String bitNumber = jsonConInfo.getString("bitNumber");
		int pb=64;
		if(packageBit!=null&&!packageBit.equals("")) {
			pb=Integer.parseInt(packageBit);
		}
		int bn=10;
		if(bitNumber!=null&&!bitNumber.equals("")) {
			bn=Integer.parseInt(bitNumber);
		}
		timeOut = jsonConInfo.getString("timeOut");
		TestSerialPortWrapper wrapper = null;
		if(type.equals("MODBUSRTU")||type.equals("MODBUSASCII")) {
			String commPortId = jsonConInfo.getString("com");
			int baudRate = Integer.parseInt(jsonConInfo.getString("baudRate"));
			int flowControlIn = 0;
			int flowControlOut = 0; 
			int dataBits = Integer.parseInt(jsonConInfo.getString("dataBits"));
			int stopBits = Integer.parseInt(jsonConInfo.getString("stopBits"));
			int parity = Integer.parseInt(jsonConInfo.getString("parity"));
			
			wrapper = new TestSerialPortWrapper(commPortId, baudRate, flowControlIn, flowControlOut, dataBits, stopBits, parity);
		}
		IpParameters ipParameters = null;
		if(type.equals("MODBUSTCP")||type.equals("SOCKET")) {
			String ipAddress = jsonConInfo.getString("ipAddress");
			String port = jsonConInfo.getString("port");
			ipParameters = new IpParameters();
			ipParameters.setHost(ipAddress);
			ipParameters.setPort(Integer.parseInt(port));
			ipParameters.setEncapsulated(true);
		}
		ModbusFactory modbusFactory = new ModbusFactory();
		ModbusMaster master=null;
		if(type.equals("MODBUSRTU")) {
			master = modbusFactory.createRtuMaster(wrapper);
		}
		if(type.equals("MODBUSASCII")) {
			master = modbusFactory.createAsciiMaster(wrapper);
		}
		if(type.equals("MODBUSTCP")||type.equals("SOCKET")) {
			master = modbusFactory.createTcpMaster(ipParameters, false);
		}
		boolean flag = false;
		try {
			master.init();
			int slaveId = 0;
			slaveId = Integer.parseInt(slave);
			BatchRead<String> batch = new BatchRead<String>();
			String tmpInstruct = null;
			int pointNumber = 0;
			int tmp2Offset = 0;
			boolean batchSend = false;
			for (int i = 0; i < jztList.size(); i++) {
				JstZcTarget jzt = jztList.get(i);
				String di = jzt.getInstruct().substring(0,2);
				
				int	offset = Integer.parseInt(jzt.getAddress());
				if (offset==tmp2Offset&&pointNumber>0) {
					continue;
				}
				if (pointNumber>0 && (pointNumber > pb || (offset - tmp2Offset >= bn))) {
					flag = true;
				}
				pointNumber++;
				tmp2Offset=offset;
				if (flag == true) {
			//		System.out.println(i + "::" + offset);
					results = master.send(batch);
					Thread.sleep(JstConstant.sleeptime);
					if(!results.toString().equals("{}")) {
						resList.add(results.toString());
					}
					if(JstConstant.debugflag==1) {
						System.out.println(results);
					}
					batch = new BatchRead<String>();
					flag = false;
					pointNumber = 0;
				}
				

				if (di.equals("04")) {
						batch.addLocator(jzt.getId(),
								BaseLocator.inputRegister(slaveId, offset, Integer.parseInt(jzt.getDataType())));
						batchSend = true;
				}
				if (di.equals("03")) {
						batch.addLocator(jzt.getId(),
								BaseLocator.holdingRegister(slaveId, offset, Integer.parseInt(jzt.getDataType())));
						batchSend = true;
				}
				if (di.equals("02")) {
					batch.addLocator(jzt.getId(), BaseLocator.inputStatus(slaveId, offset));
					batchSend = true;
				}
				Thread.sleep(JstConstant.sleeptime/2);
			}
			if (batchSend == true) {
				results = master.send(batch);
				Thread.sleep(JstConstant.sleeptime);
				if(JstConstant.debugflag==1) {
					System.out.println(results);
				}
				resList.add(results.toString());
			}
			if(JstConstant.debugflag==1) {
				System.out.println(devNo+"::"+resList.size());
			}				
		} catch (ModbusInitException e) {
			e.printStackTrace();
		} catch (ModbusTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			master.destroy();
		}
	}
	
//	@AutoLog(value = "jst_zc_dev-读取")
	@ApiOperation(value = "jst_zc_dev-读取", notes = "jst_zc_dev-读取")
	@GetMapping(value = "/readClose")
	public Result<?> readClose(HttpServletRequest req) {
		JstConstant.runflag=false;
		JstConstant.runall=false;
		return Result.ok("ok",false);
	}
	
//	@AutoLog(value = "jst_zc_dev-读取")
	@ApiOperation(value = "jst_zc_dev-读取", notes = "jst_zc_dev-读取")
	@GetMapping(value = "/handleRead")
	public Result<?> handleRead(HttpServletRequest req) {
		String catNo = req.getParameter("devCat");
		if(catNo==null) {
			return Result.ok("choose category");
		}
		JstConstant.runflag=true;
		if(catNo.equals("all") && JstConstant.runall==true) {
			return Result.ok("reading all",true);
		}else {
			String hr = jstZcDevService.handleRead(catNo);
		}
		return Result.ok("ok",JstConstant.runflag);
	}
	
	@AutoLog(value = "jst_zc_dev-队列")
	@ApiOperation(value = "jst_zc_dev-队列", notes = "jst_zc_dev-队列")
	@GetMapping(value = "/readAmq")
	public Result<?> readAmq(HttpServletRequest req) {
        StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
        factory.setBrokerURI("tcp://" + JstConstant.host + ":" + JstConstant.port);

        Connection connection;
		try {
			connection = factory.createConnection(JstConstant.user, JstConstant.password);
	        connection.start();
	        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	        Destination dest = new StompJmsDestination(JstConstant.destination);
	        MessageProducer producer = session.createProducer(dest);
	        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			log.info(String.format(" Jeecg-Boot 普通定时任务 SampleJob !  时间:" + DateUtils.getTimestamp()));
			List<Audit> auditList = repository.findAllAudit();
			for(int i=0;i<auditList.size();i++) {
				Audit audit = auditList.get(i);
	            TextMessage msg = session.createTextMessage(audit.getAuditValue());
	            msg.setIntProperty("id", i);
	            producer.send(msg);
  //              System.out.println(String.format("Sent %d messages", i));
				
			}
	        connection.close();

		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Result.ok("ok");
	}
    private static String env(String key, String defaultValue) {
        String rc = System.getenv(key);
        if( rc== null )
            return defaultValue;
        return rc;
    }


	public boolean isNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			System.out.println(str.charAt(i));
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}


	/**
	 * 编辑
	 *
	 * @param jstZcDev
	 * @return
	 */
	@AutoLog(value = "jst_zc_dev-编辑")
	@ApiOperation(value = "jst_zc_dev-编辑", notes = "jst_zc_dev-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody JstZcDev jstZcDev) {
		jstZcDevService.updateById(jstZcDev);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "jst_zc_dev-通过id删除")
	@ApiOperation(value = "jst_zc_dev-通过id删除", notes = "jst_zc_dev-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		jstZcDevService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "jst_zc_dev-批量删除")
	@ApiOperation(value = "jst_zc_dev-批量删除", notes = "jst_zc_dev-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		this.jstZcDevService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "jst_zc_dev-通过id查询")
	@ApiOperation(value = "jst_zc_dev-通过id查询", notes = "jst_zc_dev-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		JstZcDev jstZcDev = jstZcDevService.getById(id);
		if (jstZcDev == null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(jstZcDev);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param jstZcDev
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, JstZcDev jstZcDev) {
		return super.exportXls(request, jstZcDev, JstZcDev.class, "jst_zc_dev");
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
		return super.importExcel(request, response, JstZcDev.class);
	}

}
