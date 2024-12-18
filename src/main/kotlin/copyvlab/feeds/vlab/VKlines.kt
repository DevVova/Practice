package copyvlab.feeds.vlab

/**
 * Данный дата класс используется вместо других дата классов котировок в виде
 * свечей от других брокеров. В нём нет ничего лишнего, а только самые
 * необходимые свойства.
 */
data class VKlines(
    val openTime: Long,//Время открытия свечи.
    val open: String,//Цена открытия свечи.
    val high: String,//Максимальная цена свечи.
    val low: String,//Минимальная цена свечи.
    val close: String,//Цена закрытия свечи.
    val volume: String,/*
                         Количество базового актива, которое было куплено и продано.
                         Например, для пары SOL/USDT объем будет представлен в SOL (базовый актив).
                         */
    val closeTime: Long//Время закрытия свечи.
)