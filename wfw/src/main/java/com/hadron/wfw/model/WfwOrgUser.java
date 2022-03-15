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
 * @classname WfwOrgUser.java
 * @email xuychao@163.com  git@github.com:geek208/wfw.git
 */
@Entity
@Table(name = "t_wfw_org_user")
@Data
@AllArgsConstructor
@NoArgsConstructor 
public class WfwOrgUser {

    
    @Id
    @GeneratedValue
    private long id;
    private String userId;
    private String orgId;
}
