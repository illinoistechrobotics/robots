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
int l_vel=0;
int r_vel=0;

int curr_r_vel=0;
int curr_l_vel=0;

void initialize(){
  //encoder_setup();
}

void on_command_code(robot_event *ev){

}

void on_failsafe(robot_event *ev){
  //motor(MOTOR_LEFT,0);
  //motor(MOTOR_RIGHT,0);
}

void on_heartbeat(robot_event *ev){

}

void on_status(robot_event *ev){

}

void on_axis_change(robot_event *ev){
  int l_stick, r_stick;

  if(ev->index == 1)
    l_stick = ev->i;
  if(ev->index == 2)
    r_stick = ev->i;
  int tempL = (l_stick)+(r_stick-127);
  int tempR = l_stick-(r_stick-127);
  if(tempL<0)
    tempL = 0;
  if(tempL>255)
    tempL = 255;
  if(tempR<0)
    tempR = 0;
  if(tempR>255)
    tempR = 255;

  r_vel=tempR;
  l_vel=tempL;

  //analogWrite(10, map(tempL,0,255,MIN_MOTOR_SPEED,MAX_MOTOR_SPEED));
  //analogWrite(3,map(tempR,0,255,MIN_MOTOR_SPEED,MAX_MOTOR_SPEED));

}

void on_button_down(robot_event *ev){

}

void on_button_up(robot_event *ev){

}

void on_joy_hat(robot_event *ev){

}

void on_joy_status(robot_event *ev){

}

void on_keyboard(robot_event *ev){

}

void on_display(robot_event *ev){

}

void on_p1hz_timer(robot_event *ev){
  //if(!Volt_Check())
  //shutoff();


}

void on_1hz_timer(robot_event *ev){
  for(int i = 0; i<8;++i){
    float curr = readVolts_norm(54+i);
    if(curr<3.0){
      on_failsafe(NULL);
    }
  }
  robot_event event;
  event.index=4;
  event.type = FLOAT;
  event.command = ROBOT_EVENT_VARIABLE;
  event.f = readVolts(CELL_4);
  robot.sendEvent(&event);
  event.index=8;
  event.f=readVolts(CELL_8);
  robot.sendEvent(&event);
}

void on_2hz_timer(robot_event *ev){

}

void on_5hz_timer(robot_event *ev){

}

void on_10hz_timer(robot_event *ev){

}

void on_20hz_timer(robot_event *ev){

}

void on_25hz_timer(robot_event *ev){

}

void on_50hz_timer(robot_event *ev){

}

void on_100hz_timer(robot_event *ev){

}

void on_other_timer(robot_event *ev){

}

void on_motor(robot_event *ev){
  //motor(ev->index,ev->i - 0x7FFF);
}

void on_solenoid(robot_event *ev){

}

void on_pose(robot_event *ev){

}

void on_adc(robot_event *ev){

}

void on_variable(robot_event *ev){

}

void on_imu(robot_event *ev){

}

void on_encoder(robot_event *ev){

}

void on_eeprom(robot_event *ev){

}

void on_io(robot_event *ev){

}

void on_shutdown(robot_event *ev){

}

void on_unknown_command(robot_event *ev){

}



