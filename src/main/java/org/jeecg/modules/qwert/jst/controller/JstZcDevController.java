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
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ReadInputRegistersRequest;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ReadInputRegistersResponse;
import org.jeecg.modules.qwert.conn.modbus4j.test.TestSerialPortWrapper;
import org.jeecg.modules.qwert.conn.qudong.QwertFactory;
import org.jeecg.modules.qwert.conn.qudong.QwertMaster;
import org.jeecg.modules.qwert.conn.qudong.base.QudongUtils;
import org.jeecg.modules.qwert.conn.qudong.exception.QudongTransportException;
import org.jeecg.modules.qwert.conn.qudong.msg.ReadDianzongRequest;
import org.jeecg.modules.qwert.conn.qudong.msg.ReadDianzongResponse;
import org.jeecg.modules.qwert.conn.qudong.msg.ReadM7000Request;
import org.jeecg.modules.qwert.conn.qudong.msg.ReadM7000Response;
import org.jeecg.modules.qwert.conn.qudong.msg.delta.ReadDeltaRequest;
import org.jeecg.modules.qwert.conn.qudong.msg.delta.ReadDeltaResponse;
import org.jeecg.modules.qwert.conn.qudong.msg.kstar.ReadKstarRequest;
import org.jeecg.modules.qwert.conn.qudong.msg.kstar.ReadKstarResponse;
import org.jeecg.modules.qwert.conn.snmp.SnmpData;
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
import org.jetbrains.annotations.Nullable;
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
		QueryWrapper<JstZcDev> queryWrapper = new QueryWrapper<JstZcDev>();
		queryWrapper.eq("dev_no", devNo);
		JstZcDev jzd = jstZcDevService.getOne(queryWrapper);
		if(jzd==null) {
			return null;
		}
//		String devName = jzd.getDevName();
		String catNo = jzd.getDevCat();
		String conInfo = jstZcRev.getConnInfo();
		String revList = jstZcRev.getRevList();
		List<JstZcTarget> jztList = null;
		List resList = new ArrayList();
		if (jstZcRev.getDevType() != null) {
			if(devNo.indexOf("gf")!=-1){
				jztList = jstZcTargetService.queryJztList5(devNo);
			}else {
				jztList = jstZcTargetService.queryJztList4(jstZcRev.getDevType());
			}
		} else {
			jztList = JSONArray.parseArray(revList, JstZcTarget.class);
		}
		JSONObject jsonConInfo = JSON.parseObject(conInfo);
		String type = jsonConInfo.getString("type");
		String proType = jsonConInfo.getString("proType");
		BatchResults<String> results = null;
