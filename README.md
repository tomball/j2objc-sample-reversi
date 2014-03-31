j2objc-sample-reversi
=====================

This is a sample showing how to use a Reversi game engine in an iOS app
using J2ObjC.

The original game, Othello, is written by Mats Luthman and available from
[his site](http://www.luthman.nu/Othello/Othello.html). Mats designed it to work as
both a Swing and command-line app, so the game engine and the user interface
are isolated by two interfaces. 

Because of this separation, the game engine is an ideal candidate for an
iOS application using [J2ObjC](http://j2objc.org). This Xcode project has
a (very) simple user interface written in Objective-C, game engine 
files in Java, and 
[a build rule](https://github.com/google/j2objc/wiki/Xcode-Build-Rules) 
to translate the Java files and compile them during the build.

To build this project, first install J2ObjC, then edit the Settings.xcconfig
file to update the J2OBJC_HOME environment variable with the directory where
it was installed.

### Things Left Undone

Since this project is just an example of how to use J2ObjC, the game's user
interface is missing several important features:

 * Game Over screen
 * Indication that either player had to pass due to no possible move
 * Options panel to set the game difficulty (it's set to 5 out of 10, so 
it can provide a much stronger game)
 * iPad design needs polish

Since I'm not a UI designer, any help would be welcome and appreciated.

### J2ObjC Project

**Project site:** <http://j2objc.org><br>
**J2ObjC blog:** <http://j2objc.blogspot.com><br>
**Questions and discussion:** <http://groups.google.com/group/j2objc-discuss>

