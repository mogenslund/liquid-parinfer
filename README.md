# liquid-parinfer
Perinfer for [liquid text editor](https://github.com/mogenslund/liquid)

## Status
Status is: Ready to try and test!

I do not have much experience with parinfer so far, so feedback on expectations are welcome. I have kind of started to like parinfer through this project.

## Install
I will assume you have a local setup which depends on liquid. The steps below may be adjusted to your local setup and if you use Leiningen instead of deps.edn. (It is also possible to reference the github project directly from deps.edn)

Basically the goal is to include liquid-parinfer and activate it by executing the `dk.salza.parinfer/run` function.

Clone this repo to some folder and reference it from your deps.edn file, like this

```clojure
{:deps {parinfer {:local/root "<some path>"}}}
```

In my own setup it looks like:

```clojure
{:deps {parinfer {:local/root "../../proj/liquid-parinfer"}}}
```
together with a lot of other dependencies and paths.

To enable the extension require the following in you code:

```clojure
(ns user
  (:require [dk.salza.parinfer]))
```

and execute the following in the code:

```clojure
(dk.salza.parinfer/run)
```

This will enable parinfer and add an interactive function "Toggle parinfer" (Use C-space to get typeahead to choose the interactive function).
Toggle parinfer will toggle "insert-mode" between using parinfer and not using parinfer.

## Credits
This project takes advantage of [github.com/oakmac/parinfer-jvm](https://github.com/oakmac/parinfer-jvm).
