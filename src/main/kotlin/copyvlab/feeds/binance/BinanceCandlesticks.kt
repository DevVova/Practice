package copyvlab.feeds.binance

import kotlinx.serialization.Serializable

/**
 * При обращении через get запрос к api binance для получения котировок в виде
 * свечей, мы получим ответ в виде json файла:
 *
 * [
 *
 *   [
 *
 *     1499040000000,      // Kline время открытия свечи.
 *
 *     "0.01634790",       // Цена открытия свечи.
 *
 *     "0.80000000",       // Максимальная цена свечи.
 *
 *     "0.01575800",       // Минимальная цена свечи.
 *
 *     "0.01577100",       // Цена закрытия свечи.
 *
 *     "148976.11427815",  /*
 *                           Количество базового актива, которое было куплено и продано.
 *                           Например, для пары SOL/USDT объем будет представлен в SOL (базовый актив).
 *                           */
 *
 *     1499644799999,      // Kline время закрытия свечи.
 *
 *     "2434.19055334",    /*
 *                           Весь объём котируемых активов. Что такое котируемый актив?
 *                           Котируемый актив — это актив, в котором измеряется
 *                           стоимость базового актива. В паре SOL/USDT:
 *                           Базовый актив: SOL (криптовалюта, которую вы хотите купить или продать).
 *                           Котируемый актив: USDT (стабильная монета, которая используется для
 *                           оценки стоимости SOL).
 *                           */
 *
 *     308,                // Общее количество сделок, совершенных за период свечи.
 *
 *     "1756.87402397",    // Объём купленного базового актива.
 *
 *     "28.46694368",      // Объём купленного котируемого актива.
 *
 *     "0"                 /*
 *                         Параметр, который обычно игнорируется при анализе данных.
 *                         В контексте Binance API он может использоваться для передачи
 *                         дополнительных данных, которые не являются важными для пользователей.
 *                        */
 *
 *   ]
 *
 * ]
 *
 * Для того чтобы нам было проще этот ответ преобразовать в тип данных с похожими свойствами
 * и создан данный дата класс. Его свойства идут в том же порядке и соответствуют ответу
 * от Binance.
 */
@Serializable
data class BinanceCandlestick(
    val openTime: Long,
    val open: String,
    val high: String,
    val low: String,
    val close: String,
    val volume: String,
    val closeTime: Long,
    val quoteAssetVolume: String,
    val numberOfTrades: Int,
    val takerBuyBaseAssetVolume: String,
    val takerBuyQuoteAssetVolume: String,
    val ignore: String
)