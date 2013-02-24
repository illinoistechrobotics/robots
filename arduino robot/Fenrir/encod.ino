//    encoder.c - uses interupts to count the encoders value
//    Copyright (C) 2011 Illinois Institute of Technology Robotics
//	  <robotics@iit.edu>
//
//    This program is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License along
//    with this program; if not, write to the Free Software Foundation, Inc.,
//    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.


struct encoder{
  long count = 0;
  unsigned long time = 0;
  int dir;
  int prev;
};


volatile struct encoder encod1;
volatile struct encoder encod2;

void encoder_setup(){
  
  pinMode(ENCOD1_PINA, INPUT);
  pinMode(ENCOD1_PINB, INPUT);
  pinMode(ENCOD2_PINA, INPUT);
  pinMode(ENCOD2_PINB, INPUT);
    
  //not using arduino built in interrupt function for speed reason
  //this requires to uncommment line 230-240 in WInterrrupt.c in the arduino library
  //this uses the accutally pin interrupts which allow more flexiblity but there are only two pins
  //EICRA = (EICRA & ~((1 << ISC00) | (1 << ISC01))) | (CHANGE << ISC00);
  //EIMSK |= (1 << INT0);
  
  //using the pin interrupt vector. don't need to uncomment the interrupt in the arduino library. 
  //page 69 on the data sheet explains how to set up the interrupts
  //datasheet: http://www.atmel.com/dyn/resources/prod_documents/doc2545.pdf
  //arduino pins: http://arduino.cc/en/Hacking/PinMapping168
  PCMSK2 = (1 << PCINT22) | (1 << PCINT23);  //PIN A14 and A15
  PCIFR |= (1 << PCIF2);
  
  PCMSK0 = (1 << PCINT6) | (1 << PCINT7);  //PIN 12 and 13
  PCIFR |= (1 << PCIF0);
  
  encod1.count = 0;
  encod2.count = 0;
  
  encod1.prev = digitalRead(ENCOD1_PINA) << 1;
  encod1.prev |= digitalRead(ENCOD1_PINB);
  
  encod2.prev = digitalRead(ENCOD2_PINA) << 1;
  encod2.prev |= digitalRead(ENCOD2_PINB);
}

//encoder1 interrupt
ISR(PCINT2_vect) {
  
  int now;
  int n_dir;
  
  //XOR prevA phase with current phase will tell the direction the encoder moved 0 clockwise 1 counterclockwise 
  //Using port manipulation to read the pin which is considerably faster than using DigitalRead
  //the link below explains how port mainpulation works and requires some boolean math and bit shifting
  //http://www.arduino.cc/en/Reference/PortManipulation 
  //places the vlaue of the encoder to the lower bits
  now = (PINK & B11000000) >> 6; //pin A14 and A15
  n_dir = (encod1.prev & B00000001) ^ ((now & B00000010) >> 1);
  
  //checks to make sure we don't get false reads 
  //the grayhill can trigger the interupt twice between clicks
  //makes sure that the encoder moved from last positions
  //souldn't be a problem with higher quality encoders   
  if(now == encod1.prev) 
    return;
  encod1.prev = now;
  
  if(n_dir)
    encod1.count--;
  else
    encod1.count++;
  
  encod1.dir = n_dir;  
}

//this is angle only so the count will be between 0 and 15
ISR(PCINT0_vect) {
  int now;
  int n_dir;
  
  now = (PINB & B11000000) >> 6; //pin 12 and 13
  n_dir = (encod2.prev & B00000001) ^ ((now & B00000010) >> 1);
  
  if(now == encod2.prev) //checks to make sure we don't get false trigger 
    return;
  encod2.prev = now;
  
  if(n_dir){
      encod2.count--;
  }
  else{
      encod2.count++; 
  }
  
  encod2.dir = n_dir;
}
void zero_encod(struct encoder * encod){
  encod->count=0;
}
int velocity(struct encoder * encod){
  int velocity = encod->count/(millis()-encod->time);
  encod->time=millis();
  zero_encod(encod);
  return velocity;
}


void print_encod(){
  Serial.println("A ");
  Serial.println(encod1.count);
  Serial.println("B ");
  Serial.println(encod2.count);
}
