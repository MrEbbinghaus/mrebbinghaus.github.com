**Authors**: Christian Meter, <a class="author" href="mailto:ebbinghaus@hhu.de">Björn Ebbinghaus</a>, Martin Mauve

[PDF](https://wwwcn.cs.uni-duesseldorf.de/publications/publications/library/Meter2018a.pdf)

## Introduction
In prior work we introduced the [_Dialog-Based Argumentation System_ (D-BAS)](https://dbas.cs.uni-duesseldorf.de) [1], a
_Dialog Game Execution Platform_ (DGEP) [2] for dialog-based online argumentation.
D-BAS allows users to exchange proposals and arguments with each other in the form of
a time-shifted dialog where arguments are presented and acted upon one-at-a-time. It is
designed as a full-stack, stand alone web-application.

However, currently, the vast majority of online discussions takes place in social networks such as Facebook or Twitter and not on dedicated argumentation web-sites. 
We therefore investigated how the functionality of a dialog-game execution platform, such
as D-BAS, can be included in a seamless way into social networks. Our solution to the
problem is a social bot called Jebediah. It provides a front-end to DGEPs that can be
integrated into social networks in a seamless way.

## _Jebediah_ – a social bot for online argumentation
<figure>
    <img src="assets/jeb.png" alt="Dialog between a user and the conversation agent.">
    <figcaption><b>Figure 1.</b> Conversation with Jebediah. On the right side is the user's
          input. On the left side are the answers.
    </figcaption>
</figure>

_Jebediah_ is a social bot based on Google’s framework [Dialogflow](https://dialogflow.com)
for _Artificial Intelligence_ (AI) development. It connects Dialogflow with a DGEP such as D-BAS’ backend.
Dialogflow enables a seamless integration into many popular social networks, e.g. Facebook or Twitter, and provides 
processing of text-input from conversations. We leverage this to enable natural language access where the AI is used to parse and interpret the user’s input, whereas the interpreted data is sent to D-BAS’ DGEP, in order to calculate
the next steps in the discussion. This setup allows us to directly have a conversation with interested users without the need to leave the current site and to provide a solution to reduce crowded comment sections, e.g. inside a Facebook post.

Jebediah exposes the full functionality of D-BAS, i.e. collect statements from users,
integrate them into a discussion graph and present the next statement to the user (see
Fig. 1). It is then possible, in natural language, to interact with arguments and experiences
from those users. Users can also start a dialog with the agent and ask for possible entities
in the discussion, e.g. topics or other positions.

Where D-BAS’ interface shows the user a list of possible steps to choose from, this
is hardly manageable in a text-only or even voice-only environment. Therefore Jebediah
has to lead the user in a way that advances the conversation into deeper levels of the
topic, while being flexible enough to react to user actions which are not a usual part of
the D-BAS discussion flow. This is even more important in a voice interface where the
user has to memorize the current part of the discussion.

## Related Work
Arvina [3] is a system that bears a lot of similarities to our work. With Arvina it is
possible to replay previously stored discussions and interact with the recording. Multiple
real users can participate in the debate and also add new statements. Jebediah, in contrast,
aims at enabling a seamless integration of a DGEP into social networks and at providing
a discussion using natural language.

## References
- [1] T. Krauthoff, C. Meter, G. Betz, M. Baurmann, and M. Mauve, “D-BAS – A Dialog-Based Online Argumentation System,” in Computational Models of Argument, September 2018, pp. 325–336.
- [2] F. Bex, J. Lawrence, and C. Reed, “Generalising argument dialogue with the dialogue game execution
platform.” in COMMA, 2014, pp. 141–152.
- [3] M. Snaith, J. Lawrence, and C. Reed, “Mixed initiative argument in public deliberation,” Online Deliberation, 2010.