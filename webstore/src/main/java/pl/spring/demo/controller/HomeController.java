package pl.spring.demo.controller;

import static pl.spring.demo.constants.MessagesConstants.INFO_TEXT;
import static pl.spring.demo.constants.MessagesConstants.WELCOME;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import pl.spring.demo.constants.ModelConstants;
import pl.spring.demo.constants.ViewNames;

@Controller
public class HomeController {


	/**
	 * Method gives welcome view
	 * @param model - model class with parameters to set in view
	 * @return view "WELCOME" name
	 */
	@RequestMapping("/")
	public String welcome(Model model) {
		model.addAttribute(ModelConstants.GREETING, WELCOME);
		model.addAttribute(ModelConstants.INFO, INFO_TEXT);
		return ViewNames.WELCOME;
	}
}
