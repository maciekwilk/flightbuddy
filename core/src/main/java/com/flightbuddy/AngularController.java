package com.flightbuddy;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
class AngularController {

	@SuppressWarnings("SameReturnValue")
	@RequestMapping(value = "/{path:[^\\.]*}")
	public String redirect() {
	  return "forward:/";
	}
}
