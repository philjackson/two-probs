# Now you've got Two Problems

If you're unlucky enough to have to process large regexps, this
library should help you make them more readable, composable and
maintainable.

It's very simple - don't expect magic.

## Install

### Leiningen

    [two-probs "0.1.0"]

### Gradle

    compile "two-probs:two-probs:0.1.0"

### Maven

    <dependency>
      <groupId>two-probs</groupId>
      <artifactId>two-probs</artifactId>
      <version>0.1.0</version>
    </dependency>

## Usage

    (ns two-probs-examples
      (:require [two-probs.core :refer :all]))

    ;; simple email matching pattern
    (let [email (re-pattern
                 (re [:beg
                      (one-or-more :word true)
                      \@
                      (cap (one-or-more [:word]))
                      \.
                      (one-or-more :word)
                      :end]))]
      (second (re-matches email "bob@example.com"))) => "example"

### Functions

* `group` - Group `expressions`, truthy final arity determines capturing status.
* `cap` - Capturing group.
* `non-cap` - Non-capturing group.
* `zero-or-more` - Match zero or more instances of `expressions`.
* `zero-or-one` - 
* `one-or-more` - Match one or more instances of `expressions`.
* `re-or` - Returns `expressions` logically or'd together.
* `literal` - Returns a quoted version of expression that can be used as a literal in a regexp.
* `times` - Match `expression` a minimum of `mn` times and a maximum of `mx` times.

### Simple chars

What's produced by Two Problems and the aliases you can use:

* `\W` (`:W`, `:non-word`)
* `\A` (`:A`, `:beginning-input`, `:beg-input`, `:beg-in`)
* `\b` (`:b`, `:boundary`, `:bndry`)
* `\B` (`:B`, `:non-boundary`, `:non-bndry`)
* `\w` (`:w`, `:word`)
* `\Z` (`:Z`, `:end-term`)
* `\S` (`:S`, `:non-space`)
* `\z` (`:z`, `:end-input`, `:end-in`)
* `\D` (`:D`, `:non-digit`)
* `.` (`:.`, `:any`)
* `\d` (`:d`, `:digit`)
* `\s` (`:s`, `:space`)
* `^` (`:start`, `:beg`, `:beginning`)
* `$` (`:$`, `:end`)
* `\G` (`:G`, `:end-match`)

## License

Copyright © 2015 Phil Jackson

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
