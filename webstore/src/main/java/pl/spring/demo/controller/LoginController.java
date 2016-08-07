package pl.spring.demo.controller;

import static pl.spring.demo.constants.MessagesConstants.ACCES_DENEID;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import pl.spring.demo.constants.ModelConstants;
import pl.spring.demo.constants.ViewNames;

/**
 * Login controller
 * 
 * @author PWOJTKOW
 */
@Controller
public class LoginController {

	/**
	 * Method gives view to login
	 * @return - view "login" name
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {
		return "login";
	}

	/**
	 * Method gives view when login action failed
	 * @param model - model class with attributes passed to view
	 * @return - view "LOGIN" name
	 */
	@RequestMapping(value = "/loginfailed", method = RequestMethod.GET)
	public String loginerror(Model model) {
		model.addAttribute("error", "true");
		return ViewNames.LOGIN;
	}

	/**
	 * Method gives view after logout action
	 * @param model - model class with attributes passed to view
	 * @return - view "LOGIN" name
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(Model model) {
		return ViewNames.LOGIN;
	}

	/**
	 * Method gives view when something went wrong
	 * @param user - already logged user
	 * @return model with view "_403" name
	 */
	@RequestMapping(value = "/403", method = RequestMethod.GET)
	public ModelAndView accesssDenied(Principal user) {
		ModelAndView model = new ModelAndView();
		model.addObject(ModelConstants.ERROR_MESSAGE, ACCES_DENEID);
		model.setViewName(ViewNames._403);
		return model;

	}
}
