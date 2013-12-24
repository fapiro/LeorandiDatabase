package leorandiDatabase;

public class LeOraException extends Exception{}

class WrongParameterException extends LeOraException{}
class WrongPasswordException extends WrongParameterException{}

class NotFoundException extends LeOraException{}
class NotFoundDatabaseException extends NotFoundException{}
class NotFoundUserException extends NotFoundException{}
class NotFoundTableException extends NotFoundException{}

class SessionCreationFailedException extends LeOraException{}

class ColumnCountException extends LeOraException{}
class PartitionKeyIsNullException extends LeOraException{}

