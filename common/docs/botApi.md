# Bot Api Docs

### Exceptions
___
Located in: `net.buildtheearth.terrabungee.common.exceptions`
> Note all exceptions are RuntimeExceptions!

`InvalidParameterException`:
This exception is thrown when a parameter has an invalid value.

`NotFoundException`:
This exception is thrown when the target (user or role) cannot be found

`NotImplementedException`:
This exception is thrown when the target feature is not implemented

`ServerException`:
This exception is thrown when something goes catastrophically wrong, equivelent to HTTP 500

>All error messages will have a reason attached in the message thrown.

### Structures
___
Located in: `net.buildtheearth.terrabungee.common.discord.structures`
> Note that this is what GSON serializes all responses to
> > See each class for a list of methods.

`Builder`:
Represents a user in terms of being builder or not, contains user id and a boolean.

`User`:
Represents a user in terms of the roles they have, contains a list of `Role`

`Role`:
Represents a role, containing the ID and name, an important note is to only compare using `(instanceof Role).equals()`

### Api methods
___
> Note that this requires a token and ip whitelist setup on the Main-bot

Located in: `net.buildtheearth.terrabungee.common.discord.BotApi`


`Constructor`:
```java
//Url is api endpoint
//Token is the bot team provided token
new BotApi(String url, String token)
```

`getUser`:
```java
//Id is the discord id of the user
User user = botApi.getUser(String id)
```

`getBuilder`:
```java
//Id is the discord id of the user
Builder bld = botApi.getBuilder(String id)
```

    
