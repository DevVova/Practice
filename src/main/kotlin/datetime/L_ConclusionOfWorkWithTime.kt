package datetime

import java.time.Instant
import java.time.ZoneId

/**
 * Ниже я разбираю то как можно работать с секундами пришедшими от брокера,
 * то есть время пришло в формате Unix Time и мы его преобразовываем в
 * другой тип удобный для работы.
 */
fun main() {
    val inputSeconds: Long = 1723700640//Допустим это секунды пришедшие из запроса.

    //Вот так можно перевести в тип Instant.
    var instant = Instant.ofEpochSecond(inputSeconds)

    //Далее переводим Instant! в ZonedDateTime! для дальнейшей работы.
    val zonedDateTime = instant.atZone(ZoneId.of("UTC"))
    println(zonedDateTime.hour)//К примеру можно получить сколько часов.

    //А здесь после работы с данными переводим ZonedDateTime! в Instant!
    instant = zonedDateTime.toInstant()
    println(instant)

    //А это окончательный перевод из Instant! в секунды. Для binance делить на 1000 не нужно.
    val newInputSeconds: Long = instant.toEpochMilli() / 1000
    println(newInputSeconds)
}