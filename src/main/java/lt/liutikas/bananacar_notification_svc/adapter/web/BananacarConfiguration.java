package lt.liutikas.bananacar_notification_svc.adapter.web;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class BananacarConfiguration {

    @Bean
    public BrowserMobProxy browserMobProxy() {

        BrowserMobProxyServer proxy = new BrowserMobProxyServer();

        proxy.enableHarCaptureTypes(
                CaptureType.RESPONSE_CONTENT,
                CaptureType.RESPONSE_HEADERS,
                CaptureType.RESPONSE_BINARY_CONTENT
        );

        proxy.start();

        return proxy;
    }

    @Bean
    public WebDriver firefoxWebDriver(BrowserMobProxy browserMobProxy) {

        Proxy proxy = ClientUtil.createSeleniumProxy(browserMobProxy);

        FirefoxOptions options = new FirefoxOptions();

        options.setProfile(new FirefoxProfile());
        options.setProxy(proxy);
        options.setImplicitWaitTimeout(Duration.ofSeconds(5));
        options.addPreference("network.http.accept-encoding.secure", "gzip, deflate");
        options.addPreference("network.http.accept-encoding", "gzip, deflate");

        return new FirefoxDriver(options);
    }
}
