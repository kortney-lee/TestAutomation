import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.*;
import org.openqa.selenium.JavascriptExecutor;
import java.util.List;
import java.util.Random;


public class verifyAddToCartWorkflow {

    private WebDriver driver;
    private static final String URL = "https://www.nike.com/";

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.get(URL);
    }

    @Test(description = "Verify landing on the right URL and page loading successfully")
    public void verifyLandingPage() {
        driver.get(URL);
        Assert.assertEquals(driver.getCurrentUrl(), URL);
        Assert.assertTrue(driver.getTitle().length() > 0);
    }

    @Test(description = "Search for a product and verify the search results return products", dependsOnMethods = {"verifyLandingPage"})
    @Parameters({"productName"})
    public void searchProduct(String productName) {
        WebElement searchBox = driver.findElement(By.id("nav-search-icon"));
        searchBox.click();
        WebElement inputBox = driver.findElement(By.id("gn-search-input"));
        inputBox.sendKeys(productName + Keys.RETURN);

        // This is for testing only I will remove later
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify search results return products
        boolean productFound = false;
        List<WebElement> products = driver.findElements(By.className("product-card__link-overlay"));
        for (WebElement product : products) {
            if (product.getText().contains(productName)) {
                productFound = true;
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", product);
                break;
            }
        }

        Assert.assertTrue(productFound, "Product not found in search results");
    }

    @Test(description = "Select a random shoe size", dependsOnMethods = {"searchProduct"})
    public void selectShoeSize() {
        List<WebElement> sizeLabels = driver.findElements(By.xpath("//form[@id='buyTools']/div/fieldset/div/div/label"));
        if (!sizeLabels.isEmpty()) {
            Random random = new Random();
            WebElement randomSizeLabel = sizeLabels.get(random.nextInt(sizeLabels.size()));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", randomSizeLabel);
        } else {
            Assert.fail("No shoe sizes available to select or not in stock");
        }
    }

    @Test(description = "Add the item to the cart", dependsOnMethods = {"selectShoeSize"})
    public void addToCart() {
        WebElement addToCartButton = driver.findElement(By.xpath("//form/div[2]/div/div/button"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addToCartButton);

        // This is for testing only I will remove later
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test(description = "Open cart and increase quantity", dependsOnMethods = {"addToCart"})
    @Parameters({"quantity"})
    public void openCartAndIncreaseQuantity(int quantity) {
        WebElement cartButton = driver.findElement(By.cssSelector(".nav-bag-icon"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", cartButton);

        // This is for testing only I will remove later
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WebElement dropdownElement = driver.findElement(By.cssSelector(".css-1grl6ds:nth-child(2) .css-46rwad"));
        Select dropdown = new Select(dropdownElement);
        dropdown.selectByIndex(quantity);
    }

    @Test(description = "Verify the total price for the quantity", dependsOnMethods = {"openCartAndIncreaseQuantity"})
    @Parameters({"quantity"})
    public void verifyTotalPrice(int quantity) {
        // This should locate the price for the item in the cart
        String pricePerItemText = driver.findElement(By.cssSelector("span:nth-child(2) .formatted-price")).getText();
        double pricePerItem = Double.parseDouble(pricePerItemText.substring(1));

        // Once located this will calculate the total
        double expectedTotalPrice = pricePerItem * quantity;

        // This will get the total price in cart including quantity increase
        String totalPriceText = driver.findElement(By.xpath("//main[@id='maincontent']/div[2]/div/div/div[3]/div/div/div/div/div[2]/p/span/span/span")).getText();
        double totalPrice = Double.parseDouble(totalPriceText.substring(1));

        // This will compare the two
        Assert.assertEquals(totalPrice, expectedTotalPrice, 0.01);
    }



    @AfterClass
    public void close() {
        if (driver != null) {
            driver.quit();
        }
    }
}
