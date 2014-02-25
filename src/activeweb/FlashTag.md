Title: How to use Flash - single use message

-   [Introduction](#Introduction)
-   [Working with flash message](#Working_with_flash_message)
-   [Rendering dynamic snippets of HTML](#Rendering_dynamic_snippets_of_HTML)

Introduction
============

Flash is a concept which does not exist in standard Java API, but is very useful in web applications. Flash is a snippet of HTML whose life cycle is limited by the next HTTP request. In other words, a flash is created during a request, then it can be used in a subsequent request (of the same session), after which it dies.

Flash messages are useful in cases when a POST/Redirect to GET pattern is used.

Working with flash message
==========================

Flash messages are created in controllers (or filters) like so:

~~~~ {.prettyprint}
1. public class BooksController extends AppController {
2.     @POST
3.     public void create(){
4.         Book book = new Book();
5.         book.fromMap(params1st());
6.         if(book.save()){ 
7.             flash("message", "New book was added: " + book.get("title"));
8.             redirect(BooksController.class);
9.         }else{
10.           //handle errors
11.        }
12.    }
13. }
~~~~

In a code of controller above, a new book is submitted in a POST request. Line 6 is making attempt to save the book information to DB. If this succeeds, the flash message is sent to view on line 7, and request is redirected to BooksController index method. The file `/views/books/index.ftl` would have a flash message displayed with "flash" tag:

~~~~ {.prettyprint}
<@flash name="message"/>
~~~~

The Flash tag automatically detects if a flash message exists and displays it. If a message is missing, nothing is rendered.

Since the user was redirected to the BooksController with an HTTP GET method, the page can simply be reloaded if a user presses "Reload" button on browser. However, if a user actually reloads the page, the flash message will disappear, because it cannot survive across more than one request.

In general, POST/Redirect to GET is a good programming pattern to use in case you need "destructive" operations. Leaving a user on a POSTed page is a bad idea, because the same request can be re-submitted if user presses Reload.

Rendering dynamic snippets of HTML
==================================

In the example above, a flash message is a simple string. However, it is possible to have it render entire chunks of HTML based on presence or absence of a flash object:

~~~~ {.prettyprint}
<#if (flasher.message) ??>
   <span style="background-color:red"> Information was successfully saved</span>
</#if>
~~~~

The HTML code inside the IF condition has no restrictions to use any dynamic values from session of those passed into view by controller.
