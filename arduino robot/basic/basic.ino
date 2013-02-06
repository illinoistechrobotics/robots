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
#include <robot.h>

#define COMM Serial
#define BAUDRATE 57600

Robot robot = Robot((HardwareSerial&)COMM, BAUDRATE, TIMER_25HZ_MASK | TIMER_100HZ_MASK, Robot::UNKNOWN_ROBOT);
//The third argument sets the timers as a mask. These timers can not be changed at run time!!! 

void setup() {
  init();
}

void loop() {
  robot_event ev;
  robot.update();
  if(robot.getEvent(&ev) == true){ 
    switch(ev.command){
    case ROBOT_EVENT_CMD:
      //break left out
    case ROBOT_EVENT_CMD_STOP:
      //break left out
    case ROBOT_EVENT_CMD_START:
      //break left out
    case ROBOT_EVENT_CMD_REBOOT:
      on_command_code(&ev);
      break;
    case ROBOT_EVENT_CMD_FAILSAFE:
      on_failsafe(&ev);
      break;
    case ROBOT_EVENT_CMD_HEARTBEAT:
      on_heartbeat(&ev);
      break;
    case ROBOT_EVENT_STATUS:
      on_status(&ev);
      break;
    case ROBOT_EVENT_JOY_AXIS:
      on_axis_change(&ev);
      break;
    case ROBOT_EVENT_JOY_BUTTON:
      if(ev.b == 1)
        on_button_down(&ev);
      else if (ev.b == 0)
        on_button_up(&ev);
      break;
    case ROBOT_EVENT_JOY_HAT:
      on_joy_hat(&ev);
      break;
    case ROBOT_EVENT_JOY_STATUS:
      on_joy_status(&ev);
      break;
    case ROBOT_EVENT_KEYBOARD:
      on_keyboard(&ev);
      break;
    case ROBOT_EVENT_DISPLAY:
      on_display(&ev);
      break;
    case ROBOT_EVENT_TIMER:
      switch(ev.index){
        case TIMER_P1HZ:
          on_p1hz_timer(&ev);
          break;
        case TIMER_1HZ:
          on_1hz_timer(&ev);
          break;
        case TIMER_2HZ:
          on_2hz_timer(&ev);
          break;
        case TIMER_5HZ:
          on_5hz_timer(&ev);
          break;
        case TIMER_10HZ:
          on_10hz_timer(&ev);
          break;
        case TIMER_20HZ:
          on_20hz_timer(&ev);
          break;  
        case TIMER_25HZ:
          on_25hz_timer(&ev);
          break;
        case TIMER_50HZ:
          on_50hz_timer(&ev);
          break;
        case TIMER_100HZ:
          on_100hz_timer(&ev);
          break;
        default:
          on_other_timer(&ev);
          break;
      }	
    case ROBOT_EVENT_MOTOR:
      on_motor(&ev);
      break;
    case ROBOT_EVENT_SOLENOID:
      on_solenoid(&ev);
      break;
    case ROBOT_EVENT_POSE:
      on_pose(&ev);
      break;
    case ROBOT_EVENT_ADC:
      on_adc(&ev);
      break;
    case ROBOT_EVENT_VARIABLE:
      on_variable(&ev);
      break;
    case ROBOT_EVENT_IMU:
      on_imu(&ev);
      break;
    case ROBOT_EVENT_ENCODER:
      on_encoder(&ev);
      break;
    case ROBOT_EVENT_EEPROM:
      on_eeprom(&ev);
      break;
    case ROBOT_EVENT_IO:
      on_io(&ev);
      break;
    case ROBOT_EVENT_CMD_SHUTDOWN:
      on_shutdown(&ev);
      break;
    default:
      on_unknown_command(&ev);
      break;
    }
  }
}
