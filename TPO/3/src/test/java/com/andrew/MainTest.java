package com.andrew;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import com.andrew.pages.MainPage;

public class MainTest {
    private static Utils utils;
    private static WebDriver driver;
    private static MainPage mainPage;

    @BeforeEach
    public void setUp() {
        utils = new Utils();
        utils.setupDriver();
        driver = utils.getDriver();
        mainPage = new MainPage(utils);
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void testTextInput() {
        mainPage.enterText("Hallo Welt!");

        assertEquals("Hello World!", mainPage.getTargetText());
    }

    @Test
    public void testSpecialChars() {
        mainPage.enterText("Hello 123!@#$");
        assertEquals("Hello 123!@#$", mainPage.getTargetText());
    }

    @Test
    public void testMultiline() {
        mainPage.enterText("Hallo\nich\nHallo");
        assertEquals("Hello\nI\nHello", mainPage.getTargetText());
    }

    @Test
    public void testSourceLanguage() {
        mainPage.selectSourceLanguage("Russian");
        
        assertEquals("Russian", mainPage.getSelectedSourceLanguage());
    }

    @Test
    public void testAutodetect() {
        mainPage.enterText("Hallo Welt!");

        assertEquals("Hello World!", mainPage.getTargetText());
        assertEquals("German - Detected", mainPage.getAutodetectText());
    }

    @Test
    public void testTargetLanguage() {
        mainPage.selectTargetLanguage("Russian");

        assertEquals("Russian", mainPage.getSelectedTargetLanguage());
    }

    @Test
    public void testSwap() {
        mainPage.selectSourceLanguage("English");
        mainPage.selectTargetLanguage("Russian");

        assertEquals("English", mainPage.getSelectedSourceLanguage());
        assertEquals("Russian", mainPage.getSelectedTargetLanguage());
        
        mainPage.swapLanguages();

        assertEquals("Russian", mainPage.getSelectedSourceLanguage());
        assertEquals("English", mainPage.getSelectedTargetLanguage());
    }

    @Test
    public void testSwapTranslate() {
        mainPage.selectSourceLanguage("English");
        mainPage.selectTargetLanguage("German");
        
        mainPage.enterText("Hello");

        mainPage.swapLanguages();

        try {
            Thread.sleep(500);
        } catch (Exception e) {}

        assertEquals("Hello", mainPage.getTargetText());
    }

    @Test
    public void testCopy() {
        mainPage.enterText("Hallo Welt!");
        mainPage.copyTargetText();

        String text = (String) utils.getJsExecutor().executeAsyncScript(
            "navigator.clipboard.readText().then(arguments[0]).catch(err => arguments[0]('ERROR: ' + err));"
        );

        assertEquals("Hello World!", text);
    }

    @Test
    public void testUrl() {
        utils.getDriver().get("https://translate.google.com/?sl=de&tl=en&text=Hallo%20Welt!&op=translate");

        assertEquals("German", mainPage.getSelectedSourceLanguage());
        assertEquals("English", mainPage.getSelectedTargetLanguage());
        assertEquals("Hello World!", mainPage.getTargetText());
    }

    @Test
    public void testMic() {
        mainPage.selectSourceLanguage("English");
        mainPage.selectTargetLanguage("German");
        
        mainPage.startMic();

        try {
            Thread.sleep(200);
        } catch (Exception e) {}

        mainPage.stopMic();
    }
}
