package li.brianv.bidtactoe.gameservice.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Game code does not exists")
class BadGameCodeException : RuntimeException()