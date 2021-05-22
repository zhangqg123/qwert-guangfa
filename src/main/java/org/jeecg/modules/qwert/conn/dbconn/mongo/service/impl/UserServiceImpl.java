package org.jeecg.modules.qwert.conn.dbconn.mongo.service.impl;

import org.jeecg.modules.qwert.conn.dbconn.mongo.common.model.User;
import org.jeecg.modules.qwert.conn.dbconn.mongo.repository.UserRepository;
import org.jeecg.modules.qwert.conn.dbconn.mongo.service.UserService;
import org.springframework.stereotype.Service;

/**
 *  UserServiceImpl.java
 *
 *  @author ：Mc_GuYi
 *  @since  ：7/8/2019
 */
@Service
public class UserServiceImpl extends BaseMongoServiceImpl<UserRepository, User, String> implements UserService {

}
