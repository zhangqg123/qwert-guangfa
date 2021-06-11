package org.jeecg.modules.qwert.jst.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
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
import org.jeecg.modules.qwert.conn.snmp.SnmpData;
import org.jeecg.modules.qwert.jst.entity.*;
import org.jeecg.modules.qwert.jst.mapper.JstZcDevMapper;
import org.jeecg.modules.qwert.jst.service.*;
import org.jeecg.modules.qwert.jst.utils.JstConstant;
import org.springframework.beans.factory.annotation.Autowired;
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
	public void readDev(String devId) {
		long start, end;
		start = System.currentTimeMillis();
		List resList = new ArrayList();
		JstZcDev jzd = getById(devId);
		String devNo=jzd.getDevNo();
		String devName=jzd.getDevName();
		String catNo = jzd.getDevCat();
		String orgUser = jzd.getOrgUser();
		String modNo=jzd.getModNo();
		if(modNo==null||modNo.equals("")) {
			modNo="blank";
		}
		String conInfo = jzd.getConInfo();
//			System.out.println(conInfo);
		JSONObject jsonConInfo = JSON.parseObject(conInfo);
		String ipAddress = jsonConInfo.getString("ipAddress");
		String port = jsonConInfo.getString("port");
		String type = jsonConInfo.getString("type");
		String proType = jsonConInfo.getString("proType");
		String stime = jsonConInfo.getString("sleeptime");
		int sleeptime= JstConstant.sleeptime;
		if(stime!=null && !stime.equals("")) {
			sleeptime=Integer.parseInt(stime);
		}

		String version = null;
		String community = null;

		BatchResults<String> results = null;
		List<JstZcTarget> jztCollect = null;
		if(orgUser.equals("guangfa")){
			jztCollect = jstZcTargetService.queryJztList5(devNo);
		}
		if(orgUser.equals("jinshitan")){
			jztCollect = jstZcTargetService.queryJztList4(catNo);
		}

//		if (type.equals("SOCKET")||type.equals("MODBUSRTU")||type.equals("MODBUSASCII")||type.equals("MODBUSTCP")) {
		if (proType.toUpperCase().equals("MODBUS")) {
			handleModbus(type, resList, devNo, devName, catNo, jsonConInfo, sleeptime,
					jztCollect);
		}

		if (proType.toUpperCase().equals("SNMP")) {
			handleSnmp(type, resList, modNo,devNo, devName, catNo, jsonConInfo, ipAddress, jztCollect);
		}
		end = System.currentTimeMillis();
		System.out.println(devName+" 开始时间:" + start + "; 结束时间:" + end + "; 用时:" + (end - start) + "(ms)");
	}

	private void handleSnmp(String type,List resList, String modNo,String devNo, String devName, String catNo, JSONObject jsonConInfo, String ipAddress, List<JstZcTarget> jztCollect)  {
		String version;
		String community;
		version = jsonConInfo.getString("version");
		String timeOut = jsonConInfo.getString("timeOut");
		community = jsonConInfo.getString("community");
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
			handelDbMq(type, resList, devNo);
		}
	}

	private void handelDbMq(String type, List resList, String devNo) {
		String resValue = org.apache.commons.lang.StringUtils.join(resList.toArray(),";");
		Audit audit = new Audit();
		audit.setDevNo(devNo);
		audit.setAuditValue(resValue);
		audit.setAuditTime(new Date());
		repository.insertAudit(audit);
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
		if(type.equals("SOCKET")||type.equals("MODBUSTCP")) {
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
					alarm=trackAlarm(resList, jztCollect);
				}
				if(alarm!=null) {
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
		handelDbMq(type, resList, devNo);
	}

	private String trackAlarm(List resList, List<JstZcTarget> jztCollect) {
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

				if(jzt.getAlarmPoint()!=null&&jzt.getAlarmPoint().equals("1")&& jzt.getInfoType()!=null ) {
					jztfind=true;
				}
				if(jzt.getInterceptBit()!=null && jzt.getInfoType()!=null) {
					jztfind=true;
				}
				if(jzt.getAddress().indexOf('.')!=-1){
					jztfind=true;
				}
				if(jztfind) {
					if(jzt.getInfoType().equals("digital")||jzt.getInfoType().equals("状态量")){
						if((jzt.getInterceptBit()!=null&&jzt.getInterceptBit().indexOf("bitIndex")!=-1)||
							jzt.getAddress().indexOf(".")!=-1) {
							String tmpinstruct=jzt.getInstruct();
							String tmpaddress=jzt.getAddress();
							List<JstZcTarget> jztc=null;
							if(tmpaddress.indexOf('.')!=-1){
								String[] tas = tmpaddress.split("\\.");
								String ta = tas[0];
								jztc = jztCollect.stream().filter(u -> ta.equals(u.getAddress().substring(0,4))).collect(Collectors.toList());
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
								String bjz = item.getCtrlUp();
								if(bjz!=null) {
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
						}else {
							String bjz = jzt.getCtrlUp();
							if(bjz!=null) {
								if(bjz.indexOf("==")!=-1) {
									String[] bj = bjz.split("==");
									if(r3[1].equals(bj[1])) {
										alarmNo+=jzt.getId()+",";
										alarmValue+=jzt.getTargetName()+",";
									}
								}
								if(bjz.indexOf("!=")!=-1) {
									String[] bj = bjz.split("!=");
									if(!r3[1].equals(bj[1])) {
										alarmNo+=jzt.getId()+",";
										alarmValue+=jzt.getTargetName()+",";
									}
								}
							}
						}
					}else {
						String[] mn = jzt.getCtrlDown().split(";");


						for(int rm=0;rm<mn.length;rm++) {
							String yinzi=jzt.getYinzi();
							if(mn[rm].indexOf("<")!=-1) {
								String a1 = mn[rm].replace("<", "").replace("=", "");
								float r4=0f;
								String str=r3[1];
								if(str.contains(".")) {
									 int indexOf = str.indexOf(".");
									 str = str.substring(0, indexOf);
								}
								if(yinzi!=null) {
									r4=Integer.parseInt(str)/Integer.parseInt(yinzi);
								}else {
									r4=Integer.parseInt(str);
								}
								if(r4<=Integer.parseInt(a1)) {
									alarmNo+=jzt.getId()+",";
									alarmValue+=jzt.getTargetName()+"-报警值-"+r4+",";
								}
							}
							if(mn[rm].indexOf(">")!=-1) {
								String a1 = mn[rm].replace(">", "").replace("=", "");
						//		String yinzi=jzt.getYinzi();
								float r4=0f;
								String str=r3[1];
								if(str.contains(".")) {
									 int indexOf = str.indexOf(".");
									 str = str.substring(0, indexOf);
								}
								if(yinzi!=null) {
									r4=Integer.parseInt(str)/Integer.parseInt(yinzi);
								}else {
									r4=Integer.parseInt(str);
								}
								if(r4>=Integer.parseInt(a1)) {
									alarmNo+=jzt.getId()+",";
									alarmValue+=jzt.getTargetName()+"-报警值-"+r4+",";
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

}
