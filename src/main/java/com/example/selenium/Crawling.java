package com.example.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Crawling {
    public static final String WEB_DRIVER_ID = "webdriver.chrome.driver"; //드라이버 ID
    public static final String WEB_DRIVER_PATH = "C:\\chromedriver_win32\\chromedriver.exe"; //드라이버 경로
    public static final List<String> urls = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        try {
            System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //크롬 설정을 담은 객체 생성
        ChromeOptions options = new ChromeOptions();
        //브라우저가 눈에 보이지 않고 내부적으로 돈다.
        //설정하지 않을 시 실제 크롬 창이 생성되고, 어떤 순서로 진행되는지 확인할 수 있다.
//        options.addArguments("headless");

        //위에서 설정한 옵션은 담은 드라이버 객체 생성
        //옵션을 설정하지 않았을 때에는 생략 가능하다.
        //WebDriver객체가 곧 하나의 브라우저 창이라 생각한다.
        WebDriver driver = new ChromeDriver(options);

        //이동을 원하는 url
        String url = "url";

        //WebDriver을 해당 url로 이동한다.
        driver.get(url);

        //HTTP응답속도보다 자바의 컴파일 속도가 더 빠르기 때문에 임의적으로 대기한다.
        try {Thread.sleep(15000);} catch (InterruptedException e) {}
        String preUrl = "";
        for(int i = 1; i <= 34; i++) {
//            try {Thread.sleep(500);} catch (InterruptedException e) {}
            System.out.println(i);
            url = "url"+ i;
            driver.get(url);
            // <div> 태그 찾기 (style 속성 값으로 검색)
            try{
                //3초동안 쓰레드 대기 , 이전에 가능하면 실행
                driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
                WebElement divElement = driver.findElement(By.className("slideshow"));
                // <img> 태그 찾기
                WebElement imgElement = divElement.findElement(By.tagName("img"));
                // src 속성값 가져오기
                String srcAttributeValue = imgElement.getAttribute("src");
                if(preUrl.equals(srcAttributeValue)) {
                    System.out.println(preUrl + " ==== " + srcAttributeValue);
                    i--;
                }else {
                    System.out.println(srcAttributeValue);
                    preUrl = srcAttributeValue;
                    String dir="dir";
                    try {
                        Crawling.downloadToDir(new URL(preUrl), new File(dir));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }catch (Exception E) {
                driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
                // <div> 태그 찾기 (style 속성 값으로 검색)
                WebElement divElement = driver.findElement(By.cssSelector("tag"));

                // <img> 태그 찾기
                WebElement imgElement = divElement.findElement(By.tagName("img"));

                // src 속성값 가져오기
                String srcAttributeValue = imgElement.getAttribute("src");
                System.out.println("src attribute value: " + srcAttributeValue);
                if(preUrl.equals(srcAttributeValue)) {
                    System.out.println(preUrl + " ==== " + srcAttributeValue);
                    i--;
                }else {
                    System.out.println(srcAttributeValue);
                    preUrl = srcAttributeValue;
                    String dir="C:\\Users\\USER\\Desktop\\asd";
                    try {
                        Crawling.downloadToDir(new URL(preUrl), new File(dir));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        //WebElement는 html의 태그를 가지는 클래스이다.
//        List<WebElement> el1 = driver.findElements(By.className("advance-link"));
//        System.out.println(el1);

        try {
            //드라이버가 null이 아니라면
            if(driver != null) {
                //드라이버 연결 종료
                driver.close();
                //프로세스 종료
                driver.quit();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        System.out.println(urls.size());
        for (String u : urls) {
            System.out.println(u);

        }
    }

    /** 정해진 file로 url의 내용을 저장한다. (저장되는 파일명은 url과 무관함)  **/
    public static void downloadToFile(URL url, File savedFile) throws IOException {
        if (url==null) throw new IllegalArgumentException("url is null.");
        if (savedFile==null) throw new IllegalArgumentException("savedFile is null.");
        if (savedFile.isDirectory()) throw new IllegalArgumentException("savedFile is a directory.");
        downloadTo(url, savedFile, false);
    }

    /** 정해진 디렉토리로 url의 내용을 저장한다. (저장되는 파일명이 url에 따라서 달라짐) **/
    public static void downloadToDir(URL url, File dir) throws IOException {
        if (url==null) throw new IllegalArgumentException("url is null.");
        if (dir==null) throw new IllegalArgumentException("directory is null.");
        if (!dir.exists()) throw new IllegalArgumentException("directory is not existed.");
        if (!dir.isDirectory()) throw new IllegalArgumentException("directory is not a directory.");
        downloadTo(url, dir, true);
    }

    private static void downloadTo(URL url, File targetFile, boolean isDirectory) throws IOException{

        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            File saveFilePath=null;

            if (isDirectory) {
                if (disposition != null) {
                    int index = disposition.indexOf("filename=");
                    if (index > 0) {
                        fileName = disposition.substring(index + 10,
                                disposition.length() - 1);
                    }
                } else {
                    String fileURL=url.toString();
                    fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
                    int questionIdx=fileName.indexOf("?");
                    if (questionIdx>=0) {
                        fileName=fileName.substring(0, questionIdx);
                    }
                    fileName= URLDecoder.decode(fileName);
                }
                saveFilePath = new File(targetFile, fileName);
            }
            else {
                saveFilePath=targetFile;
            }

            InputStream inputStream = httpConn.getInputStream();

            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            byte[] buffer = new byte[4096];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();
            System.out.println("File downloaded to " + saveFilePath);
        } else {
            System.err.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }

}
