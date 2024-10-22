package com.example.SellerService.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.example.SellerService.Model.Seller;
import com.example.SellerService.Service.EmailService;
import com.example.SellerService.Service.SellerService;
import com.example.SellerService.Utils.PasswordUtils;

@Controller
@RequestMapping("/seller")
public class SellerController {

	@Autowired
	private SellerService sellerService;

	@Autowired
	private PasswordUtils pwd_obj;

	@Autowired
	private EmailService emailService;

	private static final Logger logger = LoggerFactory.getLogger(SellerController.class);

	public Long getSellerIdFromCookies(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("sellerId".equals(cookie.getName())) {
					return Long.parseLong(cookie.getValue());
				}
			}
		}
		return null;
	}

	// Registration form
	@GetMapping("/sellerRegistration")
	public String registrationForm(Model model) {
		model.addAttribute("seller", new Seller());
		return "SellerRegistration";
	}
	
	@GetMapping("/buyer/sellerRegistration")
	public String regBuyer(Model model) {
		model.addAttribute("seller",new Seller());
		return "buyersellerreg";
	}
	// Handle registration
	@PostMapping("/sellerRegistration")
	public String registration(@ModelAttribute Seller seller) throws NoSuchAlgorithmException {
		sellerService.insertSeller(seller);
		logger.info("New seller registered with email: {}", seller.getEmail());
		return "redirect:http://localhost:8080/ecom/LoginPage";
	}

	// Send verification email (OTP)
	@PostMapping("/api/send-verificationseller")
	@ResponseBody
	public ResponseEntity<String> sendVerificationEmail(@RequestParam("email") String sellerEmail) {
		String otp = emailService.generateOtp();
		boolean emailSent = emailService.sendEmail(sellerEmail, otp);

		if (emailSent) {
			logger.info("OTP sent successfully to email: {}", sellerEmail);
			return ResponseEntity.ok("OTP sent successfully.");
		} else {
			logger.error("Failed to send OTP to email: {}", sellerEmail);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send OTP.");
		}
	}

	// Verify OTP
	@PostMapping("/api/verify-codeseller")
	@ResponseBody
	public ResponseEntity<String> verifyOtp(@RequestParam("email") String sellerEmail,
			@RequestParam("code") String otp) {
		boolean isOtpValid = emailService.verifyOtp(sellerEmail, otp);

		if (isOtpValid) {
			logger.info("OTP verified successfully for email: {}", sellerEmail);
			return ResponseEntity.ok("OTP verified successfully.");
		} else {
			logger.warn("Invalid OTP attempted for email: {}", sellerEmail);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP.");
		}
	}

	// Handle login
	@PostMapping("/seller/handleLogin")
	public String sellerLogin(@RequestParam(required = false) String email,
			@RequestParam(required = false) String mobileNumber, @RequestParam String password, Model model,
			HttpServletResponse response) throws NoSuchAlgorithmException {
		Seller seller_obj = null;

		if (email != null) {
			seller_obj = sellerService.getSellerDetailsByEmail(email);
		} else if (mobileNumber != null) {
			seller_obj = sellerService.getSellerDetailsByMobileNumber(mobileNumber);
		}
		if (seller_obj == null || !seller_obj.getPassword().equals(pwd_obj.hashPassword(password))) {
			logger.warn("Login failed for email: {} or mobileNumber: {}", email, mobileNumber);
			String msg = "Invalid Email or Password...\nIf you are a new user kindly register to access our services.";
			model.addAttribute("errorMessage", msg);
			return "redirect:http://localhost:8080/ecom/LoginPage";
		} else {
			// Set seller ID in a cookie
			Cookie sellerCookie = new Cookie("sellerId", seller_obj.getSellerId().toString());
			sellerCookie.setPath("/");
			sellerCookie.setMaxAge(24 * 60 * 60); // Expires in 1 day
			sellerCookie.setHttpOnly(true); // Cookie is only accessible by the server
			response.addCookie(sellerCookie);

			logger.info("Login successful for seller with ID: {}", seller_obj.getSellerId());
			model.addAttribute("sellerId",seller_obj.getSellerId());

			return "SellerDashboard";
//            return "forward:/SellerDashboard";

		}
	}

	// Seller dashboard
	@GetMapping("/SellerDashboard")
	public String showDashboard(HttpServletRequest request, Model model) {
		// Retrieve the sellerId from the cookie
		String sellerIdStr = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("sellerId")) {
					sellerIdStr = cookie.getValue();
					break;
				}
			}
		}

		if (sellerIdStr == null) {
			logger.warn("sellerId cookie not found, redirecting to login.");
			return "redirect:http://localhost:8080/ecom/LoginPage";
		}

		Long sellerId = null;
		try {
			sellerId = Long.parseLong(sellerIdStr);
		} catch (NumberFormatException e) {
			logger.error("Invalid sellerId in cookie: {}", sellerIdStr);
			return "redirect:http://localhost:8080/ecom/LoginPage";
		}

		Seller seller = sellerService.getSellerDetailsById(sellerId);
		if (seller == null) {
			logger.warn("No seller found with ID: {}", sellerId);
			return "redirect:http://localhost:8080/ecom/LoginPage";
		}

		model.addAttribute("seller", seller);
		logger.info("Displaying dashboard for seller with ID: {}", sellerId);
		return "SellerDashboard";
	}

	// View profile page with pre-filled details
	@GetMapping("/seller/sellerprofile")
	public String viewProfile(HttpServletRequest request, Model model) {
		// Retrieve the sellerId from the cookie
		String sellerIdStr = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("sellerId")) {
					sellerIdStr = cookie.getValue();
					break;
				}
			}
		}

		if (sellerIdStr == null) {
			logger.warn("sellerId cookie not found, redirecting to login.");
			return "redirect:http://localhost:8080/ecom/LoginPage";
		}

		Long sellerId = null;
		try {
			sellerId = Long.parseLong(sellerIdStr);
		} catch (NumberFormatException e) {
			logger.error("Invalid sellerId in cookie: {}", sellerIdStr);
			return "redirect:http://localhost:8080/ecom/LoginPage";
		}

		Seller seller = sellerService.getSellerDetailsById(sellerId);
		if (seller == null) {
			logger.warn("No seller found with ID: {}", sellerId);
			return "redirect:http://localhost:8080/ecom/LoginPage";
		}

		logger.info("Displaying profile for seller with email: {}", seller.getEmail());

		model.addAttribute("seller", seller);
		return "sellerprofile";
	}

	// Update profile information
	@PostMapping("/seller/updateProfile")
	public String updateProfileInfo(@RequestParam("firstName") String firstName,
			@RequestParam("lastName") String lastName, @RequestParam("email") String email,
			@RequestParam("mobileNumber") String mobileNumber, HttpServletRequest request, HttpServletResponse response,
			Model model) {

		// Retrieve the sellerId from the cookie
		String sellerIdStr = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("sellerId")) {
					sellerIdStr = cookie.getValue();
					break;
				}
			}
		}

		if (sellerIdStr == null) {
			logger.warn("sellerId cookie not found during profile update, redirecting to login.");
			return "redirect:http://localhost:8080/ecom/LoginPage";
		}

		Long sellerId;
		try {
			sellerId = Long.parseLong(sellerIdStr);
		} catch (NumberFormatException e) {
			logger.error("Invalid sellerId in cookie during profile update: {}", sellerIdStr);
			return "redirect:http://localhost:8080/ecom/LoginPage";
		}

		Seller existingSeller = sellerService.getSellerDetailsById(sellerId);
		if (existingSeller == null) {
			logger.warn("No seller found with ID: {} during profile update.", sellerId);
			return "redirect:http://localhost:8080/ecom/LoginPage";
		}

		// Update seller's information
		existingSeller.setFirstName(firstName);
		existingSeller.setLastName(lastName);
		existingSeller.setEmail(email);
		existingSeller.setMobileNumber(mobileNumber);
		sellerService.updateSellerProfile(existingSeller);
		logger.info("Seller profile updated successfully for ID: {}", sellerId);

		model.addAttribute("seller", existingSeller);
		return "sellerprofile"; // Redirect to profile page after updating
	}

	@PostMapping("/seller/updateAddress")
	public String updateAddress(@RequestParam("street") String street, @RequestParam("city") String city,
			@RequestParam("state") String state, @RequestParam("postalCode") int postalCode,
			@RequestParam("country") String country, HttpServletRequest request, HttpServletResponse response,
			Model model) {

		// Retrieve the buyerId from the cookie
		Long sellerId = null;
		if (getSellerIdFromCookies(request) == null) {
			return "redirect:http://localhost:8080/ecom/LoginPage";
		} else {
			sellerId = getSellerIdFromCookies(request);
		}

		Seller existingSeller = sellerService.getSellerDetailsById(sellerId);
		if (existingSeller == null) {
			return "redirect:http://localhost:8080/ecom/LoginPage";
		}

		// Update buyer's address information
		existingSeller.setStreet(street);
		existingSeller.setCity(city);
		existingSeller.setState(state);
		existingSeller.setPostalCode(postalCode);
		existingSeller.setCountry(country);

		// Save the updated buyer back to the database
		sellerService.updateSellerProfile(existingSeller);

		// Update the model with updated buyer details
		model.addAttribute("seller", existingSeller);

		return "sellerprofile"; // Redirect to profile page after updating
	}

	// Change password
	@PostMapping("/seller/changePassword")
	public String changePassword(@RequestParam("current-password") String currentPassword,
			@RequestParam("new-password") String newPassword, HttpServletRequest request, Model model)
			throws NoSuchAlgorithmException {

		// Retrieve the sellerId from the cookie
		String sellerIdStr = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("sellerId")) {
					sellerIdStr = cookie.getValue();
					break;
				}
			}
		}

		if (sellerIdStr == null) {
			logger.warn("sellerId cookie not found during password change, redirecting to login.");
			return "redirect:http://localhost:8080/ecom/LoginPage";
		}

		Long sellerId;
		try {
			sellerId = Long.parseLong(sellerIdStr);
		} catch (NumberFormatException e) {
			logger.error("Invalid sellerId in cookie during password change: {}", sellerIdStr);
			return "redirect:http://localhost:8080/ecom/LoginPage";
		}

		Seller seller = sellerService.getSellerDetailsById(sellerId);
		if (seller == null) {
			logger.warn("No seller found with ID: {} during password change.", sellerId);
			return "redirect:http://localhost:8080/ecom/LoginPage";
		}

		// Verify the current password
		if (!seller.getPassword().equals(pwd_obj.hashPassword(currentPassword))) {
			logger.warn("Incorrect current password for seller with ID: {}", sellerId);
			model.addAttribute("errorMessage", "Incorrect current password. Please try again.");
			return "sellerprofile";
		}

		// Update the password
		seller.setPassword(pwd_obj.hashPassword(newPassword));
		sellerService.updateSellerProfile(seller);

		logger.info("Password updated successfully for seller with ID: {}", sellerId);
		model.addAttribute("successMessage", "Password updated successfully.");
		model.addAttribute("seller", seller);
		return "sellerprofile"; // Redirect or return to the profile page
	}

	// Logout
	@GetMapping("/logout")
	public String logout(HttpServletResponse response) {
		// Invalidate the sellerId cookie by setting maxAge to 0
		Cookie sellerCookie = new Cookie("sellerId", null);
		sellerCookie.setPath("/");
		sellerCookie.setMaxAge(0); // Deletes the cookie
		response.addCookie(sellerCookie);

		logger.info("Seller logged out, sellerId cookie invalidated.");
		return "redirect:http://localhost:8080/ecom/LoginPage";
	}

	@GetMapping("/manage")
	public String showSellerProducts(HttpServletRequest request, Model model) {
		// Retrieve sellerId from cookies
		String sellerId = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("sellerId")) {
					sellerId = cookie.getValue();
					break;
				}
			}
		}

		// If sellerId is null, redirect to the login page
		if (sellerId == null) {
			return "redirect:http://localhost:8080/ecom/LoginPage";
		}

		// Redirect to the Product Microservice through the API Gateway
		String redirectUrl = "http://localhost:8080/products/manage?sellerId=" + sellerId;
		return "redirect:" + redirectUrl;
	}
	@GetMapping("/sellerController")
	public ResponseEntity<Seller> getSellerObj(@RequestParam("sellerId") Long sellerId){
		Seller seller=sellerService.findById(sellerId);
		return ResponseEntity.ok(seller);
	}
}
