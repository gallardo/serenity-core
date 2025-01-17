package net.thucydides.core.webdriver;

import net.serenitybdd.core.collect.NewList;
import net.thucydides.core.annotations.locators.SmartAjaxElementLocator;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepFailure;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
public class WhenLocatingWebElements {

    @Mock
    WebDriverFacade driver;

    @Mock
    WebElement webElement;

    @Mock
    StepFailure failure;

    Field field;

    static class SomePageObject {

        @FindBy(id = "someId")
        public WebElement someField;

    }

    @Before
    public void initMocks() throws NoSuchFieldException, IOException {
        MockitoAnnotations.initMocks(this);

        field = SomePageObject.class.getField("someField");

        StepEventBus.getEventBus().reset();
        StepEventBus.getEventBus().registerListener(new BaseStepListener(Files.createTempDirectory("out").toFile()));

        when(driver.withTimeoutOf(any(Duration.class))).thenReturn(driver);
        when(driver.getCurrentImplicitTimeout()).thenReturn(Duration.ofSeconds(0));
        when(driver.findElement(By.id("someId"))).thenReturn(webElement);
        when(driver.findElements(By.id("someId"))).thenReturn(NewList.of(webElement));
    }

    @Test(timeout = 2000)
    public void should_find_element_immediately_if_a_previous_step_has_failed() throws IOException {

        StepEventBus.getEventBus().registerListener(new BaseStepListener(Files.createTempDirectory("out").toFile()));

        SmartAjaxElementLocator locator = new SmartAjaxElementLocator(driver, field, MobilePlatform.NONE);
        StepEventBus.getEventBus().stepFailed(failure);
        locator.findElement();
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void should_wait_for_find_element_immediately_if_no_previous_step_has_failed() {

        expectedException.expect(ElementNotInteractableException.class);
        SmartAjaxElementLocator locator = new SmartAjaxElementLocator(driver, field, MobilePlatform.NONE);
        locator.findElement();
    }

}
