package com.example.jsoup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;

public class Crawling {
    private static WebDriver driver;
    public static final String WEB_DRIVER_ID = "webdriver.chrome.driver"; //드라이버 ID
    public static final String WEB_DRIVER_PATH = "C:\\chromedriver_win32\\chromedriver.exe"; //드라이버 경로

    public static void main(String[] args) throws IOException {
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);

//        Runtime.getRuntime().exec("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe --remote-debugging-port=9222 --user-data-dir=\"C:\\dd");

        ChromeOptions options = new ChromeOptions();

//        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");

        driver = new ChromeDriver(options);



        System.out.println(driver.getTitle());

        driver.manage().window().maximize();

        driver.get("https://www.naver.com");

    }
}
