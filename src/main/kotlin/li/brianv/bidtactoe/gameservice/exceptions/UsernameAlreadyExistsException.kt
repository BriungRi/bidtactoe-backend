package li.brianv.bidtactoe.gameservice.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Username already in use")
class UsernameAlreadyExistsException : RuntimeException()