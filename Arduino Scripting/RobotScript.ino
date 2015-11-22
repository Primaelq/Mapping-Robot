
#include <AFMotor.h>
#include <Servo.h>

const int trigPin = 39;
const int echoPin = 45;

AF_DCMotor motorRight (3);
AF_DCMotor motorLeft (4);

Servo servo1;

long obstacleDistance;
long lecture_echo;

int i = 0;

bool turningRight = false;

void setup()
{
  Serial.begin(9600);

  servo1.attach(9);
  
  pinMode(trigPin, OUTPUT);
  digitalWrite(trigPin, LOW);
  pinMode(echoPin, INPUT); 

  motorRight.setSpeed (150);
  motorRight.run (RELEASE);

  motorLeft.setSpeed (150);
  motorLeft.run (RELEASE);

  servo1.attach(9);
}

void loop()
{

  DistanceCalculation ();
  
  motorRight.run (FORWARD);
  motorLeft.run (FORWARD);
  
  if (i <= 50)
  {
    turningRight = true;
  }
  else if (i >= 130)
  {
    turningRight = false;
  }

  if (turningRight)
  {
    LookRight ();
  }
  else
  {
    LookLeft ();
  }

  Serial.print (i);
  
  servo1.write (i);
    
  if (obstacleDistance < 20 && i > 40)
  {
    TurnLeft ();
  }
  else if (obstacleDistance < 20 && i < 40)
  {
    TurnRight ();
  }
  
  delay (5);
}

// Turn Servo
void LookRight ()
{
  i++;
}

// Turn Servo
void LookLeft ()
{
  i--;
}

void TurnRight ()
{
  motorRight.run (BACKWARD);
  motorLeft.run (FORWARD);
}

void TurnLeft ()
{
  motorRight.run (FORWARD);
  motorLeft.run (BACKWARD);
}

void DistanceCalculation ()
{
  digitalWrite(trigPin, HIGH); 
  delayMicroseconds(10); 
  digitalWrite(trigPin, LOW); 
  lecture_echo = pulseIn(echoPin, HIGH);
  obstacleDistance = lecture_echo / 58;
}

