package com.hadron.wfw.api;


import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hadron.wfw.GetPayNo;
import com.hadron.wfw.HttpAPIService;
import com.hadron.wfw.PayStatus;
import com.hadron.wfw.RedisService;
import com.hadron.wfw.ResultData;
import com.hadron.wfw.model.EasyPay;
import com.hadron.wfw.producer.Producer;
import com.hadron.wfw.service.EasyPayService;

import io.swagger.annotations.ApiOperation;

/**
 * User controller
 * <p/>
 * Created in 2018.11.16
 * <p/>
 *
 */
@Controller
@RequestMapping("/pay")
public class PayController {

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
	private Environment env;
	
    private static Gson gson = new GsonBuilder().create();
    /**
     * Add string.
     *
     * @param user the user
     * @return the string
     */
    @RequestMapping("/createOrder")
    @ResponseBody
    public ResultData createOrder(EasyPay pay) {
    	
    	
        pay.setPaySn(GetPayNo.getOrderNo());
        pay.setCreateDate(new Date());
        pay.setUpdateDate(new Date());
        
    	payService.save(pay);
//        Message message = new Message();
//        message.setId("KFK_STOCK"+System.currentTimeMillis());
//        message.setMsg(order.getName());
//        message.setSendTime(new Date());

        try {
    		//producer.send();
    		   //redisService.set(message.getId(), message.getMsg());
    	 	   //System.err.print("send kfk express="+gson.toJson(message));
    	       // kafkaTemplate.send("mall", gson.toJson(message));
    	        //kafkaTemplate.send("pay", gson.toJson(message));
        	
			   // 
		} catch (Exception e) {
			e.printStackTrace();
		}

        ResultData data =new ResultData();
        data.setCode(200);
        data.setSuccess(true);
        data.setMessage("??????");
        data.setData(pay.getPaySn());
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
    public ResultData  pay(String orderSn,String status) {
    	
    	

    	
//        Message message = new Message();
//        message.setId("KFK_STOCK"+System.currentTimeMillis());
//        
//        message.setMsg(order.getName());
//        message.setSendTime(new Date());
        try {
    		//producer.send();
    		  /// redisService.set(message.getId(), message.getMsg());
    	 	   //System.err.print("send kfk express="+gson.toJson(message));
    	       // kafkaTemplate.send("mall", gson.toJson(message));
        	    //??????????????????
    	        //kafkaTemplate.send("order", gson.toJson(message));
		    	//??????????????????
		    	payService.updatePayStatus(orderSn, status);
			    String result;
				try {
					result = httpAPIService.doGet(env.getProperty("spring.bizmate.order")+"/callbackPay?orderSn="+orderSn+"&payStatus=1");
					ResultData data = gson.fromJson(result, ResultData.class);
					    //??????????????????
					      String sucesss  = (String) data.getData();
					      if(PayStatus.PAYSUCESS.getValue().equalsIgnoreCase(sucesss)){
					    	  System.err.println("??????????????????"+sucesss);
					    	  //payRepository.updatePayStatus(orderSn, status);
					    }else{
					    	new Exception("??????????????????");
					    }
				} catch (Exception e) {
					e.printStackTrace();
				}
        	    
    	        //???????????????
    	       // kafkaTemplate.send("express", gson.toJson(message));
			    //httpAPIService.doGet(env.getProperty("spring.bizmate.express")+"/addExpress?id="+order.getId()+"&name="+order.getName() +"&money=2&fee=2");
		} catch (Exception e) {
			e.printStackTrace();
		}

        ResultData data =new ResultData();
        data.setCode(200);
        data.setSuccess(true);
        data.setMessage("??????");
        data.setData(orderSn);
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
	@RequestMapping("/getEasyPayList")
	@ApiOperation("???????????????")
	@ResponseBody
	// @CrossOrigin(originPatterns = "*", methods = {RequestMethod.GET,
	// RequestMethod.POST})
	public ResultData getEasyPayList() throws Exception {

		ResultData data = new ResultData();
		data.setCode(20000);
		data.setSuccess(true);
		data.setMessage("??????");

		// ArrayList list = new ArrayList();
		// list.add(mallService.findOrderById(id));

		data.setData(payService.getEasyPayList());
		
		return data;
	}
}