//		if (type.equals("SOCKET")||type.equals("MODBUSRTU")||type.equals("MODBUSASCII")||type.equals("MODBUSTCP")) {
		if (proType.toUpperCase().equals("MODBUS")) {
			handleModbus(type,devNo,catNo, jztList, resList, jsonConInfo);
		}
		if (proType.toUpperCase().equals("SNMP")) {
			handleSnmp(jztList, resList, jsonConInfo);
		}
		if (proType.toUpperCase().equals("DELTA")) {
			handleDelta(jztList, resList, jsonConInfo);
		}
		if (proType.toUpperCase().equals("KSTAR")) {
			handlekStar(jztList, resList, jsonConInfo);
		}
		if (proType.toUpperCase().equals("7000D")) {
			handleM7000D(jztList, resList, jsonConInfo);
		}
		if (proType.toUpperCase().equals("PMBUS")) {
			handlePmbus(jztList, resList, jsonConInfo);
		}
		end = System.currentTimeMillis();
		System.out.println((resList.toString()));
		System.out.println("开始时间:" + start + "; 结束时间:" + end + "; 用时:" + (end - start) + "(ms)");
		return Result.ok(resList);
	}

	private void handlePmbus(List<JstZcTarget> jztList, List resList, JSONObject jsonConInfo) {
		int slaveId = Integer.parseInt(jsonConInfo.getString("slave"));
		QwertMaster master = QudongUtils.getQwertMaster(jsonConInfo);
		String tmpInstruct=null;
		byte[] retmessage = null;
		for (int i = 0; i < jztList.size(); i++) {
			JstZcTarget jzt = jztList.get(i);
			String instruct = jzt.getInstruct();
			if(instruct.equals(tmpInstruct)){
				String rm1 = jzt.getTargetNo();
				String rm2 = jzt.getAddress();
				String rm = null;
				if(rm2.indexOf("$")!=-1){
					String[] rm3=rm2.split("\\$");
					rm=retmessage[Integer.parseInt(rm3[0])-1]+"";
				}
				resList.add(rm1+"="+rm);
				continue;
			}
			String[] tmp = instruct.split("/");
			try {
				ReadDianzongRequest request = new ReadDianzongRequest(2.0f,slaveId, Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]),Integer.parseInt(tmp[2]));
				ReadDianzongResponse response = (ReadDianzongResponse) master.send(request);

				if (response.isException())
					System.out.println("Exception response: message=" + response.getExceptionMessage());
				else{
					System.out.println(Arrays.toString(response.getShortData()));
		//			resList.add(Arrays.toString(response.getShortData()));
					retmessage =  response.getRetData();
					String rm1 = jzt.getTargetNo();
					String rm2 = jzt.getAddress();
					int rm = 0;
					if(rm2.indexOf("_")!=-1){
						short[] rp = response.getShortData();
				//		String[] rm3=rm2.split("_");
				//		byte trm = retmessage[Integer.parseInt(rm3[0])] ;
				//		rm=trm & 0xff;
						rm=rp[7];
					}
					if(rm2.indexOf("$")!=-1){
						String[] rm3=rm2.split("\\$");
						rm=retmessage[Integer.parseInt(rm3[0])-1];
					}
					resList.add(rm1+"="+rm);
				}
			}
			catch (QudongTransportException e) {
				e.printStackTrace();
			}
			tmpInstruct = jzt.getInstruct();
		}
	}

	private void handleM7000D(List<JstZcTarget> jztList, List resList, JSONObject jsonConInfo) {
		int slaveId = Integer.parseInt(jsonConInfo.getString("slave"),16);
		QwertMaster master = QudongUtils.getQwertMaster(jsonConInfo);
		String tmpInstruct=null;
		String retmessage=null;
		for (int i = 0; i < jztList.size(); i++) {
			JstZcTarget jzt = jztList.get(i);
			String instruct = jzt.getInstruct();
			if(instruct.equals(tmpInstruct)){
				String rm1 = jzt.getTargetNo();
				String rm2 = jzt.getAddress();
				String rm = QudongUtils.getM7000DString(retmessage, rm2);
				resList.add(rm1+"="+rm);
				continue;
			}
			try {

				ReadM7000Request request = new ReadM7000Request(slaveId, 6);
				ReadM7000Response response = (ReadM7000Response) master.send(request);

				if (response.isException())
					System.out.println("Exception response: message=" + response.getExceptionMessage());
				else{
					String rbd=null;
					if(response.getBinData().length()<8) {
						rbd=response.getBinData()+"00";
					}else {
						rbd=response.getBinData();
					}
					retmessage =  rbd.substring(0,8);
					String rm1 = jzt.getTargetNo();
					String rm2 = jzt.getAddress();
					// 不清楚应该是低位在前，高位在前
					String rm = QudongUtils.getM7000DString(retmessage, rm2);
					resList.add(rm1+"="+rm);
				}
			}
			catch (QudongTransportException e) {
				e.printStackTrace();
			}
			tmpInstruct = jzt.getInstruct();
		}
	}


	private void handlekStar(List<JstZcTarget> jztList, List resList, JSONObject jsonConInfo) {
		int slaveId = Integer.parseInt(jsonConInfo.getString("slave"));
		QwertMaster master = QudongUtils.getQwertMaster(jsonConInfo);
		String tmpInstruct=null;
		String retmessage=null;
		for (int i = 0; i < jztList.size(); i++) {
			JstZcTarget jzt = jztList.get(i);
			String instruct = jzt.getInstruct();
			if(instruct.equals(tmpInstruct)){
				String rm1 = jzt.getTargetNo();
				String rm2 = jzt.getAddress();
				String rm = getKstarString(retmessage, rm2);

				resList.add(rm1+"="+rm);
				continue;
			}
			try {
				ReadKstarRequest request = new ReadKstarRequest(slaveId, 81,49);
				ReadKstarResponse response = (ReadKstarResponse) master.send(request);

				if (response.isException())
					System.out.println("Exception response: message=" + response.getExceptionMessage());
				else{
					System.out.println(response.getMessage());
					retmessage =  response.getMessage();
					String rm1 = jzt.getTargetNo();
					String rm2 = jzt.getAddress();
					String rm = getKstarString(retmessage, rm2);
					resList.add(rm1+"="+rm);
				}
			}
			catch (QudongTransportException e) {
				e.printStackTrace();
			}
			tmpInstruct = jzt.getInstruct();
		}
	}

	@Nullable
	private String getKstarString(String retmessage, String rm2) {
		String rm = null;
		if(rm2.indexOf("_")!=-1) {
			String[] rm3 = rm2.split("_");
			rm = retmessage.substring(Integer.parseInt(rm3[0]) - 1, Integer.parseInt(rm3[0]) + Integer.parseInt(rm3[1]) -1);
		}
		if(rm2.indexOf("$")!=-1) {
			String[] rm3 = rm2.split("\\$");
			int rm4 = Integer.parseInt(rm3[0]) - 1;
			int rm5 = Integer.parseInt(rm3[0]) + Integer.parseInt(rm3[1]);
			rm = retmessage.substring(Integer.parseInt(rm3[0]) - 1, Integer.parseInt(rm3[0]) + Integer.parseInt(rm3[1])-1);
		}
		return rm;
	}

	private void handleDelta(List<JstZcTarget> jztList, List resList, JSONObject jsonConInfo) {
		int slaveId = Integer.parseInt(jsonConInfo.getString("slave"));
		QwertMaster master = QudongUtils.getQwertMaster(jsonConInfo);
		String tmpInstruct=null;
		String retmessage=null;
		for (int i = 0; i < jztList.size(); i++) {
			JstZcTarget jzt = jztList.get(i);
			String instruct = jzt.getInstruct();
			if(instruct.equals(tmpInstruct)){
				String rm1 = jzt.getTargetNo();
				String rm2 = jzt.getAddress();
				String rm = retString(retmessage, rm2);
				resList.add(rm1+"="+rm);
				continue;
			}
			try {
				ReadDeltaRequest request = new ReadDeltaRequest(slaveId, instruct);
				ReadDeltaResponse response = (ReadDeltaResponse) master.send(request);

				if (response.isException())
					System.out.println("Exception response: message=" + response.getExceptionMessage());
				else{
	//				System.out.println(response.getMessage());
					retmessage =  response.getMessage();
					String rm1 = jzt.getTargetNo();
					String rm2 = jzt.getAddress();
					String rm = retString(retmessage, rm2);
					resList.add(rm1+"="+rm);
				}
			}
			catch (QudongTransportException e) {
				e.printStackTrace();
			}
			tmpInstruct = jzt.getInstruct();
		}
		int rr=0;
	}

	private String retString(String retmessage, String rm2) {
		String retValue="--";
		int rn = rm2.lastIndexOf(",");
		String rm3 = rm2.substring(rn + 1);
		String rm4[] = rm3.split("\\)");
		String rm5 = rm4[0];
		String[] rm6 = retmessage.split(";");
		String r7 = rm6[Integer.parseInt(rm5)];
		if (rm2.indexOf("=") == -1) {
			retValue= r7;
		}else{
			String[] r8 = rm2.split("=");
			if(r7.equals(r8[1])){
				retValue= r7;
			}
		}
		return retValue;
	}

	private void handleSnmp(List<JstZcTarget> jztList, List resList, JSONObject jsonConInfo) {
		String version;
		String timeOut;
		String community;
		String ipAddress=jsonConInfo.getString("ipAddress");
		version = jsonConInfo.getString("version");
		timeOut = jsonConInfo.getString("timeOut");
		community = jsonConInfo.getString("community");
		if(community==null){
			community="public";
		}
		jztList.stream().sorted(Comparator.comparing(JstZcTarget::getInstruct));
		List<String> oidList = new ArrayList<String>();
		String tmpInstruct=null;
		String retmessage=null;
		for (int i = 0; i < jztList.size(); i++) {
			JstZcTarget jzt = jztList.get(i);
			String oidval = jzt.getInstruct();
			String rm1 = jzt.getTargetNo();
			String rm2 = jzt.getAddress();

			if(oidval.equals(tmpInstruct)){
				if(rm2!=null && jzt.getInfoType().equals("digital")){
					String rm = snmpString(retmessage, rm2);
					resList.add(rm1+"="+rm);
				};
				continue;
			}
			List snmpList = SnmpData.snmpGet(ipAddress, community, oidval,null);

			if(snmpList.size()>0) {
				String tmpRet = (String) snmpList.get(0);
				String rm=null;
				if(tmpRet!=null&&!tmpRet.equals("")){
					retmessage = tmpRet.split("=")[1];
				}
				if(rm2==null||rm2.equals("")){
					resList.add(rm1+"="+retmessage);
				}else{
					if(jzt.getInfoType().equals("digital")){
						rm = snmpString(retmessage, rm2);
						resList.add(rm1+"="+rm);
					}
				}

//				for(int j=0;j<snmpList.size();j++) {
//					resList.add(snmpList.get(j));
//				}
			}
			tmpInstruct=jzt.getInstruct();
		}
	}

	@Nullable
	private String snmpString(String retmessage, String rm2) {
		String rm = null;
		retmessage=retmessage.trim();
		if(rm2.indexOf("$")!=-1) {
			String[] rm3 = rm2.split("\\$");
			int rm4 = Integer.parseInt(rm3[0]);
			rm = retmessage.substring(rm4, rm4+1);
		}else{
			String binaryStr = Integer.toBinaryString(Integer.valueOf(retmessage.trim()));
			while(binaryStr.length() < 16){
				binaryStr = "0"+binaryStr;
			}
			int pos = 15 - Integer.valueOf(rm2);
			rm = binaryStr.substring(pos,pos+1);
		}
		return rm;
	}

	public void handleModbus(String type,String devNo,String catNo, List<JstZcTarget> jztList, List resList, JSONObject jsonConInfo)
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
		String stime = jsonConInfo.getString("sleeptime");
		int sleeptime=JstConstant.sleeptime;
		if(stime!=null && !stime.equals("")) {
			sleeptime=Integer.parseInt(stime);
		}
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
			slaveId = Integer.parseInt(slave,16);
			BatchRead<String> batch = new BatchRead<String>();
			String tmpInstruct = null;
			int pointNumber = 0;
			int tmp2Offset = 0;
			boolean batchSend = false;
			for (int i = 0; i < jztList.size(); i++) {
				JstZcTarget jzt = jztList.get(i);
				String di = jzt.getInstruct().substring(0,2);
				int offset=0;
				String ta = jzt.getAddress();
				String[] tas=null;
				if(ta!=null&&ta.indexOf(".")!=-1){
					tas = ta.split("\\.");
					ta=tas[0];
				}
				if(ta!=null&&ta.indexOf("(")!=-1){
					tas = ta.split("\\(");
					ta=tas[0];
				}
				if(jzt.getAddressType()!=null && jzt.getAddressType().equals("HEX")){
					offset = Integer.parseInt(ta,16);
				}else{
					offset = Integer.parseInt(ta);
				}

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
					Thread.sleep(sleeptime);
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

				String dataType = "2";
				if(jzt.getDataType()!=null){
					dataType=jzt.getDataType();
				}
				if (di.equals("04")) {
					if(catNo.trim().equals("D86")){
						ReadInputRegistersRequest request = new ReadInputRegistersRequest(slaveId, offset, 2);
						ReadInputRegistersResponse response = (ReadInputRegistersResponse) master.send(request);
						if (response.isException()){
							System.out.println("Exception response: message=" + response.getExceptionMessage());
						}else{
							short[] retMessage = response.getShortData();
							String rm1 = jzt.getId();
		//					resList.add(rm1+"="+retMessage[0]);
							resList.add("{"+rm1+"="+retMessage[0]+"}");
						}
					}else{
						batch.addLocator(jzt.getId(),
								BaseLocator.inputRegister(slaveId, offset, Integer.parseInt(dataType)));
						batchSend = true;
					}
				}
				if (di.equals("03")) {
						batch.addLocator(jzt.getId(),
								BaseLocator.holdingRegister(slaveId, offset, Integer.parseInt(dataType)));
						batchSend = true;
				}
				if (di.equals("02")) {
					batch.addLocator(jzt.getId(), BaseLocator.inputStatus(slaveId, offset));
					batchSend = true;
				}
				Thread.sleep(sleeptime/2);
			}
			if (batchSend == true) {
				results = master.send(batch);
				Thread.sleep(sleeptime);
				if(JstConstant.debugflag==1) {
					System.out.println(results);
				}
				resList.add(results.toString());
//				resList.add("{gf10001c027=22354}");
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
			if(hr.equals("stop")){
				JstConstant.runflag=false;
			}
		}
		return Result.ok("ok",JstConstant.runflag);
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
