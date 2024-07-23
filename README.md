This package contains a set of automated tests using Selenium WebDriver for testing an e-commerce web application. It includes various functionalities such as cart operations, checkout process, product details, and sorting of products. The tests use JUnit 5 for test management and ExtentReports for generating test reports.

**Prerequisites**
Java Development Kit (JDK): Ensure JDK 8 or later is installed.
Maven: For dependency management and build automation.
Selenium WebDriver: For browser automation.
JUnit 5: For running the tests.
ExtentReports: For generating HTML reports.
Browser Drivers: ChromeDriver and GeckoDriver for Chrome and Firefox respectively.
Configuration Files: config.json and Credentials.json for storing configuration and credentials.
**Setup
Clone the Repository**

Clone the repository where the package is located:

Navigate to Project Directory

Place config.json and Credentials.json in the appropriate directory:

bash
Copy code
src/test/java/Assignment2/
Ensure config.json contains:

json
Copy code
{
     "browser": "chrome",
  "gridUrl": "http://localhost:4444/wd/hub",
  "url": "https://www.saucedemo.com",
  "chromedriver.path": "C:\\Users\\Admin\\IdeaProjects\\Junitcourse\\resources\\chromedriver.exe",
  "geckodriver.path": "C:\\Users\\Admin\\IdeaProjects\\Junitcourse\\resources\\geckodriver.exe"
}
And Credentials.json contains:

json
Copy code
{
    "username": "standard_user",
  "password": "secret_sauce"
}
Tests
The tests are designed to verify various functionalities of the web application. Each test is annotated with JUnit 5 annotations and logged using ExtentReports.

Test Class: TestClass
setup(): Initializes WebDriver, reads configuration files, and logs into the application.
tearDown(): Closes the WebDriver and flushes the ExtentReports.
testCart(): Tests adding and removing items from the cart.
CheckOutTest(): Tests the checkout process.
ProductDetailsPageTest(): Tests viewing and removing product details.
BuyItemsTest(): Tests the functionality of buying items.
Cart_Persistence(): Tests cart persistence after logging out and back in.
SortingOfProducts(): Tests sorting of products by various criteria.
**Running the Tests**
Using IDE such as IntelliJ right-clicking on the test class or method and selecting "Run".

**Report Generation**
ExtentReports generates an HTML report for the test execution. The report is saved as extentReport.html in the root directory of the project.

Troubleshooting
FileNotFoundException: Ensure config.json and Credentials.json are located in the correct directory and the paths in the code are accurate.
WebDriver Issues: Verify that the paths to ChromeDriver and GeckoDriver are correct and the drivers are compatible with your browser versions.
Configuration Issues: Ensure config.json fields such as browser, url, chromedriver.path, and geckodriver.path are correctly set
