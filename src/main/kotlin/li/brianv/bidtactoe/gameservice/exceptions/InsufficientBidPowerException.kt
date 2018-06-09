package li.brianv.bidtactoe.gameservice.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Insufficient bidding power for bid")
class InsufficientBidPowerException : RuntimeException()
