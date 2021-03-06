package com.hadron.wfw.api;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hadron.wfw.CalculationUtil;
import com.hadron.wfw.GetPayNo;
import com.hadron.wfw.HttpAPIService;
import com.hadron.wfw.PayStatus;
import com.hadron.wfw.RedisService;
import com.hadron.wfw.ResultData;
import com.hadron.wfw.SalaryPhase;
import com.hadron.wfw.SalaryType;
import com.hadron.wfw.SourceType;
import com.hadron.wfw.model.EasyPay;
import com.hadron.wfw.model.SalaryGroup;
import com.hadron.wfw.model.SalaryItem;
import com.hadron.wfw.model.SalaryProcess;
import com.hadron.wfw.model.SalaryTemplate;
import com.hadron.wfw.model.SalaryUser;
import com.hadron.wfw.model.WfwActivity;
import com.hadron.wfw.model.WfwActivityRules;
import com.hadron.wfw.model.WfwFlow;
import com.hadron.wfw.model.UserField;
import com.hadron.wfw.model.WfwProcess;
import com.hadron.wfw.model.WfwUser;
import com.hadron.wfw.producer.Producer;
import com.hadron.wfw.service.EasyPayService;
import com.hadron.wfw.service.SalaryGroupRepository;
import com.hadron.wfw.service.SalaryItemRepository;
import com.hadron.wfw.service.SalaryProcessRepository;
import com.hadron.wfw.service.SalaryTemplateRepository;
import com.hadron.wfw.service.UserFieldRepository;
import com.hadron.wfw.service.UserRepository;
import com.hadron.wfw.service.WfwUserRepository;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;

import io.swagger.annotations.ApiOperation;

/**
 * User controller
 * <p/>
 * Created in 2018.11.16
 * <p/>
 *
 */
@Controller
@RequestMapping("/salary")
public class SalaryTeamplateController {

    /**
     * The User service.
     */
    @Autowired
    EasyPayService payService;
    @Autowired
    private HttpAPIService httpAPIService;
    @Autowired
    private Producer producer;
    @Autowired
    private KafkaTemplate kafkaTemplate;
	@Autowired
	private RedisService redisService;
	
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    SalaryProcessRepository salaryProcessRepository;
    
    @Autowired
    SalaryItemRepository salaryItemRepository;
    
    @Autowired
    SalaryTemplateRepository salaryTemplateRepository;
    
    @Autowired
    SalaryGroupRepository salaryGroupRepository;
    
    @Autowired
   	private UserFieldRepository userFieldRepository;
 
    
    @Autowired
   	private WfwUserRepository wfwUserRepository;
    
	@Autowired
	private Environment env;
	
    private static Gson gson = new GsonBuilder().create();
    
    
    /**
     * Add string.
     *
     * @param user the user
     * @return the string
     */
    @RequestMapping("/createTemp")
    @ResponseBody
    public ResultData createTemp(SalaryTemplate pay) {
        salaryTemplateRepository.save(pay);
        ResultData data =new ResultData();
        data.setCode(200);
        data.setSuccess(true);
        data.setMessage("??????");
        data.setData(pay);
        return data;
    }
    
    /**
     * Add string.
     *
     * @param user the user
     * @return the string
     */
    @RequestMapping("/createItem")
    @ResponseBody
    public ResultData createItem(SalaryItem pay) {
        salaryItemRepository.save(pay);
        ResultData data =new ResultData();
        data.setCode(200);
        data.setSuccess(true);
        data.setMessage("??????");
        data.setData(pay);
        return data;
    }
    
    
    
    /**
     * Add string.
     *
     * @param user the user
     * @return the string
     */
    @RequestMapping("/createSalrayUser")
    @ResponseBody
    public ResultData createSalrayUser(SalaryUser pay) {
    	salaryTemplateRepository.save(pay);
        ResultData data =new ResultData();
        data.setCode(200);
        data.setSuccess(true);
        data.setMessage("??????");
        data.setData(pay);
        return data;
    }
    
    
    /**
     * Add string.
     *
     * @param user the user
     * @return the string
     */
    @RequestMapping("/createSalrayGroup")
    @ResponseBody
    public ResultData createSalrayGroup(SalaryGroup pay) {
    	salaryGroupRepository.save(pay);
        ResultData data =new ResultData();
        data.setCode(200);
        data.setSuccess(true);
        data.setMessage("??????");
        data.setData(pay);
        return data;
    }
    
