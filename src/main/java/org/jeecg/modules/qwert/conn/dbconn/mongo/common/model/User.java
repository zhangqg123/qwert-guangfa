package org.jeecg.modules.qwert.conn.dbconn.mongo.common.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import org.jeecg.modules.qwert.conn.dbconn.mongo.common.enums.Gender;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *  User.java
 *
 *  @author ：machengxin
 *  @since  ：6/11/2019-2:30 PM
 */

@Getter
@Setter
@Document(collection = "t_user")
public class User  extends BaseModel{

    @Id
    private String id;

    private String name;

    private Integer age;

    private Gender gender;
    
    private Date uDate;

}
