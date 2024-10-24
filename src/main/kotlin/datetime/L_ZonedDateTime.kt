package datetime

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

fun main() {
    /**
     * Это пример того как можно получить дату и время системы в формате ISO 8601.
     */
    val currentTimePavlodarWithLocalDateTime = LocalDateTime.now()
    println(currentTimePavlodarWithLocalDateTime)
    println()

    /**
     * Также можно получить дату и время системы в формате ISO 8601, используя класс
     * ZonedDateTime.
     *
     * ZonedDateTime — это класс, который хранит дату, время, временную зону и смещение
     * от UTC (например, 2024-10-23T14:30:00+06:00[Asia/Almaty]).
     *
     * ZonedDateTime — это класс, который позволяет работать с датой и временем в
     * конкретном часовом поясе. Он сохраняет точные данные до наносекунд и учитывает
     * правила перехода на летнее и зимнее время. Например, когда время переводится
     * вперед или назад, класс умеет корректно интерпретировать неоднозначные моменты
     * времени, когда может быть несколько смещений от UTC.
     *
     * Преобразование локальной даты-времени в универсальный момент времени требует
     * учета часовых поясов и их смещений. В случаях "разрывов" (когда часы переводятся вперед)
     * и "наложений" (когда часы переводятся назад) ZonedDateTime автоматически выбирает
     * подходящее смещение, основываясь на правилах часового пояса.
     *
     * now(): Этот метод возвращает текущие дату и время, основываясь на системных часах в
     * часовом поясе по умолчанию (обычно это локальный часовой пояс системы, в которой
     * выполняется программа).
     */
    val currentTimePavlodarWithZonedDateTime = ZonedDateTime.now()
    println(currentTimePavlodarWithZonedDateTime)

    //Пример как узнать текущую зону, например "Europe/Paris". Тип ZoneId!
    println(currentTimePavlodarWithZonedDateTime.zone)

    //Пример как узнать разницу во времени относительно Гринвича. Тип ZoneOffset!
    println(currentTimePavlodarWithZonedDateTime.offset)

    //Пример как узнать сколько часов. Тип Int.
    println(currentTimePavlodarWithZonedDateTime.hour)

    //Пример как узнать сколько минут. Тип Int.
    println(currentTimePavlodarWithZonedDateTime.minute)

    //nano — это свойство объекта ZonedDateTime, которое возвращает количество наносекунд в текущем значении времени. Тип Int.
    //нано - это одна миллиардная секунды.
    println(currentTimePavlodarWithZonedDateTime.nano)

    //Пример как узнать день недели. Тип DayOfWeek!
    println(currentTimePavlodarWithZonedDateTime.dayOfWeek)

    //Пример как узнать какой по счёту день в году. Тип Int.
    println(currentTimePavlodarWithZonedDateTime.dayOfYear)

    //Пример как узнать какой по счёту день в месяце. Тип Int.
    println(currentTimePavlodarWithZonedDateTime.dayOfMonth)

    /*
    Этот метод преобразует объект ZonedDateTime в объект OffsetDateTime.
    OffsetDateTime — это объект, который хранит дату, время и только смещение от UTC,
    но без информации о временной зоне (например, 2024-10-23T14:30:00+06:00).
     */
    println(currentTimePavlodarWithZonedDateTime.toOffsetDateTime())

    //Пример как узнать дату и время минус 2 часа. Тип ZonedDateTime!
    println(currentTimePavlodarWithZonedDateTime.minusHours(2))

    //Пример как создать пару данных. Тип Pair<ZonedDateTime!, String>
    println(currentTimePavlodarWithZonedDateTime to "Time in Pavlodar")
    println(ZonedDateTime.now(ZoneId.of("UTC")) to "Время по Гринвичу")
}