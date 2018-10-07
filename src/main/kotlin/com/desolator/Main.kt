package com.desolator
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.util.*

data class Desolator (
    val responseText: String,
    val cookies: Map<String, String>,
    val nextDinkIt: String?
)

fun dinkIt (email: String, password: String, productId: String, cookies: Map<String, String>?) {
    val tokopediaLoginUrl = Jsoup.connect("https://www.tokopedia.com/login").followRedirects(true).execute()
    val milliseconds = Calendar.getInstance().timeInMillis
    var loginCookies: Map<String, String>

    val headers = hashMapOf(
            "content-type" to "application/x-www-form-urlencoded",
            "origin" to "https://accounts.tokopedia.com",
            "referer" to tokopediaLoginUrl.url().toString(),
            "user-agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36 OPR/54.0.2952.54"
    )

    if (cookies != null) {
        loginCookies = cookies
    } else {
        loginCookies = Jsoup.connect(tokopediaLoginUrl.url().toString())
                .headers(headers)
                .cookies(tokopediaLoginUrl.cookies())
                .data("email", email, "password", password, "remember_me", "on")
                .method(Connection.Method.POST)
                .execute()
                .cookies()
    }

    val dinkItUrl = "https://www.tokopedia.com/ajax/product-e4.pl?action=event_dink_it&p_id=${productId}&v=${milliseconds}"
    val dinkItResult = Jsoup
            .connect(dinkItUrl)
            .cookies(loginCookies)
            .get()

    println(dinkItResult)
}

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size < 3) {
            println("Should email password and product id to execute!")
            return
        }

        val (email, password, productId) = args
        dinkIt(email, password, productId, null)
    }
}