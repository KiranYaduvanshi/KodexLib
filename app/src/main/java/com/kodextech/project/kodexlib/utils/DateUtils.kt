package com.kodextech.project.kodexlib.utils


import org.ocpsoft.prettytime.PrettyTime
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


fun String.getDateWithUtcToLocalConversion(
    inputFormat: String = "yyyy-MM-dd'T'HH:mm:ss",
    outputFormat: String = "dd MMM yyyy"
): String {

    try {
        val utcFormat = SimpleDateFormat(inputFormat, Locale.getDefault())
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date: Date? = utcFormat.parse(this.split(".").firstOrNull() ?: "")
        val localTimeFormat = SimpleDateFormat(outputFormat, Locale.getDefault())
        localTimeFormat.timeZone = TimeZone.getDefault()
        localTimeFormat.parse(localTimeFormat.format(date ?: Date()))
        return localTimeFormat.format(date ?: Date())
    } catch (ex: ParseException) {
        try {
            val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date: Date? = utcFormat.parse(this.split(".").firstOrNull() ?: "")
            val localTimeFormat = SimpleDateFormat(outputFormat, Locale.getDefault())
            localTimeFormat.timeZone = TimeZone.getDefault()
            localTimeFormat.parse(localTimeFormat.format(date ?: Date()))
            return localTimeFormat.format(date ?: Date())
        } catch (ex: ParseException) {
            ex.printStackTrace()
        }
    }
    return ""

}

fun String.onUtcToLocal(s: String): String {
    try {//"yyyy-MM-dd'T'HH:mm:ss.SSS"
        val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
//        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date: Date? = utcFormat.parse(s.split(".").firstOrNull() ?: "")
        val localTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        localTimeFormat.timeZone = TimeZone.getDefault()
        localTimeFormat.parse(localTimeFormat.format(date ?: Date()))
        //            prettyTime.
        return localTimeFormat.format(date ?: Date())
    } catch (e: ParseException) {
//        e.printStackTrace()
        try {
            val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date: Date? = utcFormat.parse(s)
            val localTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            localTimeFormat.timeZone = TimeZone.getDefault()
            localTimeFormat.parse(localTimeFormat.format(date ?: Date()))
            //            prettyTime.
            return localTimeFormat.format(date ?: Date())
        } catch (ex: ParseException) {
//            ex.printStackTrace()
        }
        //
    }

    return ""
}

fun Date.dateToString(
    format: String = "yyyy-MM-dd HH:mm:ss",
    isConvertToUtc: Boolean = true
): String {
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    dateFormat.timeZone = if (isConvertToUtc) TimeZone.getTimeZone("UTC") else TimeZone.getDefault()
    return dateFormat.format(this)
}

fun Date.getDateCustom(format: String = "yyyy-MM-dd HH:mm:ss"): String {
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    return dateFormat.format(this)

}


private fun String.stringToDate(
    dateString: String,
    inputFormat: String = "yyyy-MM-dd HH:mm:ss"
): Date? {
    val dateFormat = SimpleDateFormat(inputFormat, Locale.getDefault())
    try {
        return dateFormat.parse(dateString)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return Date()
}

fun String.getDesiredOutputDateString(
    inputFormat: String = "yyyy-MM-dd",
    outputFormat: String = "dd MMM"
): String {
    val dateFormatInput = SimpleDateFormat(inputFormat, Locale.getDefault())
    val dateFormatOutput = SimpleDateFormat(outputFormat, Locale.getDefault())
    var date: Date? = Date()
    try {
        date = dateFormatInput.parse(this)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return dateFormatOutput.format(date ?: Date())
}

fun String.timeAgoFormatDisplay(): String {
    val p = PrettyTime(Locale.getDefault())
    val localDate = onUtcToLocal(this)
    val dtObj = stringToDate(localDate)
    return p.format(dtObj)

}

//2021-06-09T10:34:00.000000Z Converotr
fun String.zDateConvertor(
    inputFormat: String = "yyyy-MM-dd'T'HH:mm:ss",
    outputFormat: String = "dd MMM yyyy",
    date: String = "2021-06-09T10:34:00.000000Z"
): String {

    val a = SimpleDateFormat(inputFormat)
    val b = SimpleDateFormat(outputFormat)
    val parsedDate: Date = a.parse(date)
    val formattedDate: String = b.format(parsedDate)

    return formattedDate
}


fun calculateHour(
    date1: String, date2: String
): String {
    val sdf = SimpleDateFormat(
        "dd-MM-yyyy HH:mm:ss"
    )


    val d1: Date = sdf.parse(date1)
    val d2: Date = sdf.parse(date2)
    val difference_In_Time = d2.time - d1.time
    val difference_In_Seconds = ((difference_In_Time
            / 1000)
            % 60)

    val difference_In_Minutes = ((difference_In_Time
            / (1000 * 60))
            % 60)

    val difference_In_Hours = ((difference_In_Time
            / (1000 * 60 * 60))
            % 24)


    val difference_In_Years = (difference_In_Time
            / (1000L * 60 * 60 * 24 * 365))

    val difference_In_Days = ((difference_In_Time
            / (1000 * 60 * 60 * 24))
            % 365)

    return difference_In_Hours.toString()

}

fun calculateDays(
    date1: String, date2: String
): String {
    val sdf = SimpleDateFormat(
        "dd-MM-yyyy"
    )


    val d1: Date = sdf.parse(date1)
    val d2: Date = sdf.parse(date2)
    val difference_In_Time = d2.time - d1.time
    val difference_In_Seconds = ((difference_In_Time
            / 1000)
            % 60)

    val difference_In_Minutes = ((difference_In_Time
            / (1000 * 60))
            % 60)

    val difference_In_Hours = ((difference_In_Time
            / (1000 * 60 * 60))
            % 24)


    val difference_In_Years = (difference_In_Time
            / (1000L * 60 * 60 * 24 * 365))

    val difference_In_Days = ((difference_In_Time
            / (1000 * 60 * 60 * 24))
            % 365)

    return difference_In_Days.toString()

}

//fun Date.isDateToday():Boolean{
//    val currentDate = Date()
//    return this.after() == currentDate
//}