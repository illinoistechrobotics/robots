/*
Copyright 2013 (c) Illinois Tech Robotics <robotics.iit@gmail.com>
 
 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:
 
 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.
 
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
double readVolts(int cell){
  int cell_norm = cell-54;//values of analog start at 54
  int a = volt_table[cell_norm][1];
  int b = volt_table[cell_norm][0];
  double slope = 1/((double)(a-b));
  return slope*(analogRead(cell)-b)+3.0;
}
double readVolts_norm(int cell){
  if(cell==CELL_1||cell==CELL_5)
    return readVolts(cell);
  return readVolts(cell)-readVolts(cell-1);
}
double readCurrent(){
  return ((analogRead(CURRENT)-CURRENT_VOE)/(double)1023)*5*100/.625;
}
double readPower(){
  return (readVolts(CELL_4)+readVolts(CELL_8))/2*readCurrent();
}





