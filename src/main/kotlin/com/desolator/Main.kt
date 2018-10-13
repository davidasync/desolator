package com.desolator

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import org.apache.commons.io.IOUtils
import org.jsoup.Jsoup
import java.util.*

fun dinkIt (productId: String, cookies: Map<String, String>) {
    val tokopediaLoginUrl = Jsoup.connect("https://www.tokopedia.com/login").followRedirects(true).execute()
    val milliseconds = Calendar.getInstance().timeInMillis
    var loginCookies: Map<String, String>

    val headers = hashMapOf(
            "content-type" to "application/x-www-form-urlencoded",
            "origin" to "https://accounts.tokopedia.com",
            "referer" to tokopediaLoginUrl.url().toString(),
            "user-agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36 OPR/54.0.2952.54"
    )

    val dinkItUrl = "https://www.tokopedia.com/ajax/product-e4.pl?action=event_dink_it&p_id=${productId}&v=${milliseconds}"
    val dinkItResult = Jsoup
            .connect(dinkItUrl)
            .cookies(cookies)
            .get()

    println(dinkItResult)
}

fun loadConfig(): Map<String, String> {
    val stream = Main.javaClass.classLoader.getResourceAsStream("config")

    val config = mutableMapOf<String, String>()

    Scanner(stream).use { scanner ->
        while (scanner.hasNextLine()) {
            val keyValue = scanner.nextLine()
            val (key, value) = keyValue.split(
                    "=".toRegex(),
                    2
            )

            config.put(key, value)
        }
    }

    return config
}

fun loadProductIds(): List<String> {
    val stream = Main.javaClass.classLoader.getResourceAsStream("productIds.json")
    val jsonString = IOUtils.toString(stream, "UTF-8")

    return GsonBuilder()
            .create()
            .fromJson(jsonString, JsonArray::class.java)
            .map {
                it.asString
            }
}

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val config = loadConfig()

        while (true) {
            val productIds = loadProductIds()
            val randomProductId = productIds.shuffled().get(0)
            val cookies = mapOf(
                    "_SID_Tokopedia_" to config.get("sid").toString(),
                    "l" to "1"
            )

            dinkIt(
                    randomProductId,
                    cookies
            )

            Thread.sleep(60 * 60 * 1_000)
        }
    }
}
