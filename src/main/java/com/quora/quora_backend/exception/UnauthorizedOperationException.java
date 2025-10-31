package com.quora.quora_backend.exception;//this extension is for when a user tries to perform an operation they are not allowed to do.

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedOperationException extends RuntimeException{
    public  UnauthorizedOperationException (String message){
        super(message);
    }

}



/* @ResponseStatus → This is a Spring annotation (like a tag) that tells Spring what HTTP status code should be sent back when this exception happens.

HttpStatus.FORBIDDEN → That’s just the code for 403, which means “You’re not allowed to do this.”
Why we did this:

Because it makes error handling clean and automatic.

Instead of writing “if this happens, send 403” again and again,

you just define your own exception once, and Spring does the rest.

So, next time you throw:

throw new AccessDeniedException("You can’t delete this resource");


Spring instantly replies to the user’s API request with:

HTTP 403 Forbidden
{
  "message": "You can’t delete this resource"
}
  We extend RuntimeException so our custom exception behaves like a normal runtime error, is easy to throw anywhere, and plays nicely
 with Spring’s built-in error-handling system*/