# IntroToScalaTags

We start this tutorial with a project structure similar to the one with which we ended in the previous tutorial. Something like:

```
.
├── README.md
├── TUTORIAL.md
├── build.sbt
├── index.html
├── project
│   ├── build.properties
│   └── plugins.sbt
└── src
    └── main
        └── scala
            └── scalatags
                └── Index.scala
```

Note that the package `hello` has changed to `scalatags` and the file `World.scala` has changed to `Index.scala`. Other than those changes, and the name of the project, the repo is essentially identical.

If you clone this repo and `git checkout` [this exact commit (`fd7a3c1`)](https://github.com/awwsmm/IntroToScalaTags/commit/fd7a3c1), you will have a bare-minimum project structure with which we'll begin this tutorial.

---

In the last tutorial, we wrote a minimal Scala.js application which wrote text to the standard output, `STDOUT`. In Scala (and Java), `STDOUT` is the terminal, but with JavaScript, `STDOUT` is the browser console. So our `println()` and `System.err.println()` Scala commands were mapped to `console.log()` and `console.error()` JavaScript commands, which resulted in text being displayed in the browser console:

![](https://github.com/awwsmm/HelloScalaJS/blob/master/resources/Screenshot%202020-11-09%20at%2011.20.26.png?raw=true)

In this tutorial, we're going to build a visible web page, so the user doesn't need to open the browser console to interact with the application.

> We're going to build this on a "trial-and-error" basis. I'm going to purposefully make some common mistakes, so I can explain how to fix them, in case you hit them in your own Scala.js projects in the future.

So, the first thing we'll do is add the dependency for `com.lihaoyi.scalatags` to `build.sbt`

```scala
// ScalaTags
libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.9.2"
```

I'm using `0.9.2` as this is the most up-to-date version currently available on [Maven](https://mvnrepository.com/artifact/com.lihaoyi/scalatags) as of this writing.

![](https://github.com/awwsmm/IntroToScalaTags/blob/master/resources/tutorial01.png)

Then, click on the "Load sbt changes" button in the tiny modal that pops up (the little red hexagon with blue arrows) to download and initialise the library.

You should now have some code completion in IntelliJ for Scala code. Let's add the following import to the top of `Index.scala`

```scala
import scalatags.JsDom.all._
```

It will be greyed-out because we're not using it yet, but don't worry about that for now. Just run `fastOptJS` in th `sbt` shell...

![](https://github.com/awwsmm/IntroToScalaTags/blob/master/resources/tutorial02.png)

...it looks like it's working! Great, let's try to add a **paragraph** tag. In HTML markup, this looks like `<p>...</p>`. Using ScalaTags, this is created with the `p()` method from package `scalatags.JsDom.all`. Adding this paragraph and running `fastOptJS` again works:

![](https://github.com/awwsmm/IntroToScalaTags/blob/master/resources/tutorial03.png)

So let's try to view this in the browser: open `index.html` and click the icon for your preferred browser in the top-right corner of the editor window

![](https://github.com/awwsmm/IntroToScalaTags/blob/master/resources/tutorial04.png)

There's still nothing in the browser, and the console is throwing a `404 NOT FOUND` error. What happened?

Well, we ran `fastOptJS` to generate the `-fastopt.js` file, but our `index.html` is looking for the `-opt.js` (`fullOptJS`) file. Make sure the version you're generating matches the version that appears in your HTML file. During development, we'll stick to `fastOptJS`, so let's change `index.html` and try again. (There's no need to re-run `fastOptJS` now, because we've already run it with the most recent version of our Scala code.)

![](https://github.com/awwsmm/IntroToScalaTags/blob/master/resources/tutorial05.png)

We've cleared the `404` error and we again have our text in the console, but the element inspector shows nothing in the `<body>` (no `<p>` tags)! The reason for this is that `scalatags.JsDom.all.p()` just returns a paragraph object -- writing it to the body of the webpage is a side-effect, which must be done separately.

An HTML document is a tree of tags (or "elements") which typically have an opening tag (like `<p>`) and a closing tag (like `</p>`). The root element of the visible area of an HTML document is the `<body></body>` tag. In Scala.js, `body` is a field of the `abstract class HTMLDocument`, which we can access via `scala.scalajs.js.dom.document`.

> [The DOM, or _Document Object Model_](https://en.wikipedia.org/wiki/Document_Object_Model), is the tree-based model of an HTML document. Every branch of the DOM tree ends in an Element (a "Node").

In Scala.js, `body` is an `HTMLElement`, and `HTMLElement`s (as `Node`s), can have arbitrary child elements added to them, with the `appendChild` method. So, to add our paragraph to the page, we need to append it to the HTML `body`:

![](https://github.com/awwsmm/IntroToScalaTags/blob/master/resources/tutorial06.png)

When we try to add this though (as seen above), we get a type error -- `appendChild()` takes a `Node` and our `paragraph` is a `JsDom.TypedTag[html.Paragraph]`.

In Scala.js's `html.scala`, we can see that `html.Paragraph` is a type alias for `raw.HTMLParagraphElement`, which `extends HTMLElement` (the same type as `body`), which makes it a `Node`. But it's a `Node` wrapped in a `TypedTag` container, so we need to extract it from that container. We do that by `render`ing the element:

![](https://github.com/awwsmm/IntroToScalaTags/blob/master/resources/tutorial07.png)

Now when we run `fastOptJS` and view our page in a web browser, we see...

![](https://github.com/awwsmm/IntroToScalaTags/blob/master/resources/tutorial08.png)

...another error. This one says

> `Cannot read property 'appendChild' of null`

The only `appendChild` that appears in our code is when we tried to add our `paragraph` to the `document.body`. So this error is saying that `body` is `null` at the point we try to call `body.appendChild`.

What is happening here is that we're trying to access the DOM before the browser has had a chance to load it. We need to wait until the DOM is rendered before we can add anything to it, so we need to add an _event listener_, which will listen for a particular event (in this case, the `"DOMContentLoaded"` event), and trigger some code to run when it receives that event.

![](https://github.com/awwsmm/IntroToScalaTags/blob/master/resources/tutorial09.png)

Again, we run `fastOptJS` and view our page in the browser, and...

![](https://github.com/awwsmm/IntroToScalaTags/blob/master/resources/tutorial10.png)

...another error! Again, we're getting an error that seems to say that `document.body` is `null`. 

_This time_, even though we waited for the `DOMContentLoaded` event, we still tried to access `document.body` outside of that event listener, which means that the value we saved to `val body` was `null`. If we just put this `val` definition inside the event listener, and re-run `fastOptJS`...

![](https://github.com/awwsmm/IntroToScalaTags/blob/master/resources/tutorial11.png)

...and we finally have our paragraph!

![](https://github.com/awwsmm/IntroToScalaTags/blob/master/resources/tutorial12.png)
