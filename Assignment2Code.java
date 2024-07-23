package Assignment2;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestClass {
    private WebDriver driver;
    private String baseUrl;
    private String browserType;
    private String username;
    private String password;
    private String gridurl;
    private ExtentReports extent;
    private ExtentTest test;

    @BeforeEach
    public void setup() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        // Initialize ExtentReports
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("extentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        test = extent.createTest("Setup", "Setup the WebDriver and read configuration");

        // Read config.json
        File configFile = new File("C:\\Users\\Admin\\IdeaProjects\\Junitcourse\\src\\test\\java\\Assignment2\\config.Json");
        if (!configFile.exists()) {
            throw new FileNotFoundException("config.json file not found.");
        }
        JsonNode config = mapper.readTree(configFile);

        // Get browser type and base URL from config.json
        JsonNode browserNode = config.get("browser");
        JsonNode urlNode = config.get("url");
        JsonNode chrome =config.get("chromedriver.path");
        JsonNode ff =config.get("geckodriver.path");
        JsonNode gridu = config.get("gridUrl");


        if (browserNode == null || urlNode == null) {
            throw new IllegalArgumentException("Config JSON is missing required fields.");
        }

        browserType = browserNode.asText();
        baseUrl = urlNode.asText();
        gridurl = gridu.asText();

        // Read credentials.json
        File credentialsFile = new File("C:\\Users\\Admin\\IdeaProjects\\Junitcourse\\src\\test\\java\\Assignment2\\Credentials.Json");
        if (!credentialsFile.exists()) {
            throw new FileNotFoundException("credentials.json file not found.");
        }
        JsonNode credentials = mapper.readTree(credentialsFile);

        // Get username and password from credentials.json
        JsonNode usernameNode = credentials.get("username");
        JsonNode passwordNode = credentials.get("password");

        if (usernameNode == null || passwordNode == null) {
            throw new IllegalArgumentException("Credentials JSON is missing required fields.");
        }
        username = usernameNode.asText();
        password = passwordNode.asText();

        // Set up WebDriver based on browser type
        if (browserType.equalsIgnoreCase("chrome")) {
            System.setProperty("webdriver.chrome.driver", "C:\\Users\\Admin\\IdeaProjects\\Junitcourse\\resources\\chromedriver-win64 (2)\\chromedriver-win64\\chromedriver.exe");
            ChromeOptions obj1 = new ChromeOptions();
            driver = new RemoteWebDriver(new URL(gridurl), obj1);
        } else if ("firefox".equalsIgnoreCase(browserType)) {
            System.setProperty("webdriver.gecko.driver", "C:\\Users\\Admin\\IdeaProjects\\Junitcourse\\resources\\geckodriver.exe");
            FirefoxOptions obj2 = new FirefoxOptions();
            driver = new RemoteWebDriver(new URL(gridurl), obj2);
        } else {
            throw new IllegalArgumentException("Browser type not supported: " + browserType);
        }

        driver.manage().window().maximize();
        driver.get(baseUrl);
        WebElement usernameField = driver.findElement(By.id("user-name"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("login-button"));

        // Perform login actions
        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        loginButton.click();
        test.log(Status.PASS, "Setup completed and logged in successfully");

    }

    @AfterEach
    public void tearDown() {
        // Close the driver after each test
        if (driver != null) {
            driver.quit();
        }
        extent.flush();
    }

    @Test
    @Order(1)
    @Tag("cart")
    public void testCart() {
        test = extent.createTest("testCart", "Test adding and removing items from the cart");
        // Add items to the cart
        WebElement addToCartBackpack = driver.findElement(By.id("add-to-cart-sauce-labs-backpack"));
        WebElement addToCartBikeLight = driver.findElement(By.id("add-to-cart-sauce-labs-bike-light"));
        addToCartBackpack.click();
        addToCartBikeLight.click();
        // Navigate to the cart
        WebElement cartIcon = driver.findElement(By.className("shopping_cart_link"));
        cartIcon.click();
        // Verify items in the cart
        WebElement backpackItem = driver.findElement(By.xpath("//div[@class='inventory_item_name' and text()='Sauce Labs Backpack']"));
        WebElement bikeLightItem = driver.findElement(By.xpath("//div[@class='inventory_item_name' and text()='Sauce Labs Bike Light']"));
        assertEquals("Sauce Labs Backpack", backpackItem.getText(), "Backpack item not found in cart.");
        assertEquals("Sauce Labs Bike Light", bikeLightItem.getText(), "Bike Light item not found in cart.");
        // Remove items from the cart
        WebElement removeFromCartBackpack = driver.findElement(By.id("remove-sauce-labs-backpack"));
        WebElement removeFromCartBikeLight = driver.findElement(By.id("remove-sauce-labs-bike-light"));
        removeFromCartBackpack.click();
        removeFromCartBikeLight.click();
        // Verify items are removed from the cart
        WebElement emptyCartMessage = driver.findElement(By.className("cart_list"));
        assertEquals(0, emptyCartMessage.findElements(By.className("inventory_item_name")).size(), "Cart is not empty after removing items.");
        // Verify the "Add to Cart" button reappears on the products page
        driver.navigate().back(); // Navigate back to the products page
        WebElement addToCartBackpackAfterRemoval = driver.findElement(By.id("add-to-cart-sauce-labs-backpack"));
        WebElement addToCartBikeLightAfterRemoval = driver.findElement(By.id("add-to-cart-sauce-labs-bike-light"));
        assertEquals("Add to cart", addToCartBackpackAfterRemoval.getText(), "Add to Cart button not found for Sauce Labs Backpack.");
        assertEquals("Add to cart", addToCartBikeLightAfterRemoval.getText(), "Add to Cart button not found for Sauce Labs Bike Light.");
    }
    @Test
    @Order(2)
    @Tag("checkout")
    public void CheckOutTest() {
        test = extent.createTest("CheckOutTest", "Test the checkout process");
        // Add items to the cart
        WebElement addToCartBackpack = driver.findElement(By.id("add-to-cart-sauce-labs-backpack"));
        WebElement addToCartBikeLight = driver.findElement(By.id("add-to-cart-sauce-labs-bike-light"));


        addToCartBackpack.click();
        addToCartBikeLight.click();


        // Navigate to the cart
        WebElement cartIcon = driver.findElement(By.className("shopping_cart_link"));
        cartIcon.click();


        // Verify items are in the cart before removal
        List<WebElement> cartItems = driver.findElements(By.className("cart_item"));
        assertEquals(2, cartItems.size(), "Items not added to the cart correctly.");


        // Remove items from the cart
        WebElement removeItemBikeLight = driver.findElement(By.id("remove-sauce-labs-bike-light"));
        WebElement removeItemBackpack = driver.findElement(By.id("remove-sauce-labs-backpack"));


        removeItemBikeLight.click();
        removeItemBackpack.click();


        // Verify items are removed from the cart
        cartItems = driver.findElements(By.className("cart_item"));
        assertEquals(0, cartItems.size(), "Items were not removed from the cart.");


        System.out.println("Both items were successfully removed from the cart.");
    }
    @Test
    @Order(3)
    @Tag("productDetails")
    public void ProductDetailsPageTest() throws InterruptedException {
        test = extent.createTest("ProductDetailsPageTest", "Test viewing product details");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Thread.sleep(100);


        WebElement addToCartBackpack = driver.findElement(By.id("add-to-cart-sauce-labs-backpack"));
        addToCartBackpack.click();
        System.out.println("Added Backpack to Cart");
        Thread.sleep(500);


        WebElement addToCartBikeLight = driver.findElement(By.id("add-to-cart-sauce-labs-bike-light"));
        addToCartBikeLight.click();
        System.out.println("Added Bike Light to Cart");
        Thread.sleep(500);


        WebElement cartIcon = driver.findElement(By.className("shopping_cart_link"));
        String cartBadgeText = cartIcon.findElement(By.className("shopping_cart_badge")).getText();
        assertEquals("2", cartBadgeText, "Cart does not display correct number of items before removing.");


        WebElement clickOnBackPack = driver.findElement(By.className("inventory_item_name"));
        clickOnBackPack.click();
        System.out.println("Clicked on Backpack");
        WebElement removeItem1FromPDPT = driver.findElement(By.xpath("//*[@id=\"remove\"]"));
        removeItem1FromPDPT.click();
        System.out.println("Removed Backpack from Cart");
        WebElement dcartIcon = driver.findElement(By.className("shopping_cart_link"));
        String ccartBadgeText = dcartIcon.findElement(By.className("shopping_cart_badge")).getText();
        assertEquals("1", ccartBadgeText, "Cart does not display correct number of items before removing.");
        System.out.println("badge shows 1 item");
        WebElement addToCartBackpackButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"add-to-cart\"]")));
        String addToCartBackpackButtonText = addToCartBackpackButton.getText();
        assertEquals("Add to cart", addToCartBackpackButtonText, "Add to Cart button is not visible for Backpack.");
        System.out.println("visible");
        // Click back to products
        WebElement clickBackToProduct = wait.until(ExpectedConditions.elementToBeClickable(By.id("back-to-products")));
        clickBackToProduct.click();
        System.out.println("Back to Products");
        // Click on Bike Light to view product details
        WebElement inventoryItemContainerPage2 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"item_0_title_link\"]/div")));
        inventoryItemContainerPage2.click();
        System.out.println("Clicked on Bike Light");
        // Remove item from the product details page
        WebElement removeItem2FromPDPT = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"remove\"]")));
        removeItem2FromPDPT.click();
        Thread.sleep(2000);
        System.out.println("Removed Bike Light from Cart");
        Thread.sleep(1000);
        // Verify that "Add to Cart" button for Bike Light is visible again
        WebElement addToCartBikeLightButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"add-to-cart\"]")));
        String addToCartBikeLightButtonText = addToCartBikeLightButton.getText();
        assertEquals("Add to cart", addToCartBikeLightButtonText, "Add to Cart button is not visible for Bike Light.");
        // Click back to products
        WebElement clickBackToProductForItem2 = wait.until(ExpectedConditions.elementToBeClickable(By.id("back-to-products")));
        clickBackToProductForItem2.click();
        System.out.println("Back to Products from Bike Light");


    }


    @Test
    @Order(4)
    @Tag("Buyitems")

    public void BuyItemsTest() throws InterruptedException {
        test = extent.createTest("BuyItemsTest", "Test Buy Items functionality");

        // Add items to the cart
        WebElement addToCartBackpack = driver.findElement(By.id("add-to-cart-sauce-labs-backpack"));
        addToCartBackpack.click();
        System.out.println("Added Backpack to Cart");
        Thread.sleep(500); // Wait for 0.5 seconds


        WebElement addToCartBikeLight = driver.findElement(By.id("add-to-cart-sauce-labs-bike-light"));
        addToCartBikeLight.click();
        System.out.println("Added Bike Light to Cart");
        Thread.sleep(500); // Wait for 0.5 seconds


        // Click on the cart to proceed to checkout
        WebElement clickOnTheCart = driver.findElement(By.xpath("//*[@id=\"shopping_cart_container\"]/a"));
        clickOnTheCart.click();
        System.out.println("Shopping Cart Clicked");
        Thread.sleep(1000); // Wait for 1 second


        // Click on Checkout Button
        WebElement clickOnCheckoutButton = driver.findElement(By.xpath("//*[@id=\"checkout\"]"));
        clickOnCheckoutButton.click();
        System.out.println("Clicked on Checkout Button");
        Thread.sleep(1000); // Wait for 1 second


        // Enter checkout information
        WebElement firstNameField = driver.findElement(By.xpath("//*[@id=\"first-name\"]"));
        firstNameField.sendKeys("Ghazlo");
        System.out.println("Entered First Name");
        Thread.sleep(500); // Wait for 0.5 seconds


        WebElement lastNameField = driver.findElement(By.id("last-name"));
        lastNameField.sendKeys("princess");
        System.out.println("Entered Last Name");
        Thread.sleep(500); // Wait for 0.5 seconds


        WebElement postalCodeField = driver.findElement(By.id("postal-code"));
        postalCodeField.sendKeys("12345");
        System.out.println("Entered Postal Code");
        Thread.sleep(500); // Wait for 0.5 seconds


        WebElement continueButton = driver.findElement(By.xpath("//*[@id=\"continue\"]"));
        continueButton.click();
        System.out.println("Clicked Continue");
        Thread.sleep(1000); // Wait for 1 second


        // Click Finish Button
        WebElement finishButton = driver.findElement(By.xpath("//*[@id=\"finish\"]"));
        finishButton.click();
        System.out.println("Finished Checkout");
        Thread.sleep(2000); // Wait for 2 seconds


        // Click Back to Home
        WebElement backToHome = driver.findElement(By.xpath("//*[@id=\"back-to-products\"]"));
        backToHome.click();
        System.out.println("Back to Home");
    }


    @Test
    @Order(5)
    @Tag("Cart_Persistence")
    public void Cart_Persistence() throws InterruptedException {
        test = extent.createTest("Cart_Persistence", "Test Cart_Persistence functionality");

        // Add items to the cart
        WebElement addToCartBackpack = driver.findElement(By.id("add-to-cart-sauce-labs-backpack"));
        addToCartBackpack.click();
        System.out.println("Added Backpack to Cart");
      //  Thread.sleep(2000); // Wait for 0.5 seconds


        WebElement addToCartBikeLight = driver.findElement(By.id("add-to-cart-sauce-labs-bike-light"));
        addToCartBikeLight.click();
        System.out.println("Added Bike Light to Cart");
       // Thread.sleep(2000); // Wait for 0.5 seconds


        // Verify cart icon shows 2 items
        WebElement cartIcon = driver.findElement(By.className("shopping_cart_link"));
        String cartBadgeText = cartIcon.findElement(By.className("shopping_cart_badge")).getText();
        assertEquals("2", cartBadgeText, "Cart does not display correct number of items before logout.");


        // Log out from the application
        WebElement clickOnMenu = driver.findElement(By.id("react-burger-menu-btn"));
        clickOnMenu.click();
        System.out.println("Opened Menu");
        Thread.sleep(2000); // Wait for 0.5 seconds


        WebElement logOut = driver.findElement(By.id("logout_sidebar_link"));
        logOut.click();
        System.out.println("Logged Out");


        // Wait for the page to load after logout
        Thread.sleep(2000); // Wait for 2 seconds


        // Log back in
        WebElement username_Field = driver.findElement(By.id("user-name"));
        WebElement password_Field = driver.findElement(By.id("password"));
        WebElement login_Button = driver.findElement(By.id("login-button"));
        username_Field.sendKeys("standard_user");
        password_Field.sendKeys("secret_sauce");
        login_Button.click();


        // Wait for the page to load after re-login
        Thread.sleep(2000); // Wait for 2 seconds


        // Verify cart icon still shows 2 items after re-login
        cartIcon = driver.findElement(By.className("shopping_cart_link"));
        cartBadgeText = cartIcon.findElement(By.className("shopping_cart_badge")).getText();
        assertEquals("2", cartBadgeText, "Cart does not retain items after re-login.");
    }
    @Test
    @Order(6)
    @Tag("sort")
    void SortingOfProducts() throws InterruptedException {
        test = extent.createTest("SortingOfProducts", "Test product sorting functionality");

        // Wait for the page to load and products to be visible
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("inventory_item")));


        // Locate the sort dropdown
        WebElement sortDropdown = driver.findElement(By.xpath("//*[@id='header_container']/div[2]/div/span/select"));


        // Verify sorting by Name (A to Z)
        sortDropdown.click();
        System.out.println("Clicked sort dropdown");
        WebElement optionAZ = driver.findElement(By.xpath("//*[@id='header_container']/div[2]/div/span/select/option[1]"));
        optionAZ.click();
        Thread.sleep(2000); // Adding delay for sorting to complete


        List<WebElement> productElementsAZ = driver.findElements(By.xpath("//div[@class='inventory_item_name']"));
        List<String> productNamesAZ = new ArrayList<>();
        for (WebElement product : productElementsAZ) {
            productNamesAZ.add(product.getText());
        }
        List<String> sortedProductNamesAZ = new ArrayList<>(productNamesAZ);
        Collections.sort(sortedProductNamesAZ);
        assertEquals(sortedProductNamesAZ, productNamesAZ, "Products should be sorted by Name (A to Z)");
        System.out.println("Sorted by A to Z");


        WebElement UpsortDropdown = driver.findElement(By.xpath("//*[@id='header_container']/div[2]/div/span/select"));
        UpsortDropdown.click();
        System.out.println("sortDropdownclicked");
        WebElement optionZA = driver.findElement(By.xpath("//*[@id='header_container']/div[2]/div/span/select/option[2]"));
        optionZA.click();
        Thread.sleep(2000); // Adding delay for sorting to complete


        List<WebElement> productElementsZA = driver.findElements(By.xpath("//div[@class='inventory_item_name']"));
        List<String> productNamesZA = new ArrayList<>();
        for (WebElement product : productElementsZA) {
            productNamesZA.add(product.getText());
        }
        List<String> sortedProductNamesZA = new ArrayList<>(productNamesZA);
        Collections.sort(sortedProductNamesZA, Collections.reverseOrder());
        assertEquals(sortedProductNamesZA, productNamesZA, "Products should be sorted by Name (Z to A)");
        System.out.println("Sorted by Z to A");


        // Verify sorting by Price (low to high)
        WebElement DsortDropdown = driver.findElement(By.xpath("//*[@id='header_container']/div[2]/div/span/select"));
        DsortDropdown.click();
        WebElement optionLowHigh = driver.findElement(By.xpath("//*[@id='header_container']/div[2]/div/span/select/option[3]"));
        optionLowHigh.click();
        Thread.sleep(2000); // Adding delay for sorting to complete


        List<WebElement> priceElementsLowHigh = driver.findElements(By.xpath("//div[@class='inventory_item_price']"));
        List<Double> productPricesLowHigh = new ArrayList<>();
        for (WebElement price : priceElementsLowHigh) {
            productPricesLowHigh.add(Double.parseDouble(price.getText().replace("$", "")));
        }
        List<Double> sortedProductPricesLowHigh = new ArrayList<>(productPricesLowHigh);
        Collections.sort(sortedProductPricesLowHigh);
        assertEquals(sortedProductPricesLowHigh, productPricesLowHigh, "Products should be sorted by Price (low to high)");
        System.out.println("Sorted by Price (low to high)");


        // Verify sorting by Price (high to low)
        WebElement HLsortDropdown = driver.findElement(By.xpath("//*[@id='header_container']/div[2]/div/span/select"));
        HLsortDropdown.click();
        WebElement optionHighLow = driver.findElement(By.xpath("//*[@id='header_container']/div[2]/div/span/select/option[4]"));
        optionHighLow.click();
        Thread.sleep(2000); // Adding delay for sorting to complete


        List<WebElement> priceElementsHighLow = driver.findElements(By.xpath("//div[@class='inventory_item_price']"));
        List<Double> productPricesHighLow = new ArrayList<>();
        for (WebElement price : priceElementsHighLow) {
            productPricesHighLow.add(Double.parseDouble(price.getText().replace("$", "")));
        }
        List<Double> sortedProductPricesHighLow = new ArrayList<>(productPricesHighLow);
        Collections.sort(sortedProductPricesHighLow, Collections.reverseOrder());
        assertEquals(sortedProductPricesHighLow, productPricesHighLow, "Products should be sorted by Price (high to low)");
        System.out.println("Sorted by Price (high to low)");


        System.out.println("Sorting verification completed");
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        TestClass test = new TestClass();

           test.setup();
            test.testCart();
            test.CheckOutTest();
            test.tearDown();
            test.ProductDetailsPageTest();
            test.BuyItemsTest();
            test.Cart_Persistence();
            test.SortingOfProducts();
        }
}
