package com.desolator.lib

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions

fun dinkIt(driver: ChromeDriver, productDetailUrl: String): ChromeDriver {
    driver.navigate().to(productDetailUrl)

    val wait = WebDriverWait(driver, 10)
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dink-it")))

    val js = driver as JavascriptExecutor
    js.executeScript("document.getElementById('dink-it').click()")

    wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("jqmWindow")))
    println(driver.pageSource)

    return driver
}