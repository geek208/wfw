package com.hadron.wfw.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hadron.wfw.ActivityType;
import com.hadron.wfw.ApprovalType;
import com.hadron.wfw.Dozer;
import com.hadron.wfw.EntityUtils;
import com.hadron.wfw.GetPayNo;
import com.hadron.wfw.HttpAPIService;
import com.hadron.wfw.IntervalUtil;
import com.hadron.wfw.OwerType;
import com.hadron.wfw.PayStatus;
import com.hadron.wfw.RedisService;
import com.hadron.wfw.Result;
import com.hadron.wfw.ResultData;
import com.hadron.wfw.cache.UserCache;
import com.hadron.wfw.model.EasyPay;
import com.hadron.wfw.model.SysUser;
import com.hadron.wfw.model.UserVO;
import com.hadron.wfw.model.WfwActivity;
import com.hadron.wfw.model.WfwActivityRules;
import com.hadron.wfw.model.WfwActivityUser;
import com.hadron.wfw.model.WfwFlow;
import com.hadron.wfw.model.WfwForm;
import com.hadron.wfw.model.WfwFormField;
import com.hadron.wfw.model.UserField;
import com.hadron.wfw.model.WfwFormFieldV;
import com.hadron.wfw.model.WfwFormV;
import com.hadron.wfw.model.WfwLink;
import com.hadron.wfw.model.WfwProcess;
import com.hadron.wfw.model.WfwTask;
import com.hadron.wfw.model.WfwUser;
import com.hadron.wfw.producer.Producer;
import com.hadron.wfw.service.ActivityService;
import com.hadron.wfw.service.EasyPayService;
import com.hadron.wfw.service.OrgRepository;
import com.hadron.wfw.service.RoleRepository;
import com.hadron.wfw.service.UserRepository;
import com.hadron.wfw.service.WfwActivityRepository;
import com.hadron.wfw.service.WfwActivityRulesRepository;
import com.hadron.wfw.service.WfwActivityService;
import com.hadron.wfw.service.WfwFlowRepository;
import com.hadron.wfw.service.WfwFormFieldRepository;
import com.hadron.wfw.service.WfwFormRepository;
import com.hadron.wfw.service.WfwProcessRepository;
import com.hadron.wfw.service.WfwTaskRepository;
import com.hadron.wfw.service.WfwUserRepository;

import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author Administrator
 *
 */

@Controller
@RequestMapping("/wfwactivity")
public class WfwActivityController {

	/**
	 * The User service.
	 */
	// @Autowired
	// WfwActivityService wfwActivityService;
	@Autowired
	private HttpAPIService httpAPIService;
	@Autowired
	private Producer producer;
	@Autowired
	private KafkaTemplate kafkaTemplate;
	@Autowired
	private RedisService redisService;

	// @Autowired
	// private WfwActivityRepository wfwActivityRepository;

	@Autowired
	private WfwActivityRepository wfwActivityRepository;

	@Autowired
	private WfwActivityRulesRepository wfwActivityRulesRepository;

	@Autowired
	private WfwFlowRepository wfwFlowRepository;

	@Autowired
	private WfwFormRepository wfwFormRepository;

	@Autowired
	private WfwFormFieldRepository wfwFormFieldRepository;
	@Autowired
	private WfwProcessRepository wfwProcessRepository;

	@Autowired
	private WfwTaskRepository wfwTaskRepository;

	@Autowired
	private ActivityService activityService;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private OrgRepository orgRepository;

	@Autowired
	private WfwUserRepository wfwUserRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private Environment env;

	private static Gson gson = new GsonBuilder().create();

	@Autowired
	UserCache userCache;

	public static final String COOKIE_NAME = "auth";

	/**
	 * Add string.
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	@RequestMapping("/createFlow")
	@ResponseBody
	public ResultData createFlow(WfwFlow flow) {

		flow.setCreateDate(new Date());
		flow.setFlowType("0");
		wfwFlowRepository.save(flow);

		WfwActivity start = new WfwActivity();
		start.setCreateDate(new Date());
		start.setFlowId(String.valueOf(flow.getId()));
		start.setName("??????");
		start.setPreActivity("0");

		// WfwActivity end = new WfwActivity();
		wfwActivityRepository.save(start);

		WfwActivity end = new WfwActivity();
		end.setCreateDate(new Date());
		end.setFlowId(String.valueOf(flow.getId()));
		end.setName("??????");
		end.setNextActivity("0");
		wfwActivityRepository.save(end);

		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(flow);
		return data;
	}

	/**
	 * Add string.
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	@RequestMapping("/createForm")
	@ResponseBody
	public ResultData createForm(WfwForm form) {

		wfwFormRepository.save(form);

		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(form);
		return data;
	}

	/**
	 * Add string.
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	@RequestMapping("/createFlowField")
	@ResponseBody
	public ResultData createFormField(WfwFormField formfield) {
		// pay.setPaySn(GetPayNo.getOrderNo());
		formfield.setCreateDate(new Date());
		formfield.setUpdateDate(new Date());

		wfwFormFieldRepository.save(formfield);

		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(formfield);
		return data;
	}

	//
	// /**
	// * ??????
	// *
	// * @param id
	// * @return
	// */
	// @ResponseBody
	// @GetMapping("/activity/{id}")
	// public Result getMessage(@PathVariable Integer id) {
	// if (id == null) throw new NullPointerException("id????????????");
	//
	// Message message = messageRepository.findById(id).orElse(new Message());
	//
	// return Result.buildSuccess(message==null?null:message, null);
	// }
	//

	@ResponseBody
	@RequestMapping("/updateActivity")
	public ResultData updateActivity(@RequestBody WfwActivity message) {
		// addOrUpdate(message);
		// ????????????????????????
		/// List<WfwFormField> wfwFormFields =
		// wfwFormFieldRepository.findByflowId(id);

		wfwActivityRepository.save(message);

		// ??????????????????????????????
		// UPDATE t_wfw_form_field SET activity_id =?,rules =? WHERE id =?

		System.err.println(message.getId() + "  field " + message.getFieldId() + "  rules " + message.getRules());

		WfwActivityRules rules = new WfwActivityRules();
		rules.setActivityId(String.valueOf(message.getId()));
		rules.setFieldId(message.getFieldId());
		rules.setFlowId(message.getFlowId());
		rules.setUserId(message.getUserId());
		rules.setName(message.getName());
		rules.setRules(message.getRules());

		rules.setCreateDate(new Date());
		wfwFormFieldRepository.save(rules);

		//
		// wfwFormFieldRepository.updateFieldActivity(message.getFieldId(),
		// String.valueOf(message.getId()),message.getRules());

		// bindUser(String.valueOf(message.getId()), message.getUserId(),
		// message.getUserType());
		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(message);
		return data;

	}

