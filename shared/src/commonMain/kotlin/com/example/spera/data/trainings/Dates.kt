package com.example.spera.data.trainings

/**
 * Petit calendrier civil en pur Kotlin (algorithmes epoch-days de Howard
 * Hinnant), pour construire la grille mensuelle du calendrier (US12) sans
 * dépendance date-time. Les dates sont au format ISO `yyyy-MM-dd`.
 */
internal data class CivilDate(val year: Int, val month: Int, val day: Int)

internal fun parseIso(date: String): CivilDate? {
    val parts = date.split("-")
    if (parts.size != 3) return null
    val (y, m, d) = parts.map { it.toIntOrNull() ?: return null }
    return CivilDate(y, m, d)
}

/** Jours écoulés depuis le 1970-01-01. */
internal fun CivilDate.toEpochDays(): Long {
    val y = if (month <= 2) year - 1 else year
    val era = (if (y >= 0) y else y - 399) / 400
    val yoe = y - era * 400
    val doy = (153 * (if (month > 2) month - 3 else month + 9) + 2) / 5 + day - 1
    val doe = yoe.toLong() * 365 + yoe / 4 - yoe / 100 + doy
    return era.toLong() * 146097 + doe - 719468
}

/** Jour de semaine ISO : 1 = lundi … 7 = dimanche (1970-01-01 était un jeudi). */
internal fun dayOfWeekIso(epochDays: Long): Int =
    (((epochDays + 3) % 7 + 7) % 7 + 1).toInt()

/** Nombre de jours du mois (gère les années bissextiles via epoch-days). */
internal fun daysInMonth(year: Int, month: Int): Int {
    val next = if (month == 12) CivilDate(year + 1, 1, 1) else CivilDate(year, month + 1, 1)
    return (next.toEpochDays() - CivilDate(year, month, 1).toEpochDays()).toInt()
}

private val DAY_LABELS = listOf("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim")
private val MONTH_LABELS = listOf(
    "janvier", "février", "mars", "avril", "mai", "juin",
    "juillet", "août", "septembre", "octobre", "novembre", "décembre",
)

/** « Lun 6 », « Mer 8 »… pour une date ISO ; la date brute si illisible. */
fun dayLabel(isoDate: String): String {
    val civil = parseIso(isoDate) ?: return isoDate
    return "${DAY_LABELS[dayOfWeekIso(civil.toEpochDays()) - 1]} ${civil.day}"
}

/** « Juillet 2026 » — titre du mois affiché dans le calendrier. */
internal fun monthTitle(year: Int, month: Int): String =
    "${MONTH_LABELS[month - 1].replaceFirstChar { it.uppercaseChar() }} $year"

/** « 17 juillet 2026 » pour une date ISO ; la date brute si illisible. */
fun dateLabel(isoDate: String): String {
    val civil = parseIso(isoDate) ?: return isoDate
    return "${civil.day} ${MONTH_LABELS[civil.month - 1]} ${civil.year}"
}
