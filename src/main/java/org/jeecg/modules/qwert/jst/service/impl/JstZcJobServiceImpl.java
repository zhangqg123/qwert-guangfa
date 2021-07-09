package org.jeecg.modules.qwert.jst.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.jna.Library;
import com.sun.jna.Native;
import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.qwert.conn.dbconn.mongo.common.model.Alarm;
import org.jeecg.modules.qwert.conn.dbconn.mongo.common.model.Audit;
import org.jeecg.modules.qwert.conn.dbconn.mongo.repository.impl.DemoRepository;
import org.jeecg.modules.qwert.conn.modbus4j.source.BatchRead;
import org.jeecg.modules.qwert.conn.modbus4j.source.BatchResults;
import org.jeecg.modules.qwert.conn.modbus4j.source.ModbusFactory;
import org.jeecg.modules.qwert.conn.modbus4j.source.ModbusMaster;
import org.jeecg.modules.qwert.conn.modbus4j.source.exception.ErrorResponseException;
import org.jeecg.modules.qwert.conn.modbus4j.source.exception.ModbusInitException;
import org.jeecg.modules.qwert.conn.modbus4j.source.exception.ModbusTransportException;
import org.jeecg.modules.qwert.conn.modbus4j.source.ip.IpParameters;
import org.jeecg.modules.qwert.conn.modbus4j.source.locator.BaseLocator;
import org.jeecg.modules.qwert.conn.modbus4j.test.TestSerialPortWrapper;
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
import org.jeecg.modules.qwert.jst.entity.*;
import org.jeecg.modules.qwert.jst.mapper.JstZcDevMapper;
import org.jeecg.modules.qwert.jst.service.*;
import org.jeecg.modules.qwert.jst.utils.JstConstant;
import org.jeecg.modules.qwert.jst.work.TestDll1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: jst_zc_dev
 * @Author: jeecg-boot
 * @Date:   2020-07-24
 * @Version: V1.0
 */
@Service
public class JstZcJobServiceImpl extends ServiceImpl<JstZcDevMapper, JstZcDev> implements IJstZcJobService {
	@Lazy
	@Resource
	private RedisUtil redisUtil;
	@Autowired
	private IJstZcCatService jstZcCatService;
	@Resource
	private JstZcDevMapper jstZcDevMapper;
	@Autowired
	private IJstZcTargetService jstZcTargetService;
	@Autowired
	private IJstZcAlarmService jstZcAlarmService;
	@Autowired
	private IJstZcConfigService jstZcConfigService;
	@Autowired
	private IJstZcDevService jstZcDevService;
    @Autowired
    DemoRepository repository;
    
    private List<JstZcCat> jzcList;
    private List<JstZcDev> jzdList;    
    private List<JstZcTarget> jztList;
    
	public List<JstZcDev> queryJzdList2(String catNo) {
		List<JstZcDev> jzdList = this.jstZcDevMapper.queryJzdList2(catNo);
		return jzdList;
	}
	

	/**
	 * 扫描分类
	 * 
	 * @return
	 * @throws JMSException 
	 */
	@Override
	public void readCat(String catOrigin) {
		long start, end;
		start = System.currentTimeMillis();
		List<JstZcDev> jzdCollect = queryJzdList2(catOrigin);
		for (int i = 0; i < jzdCollect.size(); i++) {
//			if(!JstConstant.runflag) {
//				break;
//			}
			JstZcDev dev = jzdCollect.get(i);
			readDev(dev.getId());
		}
		end = System.currentTimeMillis();
		System.out.println(catOrigin+" 开始时间:" + start + "; 结束时间:" + end + "; 用时:" + (end - start) + "(ms)");
	}