    /**
     * Add string.
     *
     * @param user the user
     * @return the string
     */
    @RequestMapping("/pay")
    @ResponseBody
    public ResultData  start(String templateId) {
    	
    	
    	//??????????????????
    	//?????????????????????????????????
    	
    	//???????????????????????????
    	
    	//??????????????????
    	
    	//?????????????????????
    	
    	//fill ????????????


        ResultData data =new ResultData();
        data.setCode(200);
        data.setSuccess(true);
        data.setMessage("??????");
        data.setData(null);
        return data;
    }

	/**
	 * Add string.
	 *
	 * @param user
	 *            the user
	 * @return the string
	 * @throws Exception
	 */
	@GetMapping("/getTemplateList/{id}")
	@ResponseBody
	public ResultData getTemplateList(@PathVariable String id) throws Exception {

		List<SalaryTemplate> flows = salaryTemplateRepository.findAll();

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
	 * Add string.
	 *
	 * @param user
	 *            the user
	 * @return the string
	 * @throws Exception
	 */
	@GetMapping("/getSalaryItemList/{id}")
	@ResponseBody
	// @CrossOrigin(originPatterns = "*", methods = {RequestMethod.GET,
	// RequestMethod.POST})
	public ResultData getSalaryItemList(@PathVariable String id) throws Exception {

		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(salaryItemRepository.findByTempId(id));
		
		return data;
	}
	
	/**
	 * Add string.
	 *
	 * @param user
	 *            the user
	 * @return the string
	 * @throws Exception
	 */
	@GetMapping("/getSalaryColumnList/{id}")
	@ResponseBody
	// @CrossOrigin(originPatterns = "*", methods = {RequestMethod.GET,
	// RequestMethod.POST})
	public ResultData getSalaryColumnList(@PathVariable String id) throws Exception {

		List <UserField>  fields = userFieldRepository.findByGroupId(id);
		
		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(fields);
		
		return data;
	}
	
	
	@GetMapping("/getSalaryItem/{id}")
	@ResponseBody
	// @CrossOrigin(originPatterns = "*", methods = {RequestMethod.GET,
	// RequestMethod.POST})
	public ResultData getSalaryItem(@PathVariable String id) throws Exception {
		
		SalaryItem salaryItem= salaryItemRepository.findById(Long.parseLong(id));
		List <UserField>  fields = userFieldRepository.findByGroupId("4");
		StringBuffer sb =new StringBuffer();
		
		for (UserField userField : fields) {
			sb.append(userField.getName()+";");
		}
		
		
		salaryItem.setReffieldColumn(sb.toString());
		
		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(salaryItem);
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

		SalaryTemplate template = salaryTemplateRepository.findById(Long.parseLong(id));
		// ????????????
		SalaryProcess process = new SalaryProcess();
		process.setTempalteName(template.getTempalteName());
		process.setTemplateId(String.valueOf(template.getId()));

		//??????????????????
		process.setPhase(SalaryPhase.BRFORETAX.getValue());
		process.setCpyId(template.getCpyId());
		process.setCircle(template.getCircle());
		
		//??????????????????
		List<Map<String, String>> obj = wfwUserRepository.findByTemplateId(String.valueOf(template.getId()));
		String irs= JSON.toJSONString(obj);
		List<WfwUser> users = JSON.parseArray(irs, WfwUser.class);
		
		System.err.println("???????????????????????????=" + users.size());
		
		

		// ???????????????
		salaryProcessRepository.save(process);

		// ???????????????????????????
		List<SalaryItem> wfwFormFields = salaryItemRepository.findByTempId(id);

		System.err.println("?????????????????????????????????,????????????????????????    ????????????=" + wfwFormFields.size());

		// ???????????????????????????,????????????????????????
		List<SalaryItem> newwfwFormFields = new ArrayList();
		
		if(users !=null&& users.size()>0 && wfwFormFields !=null){
			
			for (SalaryItem object : wfwFormFields) {

				//????????? ??????????????????
				for (WfwUser user : users) {
					// object.setUserId(userId);
					SalaryItem field = new SalaryItem();
					field.setFieldName(object.getFieldName());
					field.setFieldType(object.getFieldType());
					field.setGroupId(object.getGroupId());
					field.setRules(object.getRules());
					field.setRulesType(object.getRulesType());
					// ???????????????
					field.setPId(String.valueOf(process.getId()));
					// field.setActivityId(object.getActivityId());
					field.setParentId(String.valueOf(object.getId()));
					field.setUserId(String.valueOf(user.getId()));
					//??????
					if(SalaryType.QUOTE.getValue().equals(object.getRulesType())){
						System.err.print("????????????      ????????????=" + object.getRulesType());
						String rule  = object.getRules();
						//?????????????????????
						if(SourceType.BASE.getValue().equals(object.getReffieldTable())){
							
							System.err.println("????????????  ???????????????" + object.getReffieldTable()+"]");
							    object.getReffieldColumn();
							    //????????????????????????
							    UserField userfield = userFieldRepository.findByNameId(object.getReffieldColumn(),String.valueOf(user.getId()));

							    if(userfield !=null){
							     System.err.println("????????????  userfield???" + userfield.getFieldvalue()+"]");
							    	field.setFieldValue(userfield.getFieldvalue());
							    	field.setFieldName(userfield.getCnName());
							    	field.setReffieldTable(object.getReffieldTable());
							    }
							    
							    
						//????????????    
						}else if(SourceType.DEDUCT.getValue().equals(object.getReffieldTable())){
							    System.err.println("????????????  ???????????????" + object.getReffieldTable()+"]");
							    
							    UserField userfield = userFieldRepository.findByNameId(object.getReffieldColumn(),String.valueOf(user.getId()));;
						}
						
						//?????????????????????
					}else if (SalaryType.CALCULATE.getValue().equals(object.getRulesType())){
						System.err.println("????????????      ?????????????????????=" + object.getRulesType());
						//?????????????????????
						if(SourceType.BASE.getValue().equals(object.getReffieldTable())){
							    object.getReffieldColumn();

							    String val =  object.getReffieldColumn();
								String [] aa1 = val.split("&");
								//??????????????????
								String newstr = val.replace("&", "");
								ExpressRunner runner = new ExpressRunner(false, false);
								DefaultContext<String, Object> context = new DefaultContext<String, Object>();
								
								for (String string : aa1) {
									//System.out.println(string+"===="+CalculationUtil.isNumericZidai(string));
									//?????????Id
									if(CalculationUtil.check(string)){
										//????????????????????????
									    UserField userfield = userFieldRepository.findByName(string);
									    
										context.put(string, new BigDecimal(userfield.getFieldvalue()));
										//context.put("c", new BigDecimal("0.15384615384615385"));
										//context.put("d", new BigDecimal("1"));
									}
									Object r = null;
									try {
										r = runner.execute(newstr, context, null, false, false);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									//??????????????????
									field.setFieldValue(r.toString());
									//
								}
						//????????????    
						}else if(SourceType.DEDUCT.getValue().equals(object.getReffieldTable())){
							    //UserField userfield = userFieldRepository.findById(Long.parseLong(object.getReffieldColumn()));
						}
					}
					
					salaryItemRepository.save(field);

					newwfwFormFields.add(field);
				}
				

			}
		}
		//
		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(newwfwFormFields);
		return data;
	}
	
	@ResponseBody
	@RequestMapping("/updateSalaryItem")
	public ResultData updateSalaryItem(@RequestBody SalaryItem message) {
		// addOrUpdate(message);
		// ????????????????????????
		/// List<WfwFormField> wfwFormFields =
		// wfwFormFieldRepository.findByflowId(id);

		salaryItemRepository.save(message);

		// ??????????????????????????????
		// UPDATE t_wfw_form_field SET activity_id =?,rules =? WHERE id =?

		System.err.println(message.getId() + "  field " + message.getId() + "  rules " + message.getRules());

		ResultData data = new ResultData();
		data.setCode(200);
		data.setSuccess(true);
		data.setMessage("??????");
		data.setData(message);
		return data;

	}
}