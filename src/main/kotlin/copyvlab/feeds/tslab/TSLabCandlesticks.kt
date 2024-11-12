package copyvlab.feeds.tslab

data class TSLabCandlesticks(
    val ticker: String,
    val period: String,
    val date: String,
    val time: String,
    val open: String,
    val high: String,
    val low: String,
    val close: String,
    val volume: String
)