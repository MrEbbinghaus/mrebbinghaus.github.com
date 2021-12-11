As a programmer who tries to use [Clojure](https://clojure.org/)[(Script)](https://clojurescript.org/) wherever possible, I am a frequent visitor of the [Clojure Meetup Düsseldorf](https://www.meetup.com/de-DE/Dusseldorf-Clojure-Meetup).
Recently I was asked, what clojure tools/librarys I am using, worth sharing. It happens that a couple of days before, I came in contact with [core.async] while writing a single page application in clojurescript.
One thing led to another and I agreed to talk about [core.async] at the next meetup.

As a novice in [core.async] myself, this was an interesting task ahead.
Having to read a paper about [staged event driven architecture](https://dl.acm.org/citation.cfm?id=502057) for the lecture *Architecture of Distributed Systems* introduced me to the advantages of seperate running threads and the communication between via queues/channels between them.

After the talk we played around a little with a playground I developed, to test [core.async] in a scenario of an ant colony.
This playground has the goal to make everything asynchronous, which maybe wasn't an ideal use of async, but it was fun at least to watch the little ants run around.
The code can be found on [Github](https://github.com/clojuredus/async-ants).

All in all it was a nice evening and I hope the listeners have learned something they can use to build great software. If I'm stumbling over something in the future I will definitely consider to share it on a meetup again.

Thanks to [InVision](https://www.invision.de/), which hosted the event and provided us with drinks and [great food](https://www.instagram.com/invision_chefs/).
Also check out [Joy Heron]s wonderful [sketchnote](https://joyheron.com/sketchnote/clojure-meetup/2018/01/24/clojure-meetup.html)!

The keynote can be found [here](https://www.icloud.com/keynote/0UasaP9fgxNOu-bwgxZMhH5Tg).

[core.async]: https://github.com/clojure/core.async 
[Joy Heron]: https://joyheron.com
