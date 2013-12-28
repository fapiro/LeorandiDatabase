package leorandiDatabase;

public class LeOraException extends Exception{}

class WrongParameterException extends LeOraException{}

class NotFoundException extends LeOraException{}
class NotFoundDatabaseException extends NotFoundException{}
class NotFoundTableException extends NotFoundException{}

class SessionCreationFailedException extends LeOraException{}
class NotFoundUserException extends SessionCreationFailedException{}
class WrongPasswordException extends SessionCreationFailedException{}

class ColumnCountException extends LeOraException{}
class PartitionKeyIsNullException extends LeOraException{}

