package org.jeecg.modules.qwert.conn.dbconn.mongo.repository.impl;

import org.jeecg.modules.qwert.conn.dbconn.mongo.common.model.User;
import org.jeecg.modules.qwert.conn.dbconn.mongo.common.utils.MongoUpdateUtils;
import org.jeecg.modules.qwert.conn.dbconn.mongo.repository.UserRepository;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 *  UserRepositoryImpl.java
 *
 *  @author ：Mc_GuYi
 *  @since  ：7/8/2019
 */
@Repository
public class UserRepositoryImpl extends BaseMongoRepositoryImpl<User, String>
        implements UserRepository {

    @Override
    public boolean update(User user) {
        Query query = Query.query(Criteria.where("id").is(user.getId()));
        Update update = MongoUpdateUtils.updateBase(user, true);
        return getMongoTemplate().updateFirst(query, update, User.class).getModifiedCount() > 0;
    }
}
