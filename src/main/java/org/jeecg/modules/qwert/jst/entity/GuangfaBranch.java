package org.jeecg.modules.qwert.jst.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: jst_zc_target
 * @Author: jeecg-boot
 * @Date:   2020-07-24
 * @Version: V1.0
 */
@Data
public class GuangfaBranch implements Serializable {
    private static final long serialVersionUID = 1L;
    private String tagName;
    private String pv;
    private String time;
}