	@Override
	public void readDev(String devNos) {
		long nstart, nend;
		nstart = System.currentTimeMillis();
		String[] tmpDevNos = devNos.split(",");
		for(int i=0;i<tmpDevNos.length;i++) {
			long start, end;
			start = System.currentTimeMillis();

			String devNo = tmpDevNos[i];
			List resList = new ArrayList();
			//	JstZcDev jzd = getById(devId);
			//	String devNo=jzd.getDevNo();
			QueryWrapper<JstZcDev> queryWrapper = new QueryWrapper<JstZcDev>();
			queryWrapper.eq("dev_no", devNo);
			JstZcDev jzd = jstZcDevService.getOne(queryWrapper);
			String devName = jzd.getDevName();
			String catNo = jzd.getDevCat();
			String orgUser = jzd.getOrgUser();
			String modNo = jzd.getModNo();
			if (modNo == null || modNo.equals("")) {
				modNo = "blank";
			}
			String conInfo = jzd.getConInfo();
			JSONObject jsonConInfo = JSON.parseObject(conInfo);
			String type = jsonConInfo.getString("type");
			String proType = jsonConInfo.getString("proType");
			String stime = jsonConInfo.getString("sleeptime");
			int sleeptime = JstConstant.sleeptime;
			if (stime != null && !stime.equals("")) {
				sleeptime = Integer.parseInt(stime);
			}


			List<JstZcTarget> jztCollect = null;
			if (orgUser.equals("guangfa")) {
				jztCollect = jstZcTargetService.queryJztList5(devNo);
			}
			if (orgUser.equals("jinshitan")) {
				jztCollect = jstZcTargetService.queryJztList4(catNo);
			}
			boolean dbflag = false;
			boolean alarmflag=false;
			String alarm=null;
			if (proType.toUpperCase().equals("7000D")) {
				alarm=handleM7000d(jsonConInfo, resList, jztCollect);
				alarmflag=true;
				dbflag=true;
			}
			if (proType.toUpperCase().equals("PMBUS")) {
				handlePmbus(jztCollect, resList, jsonConInfo);
				alarmflag=true;
				dbflag=true;
			}
			if (proType.toUpperCase().equals("KSTAR")) {
				alarm=handlekStar(jztCollect, resList, jsonConInfo);
				alarmflag=true;
				dbflag=true;
			}
			if (proType.toUpperCase().equals("DELTA")) {
				alarm=handleDelta(jztCollect, resList, jsonConInfo);
				alarmflag=true;
				dbflag=true;
			}
			if (proType.toUpperCase().equals("MODBUS")) {
				handleModbus(type, resList, devNo, devName, catNo, jsonConInfo, sleeptime,
						jztCollect);
				dbflag=true;
			}

			if (proType.toUpperCase().equals("SNMP")) {
				handleSnmp(type, resList, devNo, devName, catNo, jsonConInfo, jztCollect);
				dbflag=true;
			}
			if(dbflag||alarmflag) {
				if(alarmflag) {
					String[] tmpAlarm = alarm.split("::");
					String alarmNo = tmpAlarm[0];
					String alarmValue = tmpAlarm[1];
					JstZcAlarm jstZcAlarm = new JstZcAlarm();
					jstZcAlarm.setDevNo(devNo);
					jstZcAlarm.setDevName(devName);
					jstZcAlarm.setCatNo(catNo);
					jstZcAlarm.setTargetNo(alarmNo);
					jstZcAlarm.setAlarmValue(alarmValue);
					jstZcAlarm.setSendTime(new Date());
					jstZcAlarm.setSendType("2");
					jstZcAlarmService.saveSys(jstZcAlarm);
				}
				handleDbMq(resList, devNo);
			}
			end = System.currentTimeMillis();
			System.out.println(devName+" 用时:" + (end - start) + "(ms)");
		}
		nend = System.currentTimeMillis();
		System.out.println(devNos+" 开始时间:" + nstart + "; 结束时间:" + nend + "; 用时:" + (nend - nstart) + "(ms)");
	}

