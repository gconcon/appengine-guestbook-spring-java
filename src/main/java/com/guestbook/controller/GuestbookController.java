package com.guestbook.controller;

import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@Controller
public class GuestbookController {
	@RequestMapping("/")
	public String home() {
		return "guestbook";
	}
	
	@RequestMapping("/loggedIn")
	public ModelAndView listGuestbook() {
		UserService userService = UserServiceFactory.getUserService();
		User currentUser = userService.getCurrentUser();

		if (currentUser == null) {
			return new ModelAndView("redirect:"
					+ userService.createLoginURL("/"));
		} else {
			return new ModelAndView("guestbook", "welcomeMsg", "You are authenticated, "
					+ currentUser.getNickname());
		}
	}

	@RequestMapping("/sign")
	public String signGuestbook(
			@RequestParam(required = true, value = "guestbookName") String guestbookName,
			@RequestParam(required = true, value = "content") String content,
			Model model) {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		Key guestbookKey = KeyFactory.createKey("Guestbook", guestbookName);
		Date date = new Date();
		Entity greeting = new Entity("Greeting", guestbookKey);
		greeting.setProperty("user", user);
		greeting.setProperty("date", date);
		greeting.setProperty("content", content);

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		datastore.put(greeting);

		model.addAttribute("guestbookName", guestbookName);
		return "guestbook";
	}
}
