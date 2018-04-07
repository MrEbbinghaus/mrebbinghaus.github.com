---
layout: post
title: "Writeup for the USD-Hackers’ Days 2018"
tagline: "A hacker challange to apply for the 2018 Hackers’ Days"
header:
  image: /assets/patterns/contemporary_china.png
invert-head: true
tags: ["challenge", "security"]
keywords: usd, hackertage, security, cyber, unity, Hackers’ Days
lang: en
comments: true
---

This year I took part in the [USD Hackers’ Days](https://www.usd.de/en/usd-hackersdays/).
They were introduced to me by fellow colleuges and friends at my university, who visited USD on their recurring Hackers’ Days for the last two years.

# The challenge
As every year, there is a challenge to solve in order to apply for a workshop at USD.
You have to retrieve a number of hidden keys in an simulated environment, six this year.
At least one is required to apply for the workshop, but your chances for acceptance are greater if you manage to get more.

This years challenge only took place inside a little unity game, which is provided on USDs website.
Don't be fooled, I tried to find keys in their website too, as there is a login window and some hidden text on the download page, but in the end I was able to get six keys from the game itself.

## 1st key
The first key is usually very obvious, as it was this year.
A snoop around the configuration files revealed the key imidiatly without much further to do.
It was just *hidden* inside the `app.info` file inside the game folder.


## 2nd key
When you launch the game you are greeted by a simple UI where you can just do two things.
Enter a license key, or quit the game.
It seems there is a challenge to get the license key!
For this we have to look, how the license is verified.

#### Taking it apart
Since the game is compiled to a bunch of DLL files we have to decompile it, but since this is a Unity game as well and we maybe want to look at the assets, we have to unpack it. For this I used the [DevXUnity-Unpacker Magic Tools](http://devxdevelopment.com/UnityUnpacker) to unpack the game and inspect its content. (I have to say I have no background with Unity games at all, but some knowledge in computer science does it.)



## 3rd key

## 4th key

## 5th key

## 6th key