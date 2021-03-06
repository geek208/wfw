package com.hadron.wfw.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 
 * @author xuychao
 * @date 2022年3月15日
 * @classname WfwOrg.java
 * @email xuychao@163.com  git@github.com:geek208/wfw.git
 */

@Entity
@Table(name = "t_wfw_org")
@Data
@AllArgsConstructor
@NoArgsConstructor 
public class WfwOrg {

    
    @Id
    @GeneratedValue
    private long id;
    //上级部门
    private long parentId;
    //组织负责人
    //
    private String managerId;
    private String chargeId;
    private String orgName;
}
