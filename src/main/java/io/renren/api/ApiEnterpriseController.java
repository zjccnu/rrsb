package io.renren.api;

import io.renren.utils.annotation.IgnoreAuth;
import io.renren.utils.annotation.LoginUser;
import io.renren.entity.EmployeeEntity;
import io.renren.entity.EnterpriseEntity;
import io.renren.entity.TokenEntity;
import io.renren.service.EmployeeService;
import io.renren.service.EnterpriseService;
import io.renren.service.TokenService;
import io.renren.service.UserService;
import io.renren.service.impl.EnterpriseServiceImpl;
import io.renren.utils.DateUtils;
import io.renren.utils.R;
import io.renren.utils.validator.Assert;

import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

/**
 * API登录授权
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-03-23 15:31
 */
@RestController
@RequestMapping("/api")
public class ApiEnterpriseController {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private EnterpriseService enterpriseService;
    
    
    

    /**
     * 添加企业接口已修改，
     * 此接口未定义   
     *   
   **/
    
    
  /*  @PostMapping("enterpriseAdd")
    public R enterpriseAdd(@LoginUser EmployeeEntity emp,EnterpriseEntity ent){
    	ent.setEntemp(emp.getEmpid());
    	enterpriseService.save(ent);
        
        return R.ok();
    }*/
    
    /**
     * 插入企业信息
     * 何奔
     * 4/10
     * @throws ParseException 
     */
    
    @PostMapping("AddEnterpriseInfo")
    public R AddEnterpriseInfo(@LoginUser EmployeeEntity emp,String empName , String entName ,String entProvice,String entCity,String entArea,String entAddress,String bz1 ) throws ParseException{
    	/**
    	 * 1插入企业，
    	 * 2获取插入企业对应的企业id   
    	 * 3更新员工表的状态 	emp.state 
    	 * 4向sysuser插入用户信息，将empid作为sysuser主键。
    	 * 5向sys_user_role插入用户角色信息
    	 */
    	
    	
    	Long empid= emp.getEmpid();  
    	String empphone=emp.getEmpphone()  ;
    	String password = emp.getEmppassword() ;
    	password = new Sha256Hash(password).toHex();
    	System.out.println(password);
    
    	
    	@SuppressWarnings("deprecation")
    	
		//String newDate =new Date().toLocaleString();//创建时间
    	
    	String currentTime = DateUtils.format(new Date(), DateUtils.DATE_PATTERN);
    	
    	
    	EnterpriseEntity enterpriseEntity = new EnterpriseEntity();
    		enterpriseEntity.setEntname(entName);
    		enterpriseEntity.setEntmanager(empName);
    		enterpriseEntity.setEntcreatetime(currentTime);
    		enterpriseEntity.setEntemp(emp.getEmpid());
    		enterpriseEntity.setEntphone( emp.getEmpphone());
    		enterpriseEntity.setEntarea(entArea);
    		enterpriseEntity.setEntaddress(entAddress);
    		enterpriseEntity.setEntprovice(entProvice);
    		enterpriseEntity.setEntcity(entCity);
    		
    		enterpriseEntity.setBz1(bz1);
    		enterpriseService.save(enterpriseEntity);
    		
    		
    		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  	    	Date d=new Date();
  	
    		emp.setEmpent(enterpriseService.getEntId(enterpriseEntity));
    		emp.setEmpexpiretime(DateUtils.getTimeDelayDay(currentTime,15));
    		emp.setEmphiretime(currentTime);
    		emp.setEmpname(empName);
    		emp.setEmptype("1000");
    		emp.setEmpstate("1");
    		

        	employeeService.update(emp);
        	
    	
        	DBManager DB=new DBManager();
        	DB.insert(empid, empphone, password);
        	DB.addBoss(empid);
    
    	
    
    	
    	
    	
		return R.ok();
    	
    	
    }
    
    
    
    @PostMapping("getEnterpriseById")
    
    public R getEnterpriseById(HttpServletResponse response,@LoginUser EmployeeEntity emp ,String entId ){
    	response.setHeader("Access-Control-Allow-Origin","*");
    	/**
    	 * 通过企业ID获取企业信息
    	 * 1.获取员工状态判断
    	 * 2.类型转换并判断entid是否为空
    	 * 3.
    	 * 4/14
    	 * 何奔
    	 */	
    	String code=emp.getEmpstate();
    	//System.out.println(code);
    	if(code=="1"){
    		//System.out.println("su");
    		return R.error("该账号已加入企业");
    	}
    	
    	
    	else{
    		long s = Integer.parseInt(entId);
    		 Assert.isBlank(entId, "企业id不能为空");
    		//System.out.println("suc");
    		Map<String , Object> map=new HashMap<String , Object>();
    		EnterpriseEntity E= enterpriseService.queryObject(s);
    		Assert.isNull(E, "企业id不存在");
    		map.put("ent", E);
    		
    		Map<String , Object> mop = new HashMap<String , Object>();
    		mop.put("empent" , s);
    		map.put("enttotal", employeeService.queryTotal(mop));
    		
    		return R.ok(map);
    	}
    	
    	
    }
    
    
  

}
