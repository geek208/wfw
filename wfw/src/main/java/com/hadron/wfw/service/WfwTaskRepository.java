package com.hadron.wfw.service;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.hadron.wfw.model.WfwActivity;
import com.hadron.wfw.model.WfwActivityUser;
import com.hadron.wfw.model.WfwFormField;
import com.hadron.wfw.model.WfwTask;


/**
 * 
 * @author xuychao
 * @date 2022年3月15日
 * @classname WfwTaskRepository.java
 * @email xuychao@163.com  git@github.com:geek208/wfw.git
 */
public interface WfwTaskRepository extends JpaRepository<WfwTask, Long> {
    /**
     * Find by id user.
     *
     * @param id the id
     * @return the user
     */
	WfwTask findById(long id);
	
	@Transactional
	@Modifying
	//@Query("update t_stock   set count = count-1 where id in ids",nativeQuery = true)
	@Query(value = "UPDATE t_wfw_task SET status =?2   WHERE id =?1",nativeQuery = true)
	public void updateTaskStatus(String taskId,String status);
	
	@Query(value = "select * from t_wfw_task  WHERE user_id =?1",nativeQuery = true)
	List <WfwTask>   findTask(String  userId);
	
	@Query(value = "select * from t_wfw_task  WHERE user_id =?1 AND status =?2",nativeQuery = true)
	List <WfwTask>   findTodoTask(String  userId,String status);
	
	@Query(value = "select * from t_wfw_task  WHERE user_id =?1 AND status =1",nativeQuery = true)
	List <WfwTask>   findDoneTask(String  userId);
	
	@Query(value = "select * from t_wfw_task  WHERE pid =?1  AND  current_id =?2 AND status =0",nativeQuery = true)
	List <WfwTask>   findTaskNotFinish(String  pid,String activityId);
	

}