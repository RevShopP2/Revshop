package com.example.BuyerMicroService.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ecom")
public class FrontpagesController {
	@GetMapping("/LoginPage")
	public String loginPage(Model model) {
		return "LoginPage";
	}
	@GetMapping
	public String frontPage(Model model) {
		return "indexPage";
	}
	@GetMapping("/welcomepage")
	public String welcomePage(Model model) {
		return "welcomepage";
	}
    @GetMapping("/emptyCart")
    public String emptyCartPage(Model model) {
        return "emptyCart";
    }
}