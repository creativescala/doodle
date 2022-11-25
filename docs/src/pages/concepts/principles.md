# Principles

A few principles guide the design of Doodle, and differentiate it from other graphics libraries. The section explains these principles.


## Pictures are Created by Composition

In Doodle a picture is constructed by combining smaller pictures. For example, we can create a row of pictures by putting pictures beside each other. This idea of creating complex things from simpler things is known as *composition*.
There are several implications of this, which means that Doodle works differently from many other graphics libraries. 

The first implication is that Doodle does not draw anything on the screen until you explicitly ask it to, usually by calling the `draw` method. A picture represents a description of something we want to draw. A backend turns this description into something we can see (which might be on the screen or in a file). This separation of description and action is known as the *interpreter pattern*. The description is a "program" and a backend is an "interpreter" that runs that program. In the graphics world the approach that Doodle takes is sometimes known as [retained mode][retained-mode], while the approach of drawing immediately to the screen is known as [immediate mode][immediate-mode].

Another implication is that Doodle can allow relative layout of objects. In Doodle we can say that one picture is next to another and Doodle will work out where on the screen they should be. This requires a retained mode API as layout needs to keep around information about a picture to work out how much space it takes up.

A final implication is that pictures have no mutable state. This is needed so that we can, for example, put a picture next to itself and have things render correctly.

All of these ideas are core to functional programming, so you may have seen them in other contexts. If not, don't worry. You'll quickly understand them once you start using Doodle, as Doodle makes the ideas very concrete.


## Support Differences Between Backends

Another core goal of Doodle is to support the different capabilities of different backends. The alternative is to work only with the features that are found across all backends. In fact earlier versions of Doodle did this but we found it too limiting. There are lots of fun features only available on particular platforms and we want to be able to play with them!

The implication of this is that the core of Doodle is built in "tagless final" style, which means the core functionality is split across a number of interfaces or algebras. Backends only have to implement the subset of algebras that they support. 


## Be Easy to Use

Doodle is designed to be easy to use, while keeping to the principles above. In particular we value ease-of-use above performance, though we will certainly optimize code where possible.

[retained-mode]: https://en.wikipedia.org/wiki/Retained_mode
[immediate-mode]: https://en.wikipedia.org/wiki/Immediate_mode_(computer_graphics)
