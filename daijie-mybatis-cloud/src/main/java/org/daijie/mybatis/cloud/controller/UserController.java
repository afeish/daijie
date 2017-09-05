package org.daijie.mybatis.cloud.controller;

import org.daijie.core.controller.ApiController;
import org.daijie.mybatis.cloud.service.UserService;
import org.daijie.mybatis.model.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController extends ApiController<UserService, Exception> {
	
	@RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
	public User getUser(@PathVariable(name = "userId") Integer userId){
		return service.getUser(userId);
	}
	
	@RequestMapping(value = "/user/username/{userName}", method = RequestMethod.GET)
	public User getUser(@PathVariable(name = "userName") String userName){
		return service.getUserByUserName(userName);
	}
	
	@RequestMapping(value = "/user", method = RequestMethod.POST)
	public boolean addUser(@RequestBody User user){
		return service.addUser(user);
	}
}