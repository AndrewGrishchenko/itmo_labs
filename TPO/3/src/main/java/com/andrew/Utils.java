package com.andrew;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v148.browser.Browser;
import org.openqa.selenium.devtools.v148.browser.model.PermissionType;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Utils {
    private WebDriver driver;
    private JavascriptExecutor js;
    private WebDriverWait wait;
    private Actions actions;

    public void setupDriver() {
        WebDriverManager.chromiumdriver().driverVersion("148.0.7778.167").setup();

        driver = new ChromeDriver();
        
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));
        driver.manage().window().setSize(new Dimension(1050, 716));
        
        DevTools devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();

        devTools.send(
            Browser.grantPermissions(
                List.of(PermissionType.CLIPBOARDREADWRITE, PermissionType.AUDIOCAPTURE),
                Optional.empty(),
                Optional.empty()
            )
        );

        js = (JavascriptExecutor) driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(25));
        actions = new Actions(driver);

        driver.get("https://translate.google.com/");
    }

    public WebDriver getDriver() {
        return driver;
    }

    public WebDriverWait getWaitTime() {
        return wait;
    }

    public JavascriptExecutor getJsExecutor() {
        return js;
    }

    public Actions getActions() {
        return actions;
    }

    public void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
