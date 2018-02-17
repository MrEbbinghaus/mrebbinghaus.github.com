---
#
# Here you can change the text shown in the Home page before the Latest Posts section.
#
# Edit jekyll-theme-simple-blog's home layout in _layouts instead if you wanna make some changes
# See: https://jekyllrb.com/docs/themes/#overriding-theme-defaults
#
layout: home
permalink: /index.html
header:
  image: /assets/img/home-header.jpg
tagline: > # this means to ignore newlines until "repository:"
  Computer scientist student from Düsseldorf, Germany.
excerpt: >
  Computer scientist student from Düsseldorf, Germany.
ref: home
lang: en
---

This will be my space to just write down stuff I'm doing.

<h2>Latest Articles</h2>
<div>&nbsp;</div>
{% include list-category-posts.html lang=page.lang category="articles" %}

---

<h2>Latest 3 Projects</h2>
<div>&nbsp;</div>
{% include list-category-posts.html lang=page.lang category="projects" max=3 %}
