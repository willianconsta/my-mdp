
===============================
sw -- Scrolling Window program.
===============================

Copyright (C) 2000 Lucent Technologies
All Rights Reserved

Permission to use, copy, modify, and distribute this software and
its documentation for any purpose and without fee is hereby
granted, provided that the above copyright notice appear in all
copies and that both that the copyright notice and this
permission notice and warranty disclaimer appear in supporting
documentation, and that the name of Lucent or any of its entities
not be used in advertising or publicity pertaining to
distribution of the software without specific, written prior
permission.

LUCENT DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE,
INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS.
IN NO EVENT SHALL LUCENT OR ANY OF ITS ENTITIES BE LIABLE FOR ANY
SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER
IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION,
ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
THIS SOFTWARE.

Updated 2002 and 2005 by AMPL Optimization LLC, which also
disclaims all warranties with regard to this software.

========
sw usage
========

Sw provides a Scrolling Window to "console" programs -- those
that run in a Command Prompt window; sw lets you manipulate its text
with "copy", "cut", "paste", which exchange selected text with the
Clipboard, and with "clear", which removes text without disturbing
the Clipboard.  You can send text to the standard input of a
program that is running under sw either by typing at the bottom
of the scrolling window, or by selecting text earlier in the window
and choosing "send".

You can invoke sw from a Command Prompt window, from another sw
window, or from Run|Start.  It is best to keep sw.exe in one of
the directories on your search path.  Otherwise, unless sw.exe is in
the current directory, you will have to give a full path to sw.exe.

If the invocation is of the form

	sw progname [arg [arg...]]

then sw attempts to start progname with the specified arguments.
Otherwise sw waits for you to type the name of a program and any
arguments, and it again tries to start the program.  If the program
is a GUI (graphical) program, rather than a console program, then
sw detaches itself from the program and is ready to start another
program.  Otherwise the program is a console program, and sw
provides standard input, standard output, and standard error to
the program.

Sw looks for progname first in the current directory, then in
the directories specified by the environment variables PATH and
Path (if set).

You can determine sw's state by looking at its title bar.  If
you invoke sw without arguments, then the title bar initially
just says "sw".  Once you have given the name of a program to sw,
either on sw's command line or by typing in sw's window, the title
bar changes to a line of the form

	sw: program_state program_name

where program_state is one of the following:

	cannot find
	cannot start
	launched
	running
	finishing
	finished

If the sw detaches itself from a GUI program, the state is
"launched".  If sw successfully starts a console program,
the state is "running".  When the program terminates, the
state may briefly be "finishing" while sw appends the final
program output to the bottom of its window.  Then the state
goes to "finished", and the program's exit code (often "= 0")
appears at the end of sw's title bar.  When the state is
"finished" or "launched", you are free to invoke another
program by typing its name and arguments after the "sw: "
prompt at the end of the window.

With the "Edit" menu, mouse buttons and some control-key
combinations, you can edit text anywhere in an sw window.

The right mouse button has most of the menu items that also
appear on the "Edit" menu.

Many Windows programs recognize the control-key combinations
Ctrl+C, Ctrl+V, and Ctrl+X as synonyms for "copy", "paste",
and "cut"; sw also recognizes these control-key combinations.
If you are familiar with plan 9 or "9term" or "sam", some
other control-key combinations listed below (Ctrl+U, Ctrl+W)
will be familiar.

Sw also recognizes the following control-key combinations:

	Ctrl+C = "copy" (or send an "interrupt")
	Ctrl+V = "paste"
	Ctrl+X = "cut"
	Ctrl+U = delete current line
	Ctrl+W = delete previous word
	Ctrl+B = send an "interrupt" signal to the current
		 program and its descendents, provided they
		 are suitably linked
	Ctrl+K = terminate (kill) the current program
	Ctrl+S = temporarily suspend reading output
	Ctrl+Q = resume reading output
	Ctrl+D = send partial line, if nonempty, or end input
		 to the program (close its standard input)
	Ctrl+Z = same as Ctrl+D

By default, Ctrl+C with no text selected is treated as Ctrl+B,
to accord with behavior of Ctrl+C on Unix and Linux systems.
This treatment of Ctrl+C can be adjusted on the File menu.

When output is suspended by Ctrl+S, it is not lost, but
is not read until you type Ctrl+Q.  Suspending output will
eventually cause the program to pause until you type Ctrl+Q.
If the program has just sent a whole buffer of text to sw,
it may take a few seconds for sw to respond to Ctrl+S,
as sw will not see your keystroke until it has copied the
buffer to the screen.  When sw recognizes a new Ctrl+S, it
adjusts its title bar by changing "sw:" to "^S:"; when you
subsequently type Ctrl+Q, sw changes the title bar back.

It is best to let programs terminate normally; Ctrl+K or Kill
on the File menu are meant only as a last resort.

If no program is currently running, Ctrl+D or Ctrl+Z act the
same as File|Exit: they cause sw itself to terminate.

Sw maintains a history of recent input lines, and it takes
special note of input lines that cause it to run programs.
If sw is not currently running a program, hitting the up and
down arrow keys (on the keyboard) will cause sw to show
previous command invocations on the input line, where you
can edit them (if desired) and run them again by hitting the
enter key.  If sw is running a program, then the up and down
arrow keys let you recall recent lines of input to the
program, where again you can possibly change them and can
enter them again by hitting the enter key.  If you hold the
control key down while hitting the up or down arrow key, sw
recalls the other sort of line:  if sw is currently running
a program, control-up moves to the previous command
invocation, and if sw is not currently running a program,
control-up moves to the most recent input line of any kind
(not necessarily a program invocation).  For example, to
reach the second input line you typed at the current
program, you could type control-up arrow (up-arrow with the
control-key pressed), then plain down arrow (without the
control-key being pressed) twice.