	private String handlekStar(List<JstZcTarget> jztList, List resList, JSONObject jsonConInfo) {
		int slaveId = Integer.parseInt(jsonConInfo.getString("slave"));
		QwertMaster master = QudongUtils.getQwertMaster(jsonConInfo);
		String tmpInstruct=null;
		String retmessage=null;
		String alarmNo=null;
		String alarmValue=null;
		for (int i = 0; i < jztList.size(); i++) {
			JstZcTarget jzt = jztList.get(i);
			String instruct = jzt.getInstruct();
			String devNo=jzt.getDevNo();
			String tmpAlarm=null;
			if(instruct.equals(tmpInstruct)){
				String rm1 = jzt.getTargetNo();
				String rm2 = jzt.getAddress();
				String rm = QudongUtils.getKstarString(retmessage, rm2);
				tmpAlarm=getAlarm(jzt, devNo, rm);
				if(tmpAlarm!=null) {
					String[] ta = tmpAlarm.split(":::");
					alarmNo+=ta[0];
					alarmValue+=ta[1];
				}
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
					String rm = QudongUtils.getKstarString(retmessage, rm2);
					tmpAlarm=getAlarm(jzt, devNo, rm);
					if(tmpAlarm!=null) {
						String[] ta = tmpAlarm.split(":::");
						alarmNo += ta[0];
						alarmValue += ta[1];
					}
					resList.add(rm1+"="+rm);
				}
			}
			catch (QudongTransportException e) {
				e.printStackTrace();
			}
			tmpInstruct = jzt.getInstruct();
		}
		return alarmNo+"::"+alarmValue;
	}

	private void handlePmbus(List<JstZcTarget> jztList, List resList, JSONObject jsonConInfo) {
		int slaveId = Integer.parseInt(jsonConInfo.getString("slave"));
		QwertMaster master = QudongUtils.getQwertMaster(jsonConInfo);
		String tmpInstruct=null;
		byte[] retmessage = null;
		String alarmNo=null;
		String alarmValue=null;
		for (int i = 0; i < jztList.size(); i++) {
			JstZcTarget jzt = jztList.get(i);
			String instruct = jzt.getInstruct();
			String devNo=jzt.getDevNo();
			String tmpAlarm=null;
			if(instruct.equals(tmpInstruct)){
				String rm1 = jzt.getTargetNo();
				String rm2 = jzt.getAddress();
				String rm = QudongUtils.getPmBus(retmessage, rm2);
				tmpAlarm=getAlarm(jzt, devNo, rm);
				if(tmpAlarm!=null) {
					String[] ta = tmpAlarm.split(":::");
					alarmNo += ta[0];
					alarmValue += ta[1];
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
					String rm = QudongUtils.getPmBus(retmessage, rm2);
					tmpAlarm=getAlarm(jzt, devNo, rm);
					if(tmpAlarm!=null) {
						String[] ta = tmpAlarm.split(":::");
						alarmNo += ta[0];
						alarmValue += ta[1];
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

	private String handleDelta(List<JstZcTarget> jztList, List resList, JSONObject jsonConInfo) {
		int slaveId = Integer.parseInt(jsonConInfo.getString("slave"));
		QwertMaster master = QudongUtils.getQwertMaster(jsonConInfo);
		String tmpInstruct=null;
		String retmessage=null;
		String alarmNo=null;
		String alarmValue=null;
		for (int i = 0; i < jztList.size(); i++) {
			JstZcTarget jzt = jztList.get(i);
			String instruct = jzt.getInstruct();
			String devNo=jzt.getDevNo();
			String tmpAlarm=null;
			if(instruct.equals(tmpInstruct)){
				String rm1 = jzt.getTargetNo();
				String rm2 = jzt.getAddress();
				String rm = QudongUtils.getDeltaString(retmessage, rm2);
				tmpAlarm=getAlarm(jzt, devNo, rm);
				if(tmpAlarm!=null) {
					String[] ta = tmpAlarm.split(":::");
					alarmNo += ta[0];
					alarmValue += ta[1];
				}
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
					String rm = QudongUtils.getDeltaString(retmessage, rm2);
					tmpAlarm=getAlarm(jzt, devNo, rm);
					if(tmpAlarm!=null) {
						String[] ta = tmpAlarm.split(":::");
						alarmNo += ta[0];
						alarmValue += ta[1];
					}
					resList.add(rm1+"="+rm);
				}
			}
			catch (QudongTransportException e) {
				e.printStackTrace();
			}
			tmpInstruct = jzt.getInstruct();
		}
		return alarmNo+"::"+alarmValue;
	}

	private String handleM7000d(JSONObject jsonConInfo, List resList, List<JstZcTarget> jztCollect) {
		int slaveId = Integer.parseInt(jsonConInfo.getString("slave"));
		QwertMaster master = QudongUtils.getQwertMaster(jsonConInfo);
		String tmpInstruct=null;
		String retmessage=null;
		String alarmNo=null;
		String alarmValue=null;
		for (int i = 0; i < jztCollect.size(); i++) {
			JstZcTarget jzt = jztCollect.get(i);
			String instruct = jzt.getInstruct();
			String devNo=jzt.getDevNo();
			String tmpAlarm=null;
			if(instruct.equals(tmpInstruct)){
				String rm1 = jzt.getTargetNo();
				String rm2 = jzt.getAddress();
				String rm = QudongUtils.getM7000DString(retmessage, rm2);
				tmpAlarm=getAlarm(jzt, devNo, rm);
				if(tmpAlarm!=null) {
					String[] ta = tmpAlarm.split(":::");
					alarmNo += ta[0];
					alarmValue += ta[1];
				}
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
					tmpAlarm=getAlarm(jzt, devNo, rm);
					if(tmpAlarm!=null){
						String[] ta = tmpAlarm.split(":::");
						alarmNo+=ta[0];
						alarmValue+=ta[1];
					}
					resList.add(rm1+"="+rm);
				}
			}
			catch (QudongTransportException e) {
				e.printStackTrace();
			}
			tmpInstruct = jzt.getInstruct();
		}
		return alarmNo+"::"+alarmValue;
	}

	private String getAlarm(JstZcTarget jzt, String devNo, String rm) {
		String tmpAlarm=null;
		String rkey = devNo + "::" + jzt.getTargetNo();
		String evt = jzt.getEvt01();
		String rvalue = rm;
		Object keyValue = redisUtil.get(rkey);
		if(evt!=null) {
			if (keyValue == null || !keyValue.toString().equals(rvalue)) {
				redisUtil.set(rkey, rvalue);
				if(keyValue!=null) {
					String tmpAlarmNo = jzt.getId() + ",";
					String message = evt;
					if (rvalue.equals("0")) {
						message = jzt.getEvt10();
					}
					String tmpAlarmValue = jzt.getTargetName() + "-" + message + "-" + keyValue + "to" + rvalue + ",";
					tmpAlarm=tmpAlarmNo+":::"+tmpAlarmValue;
				}
			}
		}else{
			redisUtil.set(rkey, rvalue);
			if(keyValue!=null){
				if(Float.parseFloat((String) rvalue)>jzt.getValMax()){
					String tmpAlarmNo = jzt.getId() + ",";
					String message = String.format(jzt.getHighInfo(),rvalue);
					String tmpAlarmValue = jzt.getTargetName() + "-" + message + "-" + keyValue + "to" + rvalue + ",";
					tmpAlarm=tmpAlarmNo+":::"+tmpAlarmValue;
				}
				if(Float.parseFloat((String) rvalue)<jzt.getValMin()){
					String tmpAlarmNo = jzt.getId() + ",";
					String message = String.format(jzt.getLowInfo(),rvalue);
					String tmpAlarmValue = jzt.getTargetName() + "-" + message + "-" + keyValue + "to" + rvalue + ",";
					tmpAlarm=tmpAlarmNo+":::"+tmpAlarmValue;
				}
			}
		}
		return tmpAlarm;
	}


	private void handleSnmp(String type,List resList,String devNo, String devName, String catNo, JSONObject jsonConInfo, List<JstZcTarget> jztCollect)  {
		String ipAddress = jsonConInfo.getString("ipAddress");
		String version = jsonConInfo.getString("version");
		String timeOut = jsonConInfo.getString("timeOut");
		String community = jsonConInfo.getString("community");
		jztCollect.stream().sorted(Comparator.comparing(JstZcTarget::getInstruct));
		List<String> oidList = new ArrayList<String>();
		//		System.out.println(devNo+"::");
		for (int j = 0; j < jztCollect.size(); j++) {
			JstZcTarget jzt = jztCollect.get(j);
			String oidval = jzt.getInstruct();
			List snmpList = null ;
			snmpList = SnmpData.snmpGet(ipAddress, community, oidval,null);
			if(snmpList.size()>0) {
				for(int n=0;n<snmpList.size();n++) {
					if(catNo.equals("MicroHtm")) {
						String tmpSnmp=(String) snmpList.get(n);
						if(tmpSnmp!=null) {
						//	tmpSnmp=tmpSnmp.replaceAll(" ", "");
							String[] tmps = tmpSnmp.split("=");
							String t1 = tmps[1].replaceAll(" ", "");
		//					if(t1.equals("Null")) {
		//						System.out.println("Null");
		//					}
							if(t1!=null&&!t1.equals("Null")) {
								if(Float.parseFloat(t1)>35||Float.parseFloat(t1)<10) {
									List<JstZcAlarm> jzaList = jstZcAlarmService.queryJzaList("2");
									int dealflag=0; //初始状态
									for(int ai=0;ai<jzaList.size();ai++) {
										JstZcAlarm jza = jzaList.get(ai);
										if(jza.getDevNo().equals(devNo)&&jza.getTargetNo().equals(jzt.getTargetNo())) {
											if(jza.getDealType()=="1") {  //已处理
												JstZcAlarm jstZcAlarm = new JstZcAlarm();
												jstZcAlarm.setDevNo(devNo);
												jstZcAlarm.setDevName(devName);
												jstZcAlarm.setCatNo(catNo);
												jstZcAlarm.setTargetNo(jzt.getTargetNo());
												jstZcAlarm.setAlarmValue(jzt.getTargetName());
												jstZcAlarm.setSendTime(new Date());
												jstZcAlarm.setSendType("2");
												jstZcAlarmService.saveSys(jstZcAlarm);
												dealflag=2; //已处理
										//		break;
											}else {
												dealflag=1; //未处理
												jza.setSendTime(new Date());
												jstZcAlarmService.updateSys(jza);
											}
											break;
										}
									}
									if(dealflag==0 || dealflag==2) {
										JstZcAlarm jstZcAlarm = new JstZcAlarm();
										jstZcAlarm.setDevNo(devNo);
										jstZcAlarm.setDevName(devName);
										jstZcAlarm.setCatNo(catNo);
										jstZcAlarm.setTargetNo(jzt.getTargetNo());
										jstZcAlarm.setAlarmValue(jzt.getTargetName());
										jstZcAlarm.setSendTime(new Date());
										jstZcAlarm.setSendType("2");
										jstZcAlarmService.saveSys(jstZcAlarm);
									}
								}
							}
						}
//								System.out.println(snmpList.get(n));
					}
					resList.add(snmpList.get(n));
				}
			}else {
				break;
			}
//			handleDbMq(type, resList, devNo);
		}
	}

	private void handleDbMq(List resList, String devNo) {
		String resValue = org.apache.commons.lang.StringUtils.join(resList.toArray(),";");
/*		Audit audit = new Audit();
		audit.setDevNo(devNo);
		audit.setAuditValue(resValue);
		audit.setAuditTime(new Date());
		repository.insertAudit(audit); */
		redisUtil.set(devNo,resValue);
		redisUtil.set(devNo+"::"+ DateUtils.formatTime(),resValue);
		redisUtil.expire(devNo+"::"+ DateUtils.formatTime(), 7200);
	}

	private void handleModbus(String type, List resList, String devNo,
							String devName, String catNo, JSONObject jsonConInfo, int sleeptime, List<JstZcTarget> jztCollect)  {
		BatchResults<String> results;
		String slave = jsonConInfo.getString("slave");
		String packageBit = jsonConInfo.getString("packageBit");
		String bitNumber = jsonConInfo.getString("bitNumber");
		int pb=64;
		if(packageBit!=null&&!packageBit.equals("")) {
			pb=Integer.parseInt(packageBit);
		}
		int bn=10;
		if(bitNumber!=null&&!bitNumber.equals("")) {
			bn=Integer.parseInt(bitNumber);
		}
		
		String timeOut = jsonConInfo.getString("timeOut");

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
		if(type.equals("SOCKET")||type.equals("MODBUSTCP")||type.equals("TCP")) {
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
		if(type.equals("MODBUSTCP")||type.equals("SOCKET")||type.equals("TCP")) {
			master = modbusFactory.createTcpMaster(ipParameters, false);
		}
		boolean flag = false;
		try {
			master.init();
			int slaveId = 0;
			slaveId = Integer.parseInt(slave);
			BatchRead<String> batch = new BatchRead<String>();
			String targetNos="";
			String tmpInstruct = null;
			int pointNumber = 0;
			int tmp2Offset=0;
			boolean batchSend = false;
			if(jztCollect.size()>0) {
				boolean alarmFlag = false;
				for (int j = 0; j < jztCollect.size(); j++) {

					JstZcTarget jzt = jztCollect.get(j);
					String di = jzt.getInstruct().substring(0, 2);

			//		int	offset = Integer.parseInt(jzt.getAddress());
					int offset=0;
					String ta = jzt.getAddress();
					String[] tas=null;
					if(ta!=null&&ta.indexOf(".")!=-1){
						tas = ta.split("\\.");
						ta=tas[0];
					}
					if(jzt.getAddressType()!=null && jzt.getAddressType().equals("HEX")){
						offset = Integer.parseInt(ta,16);
					}else{
						offset = Integer.parseInt(ta);
					}

					if (pointNumber>0 && offset==tmp2Offset) {
						continue;
					}
					if (pointNumber>0 && (pointNumber > pb || (offset - tmp2Offset > bn))) {
						flag = true;
					}
					
					tmp2Offset=offset;
					pointNumber++;
					if (flag == true) {
						
						results = master.send(batch);
						Thread.sleep(sleeptime);
						if(results.toString().equals("{}")) {
							if(JstConstant.debugflag==1) {
								System.out.println("{}:"+targetNos);
							}
							if(JstConstant.poweroff==0) {
								JstZcAlarm jstZcAlarm = new JstZcAlarm();
								jstZcAlarm.setDevNo(devNo);
								jstZcAlarm.setDevName(devName);
								jstZcAlarm.setCatNo(catNo);
								jstZcAlarm.setTargetNo(targetNos);
								jstZcAlarm.setAlarmValue("connection-fail");
								jstZcAlarm.setSendTime(new Date());
								jstZcAlarm.setSendType("0");
								jstZcAlarmService.saveSys(jstZcAlarm);
								alarmFlag=true;
			//					System.out.println(devNo+"::connection-fail");
			//					break;
							}
						}else {
							resList.add(results.toString());
						}
						if(JstConstant.debugflag==1) {
							System.out.println(devNo+"::"+results);
						}
						batch = new BatchRead<String>();
						targetNos="";
						flag = false;
						pointNumber = 0;
					}

					String res = null;
					Map<String, String> resMap = new HashMap<String, String>();
					String dataType = "2";
					if(jzt.getDataType()!=null){
						dataType=jzt.getDataType();
					}

					if (di.equals("04")) {
						targetNos=targetNos+jzt.getId()+",";
						batch.addLocator(jzt.getId(), BaseLocator.inputRegister(slaveId, offset,
								Integer.parseInt(dataType)));
						batchSend = true;
					}
					if (di.equals("03")) {
						targetNos=targetNos+jzt.getId()+",";
						batch.addLocator(jzt.getId(), BaseLocator.holdingRegister(slaveId, offset,
								Integer.parseInt(dataType)));
						batchSend = true;
					}
					if (di.equals("02")) {
						batch.addLocator(jzt.getId(), BaseLocator.inputStatus(slaveId, offset));
						batchSend = true;
					}
					Thread.sleep(sleeptime/2);
				}
				if (batchSend == true && alarmFlag==false) {
					results = master.send(batch);
					Thread.sleep(sleeptime);

					if(results.toString().equals("{}")) {
						if(JstConstant.debugflag==1) {
							System.out.println("{}:"+targetNos);
						}
						if(JstConstant.poweroff==0){
							JstZcAlarm jstZcAlarm = new JstZcAlarm();
							jstZcAlarm.setDevNo(devNo);
							jstZcAlarm.setDevName(devName);
							jstZcAlarm.setCatNo(catNo);
		//					jstZcAlarm.setTargetNo("connection-fail");
							jstZcAlarm.setTargetNo(targetNos);
							jstZcAlarm.setAlarmValue("connection-fail");
							jstZcAlarm.setSendTime(new Date());
							jstZcAlarm.setSendType("0");
							jstZcAlarmService.saveSys(jstZcAlarm);
//						System.out.println(devNo+"::connection-fail");
						}
					}else {
						if(JstConstant.debugflag==1) {
							System.out.println(devNo+"::"+results);
						}
						resList.add(results.toString());
					}
					if(JstConstant.debugflag==1) {
						System.out.println(devNo+"::"+resList.size());
					}
				}
			//	resList.add("{gf10001x0010=12345}");

				String alarm=null;
				if(resList.size()>0) {
					alarm=trackAlarm(devNo,resList, jztCollect);
				}
				if(alarm!=null) {
//					AlarmSend as=new AlarmSend(alarm);
//					new Thread(as).start();

					String[] tmpAlarm = alarm.split("::");
					String alarmNo=tmpAlarm[0];
					String alarmValue=tmpAlarm[1];

					if(alarmValue.length()>0) {
						List<JstZcAlarm> jzaList = jstZcAlarmService.queryJzaList("2");
						int dealflag=0; //初始状态
						for(int ai=0;ai<jzaList.size();ai++) {
							JstZcAlarm jza = jzaList.get(ai);
							if(jza.getDevNo().equals(devNo)&&jza.getTargetNo().equals(alarmNo)) {
								if(jza.getDealType()=="1") {  //已处理
									JstZcAlarm jstZcAlarm = new JstZcAlarm();
									jstZcAlarm.setDevNo(devNo);
									jstZcAlarm.setDevName(devName);
									jstZcAlarm.setCatNo(catNo);
									jstZcAlarm.setTargetNo(alarmNo);
									jstZcAlarm.setAlarmValue(alarmValue);
									jstZcAlarm.setSendTime(new Date());
									jstZcAlarm.setSendType("2");
									jstZcAlarmService.saveSys(jstZcAlarm);
									dealflag=2; //已处理
							//		break;
								}else {
									dealflag=1; //未处理
									jza.setSendTime(new Date());
									jstZcAlarmService.updateSys(jza);
								}
								break;
							}
						}
						if(dealflag==0 || dealflag==2) {
							JstZcAlarm jstZcAlarm = new JstZcAlarm();
							jstZcAlarm.setDevNo(devNo);
							jstZcAlarm.setDevName(devName);
							jstZcAlarm.setCatNo(catNo);
							jstZcAlarm.setTargetNo(alarmNo);
							jstZcAlarm.setAlarmValue(alarmValue);
							jstZcAlarm.setSendTime(new Date());
							jstZcAlarm.setSendType("2");
							jstZcAlarmService.saveSys(jstZcAlarm);
						}
					}else {
						List<JstZcAlarm> jzaList = jstZcAlarmService.queryJzaList("1");
					//	List<JstZcAlarm> jzaList = jstZcAlarmService.queryJzaList("0");
						for(int ai=0;ai<jzaList.size();ai++) {
							JstZcAlarm jza = jzaList.get(ai);
							if(jza.getDevNo().equals(devNo)) {
								jza.setSendType("-2");
								jstZcAlarmService.updateSys(jza);
					//			jstZcAlarmService.deleteSys(jza.getId());
							}
						}
					}
				}
				Thread.sleep(sleeptime/2);
		    }
		} catch (ModbusInitException e) {
			e.printStackTrace();
		} catch (ModbusTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e)	            {
			e.printStackTrace();
		    Alarm alarm = new Alarm();
//			        alarm.setId("2");
		    alarm.setDevNo(devNo);
		    alarm.setTargetNo("mysql");
		    alarm.setAlarmValue("数据库保存失败");
		    alarm.setSendTime(new Date());
		    repository.insertAlarm(alarm);
		} finally {
			master.destroy();
		}
//		handleDbMq(type, resList, devNo);
	}

	private String trackAlarm(String devNo, List resList, List<JstZcTarget> jztCollect) {
		String alarmValue="";
		String alarmNo="";

		for(int ri=0;ri<resList.size();ri++) {
			String r1=(String) resList.get(ri);
			r1=r1.replaceAll(" ", "");
			r1=r1.substring(1, r1.length()-1);
			String[] r2 = r1.split(",");
			for(int rj=0;rj<r2.length;rj++) {
				String[] r3 = r2[rj].split("=");
				boolean jztfind = false;
				JstZcTarget jzt = jstZcTargetService.getById(r3[0]);

				if(jzt.getAlarmPoint()!=null && jzt.getInfoType()!=null ) {
					jztfind=true;
				}
				if(jzt.getAlarmPoint().equals("1")) {
					jztfind=true;
				}
	//			if(jzt.getInterceptBit()!=null && jzt.getInfoType()!=null) {
	//				jztfind=true;
	//			}
				if(jzt.getAddress().indexOf('.')!=-1){
					jztfind=true;
				}
				String rkey=null;
				String rvalue=null;
				if(!jztfind){
					rkey = devNo + "::" + jzt.getTargetNo();
					rvalue=r3[1];
					Object keyValue = redisUtil.get(rkey);
					if(keyValue==null || !keyValue.toString().equals(rvalue)){
						redisUtil.set(rkey, rvalue);
					}
				}else {
					if(jzt.getInfoType().equals("digital")||jzt.getInfoType().equals("状态量")){
						if(jzt.getAddress().indexOf(".")!=-1) {
							String tmpinstruct=jzt.getInstruct();
							String tmpaddress=jzt.getAddress();
							List<JstZcTarget> jztc=null;
							if(tmpaddress.indexOf('.')!=-1){
								String[] tas = tmpaddress.split("\\.");
								String ta = tas[0];
								jztc = jztCollect.stream().filter(u -> ta.equals((u.getAddress().split("\\."))[0])).collect(Collectors.toList());
							}else{
								String ta = jzt.getAddress();
								jztc = jztCollect.stream().filter(u -> ta.equals(u.getAddress())).collect(Collectors.toList());
							}

							for(int n=0;n<jztc.size();n++){
								JstZcTarget item = jztc.get(n);
								if(item.getAlarmPoint()==null||item.getAlarmPoint().equals("")||item.getAlarmPoint().equals("0")) {
									continue;
								}
								if(!item.getAlarmPoint().equals("1")) {
									continue;
								}
								String str1=r3[1];
								if(str1.equals("true")) {
									str1="1";
								}
								if(str1.equals("false")) {
									str1="0";
								}

								String binaryStr = Integer.toBinaryString(Integer.parseInt(str1));
								while(binaryStr.length() < 16){
									binaryStr = "0"+binaryStr;
								}
								String a6=null;
								if(item.getAddress().indexOf('.')==-1) {
									String a1 = item.getInterceptBit();
									String[] a2 = a1.split(",");
									String[] a3 = a2[0].split(":");
									int a4 = Integer.parseInt(a3[1]);
									int a5 = 15 - a4;
									a6 = binaryStr.substring(a5, a5 + 1);
								}else{
									String a1=item.getAddress();
									String[] a2 = a1.split("\\.");
									int a4 = Integer.parseInt(a2[1]);
									int a5 = 15 - a4;
									a6 = binaryStr.substring(a5, a5 + 1);
								}
								rkey = devNo + "::" + item.getTargetNo();
//								rvalue=a6;
								String evt = jzt.getEvt01();
								if(evt!=null) {
									rvalue = a6;
									Object keyValue = redisUtil.get(rkey);
									if (keyValue == null || !keyValue.toString().equals(rvalue)) {
										redisUtil.set(rkey, rvalue);
										alarmNo += jzt.getId() + ",";
										String message = evt;
										if (rvalue.equals("0")) {
											message = jzt.getEvt10();
										}
										alarmValue += jzt.getTargetName() + "-" + message + "-" + keyValue + "to" + rvalue + ",";
									}
								}else {
									String bjz = item.getCtrlUp();
									if (bjz != null) {
										if (bjz.indexOf("==") != -1) {
											String[] bj = bjz.split("==");
											if (a6.equals(bj[1])) {
												alarmNo += item.getId() + ",";
												alarmValue += item.getTargetName() + ",";
											}
										}
										if (bjz.indexOf("!=") != -1) {
											String[] bj = bjz.split("!=");
											if (!a6.equals(bj[1])) {
												alarmNo += item.getId() + ",";
												alarmValue += item.getTargetName() + ",";
											}
										}
									}
								}
							}
						}else {
							rkey = devNo + "::" + jzt.getTargetNo();
							String evt = jzt.getEvt01();
							if(evt!=null) {
								rvalue = r3[1];
								Object keyValue = redisUtil.get(rkey);
								if (keyValue == null || !keyValue.toString().equals(rvalue)) {
									redisUtil.set(rkey, rvalue);
									alarmNo += jzt.getId() + ",";
									String message = evt;
									if (rvalue.equals("0")) {
										message = jzt.getEvt10();
									}
									alarmValue += jzt.getTargetName() + "-" + message + "-" + keyValue + "to" + rvalue + ",";
								}
							}else {
								String bjz = jzt.getCtrlUp();
								if (bjz != null) {
									if (bjz.indexOf("==") != -1) {
										String[] bj = bjz.split("==");
										if (r3[1].equals(bj[1])) {
											alarmNo += jzt.getId() + ",";
											alarmValue += jzt.getTargetName() + ",";
										}
									}
									if (bjz.indexOf("!=") != -1) {
										String[] bj = bjz.split("!=");
										if (!r3[1].equals(bj[1])) {
											alarmNo += jzt.getId() + ",";
											alarmValue += jzt.getTargetName() + ",";
										}
									}
								}
							}
						}
					}else {
						rkey = devNo + "::" + jzt.getTargetNo();
						rvalue=r3[1];
						String yinzi=jzt.getYinzi();
						float r4=0f;
						if(rvalue.contains(".")) {
							int indexOf = rvalue.indexOf(".");
							rvalue = rvalue.substring(0, indexOf);
						}
						if(yinzi!=null) {
							r4=Integer.parseInt(rvalue)/Integer.parseInt(yinzi);
						}else {
							r4=Integer.parseInt(rvalue);
						}
						if(jzt.getValMax()!=null && jzt.getValMax()>0) {
							Object keyValue = redisUtil.get(rkey);
							String flag = null;
							String message=null;
							int r5=0;
							if (r4 > jzt.getValMax()) {
								flag = "1";
								if(jzt.getHighInfo().indexOf("%d")!=-1) {
									r5=(int) r4;
									message=String.format(jzt.getHighInfo(),r5);
								}else {
									message=String.format(jzt.getHighInfo(),r4);
								}
							}
							if (r4 < jzt.getValMin()) {
								flag = "1";
								if(jzt.getHighInfo().indexOf("%d")!=-1) {
									r5=(int) r4;
									message=String.format(jzt.getHighInfo(),r5);
								}else {
									message=String.format(jzt.getLowInfo(),r4);
								}
							}
							if (r4 > jzt.getValMin() && r4 < jzt.getValMax()) {
								flag = "0";
							}
							if (keyValue == null || !flag.equals(keyValue)) {
								redisUtil.set(rkey, flag);
								alarmNo += jzt.getId() + ",";
								alarmValue += jzt.getTargetName() +"-"+message+ "-报警值-" + r4 + "-" + keyValue + "to" + flag+",";
							}
						}else {
							String[] mn = jzt.getCtrlDown().split(";");
							for (int rm = 0; rm < mn.length; rm++) {
								yinzi = jzt.getYinzi();
								if (mn[rm].indexOf("<") != -1) {
									String a1 = mn[rm].replace("<", "").replace("=", "");
									r4 = 0f;
									String str = r3[1];
									if (str.contains(".")) {
										int indexOf = str.indexOf(".");
										str = str.substring(0, indexOf);
									}
									if (yinzi != null) {
										r4 = Integer.parseInt(str) / Integer.parseInt(yinzi);
									} else {
										r4 = Integer.parseInt(str);
									}
									if (r4 <= Integer.parseInt(a1)) {
										alarmNo += jzt.getId() + ",";
										alarmValue += jzt.getTargetName() + "-报警值-" + r4 + ",";
									}
								}
								if (mn[rm].indexOf(">") != -1) {
									String a1 = mn[rm].replace(">", "").replace("=", "");
									//		String yinzi=jzt.getYinzi();
									r4 = 0f;
									String str = r3[1];
									if (str.contains(".")) {
										int indexOf = str.indexOf(".");
										str = str.substring(0, indexOf);
									}
									if (yinzi != null) {
										r4 = Integer.parseInt(str) / Integer.parseInt(yinzi);
									} else {
										r4 = Integer.parseInt(str);
									}
									if (r4 >= Integer.parseInt(a1)) {
										alarmNo += jzt.getId() + ",";
										alarmValue += jzt.getTargetName() + "-报警值-" + r4 + ",";
									}
								}
							}
						}
					}
				}
			}
		}
		String alarm=null;
		if(alarmValue.length()>0) {
			alarm=alarmNo+"::"+alarmValue;
		}
		return alarm;
	}
	static class AlarmSend implements Runnable{
		private String alarm;
		AlarmSend(String alarm){
			this.alarm=alarm;
		}
/*		public interface TestDll1 extends Library {

			TestDll1 INSTANCE = (TestDll1) Native.loadLibrary("PhoneDll.dll", TestDll1.class);
			public int Dial(String phones,String alarmMessage);
		}*/
		public void run(){
			System.out.println("线程输出:"+alarm);
			System.setProperty("jna.encoding", "GBK");
			String phones="13898480908";
			String alarmMessage="2KT8,过滤网堵报警开关,,";
			int sret = TestDll1.INSTANCE.Dial(phones, alarmMessage);
			System.out.println("sret=" + sret);
		}
	}
}
