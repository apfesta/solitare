package com.andrewfesta.doublesolitare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MainController {

	@RequestMapping(value="/", method = RequestMethod.GET)
	public String displayBoard() {
		return "displayboard";
	}
	
	//TODO - totalnonsense.com vectorized playing cards in svg
	
	//Move Card around tablau
	
	//Move card from stockpile
	
	//Move card to foundation
	
	
	
}