The Edit menu provides a way to discard the accumulated history:
select "clear History".

Control-left arrow moves the cursor to the right-hand end of
the next "word" to the left, and control-right arrow moves the
cursor to the left-hand end of the next "word" to the right.

Double-clicking text with the left mouse button causes sw to
select, if possible, text to the left or right of the mouse
cursor.  If an open bracket -- any of ( { [ or < -- appears
just to the left of the cursor, sw looks for a matching closing
bracket -- one of ) } ] or > -- to the right, selecting the text
between the brackets if found.  If a closing bracket appears just
to the right of the cursor, sw similarly looks left for a
corresponding opening bracket and, if found, selects the text
between the brackets.  A single quote ' acts as both a left and
corresponding right bracket, and similarly for a double quote "
and an acute quote `.

Normally, when you type a carriage return (i.e., hit the enter
key) after typing text at the bottom of sw's window, sw
processes the line you have just entered.  If a program is
currently running, sw sends the line to the program's standard
input.  Otherwise, it treats the first word of the line as the
name of a program to start.  (If the program name has internal
spaces, surround the name with " characters.)

The File menu provides a way to control how much of the current
input line is sent when you hit the enter key.  By default (with
"Whole line at enter key" selected on the File menu), the whole
line is sent, regardless of where the cursor is within the line.
But if "Just to cursor at enter key" is checked on the File
menu, then text following the cursor is not sent, but remains
available to be edited and sent later.

On sw's File menu, you can choose between two treatments of the
Esc (escape) key, "Linekill" or "Multiline".  Linekill is the
default:  it is checked when sw starts running.  Under Linekill,
the Esc key causes the current line to be deleted, just as Ctrl+U
does.

When Multiline is checked on the File menu, if you hit the Esc
(escape) key, sw goes into multiline mode, in which it does not
process input when you type carriage return, but rather waits for
you to hit Esc again or to type Ctrl+D or Ctrl+Z.  When sw enters
multiline mode, it adjusts its title bar by inserting
"multiline".  It remains in multiline mode until you type Esc
again; then sw removes "multiline" from the title bar and
processes all the complete lines you have finished typing since
multiline mode began.  Whether or not sw is in multiline mode, if
you type Ctrl+D or Ctrl+Z and you have typed any new characters
since the last time sw processed keyboard input (by sending it to
a running program or trying to start a new program), sw processes
all of the new text, which may just be a partial line or, in
multiline mode, several lines.

A caution: sw provides standard input, output, and error over
anonymous pipes, which do not appear to the program as a console,
so the program may buffer its output rather than writing it
immediately.

You can use sw with the usual Microsoft command processor by
invoking

	sw command

under W9x or

	sw cmd

under NT.  You can also use sw with other shells.  One other
possible difficulty is that if the program you start under sw
itself starts another console program that receives its standard
input from sw, and if you type Ctrl+D or Ctrl+Z to terminate
input to the second program, then input to the first program
will also be terminated.  (Like the buffering problem mentioned
above, this one is due to poor low-level design choices by
Microsoft.)

Sw has no provision for changing environment variables or
the current directory; you must use sw with a shell (command
processor) to gain these abilities.

Sw uses a Microsoft "edit-control" window.  Unfortunately, under
Windows 9x, edit-control windows may get confused about how much
text they can hold.  This seems not to happen if characters are
written to the window one at a time, but such writing takes much
longer than the alternative of "pasting" text.  Sw operates either
in (slow) "char mode", in which it writes one character at a time,
or in (fast) "block mode", in which it pastes new text.  Block
mode is the default, but if sw detects that the edit-control bug
has bitten, it switches to "char mode" after presenting a message
box that offers to discard the text currently in the window (which
might be suitable if a program is running that is spewing lots of
text to the screen).  The File menu lets you switch at will
between "block mode" and "char mode".  Whether the edit-control
bug bites at all seems to depend in mysterious ways on the size of
sw's window; resizing it sometimes banishes the bug.  The bug
first bites when the window is close to full, and it can
occasionally bite even in "char mode" when text is discarded at
the beginning of a nearly full window.

Sw's Help|Usage summary menu item looks for this file (readme.sw)
in the directory where sw.exe resides



===================
Using sw with AMPL:
===================

If you invoke

	sw ampl

with an ampl.exe for a version < 19991223, ampl may set its
default prompts to '' (the empty string), so you will not
see prompts unless you arrange for them, e.g., by invoking

	ampl prompts -

where "prompts" is a file of suitable "option prompt" settings
that you might have obtained by starting ampl in a Command Prompt
window and typing

	option *prompt* >prompts; quit

You could also say (perhaps in a .bat file)

	set OPTIONS_IN=prompts
or
	set OPTIONS_IN=/full/path/to/prompts

before starting sw.  This issue does not arise with Win32
versions >= 19991223 of ampl.


===========
Change log:
===========

20050221:  To permit names of executed programs to contain blanks,
recognize strings quoted by " (the double-quote character) as the path
to a program to run.
