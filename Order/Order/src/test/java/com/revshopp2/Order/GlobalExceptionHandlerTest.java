package com.revshopp2.Order;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import com.revshopp2.Order.exceptions.GlobalExceptionHandler;


class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleGeneralException() {
        Exception exception = new Exception("Test exception");

        ModelAndView mav = globalExceptionHandler.handleGeneralException(exception);

        assertEquals("error", mav.getViewName());
        assertEquals("An unexpected error occurred.", mav.getModel().get("message"));
        assertEquals("Test exception", mav.getModel().get("details"));
    }
}

