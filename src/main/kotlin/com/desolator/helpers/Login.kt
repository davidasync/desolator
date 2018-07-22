package com.desolator.helpers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.google.gson.Gson
import com.google.gson.JsonArray
import org.openqa.selenium.By
import org.openqa.selenium.Cookie
import org.openqa.selenium.chrome.ChromeDriver
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.PrintWriter
import java.util.*


private fun setCookies(driver: ChromeDriver): ChromeDriver {
    val cookiePath = "tokopedia-cookie.json"
    val cookieFile = File(cookiePath)

    if (!cookieFile.exists()) {
        return driver
    }

    val gson = Gson()
    val bufferedReader = BufferedReader(FileReader(cookiePath))
    val savedJsonArrayCookies = gson.fromJson(bufferedReader, JsonArray::class.java)

    savedJsonArrayCookies.forEach {
        val savedJsonObjectCookie = it.asJsonObject
        val longExpiry = savedJsonObjectCookie.get("expiry")

        val name = savedJsonObjectCookie.get("name").asString
        val value = savedJsonObjectCookie.get("value").asString
        val domain = savedJsonObjectCookie.get("domain").asString
        val path = savedJsonObjectCookie.get("path").asString
        val expiry = if (longExpiry.isJsonNull) null else Date(longExpiry.asLong)
        val secure = savedJsonObjectCookie.get("secure").asBoolean
        val httpOnly = savedJsonObjectCookie.get("httpOnly").asBoolean

        val cookie = Cookie(name, value, domain, path, expiry, secure, httpOnly)

        driver.manage().addCookie(cookie)
    }

    return driver
}

private fun writeCookies(cookie: String) {
    val writer = PrintWriter("tokopedia-cookie.json", "UTF-8")
    writer.println(cookie)
    writer.close()
}

private fun loginWithUsernameAndPassword(driver: ChromeDriver, username: String, password: String): ChromeDriver {
    driver.navigate().to("https://www.tokopedia.com/login")
    driver.findElement(By.id("email")).sendKeys(username)
    driver.findElement(By.id("password")).sendKeys(password)
    driver.findElement(By.id("login-submit")).click()
    val cookies = driver.manage().cookies.toTypedArray()

    val objectMapper = ObjectMapper()
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
    val jsonArrayCookies = objectMapper.writeValueAsString(cookies)

    writeCookies(jsonArrayCookies)

    return driver
}

fun login(driver: ChromeDriver, username: String, password: String): ChromeDriver {
    setCookies(driver)

    driver.navigate().to("https://www.tokopedia.com")
    val isLoginButtonExists = driver.findElements(By.ById("login-ddl-link")).size > 0

    if (!isLoginButtonExists) {
        return driver
    }

    loginWithUsernameAndPassword(driver, username, password)
    return driver
}