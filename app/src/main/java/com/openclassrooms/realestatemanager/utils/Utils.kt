package com.openclassrooms.realestatemanager.utils

import android.annotation.SuppressLint
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.util.Currency
import java.util.Date
import java.util.Locale

/**
 * Created by Philippe on 21/02/2018.
 */
object Utils {
    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     *
     * @param dollars
     * @return
     */
    @JvmStatic
    fun convertDollarToEuro(dollars: Float): Float {
        return Math.round(dollars * 0.95).toFloat()
    }

    @JvmStatic
    fun convertEuroToDollar(euros: Float): Float {
        return Math.round(euros * 1.05).toFloat()

    }

    //TODO : préférence user pour devise
    fun priceFormatter(
        price: Float,
        currencyCode: CurrencyCode,
        locale: Locale = Locale.getDefault()
    ): String {
        val formatter = NumberFormat.getCurrencyInstance(locale)
        formatter.currency = Currency.getInstance(currencyCode.code)
        formatter.maximumFractionDigits = 2
        return formatter.format(price)

    }

    //TODO : tchecker format date
    val todayDate: String
        /**
         * Conversion de la date d'aujourd'hui en un format plus approprié
         * Format : "dd/MM/yyyy"
         * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
         *
         * @return
         */
        @SuppressLint("SimpleDateFormat")
        get() {
            val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy")
            return dateFormat.format(Date())
        }

    fun instantToDate(instant: Instant): String {
        val date = instantToDate(instant)
        return date.format("dd/MM/yyyy")
    }
}






