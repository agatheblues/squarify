import squarify.library.*;
import java.util.Arrays;
import java.util.ArrayList;

// CONSTANTS
float CANVAS_WIDTH = 1000;    // The width of the canvas to fill
float CANVAS_HEIGHT = 1000;   // The height of the canvas to fill
float CANVAS_ORIGIN_X = 0;    // The x origin of the canvas to fill
float CANVAS_ORIGIN_Y = 0;    // The y origin of the canvas to fill

// SQUARIFY DATA
ArrayList<SquarifyRect> rects;  // The rects list will contain geometry for each rectangle to draw
ArrayList<Float> values = new ArrayList<Float>(Arrays.asList(50f, 25f, 100f, 10f, 75f)); // Values defining the squarified layout
  
void setup() {
  size(1000, 1000);
  smooth();
  Squarify s = new Squarify(values, CANVAS_ORIGIN_X, CANVAS_ORIGIN_Y, CANVAS_WIDTH, CANVAS_HEIGHT);
  rects = s.getRects();
}

void draw() {
  background(255);
  noStroke();
  
  for (int i = 0; i < rects.size(); i++) {
    // Draw a rectangle
    SquarifyRect r = rects.get(i);
    fill(map(r.getValue(), 0, 100, 50, 255));
    rect(r.getX(), r.getY(), r.getDx(), r.getDy());
    
    // Draw rectangle value
    textAlign(CENTER, CENTER);
    textSize(14);
    fill(0);
    text("Id: " + r.getId() + ", Value: " + round(r.getValue()), r.getX() + r.getDx()/2, r.getY() + r.getDy()/2);
   }
}