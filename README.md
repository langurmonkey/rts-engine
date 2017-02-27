## RTS engine, what?
**Disclaimer:** _This is a work in progress. Most of the code is badly structured, use it at your own risk._

This is an **RTS engine** project on which I work very occasionally. It is a testing sandbox and has no design or focus whatsoever. Maybe it'll grow into a proper game in the future.
I started playing with the idea as a time killer some years ago, kicking off the development with a fast version of the A* pathfinding algorithm backed not by a grid (as usual) but by a quadtree. Quadtrees make pathfinding super-fast because of their hierarchical division of space and their adaptive partition sizes. Even though I used visibility graphs to store the navigable nodes from one given point, quadtrees are also fast for checking the properties/elements of a position's surroundings, for child nodes are always spatially contained in parent nodes.

Once I got this pathfinding on quadtree thing up and running, It was time to implement the movement of my entities. I dove a bit into the topic and stumbled upon [Craig Reynolds steering behaviours](http://www.red3d.com/cwr/papers/1999/gdc99steer.html). They turned out to be an excellent method of implementing movement. I found these steering behaviours very powerful at producing organic-like movements that do not look forced at all. However, they are usually hard to implement and need A LOT of tweaking to really get them rolling. If you are interested in the topic you can check out Reynolds' original paper or have a look at the book "Programming game AI by example" by Mat Buckland.

Currently libgdx itself provides an implementation of steering behaviours, wihch is probably neater than this one.

## Current features
- 2D graphics.
- Real time selection and movement of units.
- Unit life bars.
- Steering behaviours that work (with a lot of param tweaking).
- Tiled tile map integration.
- Quadtree and grid for spatial awareness.
- Some basic graphic effects.

## Video

Here a video demonstrating a few of the features available as of now.

[![RTS Engine demo YouTube](http://img.youtube.com/vi/17fDqcZ0mu8/0.jpg)](http://www.youtube.com/watch?v=17fDqcZ0mu8 "RTS Engine demo video")

## Licensing

This software is distributed under the [GPLv3](https://www.gnu.org/licenses/quick-guide-gplv3.html) license.
