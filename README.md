# Squarify

Squarify is a Processing library that implements the squarify treemap layout algorithm.
It divides an area into rectangles whose areas is sized based on a set of values, the rectangles being optimized to be as squary as possible.

This library is a translation in Java of an [original Python algorithm](https://github.com/laserson/squarify) made by [Laserson](https://github.com/laserson). 

## Installation

### Contributions Manager

Open up the Contributions Manager in the Processing IDE. Search for `Squarify` and install the library.

### Manual Install

Go to the releases page of this repo and download the .zip file from there.
Unzip it and move it into the libraries folder of your Processing sketches.

## Usage

You can find a fully working example called `UsingSquarify.pde` in this repository.

```
Squarify s = new Squarify(values, CANVAS_ORIGIN_X, CANVAS_ORIGIN_Y, CANVAS_WIDTH, CANVAS_HEIGHT);
ArrayList<SquarifiedRect> rects = s.getRects();
```

Instantiating a Squarify object with values and canvas geometry will give you access to a list of `SquarifiedRect` objects.
These objects contain each rectangle geomtry data:

- `x` is the top left x coordinate
- `y` is the top left y coordinate
- `dx` is the rectangle width
- `dy` is the rectangle height

As a result, to draw a rectangle on your processing sketch, you can do:

```
SquarifiedRect r = rects.get(0);	// Draws first rect in the list
rect(r.getX(), r.getY(), r.getDx(), r.getDy());
```

## UsingSquarify example output

![Image of squarified output](https://github.com/agatheblues/squarify/blob/master/examples/squarified_example.png)