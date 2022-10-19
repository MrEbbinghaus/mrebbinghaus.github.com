**Authors**: <a class="author" href="mailto:ebbinghaus@hhu.de">Björn Ebbinghaus</a>, Martin Mauve

[PDF](https://publications.cs.hhu.de/library/Ebbinghaus2020a.pdf)

<div class="aspect-w-16 aspect-h-9">
<iframe src="https://www.youtube-nocookie.com/embed/Owexcgd5pOU" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
</div>


## Introduction
With _decide_ we want to enable a large crowd of participants to decide on a complex issue, such as how to make the best use of a given budget.
In particular, we are interested in understanding how online argumentation and online prioritization schemes can be combined to support collective decision-making.

We have used decide to let our students collectively decide on how to use a real-world budget to improve the computer science course of study at HHU<!--\cite{Ebbinghaus2019a}-->. In our demo we will show the setup used in that experiment and report on the outcome.

##  The _decide_ collective decision system
_decide_ employs a three-step approach to collective decision-making.
In the first step participants can introduce proposals.
For each proposal an estimated cost is provided by that participant.
All participants then use dialog-based argumentation<!--\cite{Krauthoff2018b}--> to argue about the validity and priority of the proposals.
<!--This is shown in \autoref{fig:dbas-decide}.-->

In the second step the proposals are validated.
That is to say in our specific experiment we checked if there are any reasons why any of the proposals cannot be realized even if the proposed resources were allocated to it.
For example, one proposal required significant construction work which was not feasible.
The remaining proposals with the attached argumentation then enter the next step.

In the final step the participants prioritize the proposals.
First, the participants select the proposals that they want to support.
Then they order the supported proposals by their own priority <!--(see \autoref{fig:decide-example})-->.
The arguments attached to the proposals can be viewed and extended in this phase, but no new proposals can be created.
The final result is then calculated using a truncated Borda count followed by a greedy collection of proposals which fit the budget.

## Future Work

We have received valuable feedback from the students who used _decide_.
One main issue is that dialog-based argumentation tends to involve the participant in a lengthy exchange of pro and contra arguments.
This is good to gain an in-depth understanding of all positions, but it makes it hard to gain a quick overview of the main points.
One main issue is therefore to improve the argumentation step and also to test other approaches --– such as nested pro and contra lists.

A second issue is the algorithm used to reach a decision.
We would like to experiment with other voting schemes and see which of those are considered to be fair by the participants.