	/**
	 * ?????????????????????
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	@GetMapping("/getActivity/{id}")
	@ResponseBody
	public ResultData getActivity(@PathVariable long id) {

		WfwActivity process = wfwActivityRepository.findById(id);

		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(process);
		return data;
	}

	/**
	 * Add string.
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	@RequestMapping("/createActivity")
	@ResponseBody
	public ResultData createActivity(WfwActivity activity) {
		// pay.setPaySn(GetPayNo.getOrderNo());

		activity.setCreateDate(new Date());
		activity.setUpdateDate(new Date());
		// ???????????????
		activity.setApproveType(ApprovalType.OR.getValue());
		// ????????????
		activity.setUserType(OwerType.USER.getValue());
		// ????????????
		WfwActivity endactitvity = wfwActivityRepository.findEndByflowId(activity.getFlowId());

		// ???????????????????????? ????????????
		if (endactitvity != null) {
			//activity.setNextActivity(String.valueOf(endactitvity.getId()));
			wfwActivityRepository.save(activity);
		}
		// ????????????????????? ????????????
		WfwActivity pre = wfwActivityRepository.findById(Long.parseLong(activity.getPreActivity()));

		//pre.setNextActivity(String.valueOf(activity.getId()));

		// ???????????????
		WfwLink link = new WfwLink();
		link.setPreId(activity.getPreActivity());
		//
		link.setNextId(String.valueOf(activity.getId()));
		wfwActivityRepository.save(link);

		//
		// activity.getId()
		// ???????????????????????????
		wfwActivityRepository.save(pre);

		// String[] userlist = activity.getUserId().split(",");
		//
		// // ?????????????????????
		// for (int i = 0; i < userlist.length; i++) {
		// WfwActivityUser au = new WfwActivityUser();
		// au.setActivityId(String.valueOf(activity.getId()));
		// au.setUserId(userlist[i]);
		// wfwActivityRepository.save(au);
		// }
		//
		// try {
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(activity);
		return data;
	}

	/**
	 * Add string.
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	@RequestMapping("/bindUser")
	@ResponseBody
	public ResultData bindUser(String activityId, String users, String owerType) {

		// ????????????
		if (OwerType.USER.getValue().equals(owerType)) {
			String[] userlist = users.split(",");
			// ?????????????????????
			for (int i = 0; i < userlist.length; i++) {
				WfwActivityUser au = new WfwActivityUser();
				au.setActivityId(activityId);
				au.setUserId(userlist[i]);
				au.setCreateDate(new Date());
				au.setUpdateDate(new Date());
				wfwActivityRepository.save(au);
			}
			// ???????????? ????????????
		} else if (OwerType.ROLE.getValue().equals(owerType)) {
			// List<Map<String, String>> objects =
			// userRepository.findByRole(users);
			// String irsStr = JSON.toJSONString(objects);
			// List<WfwUser> users2 = JSON.parseArray(irsStr, WfwUser.class);
			// for (WfwUser user : users2) {
			// WfwActivityUser au = new WfwActivityUser();
			// au.setActivityId(activityId);
			// au.setUserId(String.valueOf(user.getId()));
			// au.setCreateDate(new Date());
			// au.setUpdateDate(new Date());
			// wfwActivityRepository.save(au);
			// }

			// ????????????
		} else if (OwerType.ORG.getValue().equals(owerType)) {

		}

		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(users);
		return data;
	}

	/**
	 * Add string.
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	@RequestMapping("/addTask")
	@ResponseBody
	public ResultData addTask(String pid) {

		System.err.println();
		// ????????????????????????
		// WfwActivity users =
		// wfwActivityRepository.findByActivityId(process.getCurrentActivityId());
		WfwProcess process = wfwProcessRepository.findById(Long.parseLong(pid));

		// ?????????????????????????????????
		List<Map<String, String>> objects = wfwActivityRepository.findByUActivityId(process.getCurrentActivityId());
		String irsStr = JSON.toJSONString(objects);

		List<WfwUser> users = JSON.parseArray(irsStr, WfwUser.class);

		// BeanUtil.

		// List users2 = Dozer.convert(objects, User.class);
		// //EntityUtils.castEntity(objects, User.class, new User());
		// ?????????????????????

		for (Iterator iterator = users.iterator(); iterator.hasNext();) {
			WfwUser user = (WfwUser) iterator.next();
			WfwTask task = new WfwTask();
			task.setTaskName(process.getPName());
			// task.setPid(String.valueOf(process.getId()));
			task.setPid(String.valueOf(process.getId()));
			task.setCreateDate(new Date());
			task.setUpdateDate(new Date());
			task.setStatus(0);
			task.setUserId(String.valueOf(user.getId()));
			// ????????????????????????
			task.setCurrentId(process.getCurrentActivityId());
			task.setFlowId(process.getFlowId());
			wfwTaskRepository.save(task);
		}

		try {
			// producer.send();
			// redisService.set(message.getId(), message.getMsg());
			// System.err.print("send kfk express="+gson.toJson(message));
			// kafkaTemplate.send("mall", gson.toJson(message));
			// kafkaTemplate.send("pay", gson.toJson(message));
			//
		} catch (Exception e) {
			e.printStackTrace();
		}

		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		// data.setData(pay.getPaySn());
		return data;
	}

	/**
	 * Add string.
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	@RequestMapping("/addWfwActivity")
	@ResponseBody
	public ResultData addWfwActivity(WfwActivity activity) {

		// activityService
		activityService.addActivity(activity);
		List<Map<String, String>> objects = wfwActivityRepository.findNext(activity.getPreActivity());

		String irsStr = JSON.toJSONString(objects);
		List<WfwActivity> users = JSON.parseArray(irsStr, WfwActivity.class);

		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(users);
		return data;
	}

	/**
	 * Add string.
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	@RequestMapping("/addTaskAct")
	@ResponseBody
	public ResultData addTaskAct(WfwProcess process, WfwActivity current) {

		 List<SysUser> users = new ArrayList();

		// userRepository.findUserByRole(current.getUserId());
		if (current.getUserType().equals(OwerType.ROLE.getValue())) {
			users = userRepository.findUserByRole(current.getUserId());
			System.err.println("?????????????????????=" + process.getCurrentActivityId() + "+?????????????????????===" + users.size());
			// ?????????????????????
		} else if (current.getUserType().equals(OwerType.ORG.getValue())) {
			users = userRepository.findUserByOrg(current.getUserId());
			System.err.println("?????????????????????=" + process.getCurrentActivityId() + "+?????????????????????===" + users.size());
			// ???????????????
		} else if (current.getUserType().equals(OwerType.USER.getValue())){
			users.add(userRepository.findById(Integer.parseInt(current.getUserId())));
			
			System.err.println("?????????????????????=" + process.getCurrentActivityId() + "+????????????????????????===" + users.size());
		}

		// List<Map<String, String>> objects =
		// wfwActivityRepository.findByUActivityId(process.getCurrentActivityId());
		// System.err.print("????????????????????????????????? =" + objects.size());
		// // List<User> user3 =
		// //
		// wfwActivityRepository.findByUserActivityId(process.getCurrentActivityId());
		// String irsStr = JSON.toJSONString(objects);
		// List<WfwUser> users = JSON.parseArray(irsStr, WfwUser.class);

		// ?????????????????????
        if(users !=null && users.size() >0){
        	for (Iterator iterator = users.iterator(); iterator.hasNext();) {
    			SysUser user = (SysUser) iterator.next();
    			WfwTask task = new WfwTask();
    			task.setTaskName(process.getPName() + "-" + current.getName());
    			// task.setPid(String.valueOf(process.getId()));
    			task.setPid(String.valueOf(process.getId()));
    			task.setCreateDate(new Date());
    			task.setUpdateDate(new Date());
    			task.setStatus(0);
    			task.setUserId(String.valueOf(user.getId()));
    			task.setFlowId(process.getFlowId());
    			task.setCurrentId(process.getCurrentActivityId());
    			wfwTaskRepository.save(task);
    		}
        
        	
        //???????????? ????????????????????????
        }else{
        	
        	WfwActivity end = wfwActivityRepository.findEndByflowId(process.getFlowId());
        	process.setCurrentActivityId(String.valueOf(end.getId()));
        	process.setCurrentActivityName(end.getName());
        	System.err.println("????????????=" + process.getCurrentActivityId() + "????????????===" + process.getCurrentActivityName());
        	
        }
	
		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		// data.setData(pay.getPaySn());
		return data;
	}

	/**
	 * ?????? ??????
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	@RequestMapping("/doTaskByUser")
	@ResponseBody
	public ResultData doTaskByUser(String flowId, String pid, String userId, String taskId) {

		// ??????????????????
		WfwProcess process = wfwProcessRepository.findById(Long.parseLong(pid));

		// ???????????????????????????????????????

		// ??????????????????
		process.setCurrentActivityId(process.getNextActivityId());

		WfwFlow flow = wfwFlowRepository.findById(Long.parseLong(flowId));

		// WfwFlow wfwFlowRepository.findById(Long.parseLong(flowId));

		// ????????????????????????
		// List users =
		// wfwActivityRepository.findByActivityId(process.getCurrentActivityId());

		// ?????????????????????

		// process.setNextActivityId(flow.g);

		// ????????????

		// ?????????????????????

		// ??????????????????????????????????????????

		// ??????????????????
		WfwTask wfwTask = new WfwTask();

		wfwTaskRepository.save(wfwTask);

		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(null);
		return data;
	}

	/**
	 * ?????? ??????
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	 
	@SuppressWarnings("rawtypes")
	//@GetMapping("/dotask/{id}")
	@RequestMapping("/doTask")
	@ResponseBody
	public ResultData doTask(@RequestBody WfwTask obj) {

		// doTask2(id);

		// ??????????????????
		WfwTask task = wfwTaskRepository.findById(obj.getId());
		
		//task.setComments(obj.getComments());

		// wfwProcessRepository.
		// ??????????????????
		wfwTaskRepository.updateTaskStatus(String.valueOf(obj.getId()), String.valueOf(obj.getStatus()),obj.getComments());
		// ??????????????????
		WfwProcess process = wfwProcessRepository.findById(Long.parseLong(task.getPid()));

		//WfwActivity current = wfwActivityRepository.findById(Long.parseLong(process.getCurrentActivityId()));
		
		List<WfwFormField> formFields = wfwFormFieldRepository.findByPid(task.getPid());
		
		this.doNext(process, formFields);
		
		

//		// ??????????????????????????????????????? 0????????? ???1 ????????? 2???????????????
//		if (ApprovalType.OR.getValue().equals(current.getApproveType())) {
//			
//			
//			List<Map<String, String>> objects = wfwActivityRepository.findNext(process.getCurrentActivityId());
//			String irsStr = JSON.toJSONString(objects);
//			List<WfwActivity> nexts = JSON.parseArray(irsStr, WfwActivity.class);
//			System.err.println("????????????=" + process.getCurrentActivityId() + "+????????????????????????===" + nexts.size());
//			
//
//			System.err.println("??????=" + current.getApproveType());
//			// ??????????????????????????????2
//			//process.setCurrentActivityId(process.getNextActivityId());
//			// process.setCurrentActivityName(currentActivityName);
//			//
//			// ????????????2,?????????????????????,??????????????????????????????
//			WfwActivity next = wfwActivityRepository.findById(Long.parseLong(process.getNextActivityId()));
//
//			// ??????????????????????????????2??????????????????
//			if (next != null && next.getNextActivity() != null) {
//				process.setNextActivityId(next.getNextActivity());
//				//
//			} else {
//
//				// ??????????????????2??????????????? ?????????????????????????????????
//				// process.setCurrentActivityId("1");
//
//				process.setNextActivityId("0");
//			}
//			wfwProcessRepository.save(process);
//
//			// ???????????????????????????????????????
//			// ?????????????????????????????????????????????????????????????????????
//			if (next != null) {
//				addTaskAct(process, next);
//				// List<Map<String, String>> objects = wfwActivityRepository
//				// .findByUActivityId(String.valueOf(next.getId()));
//				// String irsStr = JSON.toJSONString(objects);
//				// List<WfwUser> users = JSON.parseArray(irsStr, WfwUser.class);
//				// for (Iterator iterator = users.iterator();
//				// iterator.hasNext();) {
//				// WfwUser user = (WfwUser) iterator.next();
//				// WfwTask task2 = new WfwTask();
//				// task2.setTaskName(next.getName());
//				// // task.setPid(String.valueOf(process.getId()));
//				// task2.setPid(String.valueOf(process.getId()));
//				// task2.setCreateDate(new Date());
//				// task2.setUpdateDate(new Date());
//				// task2.setCurrentId(String.valueOf(next.getId()));
//				// // ?????????
//				// task2.setStatus(0);
//				// task2.setUserId(String.valueOf(user.getId()));
//				// task2.setFlowId(process.getFlowId());
//				// wfwTaskRepository.save(task2);
//				// }
//			}
//
//		} else if (ApprovalType.AND.getValue().equals(current.getApproveType())) {
//			// ?????????????????????????????????
//			// ???????????????????????????
//			// ????????????????????? ?????????????????????
//			// ?????????????????????????????????
//			System.err.println("??????=" + current.getApproveType());
//
//			List list = wfwTaskRepository.findTaskNotFinish(String.valueOf(process.getId()),
//					process.getCurrentActivityId());
//
//			// ???????????????
//			if (list != null && list.size() > 0) {
//				ResultData data = new ResultData();
//				data.setCode(200);
//				data.setSuccess(true);
//				data.setMessage("??????");
//				data.setData(process);
//				return data;
//
//				// ??????????????????
//			} else {
//				// ??????????????????????????????2
//				process.setCurrentActivityId(process.getNextActivityId());
//				//
//				// ????????????2
//				WfwActivity next = wfwActivityRepository.findById(Long.parseLong(process.getNextActivityId()));
//				// ??????????????????????????????2??????????????????
//				if (next != null && next.getNextActivity() != null) {
//					process.setNextActivityId(next.getNextActivity());
//					//
//				} else {
//
//					// ??????????????????2??????????????? ?????????????????????????????????
//					// process.setCurrentActivityId("1");
//
//					process.setNextActivityId("0");
//				}
//				wfwProcessRepository.save(process);
//
//				// ???????????????????????????????????????
//				// ?????????????????????????????????????????????????????????????????????
//				if (next != null) {
//
//					addTaskAct(process, next);
//
//				}
//			}
//
//			System.err.println("?????? ??????=" + current.getApproveType());
//
//		}

		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(process);
		return data;
	}
	
	@GetMapping("/getTask/{id}")
	@ResponseBody
	public ResultData getTask(@PathVariable String id) {
		
		WfwTask task = wfwTaskRepository.findById(Long.parseLong(id));
		
		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(task);
		return data;
	}

	/**
	 * ?????? ??????
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	@RequestMapping("/doTask2")
	@ResponseBody
	public ResultData doTask2(String taskId) {
		int ret = activityService.dotask(taskId);

		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(ret);
		return data;
	}

	/**
	 * Add string.
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	@RequestMapping("/getMyTask")
	@ResponseBody
	public ResultData getMyTask(String userId) {

		List tasks = wfwTaskRepository.findTask(userId);

		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(tasks);
		return data;
	}

	/**
	 * Add string.
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	@RequestMapping("/getMyTaskTodoList")
	@ResponseBody
	public ResultData getMyTaskTodoList(@CookieValue(value = COOKIE_NAME, defaultValue = "") String auth) {

		UserVO cache = userCache.getCache(auth);
		// if(cache ==null) throw new Exception("");
		List<WfwTask> tasks = wfwTaskRepository.findTodoTask(String.valueOf(cache.getId()), "0");
		
		System.err.println("???????????? =[" + cache.getEmail() + "]???????????????=???" + tasks.size()+"]");

		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(tasks);
		return data;
	}

	/**
	 * Add string.
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	@RequestMapping("/getMyTaskDoneList")
	@ResponseBody
	public ResultData getMyTaskDoneList(@CookieValue(value = COOKIE_NAME, defaultValue = "") String auth) {

		UserVO cache = userCache.getCache(auth);
		List<WfwTask> tasks = wfwTaskRepository.findTodoTask(String.valueOf(cache.getId()), "1");
		// List<WfwTask> tasks = wfwTaskRepository.findTodoTask("7", "1");
		System.err.println("???????????? =[" + cache.getEmail() + "]???????????????=???" + tasks.size()+"]");
		
		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(tasks);
		return data;
	}

	/**
	 * Add string.
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	@GetMapping("/getActivityList/{id}")
	@ResponseBody
	public ResultData getActivityList(@PathVariable String id) {

		// ????????????????????????
		List<WfwActivity> wfwActivitys = wfwActivityRepository.findByflowId(id);

		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(wfwActivitys);
		return data;
	}

	/**
	 * ????????????
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	@RequestMapping("/getForm")
	@ResponseBody
	public ResultData getForm(String formId) {

		// WfwForm form = wfwFormRepository.findById(Long.parseLong(formId));

		WfwForm form = wfwFormRepository.findById(93);

		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(form);
		return data;
	}

	/**
	 * ????????????
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	@RequestMapping("/getFormList2")
	@ResponseBody
	@GetMapping
	public Result getFormList2(Integer page, Integer pageSize) {

		Page<WfwForm> messages = wfwFormRepository.findAll(Example.of(WfwForm.builder().build()),
				PageRequest.of(page - 1, pageSize));

		List<WfwForm> contents = messages.getContent();

		Result ret = Result.buildPageObject(messages.getTotalElements(), contents);

		return ret;
	}

	/**
	 * ????????????
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	@RequestMapping("/getFormList3")
	@ResponseBody
	@GetMapping
	public ResultData getFormList3(Integer page, Integer pageSize) {

		Page<WfwForm> messages = wfwFormRepository.findAll(Example.of(WfwForm.builder().build()),
				PageRequest.of(page - 1, pageSize));

		List<WfwForm> contents = messages.getContent();

		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(contents);
		return data;
	}

	/**
	 * ????????????
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	// @RequestMapping("/getFormFields")
	@GetMapping("/getFormFields/{id}")
	@ResponseBody
	public ResultData getFormFields(@PathVariable String id) {

		List<WfwFormField> formField = wfwFormFieldRepository.findByflowId(id);

		// WfwFormV formV = new WfwFormV();
		// formV.setFormfield(formField);

		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(formField);
		return data;
	}

	/**
	 * ????????????
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	@RequestMapping("/getFlow")
	@ResponseBody
	public ResultData getFlow(String flowId) {

		WfwFlow flow = wfwFlowRepository.findById(Long.parseLong(flowId));

		// WfwFormV formV =new WfwFormV();
		// formV.setFormfield(formField);

		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(flow);
		return data;
	}

	/**
	 * ????????????
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	@RequestMapping("/getFlowList")
	@ResponseBody
	public ResultData getFlowList() {

		List<WfwFlow> flows = wfwFlowRepository.findAll();

		// WfwFormV formV =new WfwFormV();
		// formV.setFormfield(formField);
		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(flows);
		return data;
	}

	/**
	 * ??????????????????
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	@RequestMapping("/getProcess")
	@ResponseBody
	public ResultData getProcess(String pid) {

		WfwProcess process = wfwProcessRepository.findById(Long.parseLong(pid));

		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(process);
		return data;
	}

	/**
	 * ?????????????????????
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	@GetMapping("/getProcessByFlowId/{id}")
	@ResponseBody
	public ResultData getProcessByFlowId(@PathVariable String id) {

		List<WfwProcess> process = wfwProcessRepository.findProcessByflowId(id);
		
		
		
		for (WfwProcess wfwProcess : process) {
			
			int toDoNum =  wfwTaskRepository.countTaskFinish(String.valueOf(wfwProcess.getId()), 0);
			int doneNum =  wfwTaskRepository.countTaskFinish(String.valueOf(wfwProcess.getId()), 1);
			wfwProcess.setToDoNum(toDoNum);
			wfwProcess.setDoneNum(doneNum);
			//????????????
			if(toDoNum == 0 ){
				wfwProcess.setTaskStatus(wfwProcess.getCurrentActivityName()+"[?????????]");
			}else{
				wfwProcess.setTaskStatus("?????????");
			}
			//wfwProcessRepository.save(wfwProcess);
			
			System.err.println("??????=[" + wfwProcess.getId()+"] todo =["+toDoNum+"] done=["+doneNum+"]");
		}
		

		// WfwFormV formV =new WfwFormV();
		// formV.setFormfield(formField);

		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(process);
		return data;
	}
	
	/**
	 * ?????????????????????
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	@GetMapping("/getTaskBypId/{id}")
	@ResponseBody
	public ResultData getTaskBypId(@PathVariable String id) {

		List<WfwTask> tasks = wfwTaskRepository.findPorcessTask(id);

		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(tasks);
		return data;
	}

	/**
	 * Add string.
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	// @RequestMapping("/startProcess")
	@GetMapping("/startProcess/{id}")
	@ResponseBody
	public ResultData startProcess(@PathVariable String id) {
		// flowId = "1";
		// ???????????? 1??????????????? ??????????????????0???
		// 2?????????????????????????????????0???
		// 3??????????????????0?????????????????????????????????0

		WfwFlow flow = wfwFlowRepository.findById(Long.parseLong(id));

		// ??????????????????????????????
		List wfwActivitys = wfwActivityRepository.findByflowId(id);
		// ????????????????????????????????????????????????????????????0??????????????????
		WfwActivity startActivity = (WfwActivity) wfwActivitys.get(0);

		// ????????????
		WfwProcess process = new WfwProcess();
		process.setFlowId(id);
		process.setPName(flow.getFname() + "-" + id);
		process.setCreateDate(new Date());

		// ????????????????????????
		//WfwActivity current2 = wfwActivityRepository.findById(Long.parseLong(startActivity.getNextActivity()));

		// List<.> nexts
		// wfwActivityRepository.findNext(startActivity.getNextActivity());

		// ????????????????????????
		// process.setCurrentActivityId(String.valueOf(current.getId()));
		// process.setCurrentActivityName(current.getName());

		process.setCurrentActivityId(String.valueOf(startActivity.getId()));
		// process.setCurrentActivityName(current.getName());

		// process.setNextActivityId(String.valueOf(current.getNextActivity()));
		// process.setCurrentActivityName(current.getName());
		// ???????????????
		wfwProcessRepository.save(process);

		// ????????????????????????
		List<WfwFormField> wfwFormFields = wfwFormFieldRepository.findByflowId(id);

		System.err.print("???????????????????????????,????????????????????????    ????????????=" + wfwFormFields.size());

		// ???????????????????????????,????????????????????????
		List<WfwFormField> newwfwFormFields = new ArrayList();
		for (WfwFormField object : wfwFormFields) {
			// object.setUserId(userId);
			WfwFormField field = new WfwFormField();
			field.setName(object.getName());
			field.setFlowId(object.getFlowId());
			field.setFormId(object.getFormId());
			field.setUserId(object.getUserId());
			field.setCreateDate(new Date());
			// ???????????????
			field.setPId(String.valueOf(process.getId()));
			// field.setActivityId(object.getActivityId());
			field.setParentId(String.valueOf(object.getId()));
			field.setRules(object.getRules());
			field.setFieldType(object.getFieldType());
			wfwFormFieldRepository.save(field);
			newwfwFormFields.add(field);
		}

		// List<Map<String, String>> objects =
		// wfwActivityRepository.findNext(String.valueOf(startActivity.getId()));
		// String irsStr = JSON.toJSONString(objects);
		// List<WfwActivity> nexts = JSON.parseArray(irsStr, WfwActivity.class);
		// System.err.println(""+startActivity.getNextActivity()+"+????????????????????????==="
		// + nexts.size());
		//
		// // ????????????????????????1??????????????????
		// if (nexts != null && nexts.size() > 1) {
		// // ???????????????????????????
		// //field
		// }

		// ??????????????????????????????
		// addTask(String.valueOf(process.getId()));
		// ?????????????????????????????????
		//

		// WfwActivity wfwActivity=
		// wfwActivityRepository.findById(Long.parseLong(process.getCurrentActivityId()));
		// ???????????????
		// userRepository.findById(Integer.parseInt(current.getUserId()));
		// List<User> users = new ArrayList();
		//
		// // userRepository.findUserByRole(current.getUserId());
		// if (current.getUserType().equals(OwerType.ROLE.getValue())) {
		// users = userRepository.findUserByRole(current.getUserId());
		// // ?????????????????????
		// } else if (current.getUserType().equals(OwerType.ORG.getValue())) {
		// userRepository.findUserByOrg(current.getUserId());
		//
		// // ???????????????
		// } else {
		// users.add(userRepository.findById(Integer.parseInt(current.getUserId())));
		// }

		// List<Map<String, String>> objects =
		// wfwActivityRepository.findByUActivityId(process.getCurrentActivityId());
		// System.err.print("????????????????????????????????? =" + objects.size());
		// // List<User> user3 =
		// //
		// wfwActivityRepository.findByUserActivityId(process.getCurrentActivityId());
		// String irsStr = JSON.toJSONString(objects);
		// List<WfwUser> users = JSON.parseArray(irsStr, WfwUser.class);

		// ?????????????????????
		// System.err.print("????????????????????????????????? =" + users.size());
		//
		// for (Iterator iterator = users.iterator(); iterator.hasNext();) {
		// User user = (User) iterator.next();
		// WfwTask task = new WfwTask();
		// task.setTaskName(process.getPName() + "-" + current.getName());
		// // task.setPid(String.valueOf(process.getId()));
		// task.setPid(String.valueOf(process.getId()));
		// task.setCreateDate(new Date());
		// task.setUpdateDate(new Date());
		// task.setStatus(0);
		// task.setUserId(String.valueOf(user.getId()));
		// task.setFlowId(process.getFlowId());
		// task.setCurrentId(process.getCurrentActivityId());
		// wfwTaskRepository.save(task);
		// }

		// WfwTask task = new WfwTask();
		// task.setTaskName(process.getPName());
		// task.setPid(String.valueOf(process.getId()));
		// task.setStatus(0);
		// task.setUserId(String.valueOf(1));
		//
		// task.setFlowId(flowId);
		//
		// wfwTaskRepository.save(task);

		// //??????????????????????????????
		// List users =
		// wfwActivityRepository.findByActivityId(process.getNextActivityId());
		//
		// //??????????????????
		// for (Iterator iterator = users.iterator(); iterator.hasNext();) {
		// User object = (User) iterator.next();
		//
		// WfwTask task =new WfwTask();
		// task.setStaskName(process.getPName()+object.getUserName());
		// task.setPid(String.valueOf(process.getId()));
		// task.setStatus(0);
		// task.setUserId(String.valueOf(object.getId()));
		//
		// task.setFlowId(flowId);
		//
		// }

		ResultData data = new ResultData();

		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(newwfwFormFields);
		return data;
	}

	/**
	 * Add string.
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	@RequestMapping("/apply")
	// @P
	@ResponseBody
	// @RequestParam(value=???XXX???,required=false)List
	public ResultData apply(@RequestBody List<WfwFormField> formfields) {

		// // ???????????? 1??????????????? ??????????????????0???
		// // 2?????????????????????????????????0???
		// // 3??????????????????0?????????????????????????????????0
		//
		// WfwFlow flow = wfwFlowRepository.findById(Long.parseLong(flowId));
		//
		// // ????????????????????????
		// List wfwActivitys = wfwActivityRepository.findByflowId(flowId);
		//
		// // ????????????????????????????????????0??????????????????
		// WfwActivity startActivity = (WfwActivity) wfwActivitys.get(0);
		//
		// // ????????????
		// WfwProcess process = new WfwProcess();
		// process.setFlowId(flowId);
		// process.setPName(flow.getFname() + "-" + flowId);
		// process.setCreateDate(new Date());
		//

		// ?????????????????????
		for (WfwFormField object : formfields) {
			wfwFormFieldRepository.updateFieldValue(String.valueOf(object.getId()), object.getFieldvalue());
		}

		//??????????????????
		WfwProcess process = null;
		if (formfields != null && formfields.size() >= 1) {
			WfwFormField f = (WfwFormField) formfields.get(0);
			process = wfwProcessRepository.findById(Long.parseLong(f.getPId()));
		}
		
		//??????????????????
		WfwActivity current = wfwActivityRepository.findById(Long.parseLong(process.getCurrentActivityId()));
		
		System.err.println("????????????=" + process.getCurrentActivityId() + "+??????===" + current.getName());
		
		//??????????????????
		List<Map<String, String>> objects = wfwActivityRepository.findNext(process.getCurrentActivityId());
		String irsStr = JSON.toJSONString(objects);
		List<WfwActivity> nexts = JSON.parseArray(irsStr, WfwActivity.class);
		
		System.err.println("????????????=" + process.getCurrentActivityId() + "+????????????????????????===" + nexts.size());

		 
			if (nexts != null && nexts.size() >= 1) {
				// ??????????????????
				int i=0;
				for (WfwActivity wfwActivity : nexts) {
					
					//???????????????    // ????????????????????????1?????????????????????
					if(ActivityType.BRANCH.getValue().equalsIgnoreCase(wfwActivity.getActivtiyType())){

				    System.err.println("?????????["+i+"] ??????id=" + wfwActivity.getId() +  "????????????=" + wfwActivity.getName() +"????????????="+wfwActivity.getActivtiyType());
					// ????????????????????????
					//WfwActivityRules rule = wfwActivityRepository.findRulesById(String.valueOf(wfwActivity.getId()));
					List<Map<String, String>> obj = wfwActivityRepository.findRulesById(String.valueOf(wfwActivity.getId()));
					String irs= JSON.toJSONString(obj);
					List<WfwActivityRules> rules = JSON.parseArray(irs, WfwActivityRules.class);
					
					System.err.println("?????????["+i+"] ??????id=" + wfwActivity.getId() +  " ????????? ??????=" + rules.size());
					
					if(rules !=null && rules.size() >0){
						//???????????????????????????
						WfwActivityRules rule= (WfwActivityRules)rules.get(0);
						
						if(rule !=null){
							// ????????????
							String interval = rule.getRules();
							// ???????????????????????????
							// field
							for (WfwFormField object : formfields) {
								
								 // ??????????????????????????????????????????????????????????????????????????? // ?????????50??? $??????????????????
								 if(object.getParentId().equals(rule.getFieldId())){
									 
									 IntervalUtil a = new IntervalUtil();
									 boolean isOk= a.isInTheInterval(object.getFieldvalue(), interval);
									   
									    //????????????????????????????????????????????????????????????
									   if(isOk){
										   
										    
											 //// ????????????????????????
											 process.setCurrentActivityId(String.valueOf(wfwActivity.getId()));
											 process.setCurrentActivityName(wfwActivity.getName());
											 //process.setNextActivityId(String.valueOf(wfwActivity.getNextActivity()));
											 
//											    int toDoNum =  wfwTaskRepository.findTaskNotFinish(String.valueOf(process.getId()), String.valueOf(wfwActivity.getId()));
//												int doneNum =  wfwTaskRepository.findTaskFinish(String.valueOf(process.getId()), String.valueOf(wfwActivity.getId()));
//												process.setToDoNum(toDoNum);
//												process.setDoneNum(doneNum);
//												//????????????
//												if(toDoNum == 0 ){
//													process.setTaskStatus(process.getCurrentActivityName()+"[?????????]");
//												}else{
//													process.setTaskStatus("?????????");
//												}
											 
											 wfwProcessRepository.save(process);
											
												
											 System.err.println("??????????????????=[" + process.getCurrentActivityId() + "]   ??????===["+process.getCurrentActivityName()+"]");
											 //?????????????????? ????????????
											 addTaskAct(process, wfwActivity);
											 
									         break;
									    // ?????? ???????????????	   
									   }else{
										   continue;
									   }

								 }
							}
						}
					}
					i++;
				} else if (ActivityType.APPROVER.getValue().equalsIgnoreCase(current.getActivtiyType())){
					

					 //WfwActivity  wfwActivity =   (WfwActivity)nexts.get(0);
					 System.err.println("???????????? ??? ???????????? =" + wfwActivity.getId() + "+??????===" + wfwActivity.getName());
					 process.setCurrentActivityName(wfwActivity.getName());
					 process.setCurrentActivityId(String.valueOf(wfwActivity.getId()));
					 
//				    int toDoNum =  wfwTaskRepository.findTaskNotFinish(String.valueOf(process.getId()), process.getCurrentActivityId());
//					int doneNum =  wfwTaskRepository.findTaskFinish(String.valueOf(process.getId()), process.getCurrentActivityId());
//					process.setToDoNum(toDoNum);
//					process.setDoneNum(doneNum);
//					//????????????
//					if(toDoNum == 0 ){
//						process.setTaskStatus(process.getCurrentActivityName()+"[?????????]");
//					}else{
//						process.setTaskStatus("?????????");
//					}
						
					 //process.setNextActivityId(String.valueOf(wfwActivity.getNextActivity()));
					 wfwProcessRepository.save(process);
					 System.err.println("??????????????????=[" + process.getCurrentActivityId() + "]   ??????===["+process.getCurrentActivityName()+"]");
					
					 //?????????????????? ????????????
					 addTaskAct(process, wfwActivity);	
				}
			}
		
		//		
		}else{
			System.err.println("???????????? ??? ????????????=");
		}
		
        




		// // ????????????????????????
		// WfwActivity current =
		// wfwActivityRepository.findById(Long.parseLong(startActivity.getNextActivity()));
		// // ????????????????????????
		// process.setCurrentActivityId(String.valueOf(current.getId()));
		//
		//
		// process.setNextActivityId(String.valueOf(current.getNextActivity()));
		// // ???????????????
		// wfwProcessRepository.save(process);
		//
		// //?????? ?????????

		//
		// // ??????????????????????????????
		// // addTask(String.valueOf(process.getId()));
		// // ?????????????????????????????????
		// List<Map<String, String>> objects =
		// wfwActivityRepository.findByUActivityId(process.getCurrentActivityId());
		//
		// //List<User> user3 =
		// wfwActivityRepository.findByUserActivityId(process.getCurrentActivityId());
		//
		// String irsStr = JSON.toJSONString(objects);
		// List<User> users = JSON.parseArray(irsStr, User.class);
		//
		// // BeanUtil.
		// // List users2 = Dozer.convert(objects, User.class);
		// // //EntityUtils.castEntity(objects, User.class, new User());
		// // ?????????????????????
		//
		// for (Iterator iterator = users.iterator(); iterator.hasNext();) {
		// User user = (User) iterator.next();
		// WfwTask task = new WfwTask();
		// task.setTaskName(current.getName());
		// // task.setPid(String.valueOf(process.getId()));
		// task.setPid(String.valueOf(process.getId()));
		// task.setCreateDate(new Date());
		// task.setUpdateDate(new Date());
		// task.setStatus(0);
		// task.setUserId(String.valueOf(user.getId()));
		// task.setFlowId(process.getFlowId());
		// task.setCurrentId(process.getCurrentActivityId());
		// wfwTaskRepository.save(task);
		// }

		// WfwTask task = new WfwTask();
		// task.setTaskName(process.getPName());
		// task.setPid(String.valueOf(process.getId()));
		// task.setStatus(0);
		// task.setUserId(String.valueOf(1));
		//
		// task.setFlowId(flowId);
		//
		// wfwTaskRepository.save(task);

		// //??????????????????????????????
		// List users =
		// wfwActivityRepository.findByActivityId(process.getNextActivityId());
		//
		// //??????????????????
		// for (Iterator iterator = users.iterator(); iterator.hasNext();) {
		// User object = (User) iterator.next();
		//
		// WfwTask task =new WfwTask();
		// task.setStaskName(process.getPName()+object.getUserName());
		// task.setPid(String.valueOf(process.getId()));
		// task.setStatus(0);
		// task.setUserId(String.valueOf(object.getId()));
		//
		// task.setFlowId(flowId);
		//
		// }

		ResultData data = new ResultData();

		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		// data.setData(process);
		return data;
	}
	
	
	// ????????????
	public void doNext(WfwProcess process,List<WfwFormField> formFields){
		//??????????????????
		List<Map<String, String>> objects = wfwActivityRepository.findNext(process.getCurrentActivityId());
		String irsStr = JSON.toJSONString(objects);
		List<WfwActivity> nexts = JSON.parseArray(irsStr, WfwActivity.class);
		
		System.err.println("????????????=" + process.getCurrentActivityId() + "+????????????????????????===" + nexts.size());

		   // ????????????????????????1??????????????????????????????
			if (nexts != null && nexts.size() >=1 ) {
				// ??????????????????
				int i=0;
				for (WfwActivity wfwActivity : nexts) {
					
					//???????????????????????????{
					//WfwActivity end = wfwActivityRepository.findEndByflowId(process.getFlowId());
					//?????????????????????
					if(wfwActivity.getNextActivity() !=null && wfwActivity.getNextActivity().equals(ActivityType.END.getValue())){
			        	process.setCurrentActivityId(String.valueOf(wfwActivity.getId()));
			        	process.setCurrentActivityName(wfwActivity.getName());
			        	process.setTaskStatus("?????????");
			        	wfwProcessRepository.save(process);
			        	System.err.println("????????????=" + process.getCurrentActivityId() + "????????????===" + process.getCurrentActivityName());
			        	break;
					}
					
					
					//???????????????
					if(ActivityType.BRANCH.getValue().equalsIgnoreCase(wfwActivity.getActivtiyType())){

				    System.err.println("?????????["+i+"] ??????id=" + wfwActivity.getId() +  "????????????=" + wfwActivity.getName() +"????????????="+wfwActivity.getActivtiyType());
					// ????????????????????????
					//WfwActivityRules rule = wfwActivityRepository.findRulesById(String.valueOf(wfwActivity.getId()));
					List<Map<String, String>> obj = wfwActivityRepository.findRulesById(String.valueOf(wfwActivity.getId()));
					String irs= JSON.toJSONString(obj);
					List<WfwActivityRules> rules = JSON.parseArray(irs, WfwActivityRules.class);
					
					System.err.println("?????????["+i+"] ??????id=" + wfwActivity.getId() +  " ????????? ??????=" + rules.size());
					
					if(rules !=null && rules.size() >0){
						//???????????????????????????
						WfwActivityRules rule= (WfwActivityRules)rules.get(0);
						
						if(rule !=null){
							// ????????????
							String interval = rule.getRules();
							// ???????????????????????????
							// field
							for (WfwFormField object : formFields) {
								
								 // ??????????????????????????????????????????????????????????????????????????? // ?????????50??? $??????????????????
								 if(object.getParentId().equals(rule.getFieldId())){
									 
									 IntervalUtil a = new IntervalUtil();
									 boolean isOk= a.isInTheInterval(object.getFieldvalue(), interval);
									   
									    //????????????????????????????????????????????????????????????
									   if(isOk){
										   
											 //// ????????????????????????
										   if (ApprovalType.OR.getValue().equals(wfwActivity.getApproveType())) {
											     System.err.println("??????=====================" + wfwActivity.getApproveType());
											     process.setCurrentActivityId(String.valueOf(wfwActivity.getId()));
											     process.setCurrentActivityName(wfwActivity.getName());
											     
//											 	int toDoNum =  wfwTaskRepository.findTaskNotFinish(String.valueOf(process.getId()), process.getCurrentActivityId());
//												int doneNum =  wfwTaskRepository.findTaskFinish(String.valueOf(process.getId()), process.getCurrentActivityId());
//												process.setToDoNum(toDoNum);
//												process.setDoneNum(doneNum);
//												//????????????
//												if(toDoNum == 0 ){
//													process.setTaskStatus(process.getCurrentActivityName()+"[?????????]");
//												}else{
//													process.setTaskStatus("?????????");
//												}
												
												 //process.setNextActivityId(String.valueOf(wfwActivity.getNextActivity()));
												 wfwProcessRepository.save(process);
												 System.err.println("??????????????????=[" + process.getCurrentActivityId() + "]   ??????===["+process.getCurrentActivityName()+"]");
												 //?????????????????? ????????????
												 addTaskAct(process, wfwActivity);
										         continue;
										   }else{
											   
											   System.err.println("??????================" + wfwActivity.getApproveType());
											   int notDone = wfwTaskRepository.findTaskNotFinish(String.valueOf(process.getId()),
														process.getCurrentActivityId());
												// ???????????????
												if (notDone >0) {
													continue;
													// ??????????????????
												}else{
													process.setCurrentActivityId(String.valueOf(wfwActivity.getId()));
													process.setCurrentActivityName(wfwActivity.getName());
//													
//												 	int toDoNum =  wfwTaskRepository.findTaskNotFinish(String.valueOf(process.getId()), process.getCurrentActivityId());
//													int doneNum =  wfwTaskRepository.findTaskFinish(String.valueOf(process.getId()), process.getCurrentActivityId());
//													process.setToDoNum(toDoNum);
//													process.setDoneNum(doneNum);
//													//????????????
//													if(toDoNum == 0 ){
//														process.setTaskStatus(process.getCurrentActivityName()+"[?????????]");
//													}else{
//														process.setTaskStatus("?????????");
//													}
													
													 //process.setNextActivityId(String.valueOf(wfwActivity.getNextActivity()));
													 wfwProcessRepository.save(process);
													 System.err.println("??????????????? ?????????=[" + process.getCurrentActivityId() + "]   ??????===["+process.getCurrentActivityName()+"]");
													 //?????????????????? ????????????
													 addTaskAct(process, wfwActivity);
											         break;
												}
										   }
											 
									    // ?????? ???????????????	   
									   }else{
										   continue;
									   }
								 }
							}
						}
					}
					i++;
				
					// ?????????????????????
				} else if (ActivityType.APPROVER.getValue().equalsIgnoreCase(wfwActivity.getActivtiyType())){
					
					 //// ????????????????????????
					   if (ApprovalType.OR.getValue().equals(wfwActivity.getApproveType())) {
						     System.err.println("??????=====================" + wfwActivity.getApproveType());
//						     process.setCurrentActivityId(String.valueOf(wfwActivity.getId()));
//						     process.setCurrentActivityName(wfwActivity.getName());
//							 //process.setNextActivityId(String.valueOf(wfwActivity.getNextActivity()));
//						       int toDoNum =  wfwTaskRepository.findTaskNotFinish(String.valueOf(process.getId()), process.getCurrentActivityId());
//								int doneNum =  wfwTaskRepository.findTaskFinish(String.valueOf(process.getId()), process.getCurrentActivityId());
//								process.setToDoNum(toDoNum);
//								process.setDoneNum(doneNum);
//								//????????????
//								if(toDoNum == 0 ){
//									process.setTaskStatus(process.getCurrentActivityName()+"[?????????]");
//								}else{
//									process.setTaskStatus("?????????");
//								}
								
							 wfwProcessRepository.save(process);
							 System.err.println("??????????????????=[" + process.getCurrentActivityId() + "]   ??????===["+process.getCurrentActivityName()+"]");
							 //?????????????????? ????????????
							 addTaskAct(process, wfwActivity);
					         continue;
					   }else{
						   
						   System.err.println("??????================" + wfwActivity.getApproveType());
						   int toDo = wfwTaskRepository.findTaskNotFinish(String.valueOf(process.getId()),
									process.getCurrentActivityId());
							// ???????????????
							if (toDo >0) {
								continue;
								// ??????????????????
							}else{
								process.setCurrentActivityId(String.valueOf(wfwActivity.getId()));
								process.setCurrentActivityName(wfwActivity.getName());
								 //process.setNextActivityId(String.valueOf(wfwActivity.getNextActivity()));
//								int toDoNum =  wfwTaskRepository.findTaskNotFinish(String.valueOf(process.getId()), process.getCurrentActivityId());
//								int doneNum =  wfwTaskRepository.findTaskFinish(String.valueOf(process.getId()), process.getCurrentActivityId());
//								process.setToDoNum(toDoNum);
//								process.setDoneNum(doneNum);
//								//????????????
//								if(toDoNum == 0 ){
//									process.setTaskStatus(process.getCurrentActivityName()+"[?????????]");
//								}else{
//									process.setTaskStatus("?????????");
//								}
								
								 wfwProcessRepository.save(process);
								 System.err.println("??????????????????=[" + process.getCurrentActivityId() + "]   ??????===["+process.getCurrentActivityName()+"]");
								 //?????????????????? ????????????
								 addTaskAct(process, wfwActivity);
						         break;
							}
					   }
//					 //WfwActivity  wfwActivity =   (WfwActivity)nexts.get(0);
//					 System.err.println("???????????? ??? ???????????? =[" + wfwActivity.getId() + "]   ??????===[" + wfwActivity.getName());
//					 process.setCurrentActivityId(String.valueOf(wfwActivity.getId()));
//					 process.setCurrentActivityName(wfwActivity.getName());
//					 //WfwActivity  wfwActivity =   (WfwActivity)nexts.get(0);
//					 System.err.println("??????????????????=[" + process.getCurrentActivityId() + "]   ??????===["+process.getCurrentActivityName()+"]");
//					 //process.setNextActivityId(String.valueOf(wfwActivity.getNextActivity()));
//					 wfwProcessRepository.save(process);
//					 
//					 //?????????????????? ????????????
//					 addTaskAct(process, wfwActivity);	
				}
			}
		
		//		
		}else{
			System.err.println("???????????? ??? ????????????=");
		}
	}

	/**
	 * Add string.
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	@RequestMapping("/startFormProcess")
	@ResponseBody
	public ResultData startFormProcess(WfwFormFieldV WfwFormField) {

		// // ???????????? 1??????????????? ??????????????????0???
		// // 2?????????????????????????????????0???
		// // 3??????????????????0?????????????????????????????????0
		//
		// WfwFlow flow = wfwFlowRepository.findById(Long.parseLong(flowId));
		//
		// // ????????????????????????
		// List wfwActivitys = wfwActivityRepository.findByflowId(flowId);
		//
		// // ????????????????????????????????????0??????????????????
		// WfwActivity startActivity = (WfwActivity) wfwActivitys.get(0);
		//
		// // ????????????
		// WfwProcess process = new WfwProcess();
		// process.setFlowId(flowId);
		// process.setPName(flow.getFname() + "-" + flowId);
		// process.setCreateDate(new Date());
		//
		// // ????????????????????????
		// WfwActivity current =
		// wfwActivityRepository.findById(Long.parseLong(startActivity.getNextActivity()));
		// // ????????????????????????
		// process.setCurrentActivityId(String.valueOf(current.getId()));
		//
		//
		// process.setNextActivityId(String.valueOf(current.getNextActivity()));
		// // ???????????????
		// wfwProcessRepository.save(process);
		//
		// //?????? ?????????
		// for (WfwFormFieldV object : wfwFormFieldVList) {
		wfwFormFieldRepository.updateFieldValue(WfwFormField.getId(), WfwFormField.getFieldvalue());
		// }
		//
		// // ??????????????????????????????
		// // addTask(String.valueOf(process.getId()));
		// // ?????????????????????????????????
		// List<Map<String, String>> objects =
		// wfwActivityRepository.findByUActivityId(process.getCurrentActivityId());
		//
		// //List<User> user3 =
		// wfwActivityRepository.findByUserActivityId(process.getCurrentActivityId());
		//
		// String irsStr = JSON.toJSONString(objects);
		// List<User> users = JSON.parseArray(irsStr, User.class);
		//
		// // BeanUtil.
		// // List users2 = Dozer.convert(objects, User.class);
		// // //EntityUtils.castEntity(objects, User.class, new User());
		// // ?????????????????????
		//
		// for (Iterator iterator = users.iterator(); iterator.hasNext();) {
		// User user = (User) iterator.next();
		// WfwTask task = new WfwTask();
		// task.setTaskName(current.getName());
		// // task.setPid(String.valueOf(process.getId()));
		// task.setPid(String.valueOf(process.getId()));
		// task.setCreateDate(new Date());
		// task.setUpdateDate(new Date());
		// task.setStatus(0);
		// task.setUserId(String.valueOf(user.getId()));
		// task.setFlowId(process.getFlowId());
		// task.setCurrentId(process.getCurrentActivityId());
		// wfwTaskRepository.save(task);
		// }

		// WfwTask task = new WfwTask();
		// task.setTaskName(process.getPName());
		// task.setPid(String.valueOf(process.getId()));
		// task.setStatus(0);
		// task.setUserId(String.valueOf(1));
		//
		// task.setFlowId(flowId);
		//
		// wfwTaskRepository.save(task);

		// //??????????????????????????????
		// List users =
		// wfwActivityRepository.findByActivityId(process.getNextActivityId());
		//
		// //??????????????????
		// for (Iterator iterator = users.iterator(); iterator.hasNext();) {
		// User object = (User) iterator.next();
		//
		// WfwTask task =new WfwTask();
		// task.setStaskName(process.getPName()+object.getUserName());
		// task.setPid(String.valueOf(process.getId()));
		// task.setStatus(0);
		// task.setUserId(String.valueOf(object.getId()));
		//
		// task.setFlowId(flowId);
		//
		// }

		ResultData data = new ResultData();

		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		// data.setData(process);
		return data;
	}

	/**
	 * Add string.
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	@RequestMapping("/pay")
	@ResponseBody
	public ResultData pay(String orderSn, String status) {
		// Message message = new Message();
		// message.setId("KFK_STOCK"+System.currentTimeMillis());
		//
		// message.setMsg(order.getName());
		// message.setSendTime(new Date());
		try {
			// producer.send();
			/// redisService.set(message.getId(), message.getMsg());
			// System.err.print("send kfk express="+gson.toJson(message));
			// kafkaTemplate.send("mall", gson.toJson(message));
			// ??????????????????
			// kafkaTemplate.send("order", gson.toJson(message));
			// ??????????????????
			// payService.updatePayStatus(orderSn, status);
			String result;
			try {
				result = httpAPIService.doGet(
						env.getProperty("spring.bizmate.order") + "/callbackPay?orderSn=" + orderSn + "&payStatus=1");
				ResultData data = gson.fromJson(result, ResultData.class);
				// ??????????????????
				String sucesss = (String) data.getData();
				if (PayStatus.PAYSUCESS.getValue().equalsIgnoreCase(sucesss)) {
					System.err.println("??????????????????" + sucesss);
					// payRepository.updatePayStatus(orderSn, status);
				} else {
					new Exception("??????????????????");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// ???????????????
			// kafkaTemplate.send("express", gson.toJson(message));
			// httpAPIService.doGet(env.getProperty("spring.bizmate.express")+"/addExpress?id="+order.getId()+"&name="+order.getName()
			// +"&money=2&fee=2");
		} catch (Exception e) {
			e.printStackTrace();
		}

		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(orderSn);
		return data;
	}

	/**
	 * Add string. // * // * @param user // * the user // * @return the string
	 * // * @throws Exception //
	 */
	// @RequestMapping("/getEasyPayList")
	// @ApiOperation("???????????????")
	// @ResponseBody
	// // @CrossOrigin(originPatterns = "*", methods = {RequestMethod.GET,
	// // RequestMethod.POST})
	// public ResultData getEasyPayList() throws Exception {
	//
	// ResultData data = new ResultData();
	// data.setCode(20000);
	// data.setSuccess(true);
	// data.setMessage("??????");
	//
	// // ArrayList list = new ArrayList();
	// // list.add(mallService.findOrderById(id));
	//
	// //data.setData(payService.getEasyPayList());
	//
	// return data;
	// }
}