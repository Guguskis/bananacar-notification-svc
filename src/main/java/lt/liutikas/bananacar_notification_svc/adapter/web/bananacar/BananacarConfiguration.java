package lt.liutikas.bananacar_notification_svc.adapter.web.bananacar;

import lt.liutikas.bananacar_notification_svc.common.Loggable;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.net.URL;
import java.time.Duration;

@Configuration
public class BananacarConfiguration implements Loggable {

    private RemoteWebDriver remoteWebDriver;
    private BrowserMobProxyServer proxy;

    @PreDestroy
    public void closeWebDriver() {

        if (proxy != null) {
            proxy.stop();
        }

        if (remoteWebDriver != null) {
            try {
                remoteWebDriver.quit();
            } catch (Exception e) {
                // silently
            }
        }
    }

    @Bean
    public BrowserMobProxy browserMobProxy() {

        proxy = new BrowserMobProxyServer();

        proxy.enableHarCaptureTypes(
                CaptureType.RESPONSE_CONTENT,
                CaptureType.RESPONSE_HEADERS,
                CaptureType.RESPONSE_BINARY_CONTENT
        );

        proxy.start();

        return proxy;
    }

    @ConditionalOnProperty(value = "selenium.proxy.mode", havingValue = "LOCAL", matchIfMissing = true)
    @Bean
    public WebDriver firefoxWebDriverLocal(BrowserMobProxy browserMobProxy) {

        getLogger().info("Creating local Firefox web driver");

        Proxy proxy = ClientUtil.createSeleniumProxy(browserMobProxy);
        FirefoxOptions options = buildFirefoxOptions(proxy);

        return new FirefoxDriver(options);
    }

    @ConditionalOnProperty(value = "selenium.proxy.mode", havingValue = "LOCAL_TO_DOCKER")
    @Bean
    public WebDriver firefoxWebDriverLocalToDocker(
            @Value("${selenium.firefox.url}") URL seleniumFirefoxUrl,
            BrowserMobProxy browserMobProxy
    ) {

        getLogger().info("Creating local-to-remote Firefox web driver");

        Proxy proxy = ClientUtil.createSeleniumProxy(browserMobProxy);
        proxy.setHttpProxy("host.docker.internal:" + browserMobProxy.getPort());
        proxy.setSslProxy("host.docker.internal:" + browserMobProxy.getPort());

        FirefoxOptions options = buildFirefoxOptions(proxy);

        remoteWebDriver = new RemoteWebDriver(seleniumFirefoxUrl, options, false);

        return remoteWebDriver;
    }

    @ConditionalOnProperty(value = "selenium.proxy.mode", havingValue = "DOCKER")
    @Bean
    public WebDriver firefoxWebDriverDocker(
            @Value("${selenium.firefox.url}") URL seleniumFirefoxUrl,
            BrowserMobProxy browserMobProxy
    ) {

        getLogger().info("Creating docker Firefox web driver");

        Proxy proxy = ClientUtil.createSeleniumProxy(browserMobProxy);
        FirefoxOptions options = buildFirefoxOptions(proxy);

        remoteWebDriver = new RemoteWebDriver(seleniumFirefoxUrl, options, false);

        return remoteWebDriver;
    }

    private static FirefoxOptions buildFirefoxOptions(Proxy proxy) {

        FirefoxOptions options = new FirefoxOptions();

        options.setProfile(new FirefoxProfile());
        options.setProxy(proxy);
        options.setAcceptInsecureCerts(true);
        options.setImplicitWaitTimeout(Duration.ofSeconds(5));
        options.addPreference("network.http.accept-encoding.secure", "gzip, deflate");
        options.addPreference("network.http.accept-encoding", "gzip, deflate");

        return options;
    }
}
