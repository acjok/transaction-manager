package exceptions

import constants.ErrorStatusCodes

class UserNotFoundException (
    override var message: String
) : CustomThrowableException(404, ErrorStatusCodes.USER_NOT_FOUND, message)

class UserAlreadyExistsException (
    override var message: String
) : CustomThrowableException(401, ErrorStatusCodes.USER_ALREADY_EXISTS, message)

class WalletNotFoundException (
    override var message: String
) : CustomThrowableException(404, ErrorStatusCodes.WALLET_NOT_FOUND, message)

class TransactionNotFoundException (
    override var message: String
) : CustomThrowableException(404, ErrorStatusCodes.TRANSACTION_NOT_FOUND, message)

class ServerException (
    override var message: String
) : CustomThrowableException(500, ErrorStatusCodes.SERVER_ERROR, message)

class NotEnoughBalanceException (
    override var message: String
) : CustomThrowableException(400, ErrorStatusCodes.NOT_ENOUGH_BALANCE, message)

class TransactionNotAllowedException (
    override var message: String
) : CustomThrowableException(400, ErrorStatusCodes.TRANSACTION_NOT_ALLOWED, message)