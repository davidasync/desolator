package com.desolator

import com.desolator.helpers.login
import com.desolator.lib.dinkIt
import com.desolator.utils.initBrowser

object Main {
    @JvmStatic
    // dinkit [email] [password] [product detail url]
    fun main(args: Array<String>) {
        val (email, password, productDetailUrl) = args
        val driver = initBrowser(true)

        try {
            login(driver, email, password)
            dinkIt(driver, productDetailUrl)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            driver.close()
        }
    }
}