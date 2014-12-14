<ol class=breadcrumb>
   <li><a href=/>Home</a></li>
   <li><a href=/activeweb>ActiveWeb</a></li>
   <li class=active>Flash tag</li>
</ol>
<div class=page-header>
   <h1>Flash tag <small></small></h1>
</div>


Flash is a concept which does not exist in standard Java API, but is very useful in web applications. Flash is a snippet
of HTML whose life cycle is limited by the next HTTP request. In other words, a flash is created during a request, then
it can be used in a subsequent request (of the same session), after which it dies.

Flash messages are useful in cases when a POST/Redirect to GET pattern is used.

## Working with flash message

Flash messages are created in controllers (or filters) like so:

~~~~ {.java  }
@POST
public void create(){
    Book book = new Book();
    book.fromMap(params1st());
    if(!book.save()){
        flash("message", "Something went wrong, please  fill out all fields");
        flash("errors", book.errors());
        flash("params", params1st());
        redirect(BooksController.class, "new_form");
    }else{
        flash("message", "New book was added: " + book.get("title"));
        redirect(BooksController.class);
    }
}
~~~~

In a code of controller above, a new book is submitted in a POST request.

* Line 5 is making attempt to save the book information to DB.

* Lines 6, 7, 8  - in case of failure, three instances of flash are created: "message", "errors" and "params", where "message"
is a generic message displayed at the top of page, "params" is a map with submitted values
(it is used  to re-populate input fields) and the "error" is a map with error messages

* Line 11 -  In case of success request is redirected to BooksController index method and the flash message is sent to view.
The file `/views/books/index.ftl` would have a flash message displayed with "flash" tag:

~~~~ {.html}
<@flash name="message"/>
~~~~

> For a complete example of using FlashTag, please refer to [BooksController#save()](https://github.com/javalite/activeweb-simple/blob/master/src/main/java/app/controllers/BooksController.java#L45)
and corresponding views. Also, please see [ActiveJDBC Validation](validations)


The Flash tag automatically detects if a flash message exists and displays it. If a message is missing, nothing is rendered.

Since the user was redirected to the BooksController with an HTTP GET method, the page can simply be reloaded if a user
presses "Reload" button on browser. However, if a user actually reloads the page, the flash message will disappear,
because it cannot survive across more than one request.

In general, POST/Redirect to GET is a good programming pattern to use in case you need "destructive" operations.
Leaving a user on a POSTed page is a bad idea, because the same request can be re-submitted if user presses Reload.



## Rendering FlashTag with body

If you need to display a more complex HTML than a simple string, you can do so by placing a flash tag with body on the page: 

~~~~ {.html  }
<@flash>
 <div class="warning">${message}</div>
</@flash>
~~~~

and calling a single argument method inside the controller: 

~~~~ {.java  }
@POST
public void create(){
//.. code before
    view("message", "Your changes have been saved successfully");
    flash("warning");
//.. code after
}
~~~~


The body inside the flash tag lives by the same rules as any other in the template. You can use variables, FreeMarker syntax, lists or even 
partials: 

~~~~ {.html}
<@flash>
<@render partial="message"/>
</@flash>
~~~~

It allows to organize code for error and warning messages into reusable componets. 



## Rendering dynamic snippets of HTML (old method)

In the example above, a flash message is a simple string. However, it is possible to have it render entire chunks of
HTML based on presence or absence of a flash object:

~~~~ {.html}
<#if (flasher.message) ??>
   <span style="background-color:red">
   <@flash name="message"/>
   </span>
</#if>
~~~~

The HTML code inside the IF condition has no restrictions to use any dynamic values from session of those passed into view by controller.

