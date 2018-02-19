---
layout: post
title: "Talk about clojure.core.async"
tagline: "I did a talk about core.async for the 'Clojure Meetup Düsseldorf'."
image: https://pbs.twimg.com/media/DUVE-4xXkAAOmL9.jpg
header:
  image: /assets/patterns/asanoha-400px.png
invert-head: true
tags: ["meetup", "clojure", "talk"]
keywords: meetup, clojure, talk, core.async
lang: en
comments: false
---

As a programmer who tries to use [clojure](https://clojure.org/)[(script)](https://clojurescript.org/) wherever possible, I am a frequent visitor of the [Clojure Meetup Düsseldorf](https://www.meetup.com/de-DE/Dusseldorf-Clojure-Meetup).
Recently I was asked, what clojure tools/librarys I am using, worth sharing. It happens that a couple of days before, I came in contact with [`core.async`](https://github.com/clojure/core.async) while writing a single page application in clojurescript.
One thing led to another and I agreed to talk about [`core.async`](https://github.com/clojure/core.async) at the next meetup.

As a novice in [`core.async`](https://github.com/clojure/core.async) myself, this was an interesting task ahead.
Having to read a paper about [staged event driven architecture](https://dl.acm.org/citation.cfm?id=502057) for the lecture *Architecture of Distributed Systems* introduced me to the advantages of seperate running threads and the communication between via queues/channels between them.


After the talk we played around a little with a playground I developed, to test [`core.async`](https://github.com/clojure/core.async) in a scenario of an ant colony.
This playground has the goal to make everything asynchronous, which maybe wasn't an ideal use of async, but it was fun at least to watch the little ants run around.
The code can be found on [Github](https://github.com/clojuredus/async-ants).


All in all it was a nice evening and I hope the listeners have learned something they can use to build great software. If I'm stumbling over something in the future I will definitely consider to share it on a meetup again.


Thanks to [InVision](https://www.invision.de/), which hosted the event and provided us with drinks and [great food](https://www.instagram.com/invision_chefs/).
Also check out [Joy Clark](https://joyclark.org)s wonderful [sketchnote](https://joyclark.org/sketchnote/clojure-meetup/2018/01/24/clojure-meetup.html)!

The keynote can be found [here](https://www.icloud.com/keynote/0UasaP9fgxNOu-bwgxZMhH5Tg).

<div class="hide-on-small embed-responsive embed-responsive-4by3 ">
<iframe class="embed-responsive-item" src="https://ebbinghaus.me/core-async/index.html">
</iframe>
</div>