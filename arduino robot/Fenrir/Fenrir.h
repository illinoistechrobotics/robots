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

#define MOTOR_RIGHT 0
#define MOTOR_LEFT  1

#define MOTOR_RIGHT_PIN_A 3
#define MOTOR_RIGHT_PIN_B 9
#define MOTOR_LEFT_PIN_A 10
#define MOTOR_LEFT_PIN_B 11

#define CELL_1 A0 
#define CELL_2 A1
#define CELL_3 A2
#define CELL_4 A3
#define CELL_5 A4
#define CELL_6 A5
#define CELL_7 A6
#define CELL_8 A7

#define CURRENT A8

#define SHUTOFF_A 43
#define SHUTOFF_B 42
#define SHUTOFF_C 41
#define SHUTOFF_D 40

#define ENCOD1_PINA 12
#define ENCOD1_PINB 13
#define ENCOD2_PINA A14
#define ENCOD2_PINB A15
    
                      //3v, 4v
int volt_table[8][2]={{631,837},//Cell 1
                      {316,417},//Cell 2
                      {197,261},//Cell 3
                      {147,195},//Cell 4
                      {634,835},//Cell 5
                      {312,412},//Cell 6
                      {197,261},//Cell 7
                      {146,194}};//Cell 8





