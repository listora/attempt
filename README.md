# Attempt

[![Build Status](https://travis-ci.org/listora/attempt.svg?branch=master)](https://travis-ci.org/listora/attempt)

A Clojure library designed to evaluate functions asynchronously with
retries and fallback values.

## Installation

Add the following dependency to your project.clj file:

```clojure
[listora/attempt "0.1.0-SNAPSHOT"]
```

## Usage

Require the library:

```clojure
(require '[listora.attempt :refer [attempt]])
```

Then use the `attempt` function to evaluate a zero-argument function
asynchronously. The result is returned as a promise.

```clojure
@(attempt #(+ 1 1))
=> 2
```

If the function excepts, a fallback result is returned. By default
this is `nil`, but it can set to any value.

```clojure
@(attempt #(/ 1 0) {:fallback :error})
=> :error
```

The fallback can also be a delay. The delay is forced before being
returned. This allows for fallbacks that are only evaluated if the
function fails.

```clojure
@(attempt #(/ 1 0) {:fallback (delay :error)})
=> :error
```

When dealing with an I/O source there may be temporary failures, and
it's often worth retrying the function after a delay. You can do this
by specifying a sequence of numbers corresponding to the delay in
milliseconds between retries.

For example the sequence `[1000 5000 10000]` would retry the function
three times, once after 1 second, then again after 5 seconds, then
once more after 10 seconds. If all retries fail, the fallback result
is returned.

```clojure
@(attempt #(+ 1 1) {:retries [1000, 5000, 10000]})
2
```

The [listora/again][] library contains functions for creating patterns
of retry strategies compatible with `attempt`.

[listora/again]: (https://github.com/listora/again)

## License

Copyright © 2014 Listora

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
