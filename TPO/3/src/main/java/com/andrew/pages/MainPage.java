package com.andrew.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.andrew.Utils;

public class MainPage extends Page {
    public MainPage(Utils utils) {
        super(utils);
        PageFactory.initElements(utils.getDriver(), this);
    }

    // SOURCE
    @FindBy(xpath = "//button[@aria-label='More source languages']")
    private WebElement sourceLanguageSelectButton;

    @FindBy(xpath = "//input[@aria-label='Search languages']")
    private WebElement sourceLanguageSearchInput;

    @FindBy(xpath = "//div[@aria-label='Language search results']//div//div")
    private WebElement sourceLanguageButton;


    // CONTROLS
    @FindBy(xpath = "//button[@aria-label='Clear source text']")
    private WebElement clearSourceButton;

    @FindBy(xpath = "//button[contains(@aria-label,'Swap languages')]")
    private WebElement swapLanguagesButton;

    @FindBy(xpath = "//button[@aria-label='Copy translation']")
    private WebElement copyTargetTextButton;

    @FindBy(xpath = "(//button[@aria-label='Translate by voice'])[2]")
    private WebElement startMicButton;

    @FindBy(xpath = "//button[@aria-label='Stop translation by voice']")
    private WebElement stopMicButton;


    // TARGET
    @FindBy(xpath = "//button[@aria-label='More target languages']")
    private WebElement targetLanguageSelectButton;

    @FindBy(xpath = "(//input[@aria-label='Search languages'])[2]")
    private WebElement targetLanguageSearchInput;

    @FindBy(xpath = "//div[@aria-label='Language search results']//div//div")
    private WebElement targetLanguageButton;


    // TRANSLATE
    @FindBy(xpath = "//textarea[@aria-label='Source text']")
    private WebElement sourceInput;

    @FindBy(xpath = "//div[@data-text and @data-location='2']")
    private WebElement targetText;


    // TEXT
    @FindBy(xpath = "//button[@data-language-code='auto']")
    private WebElement autodetectText;

    public void selectSourceLanguage(String language) {
        waitClick(sourceLanguageSelectButton);

        untilVisible(sourceLanguageSearchInput).sendKeys(language);

        safeClick(sourceLanguageButton);
    }

    public void selectTargetLanguage(String language) {
        waitClick(targetLanguageSelectButton);

        untilVisible(targetLanguageSearchInput).sendKeys(language);

        safeClick(targetLanguageButton);
    }

    public void enterText(String text) {
        utils.getActions().moveToElement(sourceInput)
            .click()
            .sendKeys(text)
            .perform();
    }

    public void clearSource() {
        safeClick(clearSourceButton);
    }
    
    public void swapLanguages() {
        waitClick(swapLanguagesButton);
    }

    public void copyTargetText() {
        safeClick(copyTargetTextButton);
    }

    public void startMic() {
        safeClick(startMicButton);
    }

    public void stopMic() {
        safeClick(stopMicButton);
    }

    public String getTargetText() {
        return untilVisible(targetText).getAttribute("data-text");
    }

    public String getAutodetectText() {
        waitTranslating();
        return untilVisible(autodetectText).getText();
    }

    public String getSelectedSourceLanguage() {
        return utils.getDriver().findElement(
            By.xpath("(//button[@role='tab' and @aria-selected='true'])[1]")
        ).getText();
    }

    public String getSelectedTargetLanguage() {
        return utils.getDriver().findElement(
            By.xpath("(//button[@role='tab' and @aria-selected='true'])[2]")
        ).getText();
    }

    private void waitTranslating() {
        utils.getWaitTime().until(driver -> {
            WebElement el = driver.findElement(By.xpath("//div[@data-text and @data-location='2']"));
            String text = el.getAttribute("data-text");
            return text != null && !text.isEmpty();
        });
    }
}
