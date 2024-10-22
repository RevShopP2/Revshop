package com.example.BuyerMicroService;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import com.example.BuyerMicroService.controller.FrontpagesController;

class FrontpagesControllerTest {

    @InjectMocks
    private FrontpagesController frontpagesController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginPage() {
        Model model = mock(Model.class);
        String viewName = frontpagesController.loginPage(model);
        assertEquals("LoginPage", viewName);
    }

    @Test
    void testFrontPage() {
        Model model = mock(Model.class);
        String viewName = frontpagesController.frontPage(model);
        assertEquals("indexPage", viewName);
    }

    @Test
    void testWelcomePage() {
        Model model = mock(Model.class);
        String viewName = frontpagesController.welcomePage(model);
        assertEquals("welcomepage", viewName);
    }

    @Test
    void testEmptyCartPage() {
        Model model = mock(Model.class);
        String viewName = frontpagesController.emptyCartPage(model);
        assertEquals("emptyCart", viewName);
    }
}

