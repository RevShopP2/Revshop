package productservice.productservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

	// Handle generic exceptions
	@ExceptionHandler(Exception.class)
	public ModelAndView handleGeneralException(Exception ex) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("message", "An unexpected error occurred.");
		mav.addObject("details", ex.getMessage());
		mav.setViewName("error"); // Default error.html view in templates folder
		return mav;
	}
}
