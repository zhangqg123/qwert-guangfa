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
import org.jeecg.modules.qwert.jst.mapper.GuangfaFormMapper;
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
public class GuangfaFormServiceImpl extends ServiceImpl<GuangfaFormMapper, GuangfaBranch> implements IGuangfaFormService {
	@Resource
	private GuangfaFormMapper guangfaFormMapper;

	@Override
	public List<GuangfaBranch> queryGfBranch() {
		List<GuangfaBranch> pvList = this.guangfaFormMapper.queryGfBranch();
		return pvList;
	}
}
