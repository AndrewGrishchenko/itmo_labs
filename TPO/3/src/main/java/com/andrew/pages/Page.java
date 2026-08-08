package com.andrew.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.andrew.Utils;

public abstract class Page {
    protected Utils utils;

    public Page(Utils utils) {
        this.utils = utils;
    }

    public Utils getUtils() {
        return utils;
    }

    public void setUtils(Utils utils) {
        this.utils = utils;
    }

    protected void safeClick(WebElement element) {
        utils.getWaitTime().until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    protected void waitClick(WebElement element, long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {}

        element.click();
    }
    
    protected void waitClick(WebElement element) {
        waitClick(element, 300);
    }

    protected WebElement untilVisible(WebElement element) {
        return utils.getWaitTime().until(ExpectedConditions.visibilityOf(element));
    }

    protected WebElement untilWait(WebElement element, long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {}

        return element;
    }

    protected WebElement untilWait(WebElement element) {
        return untilWait(element, 100);
    }
}
