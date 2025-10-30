package com.openclassrooms.realestatemanager.utils

import assertk.assertThat
import assertk.assertions.isCloseTo
import assertk.assertions.isEqualTo
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.text.NumberFormat
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Currency
import java.util.Locale

@RunWith(JUnit4::class)
class UtilsTest {

    @Test
    fun convert_Dollar_to_Euro_succeed() {
        //GIVEN
        val dollarToConvert = 10f
        val expectedConversion = 9.5f
        val delta = 0.000001f
        //WHEN
        val conversion = Utils.convertDollarToEuro(dollarToConvert)
        //THEN
        assertThat(conversion).isCloseTo(expectedConversion, delta)
    }

    @Test
    fun convert_Euro_to_Dollar_succeed() {
       //GIVEN
        val euroToConvert = 10f
        val expectedConversion =  10.5f
        val delta = 0.000001f
        //WHEN
        val conversion = Utils.convertEuroToDollar(euroToConvert)
        //THEN
        assertThat(conversion).isCloseTo(expectedConversion, delta)
    }

    @Test
    fun price_is_formatted_correctly_in_EURO() {
        //GIVEN
        val price = 123000f
        val currencyCode = CurrencyCode.EURO
        val numberFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE)
        numberFormat.currency = Currency.getInstance(currencyCode.code)
        numberFormat.maximumFractionDigits = 2
        val expectedPriceFormatted ="123 000,00 €"
        //WHEN
        val actualPrice = Utils.priceFormatter(price, CurrencyCode.EURO)
        //THEN
        assertThat(actualPrice).isEqualTo(expectedPriceFormatted)
    }

    @Test
    fun price_is_formatted_correctly_in_DOLLAR() {
        //GIVEN
        val price = 123000f
        val currencyCode = CurrencyCode.DOLLAR
        val numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
        numberFormat.currency = Currency.getInstance(currencyCode.code)
        numberFormat.maximumFractionDigits = 2
        val expectedPriceFormatted = "123 000,00 $"+"US"
        //WHEN
        val actualPrice = Utils.priceFormatter(price, CurrencyCode.DOLLAR)
        //THEN
        assertThat(actualPrice).isEqualTo(expectedPriceFormatted)
    }

    @Test
    fun convert_today_date_in_correct_format() {
        //GIVEN
        val dateInstant = Clock.systemUTC().instant()
        val dateExpected = Utils.instantToDate(dateInstant, ZoneId.of("Europe/Paris"))
        //WHEN
        val actualDate = Utils.todayDate
        //THEN
        assertThat(actualDate).isEqualTo(dateExpected)
    }

    @Test
    fun convert_Instant_to_Date_formatted_as_String() {
        //GIVEN
        val instant = LocalDateTime.of(2025, 10, 2, 14, 0).toInstant(ZoneOffset.UTC)
        val expectedDateFormatted = "02/10/2025"
        //WHEN
        val actualDateFormatted = Utils.instantToDate(instant, ZoneId.of("Europe/Paris"))
        //THEN
        assertThat(actualDateFormatted).isEqualTo(expectedDateFormatted)
    }


}